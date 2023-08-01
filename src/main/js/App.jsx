import AdminPanel from "./Admin";
import {useAuth0, withAuthenticationRequired} from '@auth0/auth0-react';
import {Route, Routes} from "react-router-dom";
import {Nav} from "./Nav";
import {Error} from "./Error";
import StorytellerDetails from "./StorytellerDetails";
import Cookies from "js-cookie";

const React = require('react');
const ReactDOM = require('react-dom');

const ProtectedUsers = withAuthenticationRequired(AdminPanel);
const ProtectedStoryteller = withAuthenticationRequired(StorytellerDetails);
function App() {
    const { isLoading, error , getAccessTokenSilently} = useAuth0();

    if (isLoading) {
        return <div>Loading...</div>;
    }
    const setAccessTokenCookie = () => {
        const expiresInSeconds = 3600; // 1 hour (adjust as needed)
        Cookies.set('accessToken', getAccessTokenSilently(), {
            expires: new Date(Date.now() + expiresInSeconds * 1000),
            secure: true, // Set to true in a production environment (requires HTTPS)
            sameSite: 'strict',
            httpOnly: true,
        });
    };

    setAccessTokenCookie()

    return (
        <>
            <Nav />
            {error && <Error message={error.message} />}
            <Routes>
                <Route path="/" />
                <Route exact path="/admin" element={<ProtectedUsers />} />
                <Route exact path="/storytellerDetails" element={<ProtectedStoryteller/>} />
            </Routes>
        </>
    );
}

export default App