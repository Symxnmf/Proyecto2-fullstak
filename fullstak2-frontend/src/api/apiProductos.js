import api from "../lib/api";

// ---- Endpoints de Productos ----
export const obtenerProductos = async () => {
  const res = await api.get("/productos");
  return res.data;
};

export const crearProducto = async (producto) => {
  const res = await api.post("/productos", producto);
  return res.data;
};

export const actualizarProducto = async (id, producto) => {
  const res = await api.put(`/productos/${id}`, producto);
  return res.data;
};

export const eliminarProducto = async (id) => {
  const res = await api.delete(`/productos/${id}`);
  return res.data;
};

export const agregarProducto = crearProducto; // Alias

// Nuevos endpoints backend
export const obtenerOfertas = async () => {
  const res = await api.get("/productos/ofertas");
  return res.data;
};

export const obtenerCategoriasStats = async () => {
  const res = await api.get("/productos/categorias");
  // Debe devolver: [{ nombre, cantidad }]
  return res.data;
};

export const buscarProductos = async (nombre) => {
  const res = await api.get(`/productos/buscar?nombre=${encodeURIComponent(nombre)}`);
  return res.data;
};

export const obtenerStockBajo = async () => {
  const res = await api.get("/productos/stock-bajo");
  return res.data;
};
