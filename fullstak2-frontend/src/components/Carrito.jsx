import React from "react";
import { useCarrito } from "../context/CarritoContext";
import { Link } from "react-router-dom";

export default function Carrito() {
  const { carrito, eliminar, total, vaciar } = useCarrito();

  if (!carrito.length) return <div className="carrito-panel"><p>El carrito está vacío.</p></div>;

  return (
    <div className="carrito-panel">
      <h4>Tu carrito</h4>
      <ul className="list-group mb-3">
        {carrito.map((p, i) => (
          <li key={i} className="list-group-item d-flex justify-content-between align-items-center">
            <div>
              <strong>{p.nombre}</strong><br />
              <small>${p.precio}</small>
            </div>
            <div>
              <button className="btn btn-sm btn-danger" onClick={() => eliminar(i)}>Eliminar</button>
            </div>
          </li>
        ))}
      </ul>
      <h5>Total: ${total}</h5>
      <div className="d-flex gap-2 mt-2">
        <Link to="/checkout" className="btn btn-success">Ir a pagar</Link>
        <button className="btn btn-outline-secondary" onClick={vaciar}>Vaciar</button>
      </div>
    </div>
  );
}
