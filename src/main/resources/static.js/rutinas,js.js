function cargarImagen(input){
    if (input.files && input.files[0]){
        var lector = new fileReader();
        lector.onload = function (e){
            $('#blash').attr('src', e.targer.result).height(200);
        };
        lector.readAsDataURL(input.files[0]);
    }
}


