import React from 'react';
import Storyteller from "./Storyteller";
const StorytellerList  = ({ storytellers, handleStorytellerDelete }) => {
        const storytellerDivs = storytellers.map(storyteller =>
        <Storyteller key={storyteller.id} storyteller={storyteller} handleStorytellerDelete={handleStorytellerDelete} />)

        return (
            <table>
                <tbody>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Is Active</th>
                    <th>Edit</th>
                    <th>Hard Delete</th>
                </tr>
                {storytellerDivs}
                </tbody>
            </table>
        )
}

export default StorytellerList