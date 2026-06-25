import React, { useState, useEffect } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { LayoutDashboard, MapPin, AlertCircle, CheckCircle, Search, Filter } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const COLORS = ['#3b82f6', '#ef4444', '#f59e0b', '#10b981'];

const Dashboard = () => {
  const { token, logout } = useAuth();
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [districtFilter, setDistrictFilter] = useState('ALL');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const [dashboardData, setDashboardData] = useState({
    totalCollected: 0,
    paidFineCount: 0,
    unpaidFineCount: 0,
    districtWiseCollections: [],
    categoryWiseCollections: []
  });
  const [fines, setFines] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchDashboardData = async () => {
    try {
      const response = await fetch('/api/admin/dashboard', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.status === 401 || response.status === 403) {
        logout();
        return;
      }
      if (response.ok) {
        const data = await response.json();
        setDashboardData(data);
      }
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
    }
  };

  const fetchFines = async () => {
    try {
      setLoading(true);
      const params = new URLSearchParams();
      if (statusFilter !== 'ALL') params.append('status', statusFilter);
      if (districtFilter !== 'ALL') params.append('district', districtFilter);
      if (categoryFilter !== 'ALL') params.append('categoryCode', categoryFilter);
      const url = `/api/admin/fines?${params.toString()}`;
      const response = await fetch(url, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.status === 401 || response.status === 403) {
        logout();
        return;
      }
      if (response.ok) {
        const data = await response.json();
        setFines(data);
      }
    } catch (error) {
      console.error('Failed to fetch fines:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      fetchDashboardData();
      fetchFines();
    }
  }, [token, statusFilter, districtFilter, categoryFilter]);

  const filteredRecords = (fines || []).filter(record => 
    record.referenceNumber.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="p-6 bg-gray-50 min-h-screen font-sans text-gray-800">
      <h1 className="text-3xl font-bold mb-8 flex items-center gap-3 text-slate-800">
        <LayoutDashboard className="text-blue-600" size={32} /> 
        Traffic Fine Monitoring Dashboard
      </h1>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
          <p className="text-sm font-medium text-gray-400 uppercase tracking-wider">Total Collections</p>
          <p className="text-3xl font-bold text-gray-900 mt-1">LKR {dashboardData.totalCollected?.toLocaleString() || 0}</p>
        </div>
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
          <p className="text-sm font-medium text-gray-400 uppercase tracking-wider">Paid Fines</p>
          <p className="text-3xl font-bold text-green-600 mt-1">{dashboardData.paidFineCount || 0}</p>
        </div>
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
          <p className="text-sm font-medium text-gray-400 uppercase tracking-wider">Unpaid Fines</p>
          <p className="text-3xl font-bold text-amber-500 mt-1">{dashboardData.unpaidFineCount || 0}</p>
        </div>
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
          <h2 className="text-xl font-semibold mb-6 flex items-center gap-2 text-gray-700">
            <MapPin size={22} className="text-blue-500"/> District-wise Collections
          </h2>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={dashboardData.districtWiseCollections || []}>
                <XAxis dataKey="name" stroke="#9ca3af" />
                <YAxis stroke="#9ca3af" />
                <Tooltip formatter={(value) => `LKR ${value.toLocaleString()}`} />
                <Bar dataKey="value" fill="#3b82f6" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
          <h2 className="text-xl font-semibold mb-6 flex items-center gap-2 text-gray-700">
            <AlertCircle size={22} className="text-amber-500"/> Fine Category Breakdown
          </h2>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={dashboardData.categoryWiseCollections || []} innerRadius={60} outerRadius={80} paddingAngle={5} dataKey="value">
                  {(dashboardData.categoryWiseCollections || []).map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => `LKR ${value.toLocaleString()}`} />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Fine Records Table with Filters */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="p-6 border-b border-gray-100 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <h2 className="text-xl font-semibold text-gray-700">Recent Fine Records</h2>
          <div className="flex flex-wrap gap-3">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
              <input 
                type="text" 
                placeholder="Search Ref #" 
                className="pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <select 
              className="px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="ALL">All Status</option>
              <option value="PAID">Paid</option>
              <option value="UNPAID">Unpaid</option>
              <option value="CANCELLED">Cancelled</option>
            </select>
            <select 
              className="px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
              value={districtFilter}
              onChange={(e) => setDistrictFilter(e.target.value)}
            >
              <option value="ALL">All Districts</option>
              <option value="Colombo">Colombo</option>
              <option value="Matara">Matara</option>
              <option value="Kandy">Kandy</option>
              <option value="Galle">Galle</option>
              <option value="Jaffna">Jaffna</option>
              <option value="Kurunegala">Kurunegala</option>
            </select>
            <select 
              className="px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
              value={categoryFilter}
              onChange={(e) => setCategoryFilter(e.target.value)}
            >
              <option value="ALL">All Categories</option>
              <option value="SPEEDING">SPEEDING</option>
              <option value="SIGNAL">SIGNAL</option>
              <option value="PARKING">PARKING</option>
              <option value="DRUNK_DRIVING">DRUNK_DRIVING</option>
              <option value="NO_HELMET">NO_HELMET</option>
              <option value="OVERLOADING">OVERLOADING</option>
            </select>
          </div>
        </div>

        <div className="overflow-x-auto">
          {loading ? (
            <div className="p-12 text-center text-gray-500">Loading fine records...</div>
          ) : (
            <table className="w-full text-left border-collapse">
              <thead className="bg-gray-50 text-gray-500 text-sm uppercase">
                <tr>
                  <th className="px-6 py-4 font-semibold">Reference #</th>
                  <th className="px-6 py-4 font-semibold">Category</th>
                  <th className="px-6 py-4 font-semibold">District</th>
                  <th className="px-6 py-4 font-semibold">Amount</th>
                  <th className="px-6 py-4 font-semibold">Status</th>
                  <th className="px-6 py-4 font-semibold">Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {filteredRecords.map((record) => (
                  <tr key={record.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 font-medium text-gray-900">{record.referenceNumber}</td>
                    <td className="px-6 py-4 text-gray-600">{record.category}</td>
                    <td className="px-6 py-4 text-gray-600">{record.district}</td>
                    <td className="px-6 py-4 text-gray-900 font-semibold">LKR {record.amount.toLocaleString()}</td>
                    <td className="px-6 py-4">
                      <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                        record.status === 'PAID' ? 'bg-green-100 text-green-700' : 
                        record.status === 'UNPAID' ? 'bg-amber-100 text-amber-700' : 'bg-red-100 text-red-700'
                      }`}>
                        {record.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-gray-500">{record.issuedAt.split('T')[0]}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;