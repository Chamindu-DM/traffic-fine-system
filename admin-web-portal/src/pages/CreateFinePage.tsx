import { useState, useEffect, type FormEvent } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api';
import type { FineCategory, CreateFineResponse } from '../types';
import { Shield, ArrowLeft, CheckCircle, Copy, AlertCircle, RefreshCw } from 'lucide-react';

export default function CreateFinePage() {
  const navigate = useNavigate();
  const [categories, setCategories] = useState<FineCategory[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [error, setError] = useState('');
  const [successResponse, setSuccessResponse] = useState<CreateFineResponse | null>(null);

  // Form Fields
  const [driverLicenseNumber, setDriverLicenseNumber] = useState('');
  const [vehicleNumber, setVehicleNumber] = useState('');
  const [district, setDistrict] = useState('Colombo');
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | ''>('');
  const [amount, setAmount] = useState<number | ''>('');
  const [officerId, setOfficerId] = useState<number | ''>('');

  const districts = [
    'Colombo',
    'Matara',
    'Kandy',
    'Galle',
    'Jaffna',
    'Kurunegala',
    'Ratnapura',
    'Anuradhapura',
  ];

  useEffect(() => {
    // Check auth
    const token = localStorage.getItem('jwt');
    if (!token) {
      navigate('/login');
      return;
    }

    fetchCategories();
  }, [navigate]);

  const fetchCategories = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get<FineCategory[]>('/fine-categories');
      setCategories(response.data);
    } catch (err) {
      console.error(err);
      setError('Failed to load traffic fine categories. Please check backend connection.');
    } finally {
      setLoading(false);
    }
  };

  const handleCategoryChange = (categoryIdStr: string) => {
    if (!categoryIdStr) {
      setSelectedCategoryId('');
      setAmount('');
      return;
    }
    const catId = parseInt(categoryIdStr, 10);
    setSelectedCategoryId(catId);

    const selectedCat = categories.find((cat) => cat.id === catId);
    if (selectedCat) {
      setAmount(selectedCat.amount);
    } else {
      setAmount('');
    }
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!driverLicenseNumber.trim() || !vehicleNumber.trim() || !district || !selectedCategoryId || !officerId) {
      setError('Please fill in all required fields.');
      return;
    }

    setSubmitLoading(true);
    setError('');

    try {
      const response = await api.post<CreateFineResponse>('/fines', {
        driverLicenseNumber: driverLicenseNumber.trim(),
        vehicleNumber: vehicleNumber.trim(),
        district,
        categoryId: selectedCategoryId,
        officerId,
      });

      setSuccessResponse(response.data);
      // Reset form
      setDriverLicenseNumber('');
      setVehicleNumber('');
      setSelectedCategoryId('');
      setAmount('');
      setOfficerId('');
    } catch (err: any) {
      console.error(err);
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Failed to issue the fine. Please verify details and try again.');
      }
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleCopy = () => {
    if (successResponse) {
      navigator.clipboard.writeText(successResponse.referenceNumber);
    }
  };

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('en-LK', {
      style: 'currency',
      currency: 'LKR',
      minimumFractionDigits: 2,
    }).format(val);
  };

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-50 text-slate-900">
        <div className="flex flex-col items-center space-y-4">
          <RefreshCw className="h-10 w-10 animate-spin text-blue-600" />
          <span className="text-lg font-semibold text-slate-700">Loading fine categories...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen font-sans text-slate-800 pb-12">
      {/* Navbar */}
      <nav className="sticky top-0 z-50 border-b border-slate-200/60 bg-white/80 backdrop-blur-md">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex h-16 items-center justify-between">
            <div className="flex items-center space-x-3">
              <Shield className="h-8 w-8 text-blue-600" />
              <span className="text-xl font-black tracking-tight text-slate-900">Traffic Fine Admin</span>
            </div>
            <Link
              to="/dashboard"
              className="flex items-center space-x-2 rounded-2xl bg-slate-100 px-4 py-2 text-sm font-bold text-slate-700 transition hover:bg-slate-200"
            >
              <ArrowLeft className="h-4 w-4" />
              <span>Back to Dashboard</span>
            </Link>
          </div>
        </div>
      </nav>

      {/* Main content */}
      <main className="mx-auto max-w-3xl px-4 sm:px-6 mt-8">
        <div className="rounded-3xl border border-slate-200/60 bg-white/80 p-8 shadow-2xl backdrop-blur-md">
          <div className="mb-8 border-b border-slate-200/60 pb-6">
            <h1 className="text-3xl font-black text-slate-900 tracking-tight">Issue New Traffic Fine</h1>
            <p className="mt-2 text-sm text-slate-500">Record a new traffic violation and generate a reference number</p>
          </div>

          {error && (
            <div className="mb-6 flex items-start space-x-2 rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700 font-medium">
              <AlertCircle className="h-5 w-5 shrink-0 text-red-500" />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Driver License & Vehicle No */}
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div>
                <label className="block text-sm font-bold text-slate-700">Driver License Number</label>
                <input
                  type="text"
                  value={driverLicenseNumber}
                  onChange={(e) => setDriverLicenseNumber(e.target.value)}
                  placeholder="e.g. B1234567"
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-slate-950 placeholder-slate-400 outline-none transition focus:border-blue-500 focus:ring-4 focus:ring-blue-500/12"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-bold text-slate-700">Vehicle Number</label>
                <input
                  type="text"
                  value={vehicleNumber}
                  onChange={(e) => setVehicleNumber(e.target.value)}
                  placeholder="e.g. WP-4567 or SP-1234"
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-slate-950 placeholder-slate-400 outline-none transition focus:border-blue-500 focus:ring-4 focus:ring-blue-500/12"
                  required
                />
              </div>
            </div>

            {/* District & Category */}
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div>
                <label className="block text-sm font-bold text-slate-700">District</label>
                <select
                  value={district}
                  onChange={(e) => setDistrict(e.target.value)}
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-slate-950 outline-none transition focus:border-blue-500 focus:ring-4 focus:ring-blue-500/12"
                  required
                >
                  {districts.map((d) => (
                    <option key={d} value={d}>
                      {d}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-bold text-slate-700">Violation Category</label>
                <select
                  value={selectedCategoryId}
                  onChange={(e) => handleCategoryChange(e.target.value)}
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-slate-950 outline-none transition focus:border-blue-500 focus:ring-4 focus:ring-blue-500/12"
                  required
                >
                  <option value="">Select a category</option>
                  {categories.map((cat) => (
                    <option key={cat.id} value={cat.id}>
                      {cat.name} ({cat.code})
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Amount & Officer ID */}
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div>
                <label className="block text-sm font-bold text-slate-700">Fine Amount</label>
                <input
                  type="text"
                  value={amount !== '' ? formatCurrency(amount) : ''}
                  placeholder="Auto-filled from category"
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-slate-500 outline-none"
                  readOnly
                  disabled
                />
              </div>
              <div>
                <label className="block text-sm font-bold text-slate-700">Officer ID</label>
                <input
                  type="number"
                  value={officerId}
                  onChange={(e) => setOfficerId(e.target.value !== '' ? parseInt(e.target.value, 10) : '')}
                  placeholder="e.g. 1"
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-slate-950 placeholder-slate-400 outline-none transition focus:border-blue-500 focus:ring-4 focus:ring-blue-500/12"
                  required
                />
              </div>
            </div>

            {/* Submit Button */}
            <div className="pt-4">
              <button
                type="submit"
                disabled={submitLoading}
                className="flex w-full items-center justify-center rounded-2xl bg-gradient-to-r from-slate-900 to-blue-700 py-4 text-sm font-bold text-white transition hover:-translate-y-px hover:shadow-lg focus:outline-none focus:ring-4 focus:ring-blue-500/20 disabled:opacity-50"
              >
                {submitLoading ? (
                  <RefreshCw className="h-5 w-5 animate-spin" />
                ) : (
                  'Submit and Generate Fine Sheet'
                )}
              </button>
            </div>
          </form>
        </div>
      </main>

      {/* Success Popup Modal */}
      {successResponse && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/40 backdrop-blur-sm p-4">
          <div className="w-full max-w-md rounded-3xl border border-slate-200/60 bg-white p-8 shadow-2xl animate-in fade-in zoom-in-95 duration-200">
            <div className="flex flex-col items-center text-center">
              <CheckCircle className="h-16 w-16 text-emerald-500 mb-4" />
              <h2 className="text-2xl font-black text-slate-900 tracking-tight">Fine Issued Successfully</h2>
              <p className="mt-2 text-sm text-slate-500">A new traffic fine record has been registered in the database.</p>

              {/* Reference Number Box */}
              <div className="mt-6 w-full rounded-2xl border border-emerald-100 bg-emerald-50/50 p-6 flex flex-col items-center">
                <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">Reference Number</span>
                <span className="mt-2 text-3xl font-mono font-black text-slate-800 select-all tracking-wider">
                  {successResponse.referenceNumber}
                </span>
                <button
                  onClick={handleCopy}
                  className="mt-4 flex items-center space-x-2 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-xs font-bold text-slate-600 hover:bg-slate-50 active:scale-95 transition"
                >
                  <Copy className="h-3.5 w-3.5" />
                  <span>Copy Reference</span>
                </button>
              </div>

              {/* Summary of Fine */}
              <div className="mt-6 w-full text-left space-y-2 border-t border-slate-100 pt-4 text-xs font-semibold text-slate-500">
                <div className="flex justify-between">
                  <span>Category Code:</span>
                  <span className="text-slate-800">{successResponse.category}</span>
                </div>
                <div className="flex justify-between">
                  <span>Amount:</span>
                  <span className="text-slate-800">{formatCurrency(successResponse.amount)}</span>
                </div>
                <div className="flex justify-between">
                  <span>Status:</span>
                  <span className="text-emerald-600 uppercase font-black">{successResponse.status}</span>
                </div>
              </div>

              {/* Close Button */}
              <button
                onClick={() => setSuccessResponse(null)}
                className="mt-8 w-full rounded-2xl bg-slate-900 py-3.5 text-sm font-bold text-white hover:bg-slate-800 transition active:scale-95"
              >
                Close and Return
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
