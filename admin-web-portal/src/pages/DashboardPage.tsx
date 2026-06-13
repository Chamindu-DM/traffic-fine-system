import { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
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
      <div className="flex min-h-screen items-center justify-center bg-slate-50 text-slate-900">
        <div className="flex flex-col items-center space-y-4">
          <RefreshCw className="h-10 w-10 animate-spin text-blue-600" />
          <span className="text-lg font-semibold text-slate-700">Loading Dashboard...</span>
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
    <div className="min-h-screen font-sans text-slate-800 pb-12">
      {/* Navbar - Glassmorphic Light */}
      <nav className="sticky top-0 z-50 border-b border-slate-200/60 bg-white/80 backdrop-blur-md">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex h-16 items-center justify-between">
            <div className="flex items-center space-x-3">
              <Shield className="h-8 w-8 text-blue-600" />
              <span className="text-xl font-black tracking-tight text-slate-900">Traffic Fine Admin</span>
            </div>
            <div className="flex items-center space-x-4">
              <span className="hidden text-sm font-semibold text-slate-600 md:inline-block">Welcome, Police Admin</span>
              <Link
                to="/fines/create"
                className="flex items-center space-x-2 rounded-2xl bg-blue-600 px-4 py-2 text-sm font-bold text-white transition hover:bg-blue-500 hover:shadow-md focus:outline-none"
              >
                <span>Create Fine</span>
              </Link>
              <button
                onClick={handleLogout}
                className="flex items-center space-x-2 rounded-2xl bg-red-50 px-4 py-2 text-sm font-bold text-red-600 transition hover:bg-red-100 focus:outline-none"
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
          <div className="rounded-2xl border border-red-200 bg-red-50 p-4 text-red-700 font-medium">
            {error}
          </div>
        )}

        {/* Summary Stats Grid */}
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4 font-sans">
          {/* Total Collected */}
          <div className="rounded-3xl border border-slate-200/60 bg-white/80 p-6 shadow-xl backdrop-blur-md">
            <p className="text-sm font-bold text-slate-500 uppercase tracking-wider">Total Collected</p>
            <p className="mt-2 text-3xl font-black text-blue-700">
              {formatCurrency(dashboardData?.totalCollected ?? 0)}
            </p>
          </div>

          {/* Total Fines */}
          <div className="rounded-3xl border border-slate-200/60 bg-white/80 p-6 shadow-xl backdrop-blur-md">
            <p className="text-sm font-bold text-slate-500 uppercase tracking-wider">Total Fines Issued</p>
            <p className="mt-2 text-3xl font-black text-slate-800">
              {(dashboardData?.paidFineCount ?? 0) + (dashboardData?.unpaidFineCount ?? 0) + (dashboardData?.cancelledFineCount ?? 0)}
            </p>
          </div>

          {/* Paid Fines */}
          <div className="rounded-3xl border border-slate-200/60 bg-white/80 p-6 shadow-xl backdrop-blur-md">
            <p className="text-sm font-bold text-slate-500 uppercase tracking-wider">Paid Fines</p>
            <p className="mt-2 text-3xl font-black text-emerald-600">
              {dashboardData?.paidFineCount ?? 0}
            </p>
          </div>

          {/* Unpaid Fines */}
          <div className="rounded-3xl border border-slate-200/60 bg-white/80 p-6 shadow-xl backdrop-blur-md">
            <p className="text-sm font-bold text-slate-500 uppercase tracking-wider">Unpaid Fines</p>
            <p className="mt-2 text-3xl font-black text-amber-600">
              {dashboardData?.unpaidFineCount ?? 0}
            </p>
          </div>
        </div>

        {/* Charts Section */}
        <div className="grid grid-cols-1 gap-8 lg:grid-cols-2">
          {/* District collections */}
          <div className="rounded-3xl border border-slate-200/60 bg-white/80 p-6 shadow-xl backdrop-blur-md">
            <div className="flex items-center space-x-2 mb-4">
              <BarChart2 className="h-5 w-5 text-blue-600" />
              <h2 className="text-lg font-extrabold text-slate-900">District-wise Collections</h2>
            </div>
            <div className="h-80 w-full">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={dashboardData?.districtWiseCollections ?? []}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                  <XAxis dataKey="label" stroke="#475569" fontSize={12} fontWeight="bold" />
                  <YAxis stroke="#475569" fontSize={12} fontWeight="bold" />
                  <Tooltip
                    contentStyle={{ backgroundColor: '#ffffff', borderColor: '#e2e8f0', color: '#0f172a', borderRadius: '16px' }}
                    formatter={(value) => [`LKR ${value}`, 'Revenue']}
                  />
                  <Bar dataKey="amount" fill="#2563eb" radius={[6, 6, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          {/* Category collections */}
          <div className="rounded-3xl border border-slate-200/60 bg-white/80 p-6 shadow-xl backdrop-blur-md">
            <div className="flex items-center space-x-2 mb-4">
              <BarChart2 className="h-5 w-5 text-purple-600" />
              <h2 className="text-lg font-extrabold text-slate-900">Category-wise Collections</h2>
            </div>
            <div className="h-80 w-full">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={dashboardData?.categoryWiseCollections ?? []}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                  <XAxis dataKey="label" stroke="#475569" fontSize={12} fontWeight="bold" />
                  <YAxis stroke="#475569" fontSize={12} fontWeight="bold" />
                  <Tooltip
                    contentStyle={{ backgroundColor: '#ffffff', borderColor: '#e2e8f0', color: '#0f172a', borderRadius: '16px' }}
                    formatter={(value) => [`LKR ${value}`, 'Revenue']}
                  />
                  <Bar dataKey="amount" fill="#8b5cf6" radius={[6, 6, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        {/* Fines Table & Filters */}
        <div className="rounded-3xl border border-slate-200/60 bg-white/80 shadow-xl backdrop-blur-md overflow-hidden">
          {/* Header & Filter Controls */}
          <div className="border-b border-slate-200/60 bg-white/40 p-6">
            <div className="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
              <div className="flex items-center space-x-2">
                <Filter className="h-5 w-5 text-blue-600" />
                <h2 className="text-lg font-extrabold text-slate-900">Fine Records</h2>
              </div>

              {/* Filters */}
              <div className="flex flex-wrap gap-4">
                {/* District Filter */}
                <div>
                  <select
                    value={selectedDistrict}
                    onChange={(e) => handleFilterChange(e.target.value, selectedCategory, selectedStatus)}
                    className="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm text-slate-700 font-bold outline-none transition focus:border-blue-500 focus:ring-4 focus:ring-blue-500/12"
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
                    className="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm text-slate-700 font-bold outline-none transition focus:border-blue-500 focus:ring-4 focus:ring-blue-500/12"
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
                    className="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm text-slate-700 font-bold outline-none transition focus:border-blue-500 focus:ring-4 focus:ring-blue-500/12"
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
                <RefreshCw className="h-8 w-8 animate-spin text-blue-600" />
              </div>
            ) : fines.length === 0 ? (
              <div className="flex h-64 flex-col items-center justify-center text-slate-400">
                <Search className="h-10 w-10 mb-2" />
                <span className="font-semibold">No fine records matched the filters</span>
              </div>
            ) : (
              <table className="w-full text-left text-sm text-slate-700">
                <thead className="bg-slate-50/70 text-xs uppercase text-slate-500 border-b border-slate-200">
                  <tr>
                    <th className="px-6 py-4 font-bold">Reference No</th>
                    <th className="px-6 py-4 font-bold">Category</th>
                    <th className="px-6 py-4 font-bold">District</th>
                    <th className="px-6 py-4 font-bold">Officer</th>
                    <th className="px-6 py-4 font-bold">Driver License</th>
                    <th className="px-6 py-4 font-bold">Vehicle No</th>
                    <th className="px-6 py-4 text-right font-bold">Amount</th>
                    <th className="px-6 py-4 text-center font-bold">Status</th>
                    <th className="px-6 py-4 font-bold">Issued Date</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 bg-white/40">
                  {fines.map((fine) => (
                    <tr key={fine.referenceNumber} className="hover:bg-slate-50/50 transition">
                      <td className="px-6 py-4 font-mono font-bold text-slate-900">{fine.referenceNumber}</td>
                      <td className="px-6 py-4">
                        <span className="font-bold text-slate-800">{fine.category}</span>
                        <span className="block text-xs text-slate-400 font-bold">{fine.categoryCode}</span>
                      </td>
                      <td className="px-6 py-4 text-slate-700 font-semibold">{fine.district}</td>
                      <td className="px-6 py-4 text-slate-700 font-semibold">{fine.officer}</td>
                      <td className="px-6 py-4 font-mono text-slate-500 font-semibold">{fine.driverLicenseNumber}</td>
                      <td className="px-6 py-4 font-mono text-slate-500 font-semibold">{fine.vehicleNumber}</td>
                      <td className="px-6 py-4 text-right font-black text-blue-700">{formatCurrency(fine.amount)}</td>
                      <td className="px-6 py-4 text-center">
                        <span
                          className={`inline-flex items-center rounded-full px-3 py-1 text-xs font-bold ${
                            fine.status === 'PAID'
                              ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                              : fine.status === 'UNPAID'
                              ? 'bg-amber-50 text-amber-700 border border-amber-200'
                              : 'bg-slate-100 text-slate-700 border border-slate-300'
                          }`}
                        >
                          {fine.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-slate-500 text-xs font-bold">{formatDate(fine.issuedAt)}</td>
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
