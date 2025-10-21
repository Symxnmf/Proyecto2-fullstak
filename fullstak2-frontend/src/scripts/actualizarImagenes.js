// Script para actualizar las imágenes de los productos
import api from "../lib/api.js";

const imagenesActualizadas = {
  "Pan de Chocolate": "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&h=400&fit=crop",
  "Pastel de Cumpleaños": "https://images.unsplash.com/photo-1558636508-e0db3814bd1d?w=500&h=400&fit=crop",
  "Pie de Limón": "https://images.unsplash.com/photo-1535920527002-b35e96722eb9?w=500&h=400&fit=crop",
  "Pastel Tres Leches": "https://images.unsplash.com/photo-1621303837174-89787a7d4729?w=500&h=400&fit=crop",
  "Tarta Sacher": "https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?w=500&h=400&fit=crop",
  "Torta de Chocolate": "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500&h=400&fit=crop",
  "Cheesecake de Frutos Rojos": "https://images.unsplash.com/photo-1524351199678-941a58a3df50?w=500&h=400&fit=crop",
  "Torta Red Velvet": "https://images.unsplash.com/photo-1586985289688-ca3cf47d3e6e?w=500&h=400&fit=crop",
  "Brownies Premium": "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&h=400&fit=crop",
  "Cupcakes Variados": "https://images.unsplash.com/photo-1426869981800-95ebf51ce900?w=500&h=400&fit=crop",
  "Torta de Zanahoria": "https://images.unsplash.com/photo-1621303837174-89787a7d4729?w=500&h=400&fit=crop",
  "Macarons Franceses": "https://images.unsplash.com/photo-1569864358642-9d1684040f43?w=500&h=400&fit=crop",
  "Tiramisú Clásico": "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=500&h=400&fit=crop",
  "Alfajores Artesanales": "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=500&h=400&fit=crop",
  "Torta Selva Negra": "https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?w=500&h=400&fit=crop",
  "Donuts Gourmet": "https://images.unsplash.com/photo-1551024506-0bccd828d307?w=500&h=400&fit=crop",
  "Pavlova de Frutas": "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=500&h=400&fit=crop",
  "Éclair de Chocolate": "https://images.unsplash.com/photo-1612201142855-0a449e54b0c5?w=500&h=400&fit=crop",
  "Croissants de Almendra": "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=500&h=400&fit=crop",
  "Profiteroles": "https://images.unsplash.com/photo-1587241321921-91a834d82ffc?w=500&h=400&fit=crop"
};

export async function actualizarImagenes() {
  try {
    console.log("🖼️ Actualizando imágenes de productos...");
    const productos = await api.get("/productos");
    
    let actualizados = 0;
    for (const producto of productos.data) {
      const nuevaImagen = imagenesActualizadas[producto.nombre];
      if (nuevaImagen && producto.imagen !== nuevaImagen) {
        await api.put(`/productos/${producto.id}`, {
          ...producto,
          imagen: nuevaImagen
        });
        console.log(`✅ Actualizado: ${producto.nombre}`);
        actualizados++;
      }
    }
    
    console.log(`✅ ${actualizados} productos actualizados con imágenes.`);
    return true;
  } catch (error) {
    console.error("❌ Error actualizando imágenes:", error);
    return false;
  }
}
