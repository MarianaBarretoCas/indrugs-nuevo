  /* ================================
     FUNCIONES BASE
  ================================ */
  function obtenerCarrito() {
      return JSON.parse(localStorage.getItem("carrito")) || [];
  }

  function guardarCarrito(carrito) {
      localStorage.setItem("carrito", JSON.stringify(carrito));
  }

  function limpiarCarrito() {
      localStorage.removeItem("carrito");
      mostrarCarrito();
  }


  /* ================================
     AGREGAR AL CARRITO (con validación stock)
  ================================ */
 async function agregarAlCarrito(idMedicamento) {
     try {
         // Obtener cantidad que eligió el usuario
       const cantidadInput = document.getElementById("cantidad_solicitada");
       let cantidadDeseada = parseInt(cantidadInput.value, 10)|| 1;

         // Consultar stock en backend
         let response = await fetch(`/paciente/api/inventario/stock/${idMedicamento}`);
         if (!response.ok) {
             alert("Error al consultar stock");
             return;
         }
         let stock = await response.json();

         if (stock <= 0) {
             alert("No hay stock disponible de este medicamento.");
             return;
         }

         if (cantidadDeseada > stock) {
             alert("Solo hay " + stock + " unidades disponibles.");
             return;
         }



         let carrito = obtenerCarrito();
         let item = carrito.find(m => m.idMedicamento === idMedicamento);

         if (item) {
             if (item.cantidad + cantidadDeseada <= stock) {
                 item.cantidad += cantidadDeseada;
             } else {
                 alert("No puedes agregar más, solo hay " + stock + " unidades disponibles.");
                 return;
             }
         } else {
             carrito.push({ idMedicamento: idMedicamento, cantidad: cantidadDeseada });
         }

         guardarCarrito(carrito);
         alert("Medicamento agregado al carrito");
         mostrarCarrito();

     } catch (error) {
         console.error("Error:", error);
         alert("Error al validar stock.");
     }
 }

  /* ================================
     ELIMINAR MEDICAMENTO DEL CARRITO
  ================================ */
  function eliminarDelCarrito(idMedicamento) {
      let carrito = obtenerCarrito();
      carrito = carrito.filter(m => m.idMedicamento !== idMedicamento);
      guardarCarrito(carrito);
      mostrarCarrito();
  }

  async function actualizarCantidad(idMedicamento, nuevaCantidad) {
      try {
          // Validar que la cantidad sea un número válido
          if (isNaN(nuevaCantidad) || nuevaCantidad < 1) {
              alert("Cantidad inválida");
              return;
          }

          // Consultar stock
          let response = await fetch(`/paciente/api/inventario/stock/${idMedicamento}`);
          if (!response.ok) {
              alert("Error al consultar stock");
              return;
          }
          let stock = await response.json();

          if (nuevaCantidad > stock) {
              alert("Solo hay " + stock + " unidades disponibles.");
              mostrarCarrito(); // volver a renderizar con valores correctos
              return;
          }

          // Actualizar carrito
          let carrito = obtenerCarrito();
          let item = carrito.find(m => m.idMedicamento === idMedicamento);

          if (item) {
              item.cantidad = nuevaCantidad;
              guardarCarrito(carrito);
              mostrarCarrito();
          }
      } catch (error) {
          console.error("Error:", error);
          alert("Error al actualizar la cantidad.");
      }
  }

  /* ================================
     MOSTRAR CARRITO
  ================================ */
  async function mostrarCarrito() {
      let carrito = obtenerCarrito();
      let contenedor = document.getElementById("carritoContainer");

      if (!contenedor) return; // prevenir error si no existe el div

      if (carrito.length === 0) {
          contenedor.innerHTML = "<p>El carrito de medicamentos está vacío</p>";
          return;
      }

      let html = "<h3>Mi Carrito</h3>";

      for (let item of carrito) {
          try {
              // Obtener detalle del medicamento
              let response = await fetch(`/paciente/medicamentos/${item.idMedicamento}/json`);
              let medicamento = await response.json();

              html += `
                  <div class="medicamento-item">
                      <img src="${medicamento.imagenMedicamento}" th:alt="${medicamento.nombreMedicamento}" width="50">
                      <strong>${medicamento.nombreMedicamento}</strong>
                      <p>${medicamento.descripcionMedicamento}</p>
                      <p>
                        Cantidad:
                        <input type="number" min="1" value="${item.cantidad}"
                               onchange="actualizarCantidad(${item.idMedicamento}, this.value)">
                      </p>
                      <button class="btn" onclick="eliminarDelCarrito(${item.idMedicamento})">Eliminar</button>
                  </div>
              `;
          } catch (error) {
              console.error("Error obteniendo medicamento:", error);
          }
      }

      html += `
            <div class="btn_carro">
            <button class="btn"  onclick="window.location.href='/paciente  /4.pagina_domicilio'">Solicitar</button>
            <button class="btn btncarro" onclick="limpiarCarrito()">Vaciar carrito</button>
            </div>

      `;
      contenedor.innerHTML = html;
  }

  // Ejecutar al cargar la página del carrito
  document.addEventListener("DOMContentLoaded", mostrarCarrito);

     window.addEventListener("DOMContentLoaded", function() {
            let mensaje = /*[[${mensaje}]]*/ '';
            if (mensaje && mensaje.length > 0) {
                localStorage.removeItem("carrito");
            }
        });
