import React, {useEffect, useState} from "react";
import {UpcomingInterviewTable} from "./UpcomingInterviewTable";
import {ScheduleInterviewForm} from "./ScheduleInterviewForm";
import {PreviousInterviewsTable} from "./PreviousInterviewsTable";
import {useAuth0} from "@auth0/auth0-react";

export const InterviewDetailsCard = ({storytellerDetails}) => {
    const {  getAccessTokenSilently } = useAuth0();
    const [ scheduledInterviews, setScheduledInterviews ] = useState([])
    const [accessToken, setAccessToken] = useState("")

    useEffect(() => {
        (async () => {setAccessToken(await getAccessTokenSilently())})();
    }, [getAccessTokenSilently]);

    useEffect(() => {
        if (accessToken) {
            fetch(
                `/api/v1/interviews/storytellers/scheduled/all`,
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                }
            )
                .then(interviewsData => interviewsData.json())
                .then(interviews => setScheduledInterviews(interviews))
                .catch(error => {
                    // Handle fetch error
                });
        }
    }, [accessToken]);


    // Function to update the upcoming interviews data when the form is submitted
    const handleInterviewSubmit = async (formData) => {

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

        fetch(`/api/v1/interviews/storytellers/scheduled/?${queryParams}`, {
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
                    `/api/v1/interviews/storytellers/scheduled/all`,
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

    // Function to delete scheduled interviews
    const handleDeleteInterview = async (index) => {
        const interviewId = scheduledInterviews[index].id

        fetch(`/api/v1/interviews/scheduled/${interviewId}`, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        }).then(response => {
            setScheduledInterviews(scheduledInterviews.filter((_, i) => i !== index))
        })
    };

    return (
        <div id="interview-view">
            <UpcomingInterviewTable upcomingInterviews={scheduledInterviews} handleDeleteInterview={handleDeleteInterview}/>
            <ScheduleInterviewForm onSubmit={handleInterviewSubmit}/>
            <PreviousInterviewsTable storyteller={storytellerDetails}></PreviousInterviewsTable>
        </div>
    )
}