import api from "../lib/api";

export const obtenerPedidos = async () => {
  const res = await api.get("/pedidos");
  return res.data;
};
