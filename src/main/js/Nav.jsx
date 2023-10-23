import React, {useEffect, useState} from 'react';
import { useLocation, Link } from 'react-router-dom';
import { useAuth0 } from '@auth0/auth0-react';
import {Loading} from "./Loading";
import {Error} from "./Error";

export function Nav() {
    const { isAuthenticated, user, loginWithRedirect, logout, getAccessTokenSilently, getIdTokenClaims } = useAuth0();
    const { pathname } = useLocation();

    // Extract roles from the user's profile
    const [roles, setRoles] = useState([]);

    useEffect(() => {
        (async () => {
            const claims = await getIdTokenClaims();
            if(claims) {
                setRoles(claims['https://app.footing.com/roles'])
            }
        })();
    }, [])



    return (
        <nav className="header">
                <div className="header-nav">
                    <span className="company-logo"></span>
                    <Link
                        to="/"
                        className={`nav-item nav-link${pathname === '/' ? ' active' : ''}`}
                    >
                        Home
                    </Link>

                    {/* Conditionally render the Admin link based on the user's role */}
                    {roles && roles.includes('Admin') && (
                        <Link
                            to="/admin"
                            className={`nav-item nav-link${pathname === '/admin' ? ' active' : ''}`}
                        >
                            Admin
                        </Link>
                    )}
                </div>

            <div className="header-right">
            {isAuthenticated ? (
                <div>
                    <span id="hello">Hello, {user?.given_name ? user?.given_name : user.name}</span>{' '}
                    <button
                        className="btn logout-button"
                        id="logout"
                        onClick={() => logout({ logoutParams:{ returnTo: window.location.origin + "/console/index.html" }})}
                    >
                        logout
                    </button>
                </div>
            ) : (
                <button
                    className="btn btn-outline-success login-button"
                    id="login"
                    onClick={() => loginWithRedirect()}
                >
                    login
                </button>
            )}
            </div>
        </nav>
    );
}

