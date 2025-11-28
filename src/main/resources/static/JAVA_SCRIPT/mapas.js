    let map;
    let marker;

    function initMap(){
        if(!map){
            map = L.map('map').setView([4.6097, -74.0817], 12);


        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '© OpenStreetMap'
        }).addTo(map);
            map.invalidateSize();
        }
    }

    document.addEventListener('DOMContentLoaded', function (){

        const modal = document.getElementById('formModal');
        const loader = document.getElementById('loader');

        modal.addEventListener('shown.bs.modal', async function (event){
            const button = event.relatedTarget;
            const direccion = button.getAttribute("data-direccion");

            console.log(direccion);
            if (!map) {
                map = L.map('map').setView([4.6097, -74.0817], 12);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '© OpenStreetMap'
                }).addTo(map);
            } else {
                map.invalidateSize();
            }

            const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(direccion)}&format=json`;
            console.log(url)
            loader.style.display = "block";

            const response = await fetch(url);
            const data = await response.json();

            if(data.length === 0 ){
                alert("No se encontró el lugar.");
                return;
            }


            const lat = parseFloat(data[0].lat);
            const lon = parseFloat(data[0].lon);

            console.log(lat, lon);

            const urlReverse = `https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lon}&format=json`;

            const resp2 = await fetch(urlReverse);
            const rev = await resp2.json();

            console.log(urlReverse)
            loader.style.display = "none";
            const ubi = rev.address;

            let ubi_final = [
                ubi.road || "",
                ubi.quarter || ubi.quarter || "",
                ubi.neighbourhood || "",
                ubi.city || ubi.town || ubi.village || "",
            ].filter(x => x !== "")
                .join(", ");

            if (marker) map.removeLayer(marker);

            marker = L.marker([lat, lon]).addTo(map)
                .bindPopup(`📍 ${ubi_final}`)
                .openPopup();

            map.setView([lat, lon], 15);
        })
    });


    //abrir ruta
    document.getElementById("mostrarInput").addEventListener("click", function (){
        document.getElementById("direccionDomi").style.display = "block";
    });
    document.addEventListener("click", function(e) {
        if (e.target.matches("[data-bs-target='#formModal']")) {
            const direccion = e.target.dataset.direccion;
            document.getElementById("direccionPaciente").value = direccion;
        }
    });

    function irMaps(){
        const origen = encodeURIComponent(document.getElementById("direccionDomic").value);
        const fin = encodeURIComponent(document.getElementById("direccionPaciente").value);

        const url = `https://www.google.com/maps/dir/?api=1&origin=${origen}&destination=${fin}`;
        console.log(url)

        window.open(url, "_blank");
        limpiarModal();
    }

    function limpiarModal() {
        document.getElementById("direccionDomi").style.display = "none";

        const input = document.getElementById("direccionDomic");
        input.value = "";
        input.classList.remove("is-valid", "is-invalid");

        document.getElementById("direccionPaciente").value = "";

        const modal = bootstrap.Modal.getInstance(document.getElementById("formModal"));
        modal.hide();
    }

