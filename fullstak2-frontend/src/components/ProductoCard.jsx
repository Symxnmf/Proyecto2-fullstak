import React from "react";
import "./ProductoCard.css";

export default function ProductoCard({ producto, onAgregar }) {
  const formatPrecio = (precio) => {
    return new Intl.NumberFormat('es-CL', {
      style: 'currency',
      currency: 'CLP'
    }).format(precio);
  };

  return (
    <div className="producto-card">
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
            onClick={() => onAgregar(producto)}
            disabled={producto.stock === 0}
          >
            {producto.stock === 0 ? '😔 Agotado' : '🛒 Agregar'}
          </button>
        </div>
      </div>
    </div>
  );
}
