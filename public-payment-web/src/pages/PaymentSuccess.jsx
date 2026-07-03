import React, { useState } from 'react';
import { CheckCircle, AlertCircle, RefreshCw } from 'lucide-react';
import { useNavigate, useSearchParams } from 'react-router-dom';

const PaymentSuccess = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const orderId = searchParams.get('order_id');

  const [simulating, setSimulating] = useState(false);
  const [simulated, setSimulated] = useState(false);
  const [simError, setSimError] = useState('');

  const handleSimulateWebhook = async () => {
    if (!orderId) return;
    setSimulating(true);
    setSimError('');
    try {
      const response = await fetch(`/api/payments/simulate-webhook?referenceNumber=${orderId}`, {
        method: 'POST'
      });
      if (response.ok) {
        setSimulated(true);
      } else {
        setSimError('Failed to simulate webhook. Make sure the backend is running.');
      }
    } catch (err) {
      setSimError('Connection error. Failed to reach simulation endpoint.');
    } finally {
      setSimulating(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center py-12 px-4 font-sans justify-center animate-in fade-in duration-300">
      <div className="max-w-md w-full">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-slate-800">Online Fine Payment</h1>
          <p className="text-gray-500">Sri Lanka Police Department Digital Services</p>
        </div>

        {/* Success Card */}
        <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 text-center">
          <div className="inline-flex p-4 bg-green-50 text-green-600 rounded-full mb-4">
            <CheckCircle size={48} />
          </div>
          <h2 className="text-2xl font-bold text-slate-800">Checkout Complete!</h2>
          <p className="text-gray-500 mt-2">
            Your payment checkout via PayHere is complete.
          </p>

          {/* Sandbox Local Dev Simulation Box */}
          {orderId && (
            <div className="mt-6 p-4 bg-amber-50 rounded-xl border border-amber-100 text-left space-y-3">
              <h3 className="text-xs font-bold text-amber-800 uppercase tracking-wider">Local Dev Webhook Simulator</h3>
              <p className="text-xs text-amber-700">
                Since PayHere Sandbox cannot send webhook notifications to <code>localhost</code> directly, use this button to simulate a signature-verified webhook.
              </p>
              {simError && (
                <div className="text-xs text-red-600 font-semibold flex items-center gap-1">
                  <AlertCircle size={12} /> {simError}
                </div>
              )}
              {simulated ? (
                <div className="text-xs text-green-700 font-semibold flex items-center gap-1">
                  <CheckCircle size={12} /> Webhook simulated! Fine marked as PAID.
                </div>
              ) : (
                <button
                  type="button"
                  disabled={simulating}
                  onClick={handleSimulateWebhook}
                  className="w-full bg-amber-600 hover:bg-amber-700 text-white font-bold py-2 px-3 rounded-lg text-xs flex items-center justify-center gap-2 transition disabled:opacity-50"
                >
                  {simulating ? (
                    <>
                      <RefreshCw size={12} className="animate-spin" /> Simulating...
                    </>
                  ) : (
                    'Simulate Webhook Callback'
                  )}
                </button>
              )}
            </div>
          )}

          <button 
            onClick={() => navigate('/')}
            className="mt-6 w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded-xl transition duration-150 shadow-sm"
          >
            Go Back Home
          </button>
        </div>
      </div>
    </div>
  );
};

export default PaymentSuccess;
