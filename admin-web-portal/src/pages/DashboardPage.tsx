import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';
import type { AdminDashboardResponse, AdminFineResponse } from '../types';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { LogOut, RefreshCw, BarChart2, Shield, Search, Filter } from 'lucide-react';

export default function DashboardPage() {
  const navigate = useNavigate();
  const [dashboardData, setDashboardData] = useState<AdminDashboardResponse | null>(null);
  const [fines, setFines] = useState<AdminFineResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [tableLoading, setTableLoading] = useState(false);
  const [error, setError] = useState('');

  // Filter states
  const [selectedDistrict, setSelectedDistrict] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('jwt');
    if (!token) {
      navigate('/login');
      return;
    }

    fetchInitialData();
  }, [navigate]);

  const fetchInitialData = async () => {
    setLoading(true);
    setError('');
    try {
      const [dashRes, finesRes] = await Promise.all([
        api.get<AdminDashboardResponse>('/admin/dashboard'),
        api.get<AdminFineResponse[]>('/admin/fines')
      ]);
      setDashboardData(dashRes.data);
      setFines(finesRes.data);
    } catch (err: any) {
      console.error(err);
      setError('Failed to load dashboard data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = async (district: string, category: string, status: string) => {
    setSelectedDistrict(district);
    setSelectedCategory(category);
    setSelectedStatus(status);

    setTableLoading(true);
    try {
      const params: any = {};
      if (district) params.district = district;
      if (category) params.categoryCode = category;
      if (status) params.status = status;

      const response = await api.get<AdminFineResponse[]>('/admin/fines', { params });
      setFines(response.data);
    } catch (err) {
      console.error(err);
    } finally {
      setTableLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('jwt');
    navigate('/login');
  };

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-900 text-white">
        <div className="flex flex-col items-center space-y-4">
          <RefreshCw className="h-10 w-10 animate-spin text-blue-500" />
          <span className="text-lg font-medium text-slate-300">Loading Dashboard...</span>
        </div>
      </div>
    );
  }

  // Format currency
  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('en-LK', {
      style: 'currency',
      currency: 'LKR',
      minimumFractionDigits: 2
    }).format(val);
  };

  // Format dates
  const formatDate = (dateStr: string) => {
    return new Date(dateStr).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="min-h-screen bg-slate-950 font-sans text-slate-100 pb-12">
      {/* Navbar */}
      <nav className="sticky top-0 z-50 border-b border-slate-800 bg-slate-900/80 backdrop-blur-md">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex h-16 items-center justify-between">
            <div className="flex items-center space-x-3">
              <Shield className="h-8 w-8 text-blue-500" />
              <span className="text-xl font-bold tracking-tight text-white">Traffic Fine Admin</span>
            </div>
            <div className="flex items-center space-x-4">
              <span className="hidden text-sm text-slate-400 md:inline-block">Welcome, Police Admin</span>
              <button
                onClick={handleLogout}
                className="flex items-center space-x-2 rounded-lg bg-red-600/10 px-4 py-2 text-sm font-semibold text-red-400 transition hover:bg-red-600/20 hover:text-red-300 focus:outline-none"
              >
                <LogOut className="h-4 w-4" />
                <span>Logout</span>
              </button>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 mt-8 space-y-8">
        {error && (
          <div className="rounded-lg border border-red-500/20 bg-red-500/10 p-4 text-red-400">
            {error}
          </div>
        )}

        {/* Summary Stats Grid */}
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {/* Total Collected */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/50 p-6 shadow-xl backdrop-blur-sm">
            <p className="text-sm font-medium text-slate-400">Total Collected</p>
            <p className="mt-2 text-3xl font-extrabold text-emerald-400">
              {formatCurrency(dashboardData?.totalCollected ?? 0)}
            </p>
          </div>

          {/* Total Fines */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/50 p-6 shadow-xl backdrop-blur-sm">
            <p className="text-sm font-medium text-slate-400">Total Fines Issued</p>
            <p className="mt-2 text-3xl font-extrabold text-white">
              {(dashboardData?.paidFineCount ?? 0) + (dashboardData?.unpaidFineCount ?? 0) + (dashboardData?.cancelledFineCount ?? 0)}
            </p>
          </div>

          {/* Paid Fines */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/50 p-6 shadow-xl backdrop-blur-sm">
            <p className="text-sm font-medium text-slate-400">Paid Fines</p>
            <p className="mt-2 text-3xl font-extrabold text-blue-400">
              {dashboardData?.paidFineCount ?? 0}
            </p>
          </div>

          {/* Unpaid Fines */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/50 p-6 shadow-xl backdrop-blur-sm">
            <p className="text-sm font-medium text-slate-400">Unpaid Fines</p>
            <p className="mt-2 text-3xl font-extrabold text-amber-500">
              {dashboardData?.unpaidFineCount ?? 0}
            </p>
          </div>
        </div>

        {/* Charts Section */}
        <div className="grid grid-cols-1 gap-8 lg:grid-cols-2">
          {/* District collections */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/30 p-6 shadow-xl">
            <div className="flex items-center space-x-2 mb-4">
              <BarChart2 className="h-5 w-5 text-blue-500" />
              <h2 className="text-lg font-bold text-white">District-wise Collections</h2>
            </div>
            <div className="h-80 w-full">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={dashboardData?.districtWiseCollections ?? []}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                  <XAxis dataKey="label" stroke="#94a3b8" />
                  <YAxis stroke="#94a3b8" />
                  <Tooltip
                    contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', color: '#f8fafc' }}
                    formatter={(value) => [`LKR ${value}`, 'Revenue']}
                  />
                  <Bar dataKey="amount" fill="#3b82f6" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          {/* Category collections */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/30 p-6 shadow-xl">
            <div className="flex items-center space-x-2 mb-4">
              <BarChart2 className="h-5 w-5 text-purple-500" />
              <h2 className="text-lg font-bold text-white">Category-wise Collections</h2>
            </div>
            <div className="h-80 w-full">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={dashboardData?.categoryWiseCollections ?? []}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
                  <XAxis dataKey="label" stroke="#94a3b8" />
                  <YAxis stroke="#94a3b8" />
                  <Tooltip
                    contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', color: '#f8fafc' }}
                    formatter={(value) => [`LKR ${value}`, 'Revenue']}
                  />
                  <Bar dataKey="amount" fill="#a855f7" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        {/* Fines Table & Filters */}
        <div className="rounded-2xl border border-slate-800 bg-slate-900/30 shadow-xl overflow-hidden">
          {/* Header & Filter Controls */}
          <div className="border-b border-slate-800 bg-slate-900/50 p-6">
            <div className="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
              <div className="flex items-center space-x-2">
                <Filter className="h-5 w-5 text-blue-500" />
                <h2 className="text-lg font-bold text-white">Fine Records</h2>
              </div>

              {/* Filters */}
              <div className="flex flex-wrap gap-4">
                {/* District Filter */}
                <div>
                  <select
                    value={selectedDistrict}
                    onChange={(e) => handleFilterChange(e.target.value, selectedCategory, selectedStatus)}
                    className="rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-sm text-slate-200 outline-none transition focus:border-blue-500"
                  >
                    <option value="">All Districts</option>
                    <option value="Colombo">Colombo</option>
                    <option value="Matara">Matara</option>
                  </select>
                </div>

                {/* Category Filter */}
                <div>
                  <select
                    value={selectedCategory}
                    onChange={(e) => handleFilterChange(selectedDistrict, e.target.value, selectedStatus)}
                    className="rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-sm text-slate-200 outline-none transition focus:border-blue-500"
                  >
                    <option value="">All Categories</option>
                    <option value="SPEEDING">Speeding</option>
                    <option value="SIGNAL">Traffic Signal Violation</option>
                    <option value="PARKING">Illegal Parking</option>
                  </select>
                </div>

                {/* Status Filter */}
                <div>
                  <select
                    value={selectedStatus}
                    onChange={(e) => handleFilterChange(selectedDistrict, selectedCategory, e.target.value)}
                    className="rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-sm text-slate-200 outline-none transition focus:border-blue-500"
                  >
                    <option value="">All Statuses</option>
                    <option value="PAID">Paid</option>
                    <option value="UNPAID">Unpaid</option>
                    <option value="CANCELLED">Cancelled</option>
                  </select>
                </div>
              </div>
            </div>
          </div>

          {/* Table container */}
          <div className="overflow-x-auto">
            {tableLoading ? (
              <div className="flex h-64 items-center justify-center">
                <RefreshCw className="h-8 w-8 animate-spin text-blue-500" />
              </div>
            ) : fines.length === 0 ? (
              <div className="flex h-64 flex-col items-center justify-center text-slate-500">
                <Search className="h-10 w-10 mb-2" />
                <span>No fine records matched the filters</span>
              </div>
            ) : (
              <table className="w-full text-left text-sm text-slate-300">
                <thead className="bg-slate-900/60 text-xs uppercase text-slate-400 border-b border-slate-800">
                  <tr>
                    <th className="px-6 py-4">Reference No</th>
                    <th className="px-6 py-4">Category</th>
                    <th className="px-6 py-4">District</th>
                    <th className="px-6 py-4">Officer</th>
                    <th className="px-6 py-4">Driver License</th>
                    <th className="px-6 py-4">Vehicle No</th>
                    <th className="px-6 py-4 text-right">Amount</th>
                    <th className="px-6 py-4 text-center">Status</th>
                    <th className="px-6 py-4">Issued Date</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/50">
                  {fines.map((fine) => (
                    <tr key={fine.referenceNumber} className="hover:bg-slate-800/10 transition">
                      <td className="px-6 py-4 font-mono font-semibold text-white">{fine.referenceNumber}</td>
                      <td className="px-6 py-4">
                        <span className="font-medium text-slate-200">{fine.category}</span>
                        <span className="block text-xs text-slate-500">{fine.categoryCode}</span>
                      </td>
                      <td className="px-6 py-4 text-slate-300">{fine.district}</td>
                      <td className="px-6 py-4 text-slate-300">{fine.officer}</td>
                      <td className="px-6 py-4 font-mono text-slate-400">{fine.driverLicenseNumber}</td>
                      <td className="px-6 py-4 font-mono text-slate-400">{fine.vehicleNumber}</td>
                      <td className="px-6 py-4 text-right font-semibold text-emerald-400">{formatCurrency(fine.amount)}</td>
                      <td className="px-6 py-4 text-center">
                        <span
                          className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold ${
                            fine.status === 'PAID'
                              ? 'bg-emerald-500/10 text-emerald-400'
                              : fine.status === 'UNPAID'
                              ? 'bg-amber-500/10 text-amber-400'
                              : 'bg-slate-500/10 text-slate-400'
                          }`}
                        >
                          {fine.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-slate-400 text-xs">{formatDate(fine.issuedAt)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}
