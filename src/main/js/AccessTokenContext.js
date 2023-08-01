import { createContext, useContext } from 'react';

const AccessTokenContext = createContext(null);

export const useAccessTokenContext = () => {
    return useContext(AccessTokenContext);
};

export default AccessTokenContext;
