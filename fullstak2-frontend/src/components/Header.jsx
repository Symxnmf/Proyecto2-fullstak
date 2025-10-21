import React from "react";
import { Link } from "react-router-dom";
import { useCarrito } from "../context/CarritoContext";
import { useAuth } from "../context/AuthContext";
import "./Header.css";

export default function Header() {
  const { carrito } = useCarrito();
  const { user, logout } = useAuth();

  return (
    <header className="header-dulce">
      <div className="container">
        <Link className="brand-dulce" to="/">
          🍰 <span>Pastelería Dulce Sabor</span>
        </Link>
        <nav className="nav-dulce">
          <Link className="nav-link-dulce" to="/">Home</Link>
          <Link className="nav-link-dulce" to="/productos">Productos</Link>
          <Link className="nav-link-dulce" to="/categorias">Categorías</Link>
          <Link className="nav-link-dulce" to="/ofertas">Ofertas</Link>
          <Link className="nav-link-dulce carrito-badge" to="/checkout">
            🛒 Carrito <span className="badge-count">{carrito.length}</span>
          </Link>
          {user ? (
            <>
              <Link className="nav-link-dulce admin-link" to="/admin">Admin</Link>
              <button className="nav-link-dulce" onClick={logout} style={{ border: 'none' }}>Salir</button>
            </>
          ) : (
            <Link className="nav-link-dulce admin-link" to="/login">Iniciar sesión</Link>
          )}
        </nav>
      </div>
    </header>
  );
}
