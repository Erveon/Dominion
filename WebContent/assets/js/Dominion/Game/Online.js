Dominion.Online = (function(Online) {
    "use strict";

    var menu;
    var host = "ws://localhost:8080/Dominion/socket";
    var that = null;
    var socket;

    Online = function(Menu) {
        this.menu = Menu;
        if(!("WebSocket" in window)) {
		    $('#lobbies table').fadeOut("fast");
            $("#lobbymessage").text("Multiplayer is not supported on your browser.");
            return;
	    }
        connect();
        that = this;
    }

    var connect = function(){
        try {
            socket = new WebSocket(host);
            socket.onopen = function(){
           		 console.log('Socket Status: '+socket.readyState+' (open)');
                 send({"type": "lobbies"});
            }

            socket.onmessage = function(msg){
           		 console.log('Received: '+msg.data);
                 var data = JSON.parse(msg.data);
                 switch(data["type"].toLowerCase()) {
                    case "lobbies":
                        createLobbies(data["lobbies"]);
                        break;
                 }
            }

            socket.onclose = function(){
           		 console.log('Socket Status: '+socket.readyState+' (Closed)');
            }
        } catch(exception) {
       		 console.send('<p>Error ' +exception);
        }
    }

    var closeConnection = function() {
        socket.close();
        
    }

    var createLobbies = function(lobbies) {
        console.log(lobbies);
        if(lobbies.length == 0) {
            $("#lobbymessage").text("There aren't any games right now, why not create one?");
            return;
        }
        for(var i = 0; i < lobbies.length; i++) {
            var lobby = lobbies[i];
            console.log(lobby);
            that.menu.addLobby(lobby["id"], lobby["name"], lobby["players"], lobby["canjoin"]);
        }
    }

    var send = function(message) {
        console.log("sending:" + message);
        socket.send(JSON.stringify(message));
    }

    return Online;
}(Dominion.Online || {}));
