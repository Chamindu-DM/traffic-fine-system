import React, { useState } from 'react';
import { Search, CreditCard, CheckCircle, AlertCircle, MapPin } from 'lucide-react';

const PaymentPortal = () => {
  const [step, setStep] = useState(1); // 1: Search, 2: Details/Pay, 3: Success
  const [refNumber, setRefNumber] = useState('');
  const [category, setCategory] = useState('');

  // Mock search result
  const mockFine = {
    ref: 'TF123456',
    category: 'Speeding',
    amount: 5000,
    district: 'Matara',
    status: 'UNPAID'
  };

  const handleSearch = (e) => {
    e.preventDefault();
    // In real implementation, you would call /api/fines/lookup
    setStep(2);
  };

  const handlePayment = (e) => {
    e.preventDefault();
    // Simulate payment processing and SMS trigger[cite: 3]
    setStep(3);
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center py-12 px-4 font-sans">
      <div className="max-w-md w-full">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-slate-800">Online Fine Payment</h1>
          <p className="text-gray-500">Sri Lanka Police Department Digital Services[cite: 3]</p>
        </div>

        {/* Step 1: Lookup[cite: 3] */}
        {step === 1 && (
          <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100">
            <form onSubmit={handleSearch} className="space-y-5">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Fine Reference Number</label>
                <input 
                  type="text" required placeholder="e.g. TF123456"
                  className="w-full px-4 py-2.5 border rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
                  onChange={(e) => setRefNumber(e.target.value)}
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Category Code</label>
                <input 
                  type="text" required placeholder="e.g. SPEEDING"
                  className="w-full px-4 py-2.5 border rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
                  onChange={(e) => setCategory(e.target.value)}
                />
              </div>
              <button className="w-full bg-blue-600 text-white font-bold py-3 rounded-xl flex items-center justify-center gap-2 hover:bg-blue-700 transition">
                <Search size={20}/> Find My Fine
              </button>
            </form>
          </div>
        )}

        {/* Step 2: Payment[cite: 3] */}
        {step === 2 && (
          <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 space-y-6">
            <div className="pb-4 border-b">
              <h2 className="font-bold text-lg text-slate-800">Fine Details</h2>
              <div className="mt-2 space-y-1 text-sm text-gray-600">
                <p>Ref: <span className="font-mono font-bold text-slate-900">{mockFine.ref}</span></p>
                <p>Violation: {mockFine.category}</p>
                <p className="flex items-center gap-1"><MapPin size={14}/> {mockFine.district}</p>
              </div>
              <div className="mt-4 p-3 bg-blue-50 rounded-lg flex justify-between items-center">
                <span className="text-blue-700 font-semibold">Total Amount</span>
                <span className="text-xl font-bold text-blue-800 text-slate-900">LKR {mockFine.amount}</span>
              </div>
            </div>

            <form onSubmit={handlePayment} className="space-y-4">
              <h3 className="text-sm font-bold text-gray-400 uppercase tracking-wider">Payment Information</h3>
              <input type="text" placeholder="Cardholder Name" className="w-full px-4 py-2.5 border rounded-xl outline-none" required />
              <input type="text" placeholder="Card Number" className="w-full px-4 py-2.5 border rounded-xl outline-none" required />
              <button className="w-full bg-green-600 text-white font-bold py-3 rounded-xl flex items-center justify-center gap-2 hover:bg-green-700 transition">
                <CreditCard size={20}/> Pay Now
              </button>
            </form>
          </div>
        )}

        {/* Step 3: Success[cite: 3] */}
        {step === 3 && (
          <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 text-center animate-in zoom-in duration-300">
            <div className="inline-flex p-4 bg-green-50 text-green-600 rounded-full mb-4">
              <CheckCircle size={48} />
            </div>
            <h2 className="text-2xl font-bold text-slate-800">Payment Successful!</h2>
            <p className="text-gray-500 mt-2">
              An SMS has been sent to the traffic officer. You may now retrieve your license[cite: 3].
            </p>
            <button 
              onClick={() => setStep(1)}
              className="mt-8 text-blue-600 font-semibold hover:underline"
            >
              Return Home
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default PaymentPortal;