import React from "react";
import { Link } from "react-router-dom";
import "./Footer.css";

export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-seccion">
          <h3>Dulce Sabor</h3>
          <p>
            Pastelería artesanal dedicada a endulzar tus momentos especiales.
            <br />
            ¡Visítanos o contáctanos para tus pedidos!
          </p>
        </div>

        <div className="footer-seccion">
          <h3>Enlaces rápidos</h3>
          <ul>
            <li><Link to="/">Home 🏠</Link></li>
            <li><Link to="/productos">Productos 🎂</Link></li>
            <li><Link to="/categorias">Categorías 📋</Link></li>
            <li><Link to="/ofertas">Ofertas 💰</Link></li>
            <li><Link to="/admin">Admin 👈</Link></li>
          </ul>
        </div>

        <div className="footer-seccion">
          <h3>Contáctanos</h3>
          <p>Email: info@dulcesabor.cl</p>
          <p>Teléfono: +56 9 1234 5678</p>
          <p>Dirección: Calle Dulce 123, Santiago, Chile</p>
        </div>

        <div className="footer-seccion">
          <h3>Síguenos</h3>
          <div className="redes">
            <a href="https://facebook.com" target="_blank" rel="noopener noreferrer" className="red">Facebook 📘</a>
            <a href="https://instagram.com" target="_blank" rel="noopener noreferrer" className="red">Instagram 📸</a>
            <a href="https://tiktok.com" target="_blank" rel="noopener noreferrer" className="red">TikTok 🎵</a>
          </div>
        </div>
      </div>

      <div className="footer-bottom">
        <p>&copy; {new Date().getFullYear()} Pastelería Dulce Sabor. Todos los derechos reservados.</p>
      </div>
    </footer>
  );
}
