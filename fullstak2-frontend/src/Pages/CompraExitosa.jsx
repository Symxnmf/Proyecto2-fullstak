import React from "react";
import { Link } from "react-router-dom";

export default function CompraExitosa() {
  return (
    <div className="text-center">
      <h2>¡Compra exitosa! 🎉</h2>
      <p>Tu pedido fue recibido y será preparado. Te enviaremos un correo con el detalle.</p>
      <Link to="/" className="btn btn-primary">Volver al inicio</Link>
    </div>
  );
}
