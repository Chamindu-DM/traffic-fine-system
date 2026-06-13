import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';
import type { LoginResponse } from '../types';
import { Shield } from 'lucide-react';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e: FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password.trim()) {
      setError('Please fill in all fields.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await api.post<LoginResponse>('/auth/login', {
        username: username.trim(),
        password: password,
      });

      localStorage.setItem('jwt', response.data.token);
      navigate('/dashboard');
    } catch (err: any) {
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Login failed. Please check your credentials and try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center px-4 font-sans">
      <div className="w-full max-w-md">
        {/* Glassmorphism login card matching the public payment design */}
        <div className="rounded-3xl border border-slate-200/60 bg-white/82 p-8 shadow-2xl backdrop-blur-md">
          <div className="mb-8 text-center flex flex-col items-center">
            <div className="mb-3 rounded-full bg-amber-500/10 p-3 text-amber-700">
              <Shield className="h-8 w-8" />
            </div>
            <h1 className="text-3xl font-extrabold tracking-tight text-slate-900">
              Traffic Fine Admin
            </h1>
            <p className="mt-2 text-sm text-slate-600">
              Authorized personnel login portal
            </p>
          </div>

          {error && (
            <div className="mb-6 rounded-xl border border-red-200 bg-red-50 p-3 text-sm text-red-700">
              {error}
            </div>
          )}

          <form onSubmit={handleLogin} className="space-y-6">
            <div>
              <label
                htmlFor="username"
                className="block text-sm font-bold text-slate-700"
              >
                Username
              </label>
              <input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                disabled={loading}
                className="mt-2 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3.5 text-slate-900 placeholder-slate-400 outline-none transition duration-200 focus:border-blue-500 focus:ring-4 focus:ring-blue-500/12"
                placeholder="Enter username"
                required
              />
            </div>

            <div>
              <label
                htmlFor="password"
                className="block text-sm font-bold text-slate-700"
              >
                Password
              </label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
                className="mt-2 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3.5 text-slate-900 placeholder-slate-400 outline-none transition duration-200 focus:border-blue-500 focus:ring-4 focus:ring-blue-500/12"
                placeholder="Enter password"
                required
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="flex w-full items-center justify-center rounded-2xl bg-gradient-to-r from-slate-900 to-blue-700 py-3.5 text-sm font-bold text-white transition duration-200 hover:-translate-y-px hover:shadow-lg focus:outline-none focus:ring-4 focus:ring-blue-500/20 disabled:opacity-50"
            >
              {loading ? (
                <div className="h-5 w-5 animate-spin rounded-full border-2 border-white border-t-transparent"></div>
              ) : (
                'Sign In'
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
