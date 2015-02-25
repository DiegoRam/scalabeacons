$(document).ready(function(){
    $('#mall').click(function(e){
        var offset_t = $(this).offset().top - $(window).scrollTop();
        var offset_l = $(this).offset().left - $(window).scrollLeft();

        var left = Math.round( (e.clientX - offset_l) );
        var top = Math.round( (e.clientY - offset_t) );
        $('#x').text('x: ' + left);
        $('#y').text('y: ' + top);

        var query = createQuery(left, top);
        updateQueryPanel(query);
        callApi(query);

    });

    function createQuery(x,y){
        return {beacon: [{ssid: 'Leo Productions', level: 0.7}]};
    }

    function callApi(query){
        $.post("/locations/signal-search", {}, function(err, data){
            showResults([{score: 0.766565},{score: 0.9987}]);
        });
    }

    function updateQueryPanel(query){
        $('#query').text(JSON.stringify(query))
    };

    function showResults(data){

    };
});