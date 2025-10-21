import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { obtenerProductos, crearProducto, actualizarProducto, eliminarProducto } from "../api/apiProductos";

export default function AdminPanel() {
  const [productos, setProductos] = useState([]);
  const [form, setForm] = useState({ nombre: "", descripcion: "", precio: 0, categoria: "Postres", imagen: "", oferta: false, stock: 0 });
  const [enviando, setEnviando] = useState(false);
  const [mensaje, setMensaje] = useState({ tipo: "", texto: "" }); // tipo: 'ok' | 'error'
  const navigate = useNavigate();

  useEffect(() => {
    cargar();
  }, []);

  async function cargar() {
    try {
      const data = await obtenerProductos();
      setProductos(Array.isArray(data) ? data : []);
    } catch (e) {
      // no bloqueamos el panel si falla
      setProductos([]);
    }
  }

  async function handleCrear(e) {
    e.preventDefault();
    setMensaje({ tipo: "", texto: "" });
    // Validaciones mínimas
    if (!form.nombre?.trim()) {
      setMensaje({ tipo: "error", texto: "El nombre es obligatorio." });
      return;
    }
    if (!form.precio || Number(form.precio) <= 0) {
      setMensaje({ tipo: "error", texto: "El precio debe ser mayor a 0." });
      return;
    }
    setEnviando(true);
    try {
      await crearProducto(form);
      setMensaje({ tipo: "ok", texto: "Producto creado con éxito. Redirigiendo..." });
      setForm({ nombre: "", descripcion: "", precio: 0, categoria: "Postres", imagen: "", oferta: false, stock: 0 });
      // recargar por si el usuario se queda aquí
      cargar();
      // pequeña pausa opcional para que se vea el mensaje
      setTimeout(() => {
        navigate("/productos", { replace: false });
      }, 300);
    } catch (e) {
      // 401 suele ser por token inválido/ausente
      const msg = e?.response?.status === 401
        ? "No autorizado. Inicia sesión con el usuario Admin del backend."
        : "No se pudo crear el producto. Intenta nuevamente.";
      setMensaje({ tipo: "error", texto: msg });
      window.scrollTo({ top: 0, behavior: "smooth" });
    } finally {
      setEnviando(false);
    }
  }

  async function handleEliminar(id) {
    if (!confirm("Eliminar producto?")) return;
    await eliminarProducto(id);
    cargar();
  }

  return (
    <div>
      <h2>Panel Administrador</h2>
      {mensaje.texto && (
        <div className={`alert ${mensaje.tipo === 'ok' ? 'alert-success' : 'alert-danger'}`} role="alert">
          {mensaje.texto}
        </div>
      )}
      <form onSubmit={handleCrear} className="mb-4">
        <div className="row g-2">
          <div className="col"><input className="form-control" placeholder="Nombre" value={form.nombre} onChange={e => setForm({...form, nombre: e.target.value})} required/></div>
          <div className="col"><input className="form-control" placeholder="Precio" type="number" value={form.precio} onChange={e => setForm({...form, precio: Number(e.target.value)})} required/></div>
          <div className="col"><input className="form-control" placeholder="Imagen (ruta)" value={form.imagen} onChange={e => setForm({...form, imagen: e.target.value})}/></div>
        </div>
        <div className="mt-2">
          <textarea className="form-control" placeholder="Descripción" value={form.descripcion} onChange={e => setForm({...form, descripcion: e.target.value})}/>
        </div>
        <button className="btn btn-primary mt-2" disabled={enviando}>{enviando ? "Creando..." : "Crear producto"}</button>
      </form>

      <h4>Lista de productos</h4>
      <table className="table">
        <thead><tr><th>ID</th><th>Nombre</th><th>Precio</th><th>Acciones</th></tr></thead>
        <tbody>
          {productos.map(p => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.nombre}</td>
              <td>{p.precio}</td>
              <td>
                <button className="btn btn-sm btn-danger me-2" onClick={() => handleEliminar(p.id)}>Eliminar</button>
                {/* Puedes añadir editar con modal más adelante */}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
