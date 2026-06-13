import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import CreateFinePage from './pages/CreateFinePage';

// Protected Route Wrapper
function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem('jwt');
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return <>{children}</>;
}

// Redirect root to dashboard or login
function RootRedirect() {
  const token = localStorage.getItem('jwt');
  return <Navigate to={token ? "/dashboard" : "/login"} replace />;
}

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<RootRedirect />} />
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/fines/create"
          element={
            <ProtectedRoute>
              <CreateFinePage />
            </ProtectedRoute>
          }
        />
        {/* Fallback route */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}
