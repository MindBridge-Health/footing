import React from 'react';
import Storyteller from "./Storyteller";
const StorytellerList  = ({ storytellers }) => {
        const storytellerDivs = storytellers.map(storyteller =>
        <Storyteller key={storyteller.id} storyteller={storyteller} />)

        return (
            <table>
                <tbody>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Edit</th>
                </tr>
                {storytellerDivs}
                </tbody>
            </table>
        )
}

export default StorytellerList