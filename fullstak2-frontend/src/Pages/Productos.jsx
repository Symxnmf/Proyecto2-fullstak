import React, { useEffect, useState } from "react";
import { obtenerProductos } from "../api/apiProductos";
import { productosData } from "../Data/productosData";
import { cargarProductosIniciales } from "../scripts/cargarProductos";
import ProductoCard from "../components/ProductoCard";
import { useCarrito } from "../context/CarritoContext";
import "./Productos.css";

export default function Productos() {
  const [productos, setProductos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState("");
  const [categoriaActiva, setCategoriaActiva] = useState("Todas");
  const { agregar } = useCarrito();

  useEffect(() => {
    async function cargar() {
      setCargando(true);
      setError("");
      try {
        let data = await obtenerProductos();
        
        // Si el backend está vacío, cargar productos iniciales
        if (!Array.isArray(data) || data.length === 0) {
          console.log("Backend vacío, intentando cargar productos iniciales...");
          data = await cargarProductosIniciales();
        }
        
        setProductos(Array.isArray(data) && data.length > 0 ? data : productosData);
      } catch (e) {
        console.error("Error cargando productos desde backend", e);
        setProductos(productosData);
      } finally {
        setCargando(false);
      }
    }
    cargar();
  }, []);

  // Obtener categorías únicas
  const categorias = ["Todas", ...new Set(productos.map(p => p.categoria))];

  // Filtrar productos por categoría
  const productosFiltrados = categoriaActiva === "Todas" 
    ? productos 
    : productos.filter(p => p.categoria === categoriaActiva);

  return (
    <div className="productos-page">
      <div className="productos-header">
        <h1 className="productos-titulo">🍰 Nuestros Productos 🍰</h1>
        <p className="productos-subtitulo">Delicias artesanales hechas con amor</p>
      </div>

      {cargando && (
        <div className="no-productos"><p>Cargando productos...</p></div>
      )}
      {error && !cargando && (
        <div className="no-productos"><p>{error}</p></div>
      )}

      {/* Filtro de categorías */}
      <div className="categorias-filtro">
        {categorias.map(cat => (
          <button
            key={cat}
            className={`filtro-btn ${categoriaActiva === cat ? 'activo' : ''}`}
            onClick={() => setCategoriaActiva(cat)}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Grid de productos */}
      <div className="productos-grid">
        {productosFiltrados.map(p => (
          <ProductoCard key={p.id} producto={p} onAgregar={agregar} />
        ))}
      </div>

      {productosFiltrados.length === 0 && !cargando && (
        <div className="no-productos">
          <p>No hay productos en esta categoría 😔</p>
        </div>
      )}
    </div>
  );
}
