$(document).ready(function(){
    var port = "";
    var protocol = "ws://";
    if(window.location.port !== ""){
        port = ":" + window.location.port;
    }
    console.log(window.location.protocol)
    if(window.location.protocol === "https:"){
        protocol = "wss://"
    }
    var ws = protocol + window.location.hostname + port;
    ws += "/chat?name=test";
    console.log(ws);

    var websocket = new WebSocket(ws);

    var sendMessage = function() {
        console.log($("#send").val());
        websocket.send($("#send").val());
    }

    var handleReturnKey = function(e) {
        console.log(e.charCode + ", " + e.keyCode);
        if(e.charCode == 13 || e.keyCode == 13) {
            e.preventDefault();
            sendMessage();
        }
    }

    $("#send").keypress(handleReturnKey)
});