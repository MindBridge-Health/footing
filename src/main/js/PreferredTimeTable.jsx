import React from "react";

export function PreferredTimeTable({preferredTimes}) {
    console.log(preferredTimes)
    return <>
        <h1>Preferred Times</h1>
        <table id="preferredTimes">
            <thead>
            <tr>
                <th>Day of Week</th>
                <th>Time</th>
            </tr>
            </thead>
            <tbody>
            {preferredTimes.map((item, index) => (
                <tr key={index}>
                <td>{item.dayOfWeek}</td>
                <td>{item.time}</td>
            </tr>
            ))}
            </tbody>
        </table>
    </>;
}