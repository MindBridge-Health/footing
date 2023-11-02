import {useAuth0} from "@auth0/auth0-react";
import React, {useEffect, useState} from "react";

export function AddStorytellerPanel({handleNewStoryteller}) {
    const {  getAccessTokenSilently } = useAuth0();
    const [isCollapsed, setIsCollapsed] = useState(true);
    const [formData, setFormData] = useState({
        auth0Id: '',
        firstname: '',
        lastname: '',
        email: '',
        mobile: '',
        contactMethod: '',
        preferredTime: '',
    });

    const handleToggleCollapsible = () => {
        setIsCollapsed((prevIsCollapsed) => !prevIsCollapsed);
    };

    const handleChange = (event) => {
        const {name, value, type, checked} = event.target;
        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: type === 'checkbox' ? checked : value,
        }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        const formattedDate = new Date(formData.preferredTime);

        const date = new Date(formData.preferredTime);
        const time = `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`;
        const daysOfWeek = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
        const dayOfWeek = daysOfWeek[date.getDay()];

        const preferredTimeData = { time, dayOfWeek };

        const storyteller = {
            firstname: formData.firstname,
            lastname: formData.lastname,
            email: formData.email,
            mobile: formData.mobile,
            contactMethod: formData.contactMethod,
            preferredTimes: [preferredTimeData]
        }

        const qParams = {
            idEncoded: false
        }
        const queryParams = new URLSearchParams(qParams).toString();

        getAccessTokenSilently().then(accessToken => {
            fetch(`/api/v1/storytellers/${formData.auth0Id}?${queryParams}`, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${accessToken}`
                },
                body: JSON.stringify(storyteller)
            }).then(response => {if(response.ok) return response.text(); else throw("") })
                .then(storyteller => handleNewStoryteller(storyteller))
                .catch(error => document.getElementById('storytellerError').innerText = error)
        })

    };

    return (
        <>
            <div className="collapsible">
                <h2 className="collapsible-heading" onClick={handleToggleCollapsible}>Add Storyteller</h2>
                {isCollapsed && (<div>click to expand</div>)}
                {!isCollapsed && (
                    <div className="collapsible-content">
                        <div>click again to collapse</div>
                        <div id="storytellerError"></div>
                        <form id="add-storyteller-form" onSubmit={handleSubmit}>
                            <label htmlFor="Auth0 ID">Auth0 ID</label>
                            <input
                                type="text"
                                id="auth0Id"
                                name="auth0Id"
                                value={formData.auth0Id}
                                onChange={handleChange}
                            />
                            <br/>
                            <label htmlFor="firstname">Firstname</label>
                            <input
                                type="text"
                                id="firstname"
                                name="firstname"
                                value={formData.firstname}
                                onChange={handleChange}
                            />
                            <br/>
                            <label htmlFor="lastname">Lastname</label>
                            <input
                                type="text"
                                id="lastname"
                                name="lastname"
                                value={formData.lastname}
                                onChange={handleChange}
                            />
                            <br/>
                            <label htmlFor="email">Email address</label>
                            <input
                                type="text"
                                id="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                            />
                            <br/>
                            <label htmlFor="mobile">Mobile</label>
                            <input
                                type="text"
                                id="mobile"
                                name="mobile"
                                value={formData.mobile}
                                onChange={handleChange}
                            />
                            <br/>
                            <label htmlFor="contactMethod">Contact Method</label>
                            <input
                                type="text"
                                id="contactMethod"
                                name="contactMethod"
                                value={formData.contactMethod}
                                onChange={handleChange}
                            />
                            <br/>
                            <label htmlFor="preferredTimePicker">Preferred Day and Time for interviews</label>
                            <input
                                id="preferredTime"
                                name="preferredTime"
                                type="datetime-local"
                                value={formData.preferredTime}
                                onChange={handleChange}
                                required={false}
                            />
                            <br/>
                            <button type="submit">Add Storyteller</button>
                        </form>
                    </div>
                )}
            </div>
        </>
    )
}