import api from "../lib/api";

export const obtenerUsuarios = async () => {
  const res = await api.get("/usuarios");
  return res.data;
};

export const obtenerUsuario = async (id) => {
  const res = await api.get(`/usuarios/${id}`);
  return res.data;
};

export const crearUsuario = async (usuario) => {
  const res = await api.post("/usuarios", usuario);
  return res.data;
};

export const actualizarUsuario = async (id, usuario) => {
  const res = await api.put(`/usuarios/${id}`, usuario);
  return res.data;
};

export const eliminarUsuario = async (id) => {
  const res = await api.delete(`/usuarios/${id}`);
  return res.data;
};
