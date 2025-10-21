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
    if (err?.response?.status === 401) {
      localStorage.removeItem("auth");
      // opcional: podemos redirigir a /login si no estamos ya en login
      // location.pathname !== "/login" && (location.href = "/login");
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
