import React, { useState, useEffect } from 'react';
import { Search, CreditCard, CheckCircle, AlertCircle, MapPin } from 'lucide-react';

const PaymentPortal = () => {
  const [step, setStep] = useState(1); // 1: Search, 2: Details/Pay, 3: Success
  const [refNumber, setRefNumber] = useState('');
  const [category, setCategory] = useState('');
  const [fine, setFine] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await fetch('/api/fine-categories');
        if (response.ok) {
          const data = await response.json();
          setCategories(data);
        } else {
          useFallbackCategories();
        }
      } catch (err) {
        console.error('Error fetching categories:', err);
        useFallbackCategories();
      }
    };

    const useFallbackCategories = () => {
      setCategories([
        { code: 'SPEEDING', name: 'Speeding' },
        { code: 'SIGNAL', name: 'Traffic Signal Violation' },
        { code: 'PARKING', name: 'Illegal Parking' },
        { code: 'DRUNK_DRIVING', name: 'Drunk Driving' },
        { code: 'NO_HELMET', name: 'No Helmet' },
        { code: 'OVERLOADING', name: 'Overloading' }
      ]);
    };

    fetchCategories();
  }, []);

  const [paymentInfo, setPaymentInfo] = useState({
    firstName: '',
    email: ''
  });

  const handleSearch = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const response = await fetch(`/api/fines/lookup?referenceNumber=${refNumber}&categoryCode=${category}`);
      if (response.ok) {
        const data = await response.json();
        if (data.status === 'PAID') {
          setError('This fine has already been paid.');
        } else {
          setFine(data);
          setStep(2);
        }
      } else {
        const text = await response.text();
        try {
          const err = JSON.parse(text);
          setError(err.message || 'Fine not found. Please check details.');
        } catch (e) {
          setError(`Server Error: ${response.status} ${response.statusText}`);
        }
      }
    } catch (err) {
      setError(`Connection error: ${err.message}. Please check if backend is running on port 8080.`);
    } finally {
      setLoading(false);
    }
  };

  const handlePayment = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const response = await fetch('/api/payments/initiate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          referenceNumber: refNumber,
          categoryCode: category
        })
      });

      if (response.ok) {
        const params = await response.json();

        // Override default values with user inputs
        params.first_name = paymentInfo.firstName.trim();
        params.email = paymentInfo.email.trim();
        params.last_name = "Name"; // required default

        // Determine the PayHere URL
        const payHereUrl = params.sandbox 
          ? 'https://sandbox.payhere.lk/pay/checkout' 
          : 'https://www.payhere.lk/pay/checkout';

        // Dynamically create a hidden HTML form
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = payHereUrl;

        // Populate hidden fields
        Object.entries(params).forEach(([key, val]) => {
          if (key !== 'sandbox') {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = key;
            input.value = String(val);
            form.appendChild(input);
          }
        });

        document.body.appendChild(form);
        form.submit();
      } else {
        const text = await response.text();
        try {
          const err = JSON.parse(text);
          setError(err.message || 'Payment initiation failed. Please try again.');
        } catch {
          setError('Payment initiation failed. Please try again.');
        }
      }
    } catch (err) {
      setError('Connection error. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center py-12 px-4 font-sans">
      <div className="max-w-md w-full">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-slate-800">Online Fine Payment</h1>
          <p className="text-gray-500">Sri Lanka Police Department Digital Services</p>
        </div>

        {/* Error Message */}
        {error && (
          <div className="mb-4 p-3 bg-red-50 text-red-600 rounded-lg text-sm border border-red-100 flex items-center gap-2">
            <AlertCircle size={16}/> {error}
          </div>
        )}

        {/* Step 1: Lookup */}
        {step === 1 && (
          <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100">
            <form onSubmit={handleSearch} className="space-y-5">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Fine Reference Number</label>
                <input 
                  type="text" required placeholder="e.g. TF123456"
                  className="w-full px-4 py-2.5 border rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
                  value={refNumber}
                  onChange={(e) => setRefNumber(e.target.value)}
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Fine Category</label>
                <select
                  required
                  className="w-full px-4 py-2.5 border rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-white"
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                >
                  <option value="">Select Category</option>
                  {categories.map((cat) => (
                    <option key={cat.code} value={cat.code}>
                      {cat.name}
                    </option>
                  ))}
                </select>
              </div>
              <button 
                disabled={loading}
                className="w-full bg-blue-600 text-white font-bold py-3 rounded-xl flex items-center justify-center gap-2 hover:bg-blue-700 transition disabled:opacity-50"
              >
                {loading ? 'Searching...' : <><Search size={20}/> Find My Fine</>}
              </button>
            </form>
          </div>
        )}

        {/* Step 2: Payment */}
        {step === 2 && fine && (
          <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 space-y-6">
            <div className="pb-4 border-b">
              <h2 className="font-bold text-lg text-slate-800">Fine Details</h2>
              <div className="mt-2 space-y-1 text-sm text-gray-600">
                <p>Ref: <span className="font-mono font-bold text-slate-900">{fine.referenceNumber}</span></p>
                <p>Violation: {fine.category}</p>
                <p className="flex items-center gap-1"><MapPin size={14}/> {fine.district}</p>
              </div>
              <div className="mt-4 p-3 bg-blue-50 rounded-lg flex justify-between items-center">
                <span className="text-blue-700 font-semibold">Total Amount</span>
                <span className="text-xl font-bold text-blue-800 text-slate-900">LKR {fine.amount.toLocaleString()}</span>
              </div>
            </div>

            <form onSubmit={handlePayment} className="space-y-4">
              <h3 className="text-sm font-bold text-gray-400 uppercase tracking-wider">Payment Information</h3>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1">First Name</label>
                <input 
                  type="text" placeholder="e.g. John" 
                  className="w-full px-4 py-2.5 border rounded-xl outline-none focus:ring-2 focus:ring-blue-500" required 
                  value={paymentInfo.firstName}
                  onChange={(e) => setPaymentInfo({...paymentInfo, firstName: e.target.value})}
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1">Email</label>
                <input 
                  type="email" placeholder="e.g. john@example.com" 
                  className="w-full px-4 py-2.5 border rounded-xl outline-none focus:ring-2 focus:ring-blue-500" required 
                  value={paymentInfo.email}
                  onChange={(e) => setPaymentInfo({...paymentInfo, email: e.target.value})}
                />
              </div>
              <button 
                disabled={loading}
                className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded-xl flex items-center justify-center gap-2 transition disabled:opacity-50 shadow-sm"
              >
                {loading ? 'Processing...' : <><CreditCard size={20}/> Pay with PayHere</>}
              </button>
              <button 
                type="button"
                onClick={() => setStep(1)}
                className="w-full text-sm text-gray-400 hover:text-gray-600"
              >
                Cancel and Search Again
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  );
};

export default PaymentPortal;