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