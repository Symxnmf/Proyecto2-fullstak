import React from "react";
import { Link } from "react-router-dom";

export default function CompraFallida() {
  return (
    <div className="text-center">
      <h2>Pago no realizado</h2>
      <p>Hubo un problema con el pago. Intenta nuevamente o contacta a soporte.</p>
      <Link to="/checkout" className="btn btn-primary">Reintentar</Link>
    </div>
  );
}
