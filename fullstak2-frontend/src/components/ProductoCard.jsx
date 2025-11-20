import React, { useState } from "react";
import "./ProductoCard.css";
import { useAuth } from "../context/AuthContext";

export default function ProductoCard({ producto, onAgregar }) {
  const { user } = useAuth();
  const [mensaje, setMensaje] = useState(null);
  const formatPrecio = (precio) => {
    return new Intl.NumberFormat('es-CL', {
      style: 'currency',
      currency: 'CLP'
    }).format(precio);
  };

  return (
    <div className="producto-card">
      {mensaje && (
        <div className={`alert alert-${mensaje.tipo} alert-dismissible fade show position-absolute top-0 start-50 translate-middle-x mt-2`} 
             style={{ zIndex: 1000, maxWidth: '90%' }}
             role="alert">
          {mensaje.texto}
          <button type="button" className="btn-close" onClick={() => setMensaje(null)}></button>
        </div>
      )}
      
      {producto.oferta && (
        <div className="oferta-badge">
          🎉 ¡Oferta!
        </div>
      )}
      
      <div className="producto-imagen-container">
        <img 
          src={producto.imagen || "/assets/placeholder.jpg"} 
          alt={producto.nombre}
          className="producto-imagen"
          onError={(e) => {
            e.target.src = "https://via.placeholder.com/300x200/FFB6C1/FFFFFF?text=Dulce+Sabor";
          }}
        />
      </div>

      <div className="producto-info">
        <div className="producto-categoria">{producto.categoria}</div>
        <h3 className="producto-nombre">{producto.nombre}</h3>
        <p className="producto-descripcion">{producto.descripcion}</p>
        
        <div className="producto-footer">
          <div className="producto-precio">
            <span className="precio-value">{formatPrecio(producto.precio)}</span>
            {producto.stock > 0 && producto.stock <= 5 && (
              <span className="stock-bajo">¡Últimas {producto.stock} unidades!</span>
            )}
          </div>
          
          <button
            className="btn-agregar-carrito"
            onClick={() => {
              if (!user) {
                setMensaje({ tipo: "warning", texto: "Debes iniciar sesión para agregar productos." });
                setTimeout(() => {
                  window.location.assign("/login");
                }, 2000);
                return;
              }
              onAgregar(producto);
              setMensaje({ tipo: "success", texto: "¡Producto agregado al carrito! 🛒" });
              setTimeout(() => setMensaje(null), 2000);
            }}
            disabled={producto.stock === 0}
          >
            {producto.stock === 0 ? '😔 Agotado' : (!user ? '🔒 Inicia sesión' : '🛒 Agregar')}
          </button>
        </div>
      </div>
    </div>
  );
}
