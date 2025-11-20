import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { useAuth } from "./AuthContext";
import { obtenerCarrito, syncCarrito } from "../api/apiCarrito";

const CarritoContext = createContext();

export function CarritoProvider({ children }) {
  const [carrito, setCarrito] = useState([]);
  const { user, token } = useAuth();

  // Cargar carrito persistente al iniciar sesión
  useEffect(() => {
    (async () => {
      if (token) {
        try {
          const items = await obtenerCarrito();
          // Expandir items a lista local (un producto por cantidad)
          const expanded = [];
          for (const it of items || []) {
            for (let i = 0; i < (it.cantidad || 1); i++) {
              expanded.push(it.producto);
            }
          }
          setCarrito(expanded);
        } catch (_) {
          // ignorar
        }
      }
    })();
  }, [token]);

  // Sincronizar backend tras cambios (si hay sesión)
  useEffect(() => {
    if (!token) return;
    const t = setTimeout(async () => {
      try {
        const aggregated = Object.values(
          carrito.reduce((acc, p) => {
            const key = p.id;
            if (!acc[key]) acc[key] = { productoId: p.id, cantidad: 0 };
            acc[key].cantidad += 1;
            return acc;
          }, {})
        );
        await syncCarrito(aggregated);
      } catch (_) {}
    }, 200);
    return () => clearTimeout(t);
  }, [carrito, token]);

  const agregar = (producto) => {
    setCarrito((prev) => [...prev, producto]);
  };

  const eliminar = (index) => {
    setCarrito((prev) => prev.filter((_, i) => i !== index));
  };

  const vaciar = () => setCarrito([]);

  const total = useMemo(() => carrito.reduce((s, p) => s + (p.precio || 0), 0), [carrito]);

  return (
    <CarritoContext.Provider value={{ carrito, agregar, eliminar, vaciar, total }}>
      {children}
    </CarritoContext.Provider>
  );
}

export const useCarrito = () => useContext(CarritoContext);
