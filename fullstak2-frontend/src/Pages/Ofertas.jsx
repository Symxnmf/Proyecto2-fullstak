import React, { useEffect, useState } from "react";
import { obtenerOfertas } from "../api/apiProductos";
import ProductoCard from "../components/ProductoCard";
import { useCarrito } from "../context/CarritoContext";
import "./Productos.css";
import { productosData } from "../Data/productosData";

export default function Ofertas() {
  const [ofertas, setOfertas] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState("");
  const { agregar } = useCarrito();

  useEffect(() => {
    async function cargar() {
      setCargando(true);
      setError("");
      try {
        const datos = await obtenerOfertas();
        const lista = Array.isArray(datos) && datos.length > 0 ? datos : productosData.filter(p => p.oferta);
        setOfertas(lista);
      } catch (e) {
        console.error("Error cargando ofertas desde backend, usando locales", e);
        setOfertas(productosData.filter(p => p.oferta));
      } finally {
        setCargando(false);
      }
    }
    cargar();
  }, []);

  return (
    <div className="productos-page">
      <div className="productos-header">
        <h1 className="productos-titulo">🎉 Ofertas Dulces 🎉</h1>
        <p className="productos-subtitulo">Aprovecha nuestros productos con descuento</p>
      </div>

      <div className="productos-grid">
        {ofertas.map(p => (
          <ProductoCard key={p.id} producto={p} onAgregar={agregar} />
        ))}
      </div>

      {cargando && (
        <div className="no-productos"><p>Cargando ofertas...</p></div>
      )}
      {error && !cargando && (
        <div className="no-productos"><p>{error}</p></div>
      )}

      {ofertas.length === 0 && !cargando && !error && (
        <div className="no-productos">
          <p>No hay ofertas por ahora. Vuelve pronto 😋</p>
        </div>
      )}
    </div>
  );
}
