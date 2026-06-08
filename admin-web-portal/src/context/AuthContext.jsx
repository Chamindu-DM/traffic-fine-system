import React, { createContext, useState, useContext } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('admin_token') || null);
  const [user, setUser] = useState(JSON.parse(localStorage.getItem('admin_user')) || null);

  // Simulates receiving a JWT token from the backend /api/auth/login endpoint
  const login = (username, password) => {
    if (username === 'admin' && password === 'password') {
      const mockToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mockTokenForAdmin';
      const mockUser = { username: 'admin', role: 'ADMIN', name: 'Senior Official' };
      
      localStorage.setItem('admin_token', mockToken);
      localStorage.setItem('admin_user', JSON.stringify(mockUser));
      
      setToken(mockToken);
      setUser(mockUser);
      return true;
    }
    return false;
  };

  const logout = () => {
    localStorage.removeItem('admin_token');
    localStorage.removeItem('admin_user');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);