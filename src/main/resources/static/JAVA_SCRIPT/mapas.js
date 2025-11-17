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

        modal.addEventListener('show.bs.modal', async function (event){
            const button = event.relatedTarget;
            const direccion = button.getAttribute("data-direccion");

            console.log(direccion);
            if (!map) {
                map = L.map("map").setView([4.6097, -74.0817], 12);
                L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
                    maxZoom: 19,
                    attribution: "© OpenStreetMap"
                }).addTo(map);
            }

            map.invalidateSize();

            const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(direccion)}&format=json`;
            console.log(url)

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

            map.setView([lat, lon], 17);
        })
    });
    //
    // document.getElementById("buscar").addEventListener("click", async () => {
    //     const lugar = document.getElementById("busqueda").value;
    //     if (!lugar) return alert("Escribe una dirección o lugar.");
    //
    //     const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(lugar)}&format=json`;
    //     console.log(url)
    //     const response = await fetch(url);
    //     const data = await response.json();
    //
    //     if (data.length === 0) {
    //         alert("No se encontró el lugar.");
    //         return;
    //     }
    //
    //     const lat = parseFloat(data[0].lat);
    //     const lon = parseFloat(data[0].lon);
    //
    //     const urlReverse = `https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lon}&format=json`;
    //
    //     const resp2 = await fetch(urlReverse);
    //     const rev = await resp2.json();
    //
    //     console.log(urlReverse)
    //
    //     const ubi = rev.address;
    //
    //     let ubi_final = [
    //         ubi.road || "",
    //         ubi.quarter || ubi.quarter || "",
    //         ubi.neighbourhood || "",
    //         ubi.city || ubi.town || ubi.village || "",
    //     ].filter(x => x !== "")
    //         .join(", ");
    //
    //     if (!map) return alert("Abre el mapa primero.");
    //
    //     if (marker) map.removeLayer(marker);
    //
    //     marker = L.marker([lat, lon], { draggable: true }).addTo(map)
    //         .bindPopup(`📍 ${ubi_final}`)
    //         .openPopup();
    //
    //     // Evento cuando termina de arrastrar
    //     marker.on("dragend", async function () {
    //         const pos = marker.getLatLng();
    //
    //         // document.getElementById("latitud").value = pos.lat;
    //         // document.getElementById("longitud").value = pos.lng;
    //
    //         const url = `https://nominatim.openstreetmap.org/reverse?lat=${pos.lat}&lon=${pos.lng}&format=json`;
    //
    //         const response = await fetch(url);
    //         const data = await response.json();
    //
    //         if (data.length === 0) {
    //             alert("No se encontró el lugar.");
    //             return;
    //         }
    //
    //         const ubicacion =  data.address || "Ubicación seleccionada"
    //         let ubi_final = [
    //             ubicacion.road || "",
    //             ubicacion.quarter || ubicacion.quarter || "",
    //             ubicacion.neighbourhood || "",
    //             ubicacion.city || ""
    //         ].filter(x => x !== "")
    //             .join(", ");
    //         marker.bindPopup(`📍 ${ubi_final}`).openPopup();
    //         document.getElementById("busqueda").value = ubi_final;
    //
    //         map.setView([pos.lat, pos.lng]);
    //     });
    //
    //     map.setView([lat, lon], 15);
    // });
