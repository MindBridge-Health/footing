import React, {useState} from 'react';
import {useAuth0} from '@auth0/auth0-react';

export const StorytellerDetailsTable = ({storytellerDetails}) => {
    const { getAccessTokenSilently } = useAuth0();
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

            fetch(`/api/v1/storytellers/${localStorytellerDetails.id}`, {
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

    return (
        <>
            <h3>Details</h3>
            <table>
                <thead>
                <tr>
                    <th>Firstname</th>
                    <th>Lastname</th>
                    <th>Email</th>
                    <th>Mobile</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>
                        {isEditing ? (
                            <input
                                type="text"
                                value={editedValues.firstname || localStorytellerDetails.firstname}
                                onChange={(e) => handleInputChange('firstname', e.target.value)}
                            />
                        ) : (
                            localStorytellerDetails.firstname
                        )}
                    </td>
                    <td>
                        {isEditing ? (
                            <input
                                type="text"
                                value={editedValues.lastname || localStorytellerDetails.lastname}
                                onChange={(e) => handleInputChange('lastname', e.target.value)}
                            />
                        ) : (
                            localStorytellerDetails.lastname
                        )}
                    </td>
                    <td>
                        {isEditing ? (
                            <input
                                type="text"
                                value={editedValues.email || localStorytellerDetails.email}
                                onChange={(e) => handleInputChange('email', e.target.value)}
                            />
                        ) : (
                            localStorytellerDetails.email
                        )}
                    </td>
                    <td>
                        {isEditing ? (
                            <input
                                type="text"
                                value={editedValues.mobile || localStorytellerDetails.mobile}
                                onChange={(e) => handleInputChange('mobile', e.target.value)}
                            />
                        ) : (
                            localStorytellerDetails.mobile
                        )}
                    </td>
                </tr>
                </tbody>
            </table>
            {isEditing ? (
                <>
                    <button onClick={handleSaveAll}>Save All</button>
                    <button onClick={handleCancel}>Cancel</button>
                </>
            ) : (
                <button onClick={handleEdit}>Edit</button>
            )}
        </>
    );
};


