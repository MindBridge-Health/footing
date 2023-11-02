import React from "react";
import { Link, useLocation } from "react-router-dom";
import {useAuth0} from "@auth0/auth0-react";

const Storyteller = ({ storyteller, handleStorytellerDelete }) => {
    const { getAccessTokenSilently} = useAuth0();
    const { pathname } = useLocation();

    const handleDelete = () => {
        const shouldDelete = window.confirm(
            "This will remove all records of the user from the database. Are you sure?"
        );

        if (shouldDelete) {
            const params = new URLSearchParams({force: true});
            getAccessTokenSilently().then(accessToken => {
                fetch(`/api/v1/storytellers/${storyteller.id}?${params}`, {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${accessToken}`,
                    },
                })
                    .then((response) => {
                        if (response.ok) {
                            handleStorytellerDelete(storyteller.id)
                            // Handle successful deletion, e.g., show a success message or refresh the list
                            console.log("Storyteller deleted successfully!");
                        } else {
                            // Handle error in deletion
                            console.error("Failed to delete storyteller.");
                        }
                    })
                    .catch((error) => {
                        console.error("Error deleting storyteller:", error);
                    });
            })
        }
    };

    return (
        <tr>
            <td>{storyteller.firstname}</td>
            <td>{storyteller.lastname}</td>
            <td>{storyteller.isActive ? "Yes" : "No"}</td>
            <td>
                <Link
                    to={"/storytellerDetails"}
                    state={{ storyteller }}
                    className={`body-item ${
                        pathname === "/storytellerDetails" ? " active" : ""
                    }`}
                >
                    Details
                </Link>
            </td>
            <td>
                <button onClick={handleDelete} className="delete-button"></button>
            </td>
        </tr>
    );
};

export default Storyteller;
