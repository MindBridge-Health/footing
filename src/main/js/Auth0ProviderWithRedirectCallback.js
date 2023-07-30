import React, { useEffect } from 'react';
import { Auth0Provider } from '@auth0/auth0-react';
import { useLocation, useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import {Auth0Client} from "@auth0/auth0-spa-js";
import AccessTokenContext from './AccessTokenContext'

const Auth0ProviderWithRedirectCallback = ({
                                               children,
                                               domain,
                                               clientId,
                                               authorizationParams,
                                               ...props
                                           }) => {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const handleRedirectCallback = async () => {
            if (location.pathname === '/auth-callback') {
                await auth0Client.handleRedirectCallback();
                navigate((location.state && location.state.returnTo) || '/');
            }
        };

        handleRedirectCallback();
    }, [location.pathname, navigate]);

    const auth0Client = new Auth0Client({
        domain,
        clientId,
        ...authorizationParams,
    });

    const getAccessToken = async () => {
        let accessToken = Cookies.get('accessToken');

        if (!accessToken) {
            const tokenResponse = await auth0Client.getTokenSilently();
            accessToken = tokenResponse.accessToken;

            // Set the access token in the cookie with a secure flag and HttpOnly
            const expiresInSeconds = tokenResponse.expiresIn;
            setAccessTokenCookie(accessToken, expiresInSeconds);
        }

        return accessToken;
    };

    const setAccessTokenCookie = (accessToken, expiresInSeconds) => {
        const expirationDate = new Date();
        expirationDate.setTime(expirationDate.getTime() + expiresInSeconds * 1000);

        const cookieOptions = {
            expires: expirationDate,
            path: '/',
            secure: true, // Enable this if your app is served over HTTPS
            httpOnly: true,
        };
        Cookies.set('accessToken', accessToken, cookieOptions);
    };

    return (
        <Auth0Provider
            {...props}
            domain={domain}
            clientId={clientId}
            redirectUri={window.location.origin}
            onRedirectCallback={(appState) => {
                navigate((appState && appState.returnTo) || '/');
            }}
        >
            <AccessTokenContext.Provider value={{ getAccessToken }}>
                {children}
            </AccessTokenContext.Provider>
        </Auth0Provider>
    );
};

export default Auth0ProviderWithRedirectCallback;
