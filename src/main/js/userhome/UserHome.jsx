import React, {useEffect, useState} from 'react';
import {useAuth0} from "@auth0/auth0-react";
import {Loading} from "../Loading";
import {Error} from "../Error";
import StorytellerDetailsCard from "./StorytellerDetailsCard";
import {InterviewDetailsCard} from "./InterviewDetailsCard";
import Uploads from "./Uploads"

const UserHome = () => {
    const { isAuthenticated, user, loginWithRedirect, logout, getAccessTokenSilently, getIdTokenClaims } = useAuth0();
    const [isNavExpanded, setIsNavExpanded] = useState(true);
    const [tab, setTab] = useState('details'); // Default to 'details' tab
    const [ storytellerDetails, setStorytellerDetails ] = useState({})
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        (async () => {
            try {
                // Fetch the accessToken
                const token = await getAccessTokenSilently();

                // Attempt to fetch StorytellerDetails
                const detailsData = await fetch(
                    `/api/v1/storytellers/`,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );

                // If the user doesn't exist (assuming a 404 for not found), create the user
                if (detailsData.status !== 200) {
                    const createUserResponse = await fetch(`/api/v1/storytellers/`, {
                        method: 'POST',
                        headers: {
                            Authorization: `Bearer ${token}`,
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({
                            lastname: user.family_name,
                            firstname: user.given_name,
                            email: user.email,
                        })
                    });

                    // Check if user creation was successful
                    if (!createUserResponse.ok) {
                        throw new Error("Failed to create user");
                    }

                    const details = await createUserResponse.json();
                    setStorytellerDetails(details);
                } else {
                    const details = await detailsData.json();
                    setStorytellerDetails(details);
                }

                setLoading(false);
            } catch (error) {
                setError(error);
                setLoading(false);
            }
        })();
    }, []);



    const scrollToSection = (sectionId) => {
        document.getElementById(sectionId).scrollIntoView({ behavior: 'smooth' });
    }

    if (loading) {
        return <Loading />;
    }

    if (error) {
        return (
            <div className="flex-container">
                <div className="content" >
                    <Error message={error.message} />
                </div>
            </div>
        );
    }


        return (
            <div className="flex-container">
                {isAuthenticated ? (
                    <div>
                        <div className={`left-navbar ${isNavExpanded ? 'expanded' : 'collapsed'}`}>
                            <div className={`collapse-button ${isNavExpanded ? 'expanded' : 'collapsed'}`}
                                 onClick={() => setIsNavExpanded(!isNavExpanded)}>
                                {isNavExpanded ? '«' : '»'}
                            </div>
                            {isNavExpanded && (
                                <div >
                                    <button className="left-nav-section" onClick={() => scrollToSection('details-section')}>Details</button>
                                    <button className="left-nav-section" onClick={() => scrollToSection('interviews-section')}>Interviews</button>
                                    <button className="left-nav-section" onClick={() => scrollToSection('images-section')}>Images</button>
                                </div>
                            )}
                        </div>
                        <div className={`content ${isNavExpanded ? 'expanded' : 'collapsed'}`}>
                            <div id="details-section" className="detail-card">
                                <StorytellerDetailsCard storytellerDetails={storytellerDetails} />
                            </div>
                            <div id="interviews-section" className="detail-card">
                                <InterviewDetailsCard storytellerDetails={storytellerDetails} />
                            </div>
                            <div id="images-section" className="detail-card">
                                <Uploads storytellerId={storytellerDetails.id}></Uploads>
                            </div>
                        </div>
                    </div>
            ) : (
                <div>
                    Please <a href='#' onClick={() => loginWithRedirect()}>login</a>  above
                </div>
            )}
        </div>
        );
}

export default UserHome