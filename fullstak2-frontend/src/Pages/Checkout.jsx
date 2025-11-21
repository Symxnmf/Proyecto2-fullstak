import React, { useEffect, useState } from "react";
import { useCarrito } from "../context/CarritoContext";
import { useNavigate } from "react-router-dom";
import api from "../lib/api";
import { useAuth } from "../context/AuthContext";

export default function Checkout() {
  const { carrito, total, vaciar } = useCarrito();
  const { token, user } = useAuth();
  const [nombre, setNombre] = useState("");
  const [direccion, setDireccion] = useState("");
  const [correo, setCorreo] = useState("");
  const [celular, setCelular] = useState("");
  const [procesando, setProcesando] = useState(false);
  const [notificacion, setNotificacion] = useState(null);
  const navigate = useNavigate();

  // Autocompletar correo cuando el usuario está autenticado
  useEffect(() => {
    if (user?.email) {
      setCorreo(user.email);
    }
  }, [user]);

  const confirmar = async () => {
    if (!user) {
      setNotificacion({ tipo: "warning", mensaje: "Debes iniciar sesión para comprar." });
      setTimeout(() => navigate("/login"), 1500);
      return;
    }
    // Si el usuario está logueado usamos su email aunque el campo esté deshabilitado
    const correoEfectivo = user?.email || correo;
    if (!nombre.trim() || !direccion.trim()) {
      setNotificacion({ tipo: "danger", mensaje: "Completa todos los campos obligatorios" });
      return;
    }
    
    setProcesando(true);
    try {
      // Construir items desde carrito
      const items = Object.values(
        carrito.reduce((acc, p) => {
          const key = p.id;
          if (!acc[key]) acc[key] = { productoId: p.id, cantidad: 0 };
          acc[key].cantidad += 1;
          return acc;
        }, {})
      );

      // Enviar a checkout protegido (usa email del token en backend)
      const response = await api.post("/pedidos/checkout", {
        items,
        detalles: JSON.stringify({ nombre, direccion, celular, correo: correoEfectivo })
      });
      
      // Mostrar notificación de éxito antes de navegar
      setNotificacion({ tipo: "success", mensaje: "¡Pedido realizado con éxito!" });
      setTimeout(() => {
        vaciar();
        navigate("/exito");
      }, 1500);
    } catch (error) {
      console.error("Error al procesar pedido:", error);
      const mensaje = error?.response?.data?.message || error?.message || "Error al procesar el pedido";
      setNotificacion({ tipo: "danger", mensaje: `Error: ${mensaje}. Por favor intenta nuevamente.` });
    } finally {
      setProcesando(false);
    }
  };

  if (!carrito.length) return <div className="mx-auto p-4" style={{ maxWidth: 600 }}><p>Tu carrito está vacío. <a href="/productos">Ver productos</a></p></div>;

  // Bloqueo visual si no hay sesión
  if (!user) {
    return (
      <div className="mx-auto p-4" style={{ maxWidth: 600 }}>
        <h2>Finalizar Compra 🛒</h2>
        <p>Necesitas iniciar sesión para completar la compra.</p>
        <a className="btn btn-primary" href="/login">Ir a iniciar sesión</a>
      </div>
    );
  }

  return (
    <div className="mx-auto p-4" style={{ maxWidth: 600 }}>
      <h2>Finalizar Compra 🛒</h2>
      
      {notificacion && (
        <div className={`alert alert-${notificacion.tipo} alert-dismissible fade show`} role="alert">
          {notificacion.mensaje}
          <button type="button" className="btn-close" onClick={() => setNotificacion(null)}></button>
        </div>
      )}
      
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
      {/* El backend utiliza el correo del usuario autenticado (JWT). Este campo se mantiene solo a modo informativo. */}
      <div className="mb-3">
        <label className="form-label">Correo electrónico</label>
        <input
          className="form-control"
          type="email"
          value={correo}
          onChange={e => setCorreo(e.target.value)}
          placeholder="(se toma del usuario autenticado)"
          disabled={!!token}
          readOnly={!!token}
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
