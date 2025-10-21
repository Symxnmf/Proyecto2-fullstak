import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { obtenerProductos } from "../api/apiProductos";
import { productosData } from "../Data/productosData";
import ProductoCard from "../components/ProductoCard";
import { useCarrito } from "../context/CarritoContext";
import "./Inicio.css";

export default function Inicio() {
  return (
    <div className="inicio-container">
      <div className="hero-section">
        <div className="hero-content">
          <div className="cake-icon">🍰</div>
          <h1 className="hero-title">Bienvenidos a Dulce Sabor</h1>
          <p className="hero-subtitle">
            ¡Endulzamos tus momentos especiales!
          </p>
          <p className="hero-description">
            Somos <strong>Dulce Sabor</strong>, una pastelería artesanal dedicada a crear los postres más
            deliciosos y hermosos para ti.
          </p>
          <p className="hero-description">
            Explora nuestras secciones para descubrir nuestros productos, contáctanos o
            regístrate para hacer pedidos.
          </p>
          <div className="button-group">
            <Link to="/productos" className="btn-dulce btn-primary-dulce">
              Ver Productos
            </Link>
            <Link to="/categorias" className="btn-dulce btn-secondary-dulce">
              Categorías
            </Link>
            <Link to="/ofertas" className="btn-dulce btn-secondary-dulce">
              Ofertas
            </Link>
            <Link to="/checkout" className="btn-dulce btn-secondary-dulce">
              Contacto
            </Link>
          </div>
        </div>
      </div>

    </div>
  );
}
