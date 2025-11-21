import api from "../lib/api";

export async function obtenerCarrito() {
  const res = await api.get("/carrito");
  return res.data; // [{id, usuario, producto:{...}, cantidad}]
}

export async function syncCarrito(items) {
  // items: [{ productoId, cantidad }]
  await api.post("/carrito/sync", { items });
}
