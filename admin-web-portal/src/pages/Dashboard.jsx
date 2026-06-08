import React from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { LayoutDashboard, MapPin, AlertCircle, CheckCircle, XCircle } from 'lucide-react';

// Mock data based on the assignment guidelines
const districtData = [
  { name: 'Colombo', total: 600000 },
  { name: 'Matara', total: 250000 },
  { name: 'Kandy', total: 400000 },
  { name: 'Galle', total: 180000 },
];

const categoryData = [
  { name: 'Speeding', value: 400000 },
  { name: 'Drunk Driving', value: 650000 },
  { name: 'No License', value: 200000 },
];

const COLORS = ['#3b82f6', '#ef4444', '#f59e0b', '#10b981'];

const Dashboard = () => {
  return (
    <div className="p-6 bg-gray-50 min-h-screen font-sans text-gray-800">
      <h1 className="text-3xl font-bold mb-8 flex items-center gap-3 text-slate-800">
        <LayoutDashboard className="text-blue-600" size={32} /> 
        Traffic Fine Monitoring Dashboard
      </h1>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-gray-400 uppercase tracking-wider">Total Collections</p>
            <p className="text-3xl font-bold text-gray-900 mt-1">LKR 1,250,000</p>
          </div>
          <div className="p-3 bg-blue-50 rounded-lg text-blue-600"><CheckCircle size={28}/></div>
        </div>
        
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-gray-400 uppercase tracking-wider">Paid Fines</p>
            <p className="text-3xl font-bold text-green-600 mt-1">245</p>
          </div>
          <div className="p-3 bg-green-50 rounded-lg text-green-600"><CheckCircle size={28}/></div>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-gray-400 uppercase tracking-wider">Unpaid Fines</p>
            <p className="text-3xl font-bold text-amber-500 mt-1">80</p>
          </div>
          <div className="p-3 bg-amber-50 rounded-lg text-amber-500"><AlertCircle size={28}/></div>
        </div>
      </div>

      {/* Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* District-wise Chart */}
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
          <h2 className="text-xl font-semibold mb-6 flex items-center gap-2 text-gray-700">
            <MapPin size={22} className="text-blue-500"/> District-wise Collections
          </h2>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={districtData}>
                <XAxis dataKey="name" stroke="#9ca3af" />
                <YAxis stroke="#9ca3af" />
                <Tooltip formatter={(value) => `LKR ${value.toLocaleString()}`} />
                <Bar dataKey="total" fill="#3b82f6" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Category Breakdown */}
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
          <h2 className="text-xl font-semibold mb-6 flex items-center gap-2 text-gray-700">
            <AlertCircle size={22} className="text-amber-500"/> Fine Category Breakdown
          </h2>
          <div className="h-72 flex items-center justify-center">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={categoryData} innerRadius={70} outerRadius={90} paddingAngle={5} dataKey="value" nameKey="name">
                  {categoryData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => `LKR ${value.toLocaleString()}`} />
              </PieChart>
            </ResponsiveContainer>
            <div className="flex flex-col gap-2 ml-4">
              {categoryData.map((entry, index) => (
                <div key={entry.name} className="flex items-center gap-2 text-sm">
                  <div className="w-3 h-3 rounded-full" style={{ backgroundColor: COLORS[index] }} />
                  <span className="text-gray-600 font-medium">{entry.name}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;