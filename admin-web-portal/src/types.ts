export interface LoginResponse {
  token: string;
  role: string;
}

export interface CollectionBreakdownResponse {
  label: string;
  amount: number;
}

export interface AdminDashboardResponse {
  totalCollected: number;
  paidFineCount: number;
  unpaidFineCount: number;
  cancelledFineCount: number;
  districtWiseCollections: CollectionBreakdownResponse[];
  categoryWiseCollections: CollectionBreakdownResponse[];
}

export interface AdminFineResponse {
  referenceNumber: string;
  categoryCode: string;
  category: string;
  district: string;
  officer: string;
  driverLicenseNumber: string;
  vehicleNumber: string;
  amount: number;
  status: string;
  issuedAt: string;
  paidAt: string | null;
}
