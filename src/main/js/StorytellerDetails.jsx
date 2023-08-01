import React, {useEffect, useState} from 'react';
import './main.css'
import {Loading} from "./Loading";
import {Error} from "./Error";
import {useLocation, useNavigate} from "react-router-dom";
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
    const nav = useNavigate()
    const queryParams = new URLSearchParams(location.search);
    const [ sid, setSid] = useState(null)
    let storyteller = null

    if(location.state != null) {
         storyteller  = location.state.storyteller;
    }

    useEffect(() => {
        (async () => {
            try {
                // Fetch the accessToken
                const token = await getAccessTokenSilently();
                setAccessToken(token);
                let localSid = null;

                const sidQp = queryParams.get('sid');
                if(storyteller) {
                    nav({
                        search: `?sid=${storyteller.id}`
                    })
                    setSid(storyteller.id)
                    localSid = storyteller.id
                } else if(sidQp) {
                    setSid(sidQp)
                    localSid = sidQp
                }
                // Fetch StorytellerDetails
                console.log("fetching with sid " + localSid)
                const detailsData = await fetch(
                    `/api/v1/storytellers/${localSid}`,
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
                    `/api/v1/interviews/storytellers/${localSid}/scheduled:all`,
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
    }, [getAccessTokenSilently]);

    // Function to delete scheduled interviews
    const handleDeleteInterview = async (index) => {
        const interviewId = scheduledInterviews[index].id
        console.log("deleting interview " + interviewId)

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
    }

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

        fetch(`/api/v1/interviews/scheduled/${sid}?${queryParams}`, {
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
                    `/api/v1/interviews/storytellers/${sid}/scheduled:all`,
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

    const updateStoryteller = (storyteller) => {
        getAccessTokenSilently().then((accessToken) => {
            fetch(`/api/v1/storytellers/${sid}`, {
                method: 'PUT',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${accessToken}`
                },
                body: JSON.stringify(storyteller)
            }).then((resp) => {
                if (!resp.ok) {
                    throw new Error('Error updating preferred times.');
                }
                // If the response is successful, call the preferredTimeHandler
                return resp.json();
            }).then((data) => {
                setPreferredTimes(data.preferredTimes)
            })
        } )
    };

    if (loading) {
        return <Loading />;
    }

    if (error) {
        return <Error message={error.message} />;
    }

    return (
        <div id="interview-view">
            <UpcomingInterviewTable upcomingInterviews={scheduledInterviews} handleDeleteInterview={handleDeleteInterview}/>
            <ScheduleInterviewForm onSubmit={handleInterviewSubmit}/>
            <PreferredTimeTable storyteller={storytellerDetails} updateStorytellerHandler={updateStoryteller}/>
            <PreferredTimeForm storyteller={storytellerDetails} updateStorytellerHandler={updateStoryteller}/>
        </div>
    )
}

export default StorytellerDetails