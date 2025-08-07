import axios from 'axios';

const http = axios.create({
  baseURL: import.meta.env.VITE_REMOTE_API + '/api/customers',
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
  
  getById(id) {
    return http.get(`/${id}`);
  },
  
  search(query) {
    return http.get(`/search?query=${encodeURIComponent(query)}`);
  },
  
  create(customer) {
    return http.post('', customer);
  },
  
  update(id, customer) {
    return http.put(`/${id}`, customer);
  },
  
  delete(id) {
    return http.delete(`/${id}`);
  }
};