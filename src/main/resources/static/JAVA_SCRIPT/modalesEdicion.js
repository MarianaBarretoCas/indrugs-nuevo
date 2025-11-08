
function abrirModalEdicion(idUsuario) {
    fetch(`/actualizar?idUsuario=${idUsuario}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById("contenedorModal").innerHTML = html;
            const modal = new bootstrap.Modal(document.getElementById('formModal'));
            modal.show();
        });
}

function abrirModalEdicionInventario(idInventario) {
    fetch(`/actualizarInventario?idInventario=${idInventario}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById("contenedorModalInv").innerHTML = html;
            const modal = new bootstrap.Modal(document.getElementById('formModalInv'));
            modal.show();
        });
}