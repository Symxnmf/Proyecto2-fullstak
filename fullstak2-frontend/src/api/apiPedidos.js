import api from "../lib/api";

export const obtenerPedidosAdmin = async (filtros = {}) => {
  const params = new URLSearchParams();
  if (filtros.cliente) params.set("cliente", filtros.cliente);
  if (filtros.desde) params.set("desde", filtros.desde);
  if (filtros.hasta) params.set("hasta", filtros.hasta);
  const qs = params.toString();
  const url = qs ? `/pedidos/admin?${qs}` : "/pedidos/admin";
  const res = await api.get(url);
  return res.data;
};

export const obtenerMisPedidos = async () => {
  const res = await api.get("/pedidos/mios");
  return res.data;
};

export const obtenerPedidos = async () => {
  const res = await api.get("/pedidos");
  return res.data;
};
