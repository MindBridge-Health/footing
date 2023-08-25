import React from 'react';
import App from './App'
import { BrowserRouter, useNavigate } from 'react-router-dom';
import {createRoot} from "react-dom/client";
import Auth0ProviderWithRedirectCallback from "./admin/Auth0ProviderWithRedirectCallback";


createRoot(document.getElementById('react')).render(
    <React.StrictMode>
        <BrowserRouter basename="/console">
            <Auth0ProviderWithRedirectCallback
                domain='dev-7hxmzq06wywhbaqg.us.auth0.com'
                clientId='4KBwlbVbIMpHuwjbWPedvx4iUfDbjtdf'
                authorizationParams={{
                    audience: 'https://footing.mindbridgehealth.com',
                    scope: 'openid profile email read:userdata',
                    responseType: 'token id_token',
                    redirect_uri: window.location.origin + "/console/index.html",
                }}
            >
                <App />
            </Auth0ProviderWithRedirectCallback>
        </BrowserRouter>
    </React.StrictMode>
);
