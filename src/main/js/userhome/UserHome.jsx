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

                // Fetch StorytellerDetails
                const detailsData = await fetch(
                    `/api/v1/storytellers/`,
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

    const scrollToSection = (sectionId) => {
        document.getElementById(sectionId).scrollIntoView({ behavior: 'smooth' });
    }

    if (loading) {
        return <Loading />;
    }

    if (error) {
        return <Error message={error.message} />;
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
                                <h2>Details</h2>
                                <StorytellerDetailsCard storytellerDetails={storytellerDetails} />
                            </div>
                            <div id="interviews-section" className="detail-card">
                                <h2>Interviews</h2>
                                <InterviewDetailsCard storytellerDetails={storytellerDetails} />
                            </div>
                            <div id="images-section" className="detail-card">
                                <h2>Images</h2>
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