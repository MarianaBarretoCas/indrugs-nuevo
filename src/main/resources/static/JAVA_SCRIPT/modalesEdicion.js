
function abrirModalEdicion(idUsuario) {
    fetch(`/admin/actualizar?idUsuario=${idUsuario}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById("contenedorModal").innerHTML = html;
            const modal = new bootstrap.Modal(document.getElementById('formModal'));
            modal.show();
        });
}

function abrirModalEdicionInventario(idInventario) {
    fetch(`/admin/actualizarInventario?idInventario=${idInventario}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById("contenedorModalInv").innerHTML = html;
            const modal = new bootstrap.Modal(document.getElementById('formModalInv'));
            modal.show();
        });
}

function abrirModalMedicamento(idMedicamento) {

    fetch(`/paciente/medicamentos/${idMedicamento}/json`)
        .then(r => r.json())
        .then(data => {

            // Llenar datos en el modal
            document.getElementById("tituloMedicamento").innerText = data.nombreMedicamento;
            document.getElementById("descripcionMedicamento").innerText = data.descripcionMedicamento;
            document.getElementById("imgMedicamento").src = data.imagenMedicamento;
            document.getElementById("cantidadModal").value = 1;

            // Guardar el id en el botón
            document.getElementById("btnAgregarCarrito").setAttribute("data-id", idMedicamento);

            // Mostrar modal
            let modal = new bootstrap.Modal(document.getElementById("modalMedicamento"));
            modal.show();
        });
}
