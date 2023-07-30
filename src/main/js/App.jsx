import AdminPanel from "./Admin";
import {useAuth0, withAuthenticationRequired} from '@auth0/auth0-react';
import {Route, Routes} from "react-router-dom";
import {Nav} from "./Nav";
import {Error} from "./Error";
import StorytellerDetails from "./StorytellerDetails";

const React = require('react');
const ReactDOM = require('react-dom');

const ProtectedUsers = withAuthenticationRequired(AdminPanel);
const ProtectedStoryteller = withAuthenticationRequired(StorytellerDetails);
function App() {
    const { isLoading, error } = useAuth0();

    if (isLoading) {
        return <div>Loading...</div>;
    }


    return (
        <>
            <Nav />
            {error && <Error message={error.message} />}
            <Routes>
                <Route exact path="/" />
                <Route exact path="/admin" element={<ProtectedUsers />} />
                <Route exact path="/storytellerDetails" element={<ProtectedStoryteller/>} />
            </Routes>
        </>
    );
}

export default App