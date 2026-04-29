document.addEventListener('DOMContentLoaded', function () {

    var eliminarModal = document.getElementById('eliminarPeliculaModal');

    eliminarModal.addEventListener('show.bs.modal', function (event) {
        var button = event.relatedTarget;

        var id = button.getAttribute('data-id');
        var titulo = button.getAttribute('data-titulo');

        document.getElementById('idPeliculaEliminar').value = id;
        document.getElementById('tituloPeliculaEliminar').textContent = titulo;
    });

});

