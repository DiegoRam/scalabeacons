$(document).ready(function(){


    var globalAPLocations = [{ssid: "AP1", x: 100, y: 315}, {ssid: "AP2", x:100, y: 90}, {ssid: "AP3", x:353, y: 90}, {ssid: "AP4", x:605, y: 90}];
    var APmaxDistInPixeles = 280;

    $('#mall').click(function(e){
        var offset_t = $(this).offset().top - $(window).scrollTop();
        var offset_l = $(this).offset().left - $(window).scrollLeft();

        var left = Math.round( (e.clientX - offset_l) );
        var top = Math.round( (e.clientY - offset_t) );
        $('#x').text('x: ' + left);
        $('#y').text('y: ' + top);

        var query = createQuery(globalAPLocations, {x: left, y: top});
        updateQueryPanel(query);
        callApi(query);

    });

    /*
      Take two points as dict with x and y keys and returns the
      distance between p1 and p2 in the same scale
    */
    function distance(p1, p2) {
        return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y));
    }

    function createQuery(apLocation, p){

        var query = [];
        for (var i=0; i<apLocation.length; i++) {
            var dist = distance(apLocation[i], p);
            if (dist < APmaxDistInPixeles) {
                var signalLevel = 1.0 - dist / APmaxDistInPixeles;
                query.push({ssid: apLocation[i].ssid, level: signalLevel});
            }
        }
        return {beacons: query};
    }

    function callApi(query){
        $.ajax({
            type: "POST",
            url: "/locations/signal-search",
            processData: false,
            contentType: 'application/json',
            data: JSON.stringify(query),
            success: function(data){ showResults(data); }
        });

    }

    function updateQueryPanel(query){
        $('#query').text(JSON.stringify(query))
    };

    function showResults(data){
        $('#result').text(JSON.stringify(data.map(function (e) {return {score: e.score, name: e.source.name};})));
    };
});