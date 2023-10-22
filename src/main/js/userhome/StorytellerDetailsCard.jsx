import React, {useEffect, useState} from 'react';
import '../main.css'
import {Loading} from "../Loading";
import {Error} from "../Error";
import {useAuth0} from "@auth0/auth0-react";

const StorytellerDetailsCard = ({storytellerDetails}) => {
    const { getAccessTokenSilently, user } = useAuth0();
    const [editedValues, setEditedValues] = useState({});
    const [isEditing, setIsEditing] = useState(false);
    const [localStorytellerDetails, setLocalStorytellerDetails] = useState(storytellerDetails)


    const handleEdit = () => {
        setEditedValues({
            firstname: localStorytellerDetails.firstname,
            lastname: localStorytellerDetails.lastname,
            email: localStorytellerDetails.email,
            mobile: localStorytellerDetails.mobile,
        });
        setIsEditing(true);
    };

    const handleCancel = () => {
        setEditedValues({});
        setIsEditing(false);
    };

    const handleInputChange = (field, value) => {
        setEditedValues((prevEditedValues) => ({
            ...prevEditedValues,
            [field]: value,
        }));
    };

    const handleSaveAll = () => {
        const updatedStorytellerDetails = {
            ...localStorytellerDetails,
            ...editedValues,
        };

        getAccessTokenSilently().then((token) => {
            const updatedFields = Object.keys(editedValues);

            const updates = updatedFields.map((field) => ({
                [field]: editedValues[field],
            }));

            fetch(`/api/v1/storytellers/`, {
                method: 'PUT',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(updatedStorytellerDetails),
            })
                .then((response) => {
                    if (!response.ok) {
                        throw new Error('Error updating values.');
                    }
                    // Update the localStorytellerDetails with the edited values
                    setLocalStorytellerDetails(updatedStorytellerDetails);
                    // Clear the edited values
                    setEditedValues({});
                    setIsEditing(false);
                })
                .catch((error) => {
                    console.error(error);
                });
        });
    };

    return (
        <div className="container">
            <div>
            <div className="detail-group">
                <label>Firstname:</label>
                {isEditing ? (
                    <input
                        type="text"
                        value={editedValues.firstname || localStorytellerDetails.firstname}
                        onChange={(e) => handleInputChange('firstname', e.target.value)}
                    />
                ) : (
                    <span>{localStorytellerDetails.firstname}</span>
                )}
            </div>

            <div className="detail-group">
                <label>Lastname:</label>
                {isEditing ? (
                    <input
                        type="text"
                        value={editedValues.lastname || localStorytellerDetails.lastname}
                        onChange={(e) => handleInputChange('lastname', e.target.value)}
                    />
                ) : (
                    <span>{localStorytellerDetails.lastname}</span>
                )}
            </div>

            <div className="detail-group">
                <label>Email:</label>
                {isEditing ? (
                    <input
                        type="text"
                        value={editedValues.email || localStorytellerDetails.email}
                        onChange={(e) => handleInputChange('email', e.target.value)}
                    />
                ) : (
                    <span>{localStorytellerDetails.email}</span>
                )}
            </div>

            <div className="detail-group">
                <label>Mobile:</label>
                {isEditing ? (
                    <input
                        type="text"
                        value={editedValues.mobile || localStorytellerDetails.mobile}
                        onChange={(e) => handleInputChange('mobile', e.target.value)}
                    />
                ) : (
                    <span>{localStorytellerDetails.mobile}</span>
                )}
            </div>

            <div className="detail-actions">
                {isEditing ? (
                    <>
                        <button onClick={handleSaveAll}>Save All</button>
                        <button onClick={handleCancel}>Cancel</button>
                    </>
                ) : (
                    <button onClick={handleEdit}>Edit</button>
                )}
            </div>
            </div>
            <div className="profile-image">
                <img alt="profile picture" src={user.picture}/>
            </div>
        </div>
    );

}

export default StorytellerDetailsCard
