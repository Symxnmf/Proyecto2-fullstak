import React, { createContext, useContext, useState } from "react";

const CarritoContext = createContext();

export function CarritoProvider({ children }) {
  const [carrito, setCarrito] = useState([]);

  const agregar = (producto) => {
    setCarrito((prev) => [...prev, producto]);
  };

  const eliminar = (index) => {
    setCarrito((prev) => prev.filter((_, i) => i !== index));
  };

  const vaciar = () => setCarrito([]);

  const total = carrito.reduce((s, p) => s + (p.precio || 0), 0);

  return (
    <CarritoContext.Provider value={{ carrito, agregar, eliminar, vaciar, total }}>
      {children}
    </CarritoContext.Provider>
  );
}

export const useCarrito = () => useContext(CarritoContext);
