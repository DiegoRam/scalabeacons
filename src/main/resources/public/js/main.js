$(document).ready(function(){
    $('#mall').click(function(e){
        var offset_t = $(this).offset().top - $(window).scrollTop();
        var offset_l = $(this).offset().left - $(window).scrollLeft();

        var left = Math.round( (e.clientX - offset_l) );
        var top = Math.round( (e.clientY - offset_t) );
        $('#x').text('x: ' + left);
        $('#y').text('y: ' + top);

    });

    function createQuery(x,y){
        return {};
    }

    function callApi(query){
        $.post("/locations/signal-search", {}, function(err, data){
            return {};
        });
    }
});