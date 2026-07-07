export interface QuoteCreateRequest {
  driverAge: number | '';
  licenseColor: string;
  usageType: string;
  annualMileage: number | '';
  driverRange: string;
  hasCurrentInsurance: boolean | null;
  grade: number | '';
  accidentTerm: number | '';
  maker: string;
  carName: string;
  firstRegistrationYearMonth: string;
  vehicleType: string;
  vehicleInsurance: boolean | null;
  propertyDamageLimit: string;
  personalInjuryAmount: string;
  lawyerOption: boolean | null;
  roadService: boolean | null;
}

export interface BreakdownResponse {
  itemCode: string;
  itemName: string;
  rate: number | null;
  amount: number | null;
  displayOrder: number;
}

export interface QuoteResultResponse extends QuoteCreateRequest {
  quoteNo: string;
  annualPremium: number;
  monthlyPremium: number;
  breakdowns: BreakdownResponse[];
  createdAt: string;
}

export interface AdminLoginRequest {
  loginId: string;
  password: string;
}

export interface AdminLoginResponse {
  loginId: string;
  displayName: string;
  message: string;
  token?: string;
}

