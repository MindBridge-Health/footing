import React, {useEffect, useState} from 'react';
import {useAuth0} from "@auth0/auth0-react";
import {Loading} from "../Loading";
import {Error} from "../Error";
import Uploads from "./Uploads"

const StoryView = (storyId) => {
    const { getAccessTokenSilently } = useAuth0();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [story, setStory] = useState(null)


    useEffect(() => {
        (async () => {
            try {
                // Fetch the accessToken
                const token = await getAccessTokenSilently();

                // Fetch StorytellerDetails
                const storyData = await fetch(
                    `/api/v1/stories/${storyId}`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );
                setStory(storyData);

                setLoading(false);
            } catch (error) {
                setError(error);
                setLoading(false);
            }
        })();
    }, [getAccessTokenSilently]);


    if (loading) {
        return <Loading />;
    }

    if (error) {
        return <Error message={error.message} />;
    }
}

export default StoryView