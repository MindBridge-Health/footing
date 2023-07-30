import React, {useEffect, useState} from "react";
import {useAccessTokenContext} from "./AccessTokenContext";
import {Error} from "./Error";
import {Loading} from "./Loading";

export function PreferredTimeForm({storyteller, preferredTimeHandler}) {
    const { getAccessToken } = useAccessTokenContext();
    const [isCollapsed, setIsCollapsed] = useState(true);
    const [formData, setFormData] = useState( {
        datePicker: ''
    })

    // Helper function to format the time as "HH:mm:ss"
    function formatTime(date) {
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        return hours + ':' + minutes + ':' + seconds;
    }

// Helper function to get the day of the week as a string
    function getDayOfWeek(date) {
        const options = {weekday: 'long'};
        return date.toLocaleDateString(undefined, options).toUpperCase();
    }

    const handleSubmit = (event) => {
        event.preventDefault();

        const datetimeValue = formData.datePicker
        // Parse the datetime value using the Date object
        const parsedDate = new Date(datetimeValue);
        // Format the parsed date in the desired format
        const time = formatTime(parsedDate);
        const dayOfWeek = getDayOfWeek(parsedDate);
        const pTime = {
            "time": time,
            "dayOfWeek": dayOfWeek
        }

        if (!storyteller["preferredTimes"]) {
            storyteller["preferredTimes"] = [pTime];
        } else {
            storyteller["preferredTimes"].push(pTime)
        }

        fetch(url, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify(storyteller)
        }).then((resp) => {
                preferredTimeHandler(resp["preferredTimes"])
        })

        // Reset the form after submission
        setFormData({
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

    return <>
        <h2 className="collapsible-heading" onClick={handleToggleCollapsible}>Add preferred time</h2>
        {!isCollapsed && (
        <form id="preferred-time-form" name="preferred-time-form" onSubmit={handleSubmit}>
            <label htmlFor="datePicker">Date and Time of Interview</label>
            <input id="datePicker"
                   name="datePicker"
                   type="datetime-local"
                   value={formData.datePicker}
                   onChange={handleChange}
            />
            <br/>
            <button type="submit">Add</button>
        </form>
            )}
    </>;
}