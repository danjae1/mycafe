import axios from "axios";

axios.defaults.withCredentials = true;

const api = axios.create({
  baseURL: "/api",
  withCredentials: true,
});

// 요청 인터셉터 - accessToken 추가
api.interceptors.request.use(
  (config) => {
    let accessToken = localStorage.getItem("accessToken");
    if (accessToken) {
      accessToken = accessToken.trim();
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터 - 토큰 만료시 alert 후 로그아웃
api.interceptors.response.use(
  response => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const res = await axios.post("/api/refresh", {}, { withCredentials: true });
        const newAccessToken = res.data.accessToken;
        if (!newAccessToken) throw new Error("Refresh API에서 토큰을 받지 못함");

        localStorage.setItem("accessToken", newAccessToken);
        api.defaults.headers.common["Authorization"] = `Bearer ${newAccessToken}`;
        originalRequest.headers["Authorization"] = `Bearer ${newAccessToken}`;

        return api({ ...originalRequest, withCredentials: true });
      } catch (refreshError) {
        localStorage.removeItem("accessToken");
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
