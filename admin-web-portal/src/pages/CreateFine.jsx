import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Plus, CheckCircle, AlertCircle, FilePlus } from 'lucide-react';

const CreateFine = () => {
  const { token } = useAuth();
  const [categories, setCategories] = useState([]);
  const [officers, setOfficers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [fetchLoading, setFetchLoading] = useState(true);
  const [successRef, setSuccessRef] = useState('');
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    driverLicenseNumber: '',
    vehicleNumber: '',
    district: '',
    categoryId: '',
    officerId: ''
  });

  const districts = ['Colombo', 'Matara', 'Kandy', 'Galle', 'Jaffna', 'Kurunegala'];

  useEffect(() => {
    const fetchData = async () => {
      try {
        setFetchLoading(true);
        const [catRes, offRes] = await Promise.all([
          fetch('/api/admin/categories', {
            headers: { 'Authorization': `Bearer ${token}` }
          }),
          fetch('/api/admin/officers', {
            headers: { 'Authorization': `Bearer ${token}` }
          })
        ]);

        if (catRes.ok && offRes.ok) {
          const cats = await catRes.json();
          const offs = await offRes.json();
          setCategories(cats);
          setOfficers(offs);
        } else {
          setError('Failed to fetch categories or officers from server.');
        }
      } catch (err) {
        console.error('Error fetching categories/officers:', err);
        setError('Connection error. Could not load form data.');
      } finally {
        setFetchLoading(false);
      }
    };

    if (token) {
      fetchData();
    }
  }, [token]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await fetch('/api/fines', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          driverLicenseNumber: formData.driverLicenseNumber.trim(),
          vehicleNumber: formData.vehicleNumber.trim(),
          district: formData.district,
          categoryId: parseInt(formData.categoryId, 10),
          officerId: parseInt(formData.officerId, 10)
        })
      });

      if (response.ok) {
        const result = await response.json();
        setSuccessRef(result.referenceNumber);
      } else {
        const text = await response.text();
        try {
          const err = JSON.parse(text);
          setError(err.message || 'Failed to create fine. Please verify inputs.');
        } catch {
          setError('Failed to create fine. Please verify inputs.');
        }
      }
    } catch (err) {
      setError('Connection error. Failed to submit fine.');
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setFormData({
      driverLicenseNumber: '',
      vehicleNumber: '',
      district: '',
      categoryId: '',
      officerId: ''
    });
    setSuccessRef('');
    setError('');
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen font-sans text-gray-800 flex justify-center items-start">
      <div className="max-w-xl w-full">
        <h1 className="text-3xl font-bold mb-8 flex items-center gap-3 text-slate-800">
          <FilePlus className="text-blue-600" size={32} /> 
          Create New Fine
        </h1>

        {error && (
          <div className="mb-6 p-4 bg-red-50 text-red-700 rounded-xl text-sm border border-red-100 flex items-center gap-3">
            <AlertCircle size={20} className="shrink-0 text-red-500" />
            <span>{error}</span>
          </div>
        )}

        {successRef ? (
          <div className="bg-white p-8 rounded-xl shadow-sm border border-gray-100 text-center animate-in zoom-in duration-300">
            <div className="inline-flex p-4 bg-green-50 text-green-600 rounded-full mb-4">
              <CheckCircle size={48} />
            </div>
            <h2 className="text-2xl font-bold text-slate-800">Fine Created Successfully!</h2>
            <div className="mt-4 p-4 bg-green-50 text-green-800 rounded-xl border border-green-100 inline-block font-semibold">
              Reference Number: <span className="font-mono font-bold text-lg">{successRef}</span>
            </div>
            <p className="text-gray-500 mt-4 text-sm">
              The fine record has been registered in the system. The designated officer will be notified.
            </p>
            <div className="mt-8">
              <button 
                onClick={handleReset}
                className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-xl transition-all shadow-sm hover:shadow"
              >
                Create Another Fine
              </button>
            </div>
          </div>
        ) : (
          <div className="bg-white p-8 rounded-xl shadow-sm border border-gray-100">
            {fetchLoading ? (
              <div className="text-center py-8 text-gray-500">Loading form components...</div>
            ) : (
              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">Driver License Number</label>
                  <input 
                    type="text" 
                    required 
                    placeholder="e.g. B1234567"
                    className="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                    value={formData.driverLicenseNumber}
                    onChange={(e) => setFormData({...formData, driverLicenseNumber: e.target.value})}
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">Vehicle Number</label>
                  <input 
                    type="text" 
                    required 
                    placeholder="e.g. WP-CAD-1234"
                    className="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                    value={formData.vehicleNumber}
                    onChange={(e) => setFormData({...formData, vehicleNumber: e.target.value})}
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">District</label>
                  <select 
                    required 
                    className="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none bg-white transition-all"
                    value={formData.district}
                    onChange={(e) => setFormData({...formData, district: e.target.value})}
                  >
                    <option value="">Select District</option>
                    {districts.map((d) => (
                      <option key={d} value={d}>{d}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">Fine Category</label>
                  <select 
                    required 
                    className="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none bg-white transition-all"
                    value={formData.categoryId}
                    onChange={(e) => setFormData({...formData, categoryId: e.target.value})}
                  >
                    <option value="">Select Category</option>
                    {categories.map((c) => (
                      <option key={c.id} value={c.id}>{c.name} (LKR {c.defaultAmount})</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">Assigned Officer</label>
                  <select 
                    required 
                    className="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none bg-white transition-all"
                    value={formData.officerId}
                    onChange={(e) => setFormData({...formData, officerId: e.target.value})}
                  >
                    <option value="">Select Officer</option>
                    {officers.map((o) => (
                      <option key={o.id} value={o.id}>{o.name} ({o.badgeNumber}) - {o.district}</option>
                    ))}
                  </select>
                </div>

                <button 
                  type="submit" 
                  disabled={loading}
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded-xl flex items-center justify-center gap-2 hover:shadow transition disabled:opacity-50"
                >
                  {loading ? 'Submitting...' : <><Plus size={20}/> Issue Fine</>}
                </button>
              </form>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default CreateFine;
