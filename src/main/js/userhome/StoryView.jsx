import React, {useEffect, useState} from 'react';
import {useAuth0} from "@auth0/auth0-react";
import {Loading} from "../Loading";
import {Error} from "../Error";
import Uploads from "./Uploads"
import {useSearchParams} from "react-router-dom";

const StoryView = () => {
    const { getAccessTokenSilently } = useAuth0();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [story, setStory] = useState(null);
    const [searchParams, setSearchParams] = useSearchParams();
    const [storytellerId, setStorytellerId] = useState(null)

    const storyId = searchParams.get('storyId');

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

                const storyMap = await storyData.json()
                setStory(storyMap);

                setLoading(false);
            } catch (error) {
                setError(error);
                setLoading(false);
            }
        })();
    }, [getAccessTokenSilently, storyId]);


    if (loading) {
        return <Loading />;
    }

    if (error) {
        console.log(error);
        return <Error message={error.message} />;
    }

    return (
        <div className="flex-container">
            <div className="content">
                <div className="detail-group">
                    <label>Story:</label>
                        <span>{story.story}</span>
                </div>

                <div className="detail-group">
                    <label>Original Transcription:</label>
                    <span>{story.originalText}</span>
                </div>
                <Uploads storyId={storyId} />
            </div>
        </div>
    );
}

export default StoryView