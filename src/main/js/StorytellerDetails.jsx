import React, {useEffect, useState} from 'react';
import './main.css'
import {Loading} from "./Loading";
import {Error} from "./Error";
import {useLocation, useNavigate} from "react-router-dom";
import {PreferredTime} from "./PreferredTime";
import {useAuth0} from "@auth0/auth0-react";
import {Interviews} from "./Interviews";
import {StorytellerDetailsTable} from "./StorytellerDetailsTable";
import ImageUpload from "./ImageUpload";

const StorytellerDetails = () => {
    const {  getAccessTokenSilently } = useAuth0();
    const [ accessToken, setAccessToken ] = useState({})
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [ storytellerDetails, setStorytellerDetails ] = useState({})
    const [ sid, setSid] = useState(null)
    const location = useLocation();
    const nav = useNavigate()
    const queryParams = new URLSearchParams(location.search);
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

                setLoading(false);
            } catch (error) {
                setError(error);
                setLoading(false);
            }
        })();
    }, [getAccessTokenSilently]);

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
                    throw new Error('Error active status');
                }
            })
        } )
    };

    // Function to handle toggling the active status
    const handleToggleActiveStatus = () => {
        storytellerDetails.isActive = !storytellerDetails.isActive
        setStorytellerDetails(storytellerDetails)
        updateStoryteller(storytellerDetails)
    };

    if (loading) {
        return <Loading />;
    }

    if (error) {
        return <Error message={error.message} />;
    }

    return (
        <div id="storyteller-details">
            <div>
                <h3>Status: {storytellerDetails.isActive ? 'Active' : 'Inactive'}</h3>
                <button onClick={handleToggleActiveStatus}>
                    {storytellerDetails.isActive ? 'Deactivate' : 'Activate'}
                </button>
            </div>
            <StorytellerDetailsTable storytellerDetails={storytellerDetails}/>
            <Interviews storytellerDetails={storytellerDetails}/>
            <PreferredTime storytellerDetails={storytellerDetails}/>
            <h1>Image Upload</h1>
            <ImageUpload storytellerId={storytellerDetails.id} />
        </div>
    )
}

export default StorytellerDetails