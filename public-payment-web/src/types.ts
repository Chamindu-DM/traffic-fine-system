export type FineLookupResponse = {
  referenceNumber: string;
  categoryCode: string;
  category: string;
  amount: number;
  district: string;
  officer: string;
  status: string;
  issuedAt: string;
  paidAt: string | null;
};

export type PaymentRequest = {
  referenceNumber: string;
  categoryCode: string;
  paymentMethod: string;
  cardLastFourDigits: string;
};

export type PaymentResponse = {
  paymentReference: string;
  referenceNumber: string;
  amount: number;
  status: string;
  paidAt: string;
  message: string;
};

export type PaymentInitiateRequest = {
  referenceNumber: string;
  categoryCode: string;
};

export type PayHerePaymentRequest = {
  sandbox: boolean;
  merchant_id: string;
  return_url: string;
  cancel_url: string;
  notify_url: string;
  order_id: string;
  items: string;
  amount: string;
  currency: string;
  hash: string;
  first_name: string;
  last_name: string;
  email: string;
  phone: string;
  address: string;
  city: string;
  country: string;
};