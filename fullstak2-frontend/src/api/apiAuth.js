import api from "../lib/api";

export async function loginApi({ email, password }) {
  const res = await api.post("/auth/login", { email, password });
  const data = res.data || {};
  // Normalizar posibles formas de respuesta
  return {
    token: data.token || data.accessToken || data.jwt || null,
    user: data.user || { email: data.email || email, nombre: data.nombre || "Usuario" },
  };
}

export async function registerApi({ nombre, email, password }) {
  const res = await api.post("/auth/register", { nombre, email, password });
  return res.data;
}
