import React, {useState, useEffect} from 'react';
import {useAuth0} from '@auth0/auth0-react';
import {Loading} from './Loading';
import {Error} from './Error';
import {useAccessTokenContext} from "./AccessTokenContext";

export function ScheduleInterviewForm({onSubmit}) {
    const {  getAccessTokenSilently } = useAuth0();
    const [isCollapsed, setIsCollapsed] = useState(true);
    const [questions, setQuestions] = useState([]);
    const [loadingQuestions, setLoadingQuestions] = useState(true);
    const [errorQuestions, setErrorQuestions] = useState(null);
    const [formData, setFormData] = useState({
        question: '',
        interviewName: '',
        usePreferredTime: false,
        datePicker: '',
    });

    const handleSubmit = (event) => {
        event.preventDefault();
        // Perform form submission logic here
        // Pass the formData to the onSubmit function to update the upcoming interviews
        onSubmit(formData);
        // Reset the form after submission
        setFormData({
            question: '',
            interviewName: '',
            usePreferredTime: false,
            datePicker: '',
        });
    };

    const handleChange = (event) => {
        const {name, value, type, checked} = event.target;
        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: type === 'checkbox' ? checked : value,
        }));
    };



    const handleToggleCollapsible = () => {
        setIsCollapsed((prevIsCollapsed) => !prevIsCollapsed);
    };

    useEffect(() => {
        (async () => {
            try {
                const accessToken = await getAccessTokenSilently();

                const response = await fetch('/api/v1/questions/', {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                });

                if (!response.ok) {
                    throw new Error('Failed to fetch questions');
                }

                const data = await response.json();
                setQuestions(data);
                setLoadingQuestions(false);
            } catch (error) {
                setErrorQuestions(error);
                setLoadingQuestions(false);
            }
        })();
    }, [getAccessTokenSilently]);

    if (loadingQuestions) {
        return <Loading/>;
    }

    if (errorQuestions) {
        return <Error message={errorQuestions.message}/>;
    }

    return (
        <>
            <div className="collapsible">
                <h2 className="collapsible-heading" onClick={handleToggleCollapsible}>Schedule Interview</h2>
                {!isCollapsed && (
                <div className="collapsible-content">
                    <form id="interview-schedule-form" onSubmit={handleSubmit}>
                        <label htmlFor="question">Question</label>
                        <select id="question" name="question" value={formData.question} onChange={handleChange}>
                            <option value=""> -- Please Select a question --</option>
                            {questions.map((question) => (
                                <option key={question.id} value={question.id}>
                                    {question.text}
                                </option>
                            ))}
                        </select>
                        <br/>
                        <label htmlFor="interviewName">Name</label>
                        <input
                            type="text"
                            id="interviewName"
                            name="interviewName"
                            value={formData.interviewName}
                            onChange={handleChange}
                        />
                        <br/>
                        <label htmlFor="usePreferredTime">Use next available preferred time</label>
                        <input
                            id="usePreferredTime"
                            name="usePreferredTime"
                            type="checkbox"
                            checked={formData.usePreferredTime}
                            onChange={handleChange}
                        />
                        <br/>
                        <label htmlFor="datePicker">Date and Time of Interview</label>
                        <input
                            id="datePicker"
                            name="datePicker"
                            type="datetime-local"
                            value={formData.datePicker}
                            onChange={handleChange}
                        />
                        <br/>
                        <button type="submit">Schedule Interview</button>
                    </form>
                </div>
                        )}
            </div>
        </>
    );
}
