Dominion.Online = (function(Online, Incoming, Outgoing, Interface) {
    "use strict";

    var host;
    var socket;

    Online = function() {
        host = "ws://localhost:8080/Dominion/socket";

        if(!("WebSocket" in window)) {
		    $('#lobbies table').fadeOut("fast");
            $(".lobbymessage").text("Multiplayer is not supported on your browser.");
	    } else {
            connect();
        }
    };

    var connect = function() {
        try {
            socketInit();
            initReceiver();
        } catch(exception) {
            console.log('<p>Error ' + exception);
        }
    };

    var socketInit = function() {
        socket = new WebSocket(host);

        socket.onopen = function(){
            console.log('Socket Status: ' + socket.readyState + ' (open)');
            send({"type": "lobbies"});
        };

        socket.onclose = function(){
            console.log('Socket Status: ' + socket.readyState + ' (closed)');
        };

        addListeners();
    };

    var initReceiver = function() {
        socket.onmessage = function(msg){
            console.log('Received: ' + msg.data);
            var data = JSON.parse(msg.data);

            switch(data.type.toLowerCase()) {
                case "lobbies":
                    createLobbies(data.lobbies);
                    break;

                case "updatelobby":
                    updateLobbies(data.lobby);
                    break;

                case "addlobby":
                    addLobby(data.lobby);
                    break;

                case "dellobby":
                    delLobby(data.lobby);
                    break;

                case "gameinfo":
                    updateGameInfo(data.game);
                    break;
            }
        };
    };

    var addLobby = function(lobby) {
        addLobbyToBrowser(lobby.id, lobby.name, lobby.players, lobby.canjoin);
    };

    var delLobby = function(dellobby) {
        delLobbyFromBrowser(dellobby.id);
    };

    var updateGameInfo = function(game) {
        $("#join-lobby").fadeOut(function() {
            $(".lobby-title").text(game.name);
            for(var player in game.players) {
                $("#lobby-players table").append("<tr><td>" + game.players[player] + "</td><td>Yes</td></tr>");
            }
            $("#lobby-screen").fadeIn();
        });
    };

    var createLobbies = function(lobbies) {
        console.log(lobbies);

        if(lobbies.length === 0) {
            $(".lobbymessage").text("There aren't any games right now, why not create one?");
            return;
        }

        for(var i = 0; i < lobbies.length; i++) {
            var lobby = lobbies[i];
            console.log(lobby);
            addLobbyToBrowser(lobby.id, lobby.name, lobby.players, lobby.canjoin);
        }
    };

    var updateLobbies = function(lobby) {
        updateLobbyBrowser(lobby.id, lobby.name, lobby.players, lobby.canjoin);
    };

    var createLobby = function(name, creator) {
        send({"type": "createlobby", "name": name, "displayname": creator});
    };

    var send = function(message) {
        console.log("sending:" + JSON.stringify(message));
        socket.send(JSON.stringify(message));
    };

    var closeConnection = function() {
        socket.close();
    };

    var addLobbyToBrowser = function(id, name, players, joinable) {
        var classes = joinable ? "lobby" : "lobby nojoin";
        var btnMsg = joinable ? "Join" : "In progress";

        $("#lobbies table").append(
            "<tr data-id='" + id + "'>" +
            "<td>" + name + "</td>" +
            "<td>" + players + "</td>" +
            "<td><button data-id='" + id + "' class='" + classes + "'>" + btnMsg + "</button></td>" +
            "</tr>"
        );

        $('#lobbies button').on('click', function(e) {
            e.preventDefault();
            var lobbyId = $(this).attr('data-id');
            var username;
            $("#lobby-browser").fadeOut(function() {
                $("#join-lobby").fadeIn();
            });
            $('.join-lobby').on('click', function(e) {
                e.preventDefault();
                username = $('#connecting-username').val();
                joinLobby(lobbyId, username);
                e.stopImmediatePropagation();
            });
            e.stopImmediatePropagation();
        });
    };

    var joinLobby = function(lobbyId, name) {
        send({"type": "joinlobby", "id": lobbyId, "name": name});
    };

    var updateLobbyBrowser = function(id, name, players, joinable) {
        var lobbies = $("#lobbies table tr");
        $("#lobbies table tr").each(function () {
            if($(this).attr("data-id") === id) {
                $(this).eq(0).text(name);
                $(this).eq(1).text(players);
                $(this).eq(2).toggleClass("nojoin", !joinable);
            }
        });
    };

    var changeLobbyName = function (newName) {
        send({"type": "changelobbyname", "name": newName});
    };

    var changeCardSet = function (cardSet) {
        send({"type": "setcardset", "cardset": cardSet});
    };

    var clearLobbies = function() {
        $("#lobbies table tr").each(function() {
            if($(this).attr("data-id")) {
                $(this).remove();
            }
        });
    };

    var delLobbyFromBrowser = function(uuid) {
        $("#lobbies table tr").each(function() {
            if($(this).attr("data-id") && $(this).data("id").equals(uuid)) {
                $(this).remove();
            }
        });
    };

    var showLobbyBrowser = function () {
        $("#lobby-creator").fadeOut(function () {
            $("#lobby-browser").fadeIn();
        });
    };

    var showCreateLobbyScreen = function() {
        $("#lobby-browser").fadeOut(function () {
            $("#lobby-creator").fadeIn();
        });
    };

    var submitLobby = function() {
        var name = $("#create-lobby-name").val();
        var username = $("#create-lobby-username").val();
        console.log(name, username);
        createLobby(name, username);
        showLobbyOwnerScreen(name, username);
    };

    var showLobbyOwnerScreen = function(name, username) {
        $("#lobby-creator").fadeOut(function() {
            $(".lobby-title").text(name);
            $("#lobby-players table").append("<tr><td>" + username + "</td><td>Yes</td></tr>");
            $("#lobby-settings").append(
                "<label for='change-lobby-name'>Change Lobby Name</label>" +
                "<input id='change-lobby-name' type='text'>" +
                "<button class='confirm-change-name'>Ok</button>" +
                "<label for='change-card-set'>Change Card Set</label>" +
                "<select name='change-card-set' id='change-card-set'>" +
                "<option value='firstgame' selected>First Game</option>" +
                "<option value='bigmoney'>Big Money</option>" +
                "<option value='interaction'>Interaction</option>" +
                "<option value='sizedistortion'>Size Distortion</option>" +
                "<option value='villagesquare'>Village Square</option>" +
                "</select>"
            );
            $('.confirm-change-name').on('click', function(e) {
                e.preventDefault();
                changeLobbyName($("#change-lobby-name").val());
                e.stopImmediatePropagation();
            });
            $('.change-card-set').on('change', function(e) {
                e.preventDefault();
                changeCardSet($(this).val());
                e.stopImmediatePropagation();
            });
            $("#lobby-screen").fadeIn();
        });
    };


    var addListeners = function() {
        $(".show-lobby-creator").on('click', showCreateLobbyScreen);
        $(".show-lobby-browser").on('click', showLobbyBrowser);
        $(".create-lobby").on('click', submitLobby);
    };

    return Online;
}(Dominion.Online || {}));
