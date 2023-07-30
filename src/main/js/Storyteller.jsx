import React from 'react';
import {Link, useLocation} from "react-router-dom";

const Storyteller = ({storyteller}) => {
    const { pathname } = useLocation();

        return (
            <tr>
                <td>{storyteller.firstname}</td>
                <td>{storyteller.lastname}</td>
                <td>                    <Link
                    to={"/storytellerDetails"}
                    state={{ storyteller}}
                    className={`nav-item nav-link${
                        pathname === '/storytellerDetails' ? ' active' : ''
                    }`}
                >
                    Details
                </Link></td>
            </tr>
        )
}

export default Storyteller