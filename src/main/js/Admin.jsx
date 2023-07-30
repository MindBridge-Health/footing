import React, {useEffect, useState} from 'react';
import StorytellerList from "./StorytellerList";
import {Error} from "./Error";
import {Loading} from "./Loading";
import {useAccessTokenContext} from "./AccessTokenContext";

const AdminPanel = () => {
    const { getAccessToken } = useAccessTokenContext();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [storytellers, setStorytellers] = useState([]);

    useEffect(() => {
        (async () => {
            try {
                const accessToken = await getAccessToken();

                const res = await fetch('/api/v1/storytellers/', {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                });

                if (!res.ok) {
                    throw new Error('Failed to fetch storytellers.');
                }

                const data = await res.json();
                setStorytellers(data);
                setLoading(false);
            } catch (error) {
                setError(error);
                setLoading(false);
            }
        })();
    }, [getAccessToken]);

    if (loading) {
        return <Loading />;
    }

    if (error) {
        return <Error message={error.message} />;
    }

    return (
        <div>
            <StorytellerList storytellers={storytellers} />
        </div>
    );
};

export default AdminPanel;
