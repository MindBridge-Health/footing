import React from "react";

export function UpcomingInterviewTable({ upcomingInterviews }) {
    return (
        <>
            <h1>Upcoming Interviews</h1>
            <table id="upcomingInterviews">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Scheduled Time</th>
                </tr>
                </thead>
                <tbody>
                {upcomingInterviews.map((interview, index) => (
                    <tr key={index}>
                        <td>{interview.name}</td>
                        <td>{interview.scheduledTime}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </>
    );
}
