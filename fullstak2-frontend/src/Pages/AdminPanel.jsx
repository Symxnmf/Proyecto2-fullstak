import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { obtenerProductos, crearProducto, actualizarProducto, eliminarProducto } from "../api/apiProductos";

export default function AdminPanel() {
  const [productos, setProductos] = useState([]);
  const [form, setForm] = useState({ nombre: "", descripcion: "", precio: 0, categoria: "Postres", imagen: "", oferta: false, stock: 0 });
  const [enviando, setEnviando] = useState(false);
  const [mensaje, setMensaje] = useState({ tipo: "", texto: "" }); // tipo: 'ok' | 'error'
  
  // Estado para edición
  const [editando, setEditando] = useState(null); // id del producto en edición
  const [formEdit, setFormEdit] = useState({ nombre: "", descripcion: "", precio: 0, categoria: "Postres", imagen: "", oferta: false, stock: 0 });
  
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
    if (!confirm("¿Eliminar producto?")) return;
    await eliminarProducto(id);
    cargar();
  }

  function iniciarEdicion(producto) {
    setEditando(producto.id);
    setFormEdit({
      nombre: producto.nombre || "",
      descripcion: producto.descripcion || "",
      precio: producto.precio || 0,
      categoria: producto.categoria || "Postres",
      imagen: producto.imagen || "",
      oferta: producto.oferta || false,
      stock: producto.stock || 0
    });
  }

  function cancelarEdicion() {
    setEditando(null);
    setFormEdit({ nombre: "", descripcion: "", precio: 0, categoria: "Postres", imagen: "", oferta: false, stock: 0 });
  }

  async function guardarEdicion() {
    setMensaje({ tipo: "", texto: "" });
    if (!formEdit.nombre?.trim()) {
      setMensaje({ tipo: "error", texto: "El nombre es obligatorio." });
      return;
    }
    if (!formEdit.precio || Number(formEdit.precio) <= 0) {
      setMensaje({ tipo: "error", texto: "El precio debe ser mayor a 0." });
      return;
    }
    
    try {
      await actualizarProducto(editando, formEdit);
      setMensaje({ tipo: "ok", texto: "Producto actualizado con éxito." });
      cancelarEdicion();
      cargar();
      setTimeout(() => setMensaje({ tipo: "", texto: "" }), 3000);
    } catch (e) {
      const msg = e?.response?.status === 401
        ? "No autorizado. Inicia sesión con el usuario Admin del backend."
        : "No se pudo actualizar el producto. Intenta nuevamente.";
      setMensaje({ tipo: "error", texto: msg });
    }
  }

  return (
    <div className="container mt-4">
      <h2>Panel Administrador</h2>
      {mensaje.texto && (
        <div className={`alert ${mensaje.tipo === 'ok' ? 'alert-success' : 'alert-danger'}`} role="alert">
          {mensaje.texto}
        </div>
      )}
      
      <h4 className="mt-4">Crear Nuevo Producto</h4>
      <form onSubmit={handleCrear} className="mb-4 p-3 border rounded bg-light">
        <div className="row g-2">
          <div className="col-md-4">
            <input className="form-control" placeholder="Nombre" value={form.nombre} onChange={e => setForm({...form, nombre: e.target.value})} required/>
          </div>
          <div className="col-md-2">
            <input className="form-control" placeholder="Precio" type="number" value={form.precio} onChange={e => setForm({...form, precio: Number(e.target.value)})} required/>
          </div>
          <div className="col-md-3">
            <select className="form-select" value={form.categoria} onChange={e => setForm({...form, categoria: e.target.value})}>
              <option value="Panadería">Panadería</option>
              <option value="Pasteles">Pasteles</option>
              <option value="Galletas">Galletas</option>
              <option value="Pies">Pies</option>
              <option value="Postres">Postres</option>
              <option value="Bebidas">Bebidas</option>
            </select>
          </div>
          <div className="col-md-3">
            <input className="form-control" placeholder="Stock" type="number" value={form.stock} onChange={e => setForm({...form, stock: Number(e.target.value)})}/>
          </div>
        </div>
        <div className="mt-2">
          <input className="form-control" placeholder="URL de imagen" value={form.imagen} onChange={e => setForm({...form, imagen: e.target.value})}/>
        </div>
        <div className="mt-2">
          <textarea className="form-control" placeholder="Descripción" rows="2" value={form.descripcion} onChange={e => setForm({...form, descripcion: e.target.value})}/>
        </div>
        <div className="form-check mt-2">
          <input className="form-check-input" type="checkbox" id="ofertaCheck" checked={form.oferta} onChange={e => setForm({...form, oferta: e.target.checked})}/>
          <label className="form-check-label" htmlFor="ofertaCheck">Producto en oferta</label>
        </div>
        <button className="btn btn-primary mt-3" disabled={enviando}>
          {enviando ? "Creando..." : "Crear Producto"}
        </button>
      </form>

      <h4 className="mt-4">Lista de Productos</h4>
      <div className="table-responsive">
        <table className="table table-striped table-hover">
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Precio</th>
              <th>Categoría</th>
              <th>Stock</th>
              <th>Oferta</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {productos.map(p => (
              <tr key={p.id}>
                {editando === p.id ? (
                  <>
                    <td>{p.id}</td>
                    <td><input className="form-control form-control-sm" value={formEdit.nombre} onChange={e => setFormEdit({...formEdit, nombre: e.target.value})}/></td>
                    <td><input className="form-control form-control-sm" type="number" value={formEdit.precio} onChange={e => setFormEdit({...formEdit, precio: Number(e.target.value)})}/></td>
                    <td>
                      <select className="form-select form-select-sm" value={formEdit.categoria} onChange={e => setFormEdit({...formEdit, categoria: e.target.value})}>
                        <option value="Panadería">Panadería</option>
                        <option value="Pasteles">Pasteles</option>
                        <option value="Galletas">Galletas</option>
                        <option value="Pies">Pies</option>
                        <option value="Postres">Postres</option>
                        <option value="Bebidas">Bebidas</option>
                      </select>
                    </td>
                    <td><input className="form-control form-control-sm" type="number" value={formEdit.stock} onChange={e => setFormEdit({...formEdit, stock: Number(e.target.value)})}/></td>
                    <td>
                      <input className="form-check-input" type="checkbox" checked={formEdit.oferta} onChange={e => setFormEdit({...formEdit, oferta: e.target.checked})}/>
                    </td>
                    <td>
                      <button className="btn btn-sm btn-success me-1" onClick={guardarEdicion}>Guardar</button>
                      <button className="btn btn-sm btn-secondary" onClick={cancelarEdicion}>Cancelar</button>
                    </td>
                  </>
                ) : (
                  <>
                    <td>{p.id}</td>
                    <td>{p.nombre}</td>
                    <td>${p.precio?.toLocaleString('es-CL')}</td>
                    <td>{p.categoria}</td>
                    <td>{p.stock}</td>
                    <td>{p.oferta ? "✅" : "❌"}</td>
                    <td>
                      <button className="btn btn-sm btn-warning me-1" onClick={() => iniciarEdicion(p)}>Editar</button>
                      <button className="btn btn-sm btn-danger" onClick={() => handleEliminar(p.id)}>Eliminar</button>
                    </td>
                  </>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
