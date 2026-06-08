import React from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';

// Inner gatekeeper component
const AppContent = () => {
  const { token, logout } = useAuth();

  if (!token) {
    return <Login />;
  }

  return (
    <div>
      {/* Universal Admin Navigation Header */}
      <div className="bg-white border-b border-gray-100 px-6 py-3 flex justify-between items-center shadow-xs">
        <span className="text-xs font-bold uppercase tracking-wider text-slate-400 bg-slate-50 px-3 py-1 rounded">
          Role: Senior Official
        </span>
        <button 
          onClick={logout}
          className="text-sm font-semibold text-red-600 hover:text-red-700 bg-red-50 hover:bg-red-100 px-4 py-2 rounded-xl transition-all"
        >
          Sign Out
        </button>
      </div>
      <Dashboard />
    </div>
  );
};

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;