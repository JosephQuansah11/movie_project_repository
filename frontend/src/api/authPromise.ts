import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';

const axiosInstance = axios.create();
const mock = new MockAdapter(axiosInstance);



mock.onPost('/api/auth/logout').reply(async (config) => {
  return Promise.resolve({
    data: { message: 'Logged out successfully' },
    status: 200,
    statusText: 'OK',
    headers: { 'Content-Type': 'application/json' },
    config,
  });
});

// Export the mocked Axios instance
export default axiosInstance;
