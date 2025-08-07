import axios from 'axios';

const http = axios.create({
  baseURL: import.meta.env.VITE_REMOTE_API + '/api/orders',
});

// Add request interceptor for auth token
http.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// Add response interceptor for error handling
http.interceptors.response.use(response => response, error => {
  if (error.response && error.response.status === 401) {
    localStorage.removeItem('token');
    window.location.href = '/login';
  }
  return Promise.reject(error);
});

export default {
  getAll() {
    return http.get('');
  },
  
  getByNumber(orderNumber) {
    return http.get(`/number/${orderNumber}`);
  },
  
  getByTrailer(trailerNumber) {
    return http.get(`/trailer/${trailerNumber}`);
  },
  
  getByCustomer(customerName) {
    return http.get(`/customer/${customerName}`);
  },
  
  getByShipper(shipperName) {
    return http.get(`/shipper/${shipperName}`);
  },
  
  getByStatus(status) {
    return http.get(`/status/${status}`);
  },
  
  create(order) {
    return http.post('', order);
  },
  
  update(orderNumber, order) {
    return http.put(`/${orderNumber}`, order);
  },
  
  delete(orderNumber) {
    return http.delete(`/${orderNumber}`);
  }
};