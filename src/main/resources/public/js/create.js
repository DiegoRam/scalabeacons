$(document).ready(function(){
    var $contextMenu = $("#contextMenu");

    $("body").on("contextmenu", "#scalamall", function(e) {
        console.log("event");
        $contextMenu.css({
            display: "block",
            left: e.pageX,
            top: e.pageY
        });
        return false;
    });

    $contextMenu.on("click", "a", function() {
        $contextMenu.hide();
    });
});
