import axios from 'axios';
import type { FineLookupResponse, PaymentRequest, PaymentResponse, PaymentInitiateRequest, PayHerePaymentRequest } from './types';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
});

export async function lookupFine(referenceNumber: string, categoryCode: string) {
  const response = await api.get<FineLookupResponse>('/fines/lookup', {
    params: { referenceNumber, categoryCode },
  });

  return response.data;
}

export async function submitPayment(payload: PaymentRequest) {
  const response = await api.post<PaymentResponse>('/payments', payload);

  return response.data;
}

export async function initiatePayment(payload: PaymentInitiateRequest) {
  const response = await api.post<PayHerePaymentRequest>('/payments/initiate', payload);
  return response.data;
}