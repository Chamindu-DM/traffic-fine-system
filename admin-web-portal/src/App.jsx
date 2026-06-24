import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import CreateFine from './pages/CreateFine';
import { LayoutDashboard, Plus } from 'lucide-react';

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
        <div className="flex items-center gap-6">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-400 bg-slate-50 px-3 py-1 rounded">
            Role: Senior Official
          </span>
          <nav className="flex items-center gap-4">
            <Link 
              to="/" 
              className="flex items-center gap-1.5 text-sm font-semibold text-gray-600 hover:text-blue-600 px-3 py-2 rounded-lg transition-colors"
            >
              <LayoutDashboard size={16} />
              Dashboard
            </Link>
            <Link 
              to="/create-fine" 
              className="flex items-center gap-1.5 text-sm font-semibold text-gray-600 hover:text-blue-600 px-3 py-2 rounded-lg transition-colors"
            >
              <Plus size={16} />
              Create Fine
            </Link>
          </nav>
        </div>
        <button 
          onClick={logout}
          className="text-sm font-semibold text-red-600 hover:text-red-700 bg-red-50 hover:bg-red-100 px-4 py-2 rounded-xl transition-all"
        >
          Sign Out
        </button>
      </div>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/create-fine" element={<CreateFine />} />
      </Routes>
    </div>
  );
};

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppContent />
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;