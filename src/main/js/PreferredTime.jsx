import React, {useState} from 'react';
import {PreferredTimeForm} from "./PreferredTimeForm";
import {Error} from "./Error";
import {useAuth0} from "@auth0/auth0-react";
import {PreferredTimeTable} from "./PreferredTimeTable";

export const PreferredTime = ({storytellerDetails}) =>{
    const {  getAccessTokenSilently } = useAuth0();
    const [ preferredTimes, setPreferredTimes ] = useState({})

    const updateStoryteller = (storyteller) => {
        getAccessTokenSilently().then((accessToken) => {
            fetch(`/api/v1/storytellers/${storytellerDetails.id}`, {
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

    return (
        <>
            <PreferredTimeTable storyteller={storytellerDetails} updateStorytellerHandler={updateStoryteller}/>
            <PreferredTimeForm storyteller={storytellerDetails} updateStorytellerHandler={updateStoryteller}/>
        </>
    )
}