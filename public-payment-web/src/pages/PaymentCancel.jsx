import React from 'react';
import { AlertCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const PaymentCancel = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center py-12 px-4 font-sans justify-center animate-in fade-in duration-300">
      <div className="max-w-md w-full">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-slate-800">Online Fine Payment</h1>
          <p className="text-gray-500">Sri Lanka Police Department Digital Services</p>
        </div>

        {/* Cancel Card */}
        <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 text-center">
          <div className="inline-flex p-4 bg-amber-50 text-amber-500 rounded-full mb-4">
            <AlertCircle size={48} />
          </div>
          <h2 className="text-2xl font-bold text-slate-800">Payment Cancelled</h2>
          <p className="text-gray-500 mt-2">
            You can try again.
          </p>
          <button 
            onClick={() => navigate('/')}
            className="mt-8 w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded-xl transition duration-150 shadow-sm"
          >
            Try Again
          </button>
        </div>
      </div>
    </div>
  );
};

export default PaymentCancel;
