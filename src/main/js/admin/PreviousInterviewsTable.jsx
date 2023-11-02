import React, {useEffect, useState} from "react";
import {useAuth0} from "@auth0/auth0-react";
import {Link} from "react-router-dom";

export function PreviousInterviewsTable({storyteller}) {
    const {  getAccessTokenSilently } = useAuth0();
    const [interviewsData, setInterviewsData] = useState([])
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        (async () => {
            try {
                const token = await getAccessTokenSilently();

                const interviewResponse = await fetch(
                    `/api/v1/interviews/storytellers/${storyteller.id}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );

                const interviewData = await interviewResponse.json();
                setInterviewsData(interviewData);
                setLoading(false);
            } catch (error) {
                setError(error);
                setLoading(false);
            }
        })();
    },[getAccessTokenSilently, storyteller.id]);

    if (loading) {
        return <p>Loading...</p>;
    }

    if (error) {
        return <p>Error: {error.message}</p>;
    }

    return (
        <>
            <h1>Previous Interviews</h1>
            <table id="upcomingInterviews">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Completed</th>
                    <th>Time Completed</th>
                </tr>
                </thead>
                <tbody>
                {interviewsData.map((interview, index) => (
                    <tr key={index}>
                        <td>{interview.name}</td>
                        <td>
                            {interview.completed
                                ? interview.interviewQuestions[0] && interview.interviewQuestions[0].response
                                    ? <Link to={`/story?storyId=${interview.interviewQuestions[0].response.id}`}>true</Link>
                                    : "true"
                                : "false"
                            }
                        </td>
                        <td>{interview.timeCompleted}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </>
    )
}




