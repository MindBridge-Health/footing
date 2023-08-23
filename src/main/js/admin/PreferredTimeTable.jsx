import React from "react";
import {useAuth0} from "@auth0/auth0-react";

export function PreferredTimeTable({storyteller, updateStorytellerHandler}) {
    const {  getAccessTokenSilently } = useAuth0();

    const handleDeletePreferredTime = (index) => {
        storyteller.preferredTimes = storyteller.preferredTimes.filter((_, i) => i !== index)
        updateStorytellerHandler(storyteller);
    };


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
            {storyteller.preferredTimes.map((item, index) => (
                <tr key={index}>
                <td>{item.dayOfWeek}</td>
                <td>{item.time}</td>
                    <td>
                        <button onClick={() => handleDeletePreferredTime(index)} className="delete-button"></button>
                    </td>
            </tr>
            ))}
            </tbody>
        </table>
    </>;
}