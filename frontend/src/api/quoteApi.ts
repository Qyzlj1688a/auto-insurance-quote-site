import axios from 'axios';
import { QuoteCreateRequest, QuoteResultResponse, AdminLoginRequest, AdminLoginResponse } from '../types';

const api = axios.create({
  headers: {
    'Content-Type': 'application/json',
  },
});

// 管理者ログイン用のJWTトークンが存在する場合、Authorizationヘッダーへ自動付与するリクエストインターセプター
api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('adminToken');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

export const createQuote = async (request: QuoteCreateRequest): Promise<QuoteResultResponse> => {
  const response = await api.post<QuoteResultResponse>('/api/quotes', request);
  return response.data;
};

export const getQuoteByQuoteNo = async (quoteNo: string): Promise<QuoteResultResponse> => {
  const response = await api.get<QuoteResultResponse>(`/api/quotes/${quoteNo}`);
  return response.data;
};

export const adminLogin = async (request: AdminLoginRequest): Promise<AdminLoginResponse> => {
  const response = await api.post<AdminLoginResponse>('/api/admin/login', request);
  return response.data;
};

export const searchAdminQuotes = async (
  quoteNo: string,
  createDateFrom: string,
  createDateTo: string
): Promise<QuoteResultResponse[]> => {
  const response = await api.get<QuoteResultResponse[]>('/api/admin/quotes', {
    params: { quoteNo, createDateFrom, createDateTo },
  });
  return response.data;
};

export const exportQuotesCsvBlob = async (
  quoteNo: string,
  createDateFrom: string,
  createDateTo: string
): Promise<Blob> => {
  const response = await api.get('/api/admin/quotes.csv', {
    params: { quoteNo, createDateFrom, createDateTo },
    responseType: 'blob',
  });
  return response.data;
};

