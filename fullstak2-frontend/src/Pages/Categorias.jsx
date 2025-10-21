import React, { useEffect, useMemo, useState } from "react";
import { obtenerProductos, obtenerCategoriasStats } from "../api/apiProductos";
import { productosData } from "../Data/productosData";
import ProductoCard from "../components/ProductoCard";
import { useCarrito } from "../context/CarritoContext";
import "./Productos.css";

export default function Categorias() {
  const [todos, setTodos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState("");
  const [categoriaActiva, setCategoriaActiva] = useState("Todas");
  const [chips, setChips] = useState([{ nombre: "Todas", cantidad: 0 }]);
  const { agregar } = useCarrito();

  useEffect(() => {
    async function cargar() {
      setCargando(true);
      setError("");
      try {
        const [data, stats] = await Promise.all([
          obtenerProductos(),
          obtenerCategoriasStats(),
        ]);
        let lista = Array.isArray(data) ? data : [];
        if (lista.length === 0) lista = productosData; // fallback local
        setTodos(lista);
        const base = Array.isArray(stats) && stats.length > 0
          ? stats
          : Array.from(new Set(lista.map(p => p.categoria || "Sin categoría")))
              .map(nombre => ({ nombre, cantidad: lista.filter(p => (p.categoria || "Sin categoría") === nombre).length }));
        setChips([{ nombre: "Todas", cantidad: lista.length }, ...base]);
      } catch (e) {
        // fallback completo a datos locales
        const lista = productosData;
        setTodos(lista);
        const base = Array.from(new Set(lista.map(p => p.categoria || "Sin categoría")))
          .map(nombre => ({ nombre, cantidad: lista.filter(p => (p.categoria || "Sin categoría") === nombre).length }));
        setChips([{ nombre: "Todas", cantidad: lista.length }, ...base]);
      } finally {
        setCargando(false);
      }
    }
    cargar();
  }, []);

  const categorias = chips;

  const visibles = useMemo(() => (
    categoriaActiva === "Todas" ? todos : todos.filter(p => (p.categoria || "Sin categoría") === categoriaActiva)
  ), [categoriaActiva, todos]);

  return (
    <div className="productos-page">
      <div className="productos-header">
        <h1 className="productos-titulo">🗂️ Categorías</h1>
        <p className="productos-subtitulo">Explora nuestras delicias por categoría</p>
      </div>

      {cargando && (
        <div className="no-productos"><p>Cargando productos...</p></div>
      )}
      {error && !cargando && (
        <div className="no-productos"><p>{error}</p></div>
      )}

      <div className="categorias-filtro">
        {categorias.map(({ nombre, cantidad }) => (
          <button
            key={nombre}
            className={`filtro-btn ${categoriaActiva === nombre ? 'activo' : ''}`}
            onClick={() => setCategoriaActiva(nombre)}
          >
            {nombre} ({cantidad})
          </button>
        ))}
      </div>

      <div className="productos-grid">
        {visibles.map(p => (
          <ProductoCard key={p.id} producto={p} onAgregar={agregar} />
        ))}
      </div>

      {visibles.length === 0 && !cargando && (
        <div className="no-productos">
          <p>No hay productos en esta categoría 😔</p>
        </div>
      )}
    </div>
  );
}
