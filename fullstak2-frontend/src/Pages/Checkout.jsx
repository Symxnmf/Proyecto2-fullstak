import React, { useState } from "react";
import { useCarrito } from "../context/CarritoContext";
import { useNavigate } from "react-router-dom";
import api from "../lib/api";

export default function Checkout() {
  const { carrito, total, vaciar } = useCarrito();
  const [nombre, setNombre] = useState("");
  const [direccion, setDireccion] = useState("");
  const [correo, setCorreo] = useState("");
  const [celular, setCelular] = useState("");
  const [procesando, setProcesando] = useState(false);
  const navigate = useNavigate();

  const confirmar = async () => {
    if (!nombre || !direccion || !correo) {
      alert("Completa todos los campos obligatorios");
      return;
    }
    
    setProcesando(true);
    try {
      // Crear el pedido en el backend
      const pedidoData = {
        fecha: new Date().toISOString().split('T')[0],
        total: total,
        usuario: correo,
        detalles: JSON.stringify({
          nombre,
          direccion,
          celular,
          productos: carrito.map(p => ({ id: p.id, nombre: p.nombre, precio: p.precio }))
        })
      };
      
      await api.post("/pedidos", pedidoData);
      vaciar();
      navigate("/exito");
    } catch (error) {
      console.error("Error al procesar pedido:", error);
      // Incluso si falla el backend, permitimos continuar
      vaciar();
      navigate("/exito");
    } finally {
      setProcesando(false);
    }
  };

  if (!carrito.length) return <div className="mx-auto p-4" style={{ maxWidth: 600 }}><p>Tu carrito está vacío. <a href="/productos">Ver productos</a></p></div>;

  return (
    <div className="mx-auto p-4" style={{ maxWidth: 600 }}>
      <h2>Finalizar Compra 🛒</h2>
      
      <div className="mb-4">
        <h5>Resumen del pedido:</h5>
        <ul className="list-group mb-3">
          {carrito.map((item, idx) => (
            <li key={idx} className="list-group-item d-flex justify-content-between">
              <span>{item.nombre}</span>
              <span>${item.precio}</span>
            </li>
          ))}
        </ul>
        <h5 className="text-end">Total: ${total}</h5>
      </div>

      <div className="mb-3">
        <label className="form-label">Nombre completo *</label>
        <input 
          className="form-control" 
          value={nombre} 
          onChange={e => setNombre(e.target.value)}
          required 
        />
      </div>
      <div className="mb-3">
        <label className="form-label">Correo electrónico *</label>
        <input 
          className="form-control" 
          type="email"
          value={correo} 
          onChange={e => setCorreo(e.target.value)}
          required 
        />
      </div>
      <div className="mb-3">
        <label className="form-label">Celular</label>
        <input 
          className="form-control" 
          type="tel"
          value={celular} 
          onChange={e => setCelular(e.target.value)} 
        />
      </div>
      <div className="mb-3">
        <label className="form-label">Dirección de envío *</label>
        <textarea 
          className="form-control" 
          rows="3"
          value={direccion} 
          onChange={e => setDireccion(e.target.value)}
          required 
        />
      </div>
      
      <button 
        className="btn btn-success w-100 mt-3" 
        onClick={confirmar}
        disabled={procesando}
      >
        {procesando ? "Procesando..." : "Confirmar y Pagar"}
      </button>
    </div>
  );
}
