import React from "react";
import { Link } from "react-router-dom";
import { useCarrito } from "../context/CarritoContext";
import { useAuth } from "../context/AuthContext";
import "./Header.css";

export default function Header() {
  const { carrito } = useCarrito();
  const { user, logout } = useAuth();
  const isAdmin = (user?.rol || "").toUpperCase() === "ADMIN";

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
              {/* Chip con usuario logueado */}
              <span className="nav-link-dulce" title={user.email || user.nombre} style={{ cursor: "default" }}>
                👤 {user.nombre || user.email}
              </span>
              {/* Link a Admin sólo si el rol es ADMIN */}
              {isAdmin && (
                <Link className="nav-link-dulce admin-link" to="/admin">Admin</Link>
              )}
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
