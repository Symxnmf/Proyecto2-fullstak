import axios from "axios";

// Axios instance centralizada
// En dev, usaremos el proxy de Vite: baseURL "/api"
// En prod, puedes cambiar a la URL real del backend si se sirve aparte
const api = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// Inyectar token si existe en localStorage
api.interceptors.request.use((config) => {
  try {
    const saved = localStorage.getItem("auth");
    if (saved) {
      const { token } = JSON.parse(saved);
      if (token) {
        config.headers = config.headers || {};
        config.headers["Authorization"] = `Bearer ${token}`;
      }
    }
  } catch (_) {}
  return config;
});

// Manejo básico de 401: limpiar sesión
api.interceptors.response.use(
  (resp) => resp,
  (err) => {
    const originalRequest = err.config || {};
    const status = err?.response?.status;
    if (status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const saved = localStorage.getItem("auth");
        const parsed = saved ? JSON.parse(saved) : null;
        const refreshToken = parsed?.refreshToken;
        if (refreshToken) {
          // Intentar refrescar access token
          return api
            .post("/auth/refresh", { refreshToken })
            .then((res) => {
              const newAccess = res?.data?.accessToken;
              if (newAccess) {
                const updated = { ...(parsed || {}), token: newAccess };
                localStorage.setItem("auth", JSON.stringify(updated));
                originalRequest.headers = originalRequest.headers || {};
                originalRequest.headers["Authorization"] = `Bearer ${newAccess}`;
                return api(originalRequest);
              }
              // Si no llegó nuevo token, limpiar sesión
              localStorage.removeItem("auth");
              return Promise.reject(err);
            })
            .catch((e) => {
              localStorage.removeItem("auth");
              return Promise.reject(e);
            });
        }
      } catch (_) {}
      // sin refresh token, limpiar sesión
      localStorage.removeItem("auth");
    }
    return Promise.reject(err);
  }
);

// ---- Endpoints de Productos ----
export const obtenerProductos = async () => {
  const res = await api.get("/productos");
  return res.data;
};

export const agregarProducto = async (producto) => {
  const res = await api.post("/productos", producto);
  return res.data;
};

export default api;
