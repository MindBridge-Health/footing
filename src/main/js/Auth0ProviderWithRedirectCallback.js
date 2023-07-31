import React, { useEffect } from 'react';
import { Auth0Provider } from '@auth0/auth0-react';
import { useLocation, useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import {Auth0Client} from "@auth0/auth0-spa-js";
import AccessTokenContext from './AccessTokenContext'
import auth0 from "auth0-js";

const Auth0ProviderWithRedirectCallback = ({
                                               children,
                                               domain,
                                               clientId,
                                               authorizationParams,
                                               ...props
                                           }) => {
    const navigate = useNavigate();

    const auth0Client = new auth0.WebAuth({
        domain,
        clientID: clientId,
        redirectUri: authorizationParams.redirect_uri,
        audience: authorizationParams.audience,
        responseType: authorizationParams.responseType,
        scope: authorizationParams.scope,
    });

    useEffect(() => {
        const handleRedirectCallback = async () => {
            if (window.location.pathname === '/auth-callback') {
                auth0Client.parseHash(async (err, authResult) => {
                    console.log(authResult)
                    if (authResult && authResult.accessToken && authResult.idToken) {
                        // Store the access token in a cookie (secure and HttpOnly)
                        console.log(authResult.accessToken)
                        setAccessTokenCookie(authResult.accessToken);
                    } else if (err) {
                        console.log(err)
                    }

                    // Redirect back to the original location
                    const returnTo = Cookies.get('returnTo') || '/';
                    Cookies.remove('returnTo');
                });
            }
        };

        handleRedirectCallback();
    }, [auth0Client]);

    const setAccessTokenCookie = (accessToken) => {
        const expiresInSeconds = 3600; // 1 hour (adjust as needed)
        Cookies.set('accessToken', accessToken, {
            expires: new Date(Date.now() + expiresInSeconds * 1000),
            secure: true, // Set to true in a production environment (requires HTTPS)
            sameSite: 'strict',
            httpOnly: true,
        });
    };

    const getAccessToken = async () => {
        let accessToken = Cookies.get('accessToken');
        if (!accessToken) {
            const tokenResponse = await auth0Client.getTokenSilently();
            accessToken = tokenResponse.accessToken;
            setAccessTokenCookie(accessToken);
        }
        return accessToken;
    };

    return (
        <Auth0Provider
            {...props}
            domain={domain}
            clientId={clientId}
            authorizationParams={authorizationParams}
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
