$(document).ready(function(){

    $.urlParam = function(name){
        var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
        if (results==null){
           return null;
        }
        else{
           return results[1] || 0;
        }
    }

    var webSocket = function(){
        var port = "";
        var protocol = "ws://";
        if(window.location.port !== ""){
            port = ":" + window.location.port;
        }
        if(window.location.protocol === "https:"){
            protocol = "wss://"
        }
        var ws = protocol + window.location.hostname + port;
        ws += "/chat?name=";
        ws += $.urlParam("name");
        console.log(ws);
        return ws;
    }

    var websocket = new WebSocket(webSocket());

    var sendMessage = function() {
        if($("#send").val().trim() !== ""){
            websocket.send($("#send").val());
            $("#send").val("");
        }
    }

    var receiveEvent = function(event) {
        var data = JSON.parse(event.data);
        var chat = $(".chat-space");
        var group = $("<div></div>").addClass("group-rom");
        if(data.type === "ChatMessage" && data.message !== ""){
            var sender = $("<div></div>").addClass("first-part odd").html(data.sender);
            var message = $("<div></div>").addClass("second-part").html(data.message);
            var time = $("<div></div>").addClass("third-part").html("");
            group.append(sender).append(message).append(time);
        }else if(data.message !== ""){
            var info = $("<div></div>").addClass("info-part odd").html(data.message);
            group.append(info);
        }
        $(".chat-space").append(group);
        $(".chat-space").animate({ scrollTop: $(".chat-space").height() }, "slow");
        if(data.member != undefined){
            memberList(data.member);
        }
    }

    var handleReturnKey = function(e) {
        if(e.charCode == 13 || e.keyCode == 13) {
            e.preventDefault();
            sendMessage();
        }
    }

    var memberList = function(member) {
        $.each(member, function(index, value){
            console.log(value);
        });
    }

    var pokeServer = function() {
        websocket.send("");
    }

    setInterval(pokeServer, 30000);

    $("#send").keypress(handleReturnKey);
    $("#sendButton").click(function(){
        sendMessage();
    });

    websocket.onmessage = receiveEvent;
});