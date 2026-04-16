import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:3000/api',
});


api.interceptors.request.use((config) => {
  const tenantId = localStorage.getItem('tenant_id');
  if (tenantId) {
    config.headers['x-tenant-id'] = tenantId;
  }
  return config;
}, (error) => Promise.reject(error));

export default api;
