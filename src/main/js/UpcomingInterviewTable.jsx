import React from "react";

export function UpcomingInterviewTable({ upcomingInterviews, handleDeleteInterview }) {

    return (
        <>
            <h1>Upcoming Interviews</h1>
            <table id="upcomingInterviews">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Scheduled Time</th>
                    <th>Question</th>
                </tr>
                </thead>
                <tbody>
                {upcomingInterviews.map((interview, index) => (
                    <tr key={index}>
                        <td>{interview.name}</td>
                        <td>{interview.scheduledTime}</td>
                        <td>{interview.question.text}</td>
                        <td>
                            <button onClick={() => handleDeleteInterview(index)} className="delete-button"></button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </>
    );
}
