import React, {useEffect, useState} from 'react';
import StorytellerList from "./StorytellerList";
import {Error} from "../Error";
import {Loading} from "../Loading";
import {useAccessTokenContext} from "../AccessTokenContext";
import {useAuth0} from "@auth0/auth0-react";
import {AddStorytellerPanel} from "./AddStorytellerPanel";

const AdminPanel = () => {
    const { getAccessTokenSilently} = useAuth0();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [storytellers, setStorytellers] = useState([]);

    useEffect(() => {
        (async () => {
            try {
                const accessToken = await getAccessTokenSilently();

                const res = await fetch('/api/v1/storytellers/all/', {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                });

                if (!res.ok) {
                    throw new Error('Failed to fetch storytellers.');
                }

                const data = await res.json();
                console.log(data)
                setStorytellers(data);
                setLoading(false);
            } catch (error) {
                setError(error);
                setLoading(false);
            }
        })();
    }, []);

    const addNewStoryteller = async (storytellerId) => {
        const accessToken = await getAccessTokenSilently();

        const res = await fetch(`/api/v1/storytellers/${storytellerId}`, {
            method: 'GET',
            headers: {
                Authorization: `Bearer ${accessToken}`,
            },
        });

        if (!res.ok) {
            throw new Error('Failed to fetch storytellers.');
        }

        const data = await res.json();

        setStorytellers([...storytellers, data])
    }

    const handleStorytellerDelete = async (storytellerId) => {
        setStorytellers(storytellers.filter(s => s.id !== storytellerId))
    }

    if (loading) {
        return <Loading />;
    }

    if (error) {
        return <Error message={error.message} />;
    }

    return (
        <div className="flex-container">
            <div className="content">
                <StorytellerList storytellers={storytellers} handleStorytellerDelete={handleStorytellerDelete}/>
                <AddStorytellerPanel handleNewStoryteller={addNewStoryteller}/>
            </div>
        </div>
    );
};

export default AdminPanel;
