import React, {useEffect, useState} from 'react';
import './main.css'
import {Loading} from "./Loading";
import {Error} from "./Error";
import {useLocation} from "react-router-dom";
import {PreferredTimeTable} from "./PreferredTimeTable";
import {PreferredTimeForm} from "./PreferredTimeForm";
import {UpcomingInterviewTable} from "./UpcomingInterviewTable";
import {ScheduleInterviewForm} from "./ScheduleInterviewForm";
import {useAuth0} from "@auth0/auth0-react";

const StorytellerDetails = () => {
    const {  getAccessTokenSilently } = useAuth0();
    const [ accessToken, setAccessToken ] = useState({})
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [ storytellerDetails, setStorytellerDetails ] = useState({})
    const [ scheduledInterviews, setScheduledInterviews ] = useState({})
    const [ preferredTimes, setPreferredTimes ] = useState({})
    const location = useLocation();
    const { storyteller } = location.state;

    useEffect(() => {
        (async () => {
            try {
                // Fetch the accessToken
                const token = await getAccessTokenSilently();
                setAccessToken(token);

                // Fetch StorytellerDetails
                const detailsData = await fetch(
                    `/api/v1/storytellers/${storyteller.id}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );
                const details = await detailsData.json();
                setStorytellerDetails(details);
                setPreferredTimes(details.preferredTimes)

                // Fetch scheduledInterviews
                const interviewsData = await fetch(
                    `/api/v1/interviews/storytellers/${storyteller.id}/scheduled:all`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );
                const interviews = await interviewsData.json();
                setScheduledInterviews(interviews);

                setLoading(false);
            } catch (error) {
                setError(error);
                setLoading(false);
            }
        })();
    }, [getAccessTokenSilently, storyteller.id]);

    //TODO: instead of handling the Submit, handle the new scheduled interviews
    // Function to update the upcoming interviews data when the form is submitted
    const handleInterviewSubmit = async (formData) => {

        console.log('Scheduling Interview');
        console.log(formData.datePicker);

        const formattedDate = formData.usePreferredTime ? undefined : new Date(formData.datePicker).toISOString();

        const qParams = {
            append: formData.usePreferredTime,
            questionId: formData.question,
            name: formData.interviewName,
        };

        if (!formData.usePreferredTime && formattedDate) {
            qParams.time = formattedDate;
        }

        const queryParams = new URLSearchParams(qParams).toString();

        fetch(`/api/v1/interviews/scheduled/${storyteller.id}?${queryParams}`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        }).then(response => {
            try {
                // Make sure to use the updated accessToken state
               fetch(
                    `/api/v1/interviews/storytellers/${storyteller.id}/scheduled:all`,
                    {
                        headers: {
                            Authorization: `Bearer ${accessToken}`,
                        },
                    }
                ).then((updatedInterviewsData) =>
                updatedInterviewsData.json().then((updatedInterviews) =>
                setScheduledInterviews(updatedInterviews)));
            } catch (error) {
                // Handle any error during the second fetch (optional)
            }
        })


    };

    const handleNewPreferredTimes = (preferredTimes) => {
        setPreferredTimes(preferredTimes)
    }

    if (loading) {
        return <Loading />;
    }

    if (error) {
        return <Error message={error.message} />;
    }

    return (
        <div id="interview-view">
            <UpcomingInterviewTable upcomingInterviews={scheduledInterviews}/>
            <ScheduleInterviewForm onSubmit={handleInterviewSubmit}/>
            <PreferredTimeTable preferredTimes={preferredTimes} preferredTimeHandler={handleNewPreferredTimes}/>
            <PreferredTimeForm storyteller={storyteller}/>
        </div>
    )
}

export default StorytellerDetails