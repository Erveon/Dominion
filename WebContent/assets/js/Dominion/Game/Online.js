Dominion.Online = (function(Online) {
    "use strict";

    var host;
    var socket;
    var Interface;
    var players;
    var board;
    var turn;
    var fieldCarousel;
    var handCarousel;
    var menuOn = false;
    var infoModeOn = false;
    var carouselAdded = false;
    var playBuffer = [];
    var handContents;
    var thisplayer;

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
            console.log('Received: ', JSON.parse(msg.data));
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

                case "chat":
                    updateLobbyChat(data);
                    break;

                case "startgame":
                    initializeGame(data);
                    break;

                case "game":
                    updateGameData(data);
                    break;
            }
        };
    };

    var updateGameData = function(data) {
        players = data.players;
        board = data.board;
        turn = data.turn;
        refreshUI();
    };

    var sendChatMessage = function(message) {
        send({"type": "chat", "message": message});
    };

    var addLobby = function(lobby) {
        addLobbyToBrowser(lobby.id, lobby.name, lobby.players, lobby.canjoin);
    };

    var delLobby = function(dellobby) {
        delLobbyFromBrowser(dellobby.id);
    };

    var initializeGame = function (data) {
        Interface = new Dominion.Interface();
        $(".headroom").hide();
        $("#game").show().addClass('multiplayer');
    };

    var updateGameInfo = function(game) {
        $(".multiconfig").hide();
        $(".lobby-title").text(game.name);
        $("#lobby-players table tr:gt(0)").remove();
        for(var player in game.players) {
            $("#lobby-players table").append("<tr><td>" + game.players[player] + "</td></tr>");
        }

        var cardset;
        var isHost = game.host;
        cardset = game.cardset.charAt(0).toUpperCase() + game.cardset.slice(1).toLowerCase();
        $("#lobby-settings").children().remove();

        if(isHost) {
            $("#lobby-settings").append(
                "<label for='change-lobby-name'>Change Lobby Name</label>" +
                "<input id='change-lobby-name' type='text'>" +
                "<button class='confirm-change-name'>Ok</button>" +
                "<label for='change-card-set'>Change Card Set</label>" +
                "<select name='change-card-set' id='change-card-set'>" +
                "<option value='firstgame'>First Game</option>" +
                "<option value='bigmoney'>Big Money</option>" +
                "<option value='interaction'>Interaction</option>" +
                "<option value='sizedistortion'>Size Distortion</option>" +
                "<option value='villagesquare'>Village Square</option>" +
                "</select>" +
                "<button class='config lobby-start-game'>Start Game</button>"
            );

            $('.confirm-change-name').on('click', function(e) {
                e.preventDefault();
                changeLobbyName($("#change-lobby-name").val().replace(/</g, "&lt;").replace(/>/g, "&gt;"));
                e.stopImmediatePropagation();
            });

            $('#change-card-set').on("change", function() {
            	changeCardSet($(this).val());
            });

            $("#change-card-set").val(game.cardset.toLowerCase());

            if(($("#lobby-players table tr").length > 2)) {
                $('.lobby-start-game').removeClass('greyed');
                $('.lobby-start-game').on('click', function(e) {
                    e.preventDefault();
                    startGame();
                    e.stopImmediatePropagation();
                });
            } else {
                $('.lobby-start-game').addClass('greyed');
                $('.lobby-start-game').off();
            }

        } else {
            $("#lobby-settings").append(
                "<h3>Card Set</h3>" +
                "<p>" + cardset + "</p>"
            );
        }

        $("#message-bar").keydown(function(e) {
            if(e.keyCode === 13 && $("#message-bar").val().length > 0) {
                e.preventDefault();
                sendChatMessage($('#message-bar').val().replace(/</g, "&lt;").replace(/>/g, "&gt;"));
                $('#message-bar').val("");
            }
        });

        $('.leave-lobby').on('click', function(e) {
            e.preventDefault();
            send({"type": "leavelobby"});
            $('#lobby-screen').hide();
            $('#lobby-browser').show();
            e.stopImmediatePropagation();

        });

        $("#lobby-screen").show();
    };

    var startGame = function() {
    	send({"type": "startgame"});
    };

    var updateLobbyChat = function(data) {
        $('#lobby-chat').append("<p><span class='username'>" + data.username + ":</span> " + data.message + "</p>");
        $('#lobby-chat').scrollTop($('#lobby-chat').height());
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
            $(".multiconfig").hide();
            $("#join-lobby").show();
            $('.join-lobby').on('click', function(e) {
                e.preventDefault();
                username = $('#connecting-username').val().replace(/</g, "&lt;").replace(/>/g, "&gt;");
                joinLobby(lobbyId, username);
                e.stopImmediatePropagation();
            });
            e.stopImmediatePropagation();
        });
    };

    var joinLobby = function(lobbyId, name) {
        send({"type": "joinlobby", "id": lobbyId, "name": name});
        thisplayer = name;
    };

    var updateLobbyBrowser = function(id, name, players, joinable) {
        $("#lobbies table tr").each(function () {
            if($(this).attr("data-id") === id) {
                $(this).children('td').eq(0).text(name);
                $(this).children('td').eq(1).text(players);
                $(this).children('td').eq(2).children().toggleClass("nojoin", !joinable);
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

    var submitLobby = function() {
        var name = $("#create-lobby-name").val().replace(/</g, "&lt;").replace(/>/g, "&gt;");
        var username = $("#create-lobby-username").val().replace(/</g, "&lt;").replace(/>/g, "&gt;");
        console.log(name, username);
        createLobby(name, username);
    };

    var addListeners = function() {
        $(".show-lobby-creator").on('click', function(e) {
            e.preventDefault();
            $('.multiconfig').hide();
            $("#lobby-creator").show();
            e.stopImmediatePropagation();
        });
        $(".show-lobby-browser").on('click', function(e) {
            e.preventDefault();
            $('.multiconfig').hide();
            $("#lobby-browser").show();
            e.stopImmediatePropagation();
        });
        $(".create-lobby").on('click', submitLobby);
    };

    var refreshUI = function() {
        updatePlayerDisplayNames();
        updatePlayerCounters(); //Individual
        updateDeckCounter(); //Individual
        updateBoardCounters();
        updateTurnDisplay(); //Individual
        updateHand(); //Individual
        updateActionCards();
        refreshField();
        updateControlListeners();
        updateCardListeners();
        updateMarketListeners();
        console.log('A refresh has been completed.');
    };

    var updatePlayerCounters = function () {
        $('span.actionC').text(turn.actionsleft);
        $('span.buyC').text(turn.buysleft);
        $('span.coinC').text(turn.buypower);
    };

    var updateDeckCounter = function () {
        for (var player in players) {
            if (thisplayer === players[player].displayname){
                $('.deckcounter').text(players[player].deck.length);
            }
        }
    };

    var updateTurnDisplay = function () {
        $('.circle').removeClass('activePhase');
        switch (turn.phase) {
            case 'ACTION':
                $('.actionDisp').addClass('activePhase');
                $('.currPhase').text("Action");
                if(thisplayer === turn.player) {
                    $('#controls .endPhase').text('End Action Phase');
                }
                break;
            case 'BUY':
                $('.buyDisp').addClass('activePhase');
                $('.currPhase').text("Buy");
                if(thisplayer === turn.player) {
                    $('#controls .endPhase').text('End Turn');
                }
                break;
        }
    };

    var updateHand = function () {
        var hand;

        for (var player in players) {
            if (thisplayer === players[player].displayname) {
                hand = players[player].hand;
            }
        }

        if (hand !== undefined) {
            loadCards(hand);
        }

        handCarousel.addCarousel();
    };

    var loadCards = function(hand) {
        $('#handPile').empty();

        for (var card in hand) {
            addCard(card, hand, $("#handPile"));
        }

        if (!carouselAdded) {
            addCarousels();
            carouselAdded = true;
        }

        handContents = $("ul#handPile").children();
    };

    var addCard = function(card, hand, element) {
        var cardname = hand[card].name;
        var cardHTML = "<li class='card " + hand[card].type.toLowerCase() +"'>";
        cardHTML += "<div class='card-header'>";
        cardHTML += "<p class='card-title'>" + cardname.replace(/_/g, " ") + "</p></div>";
        cardHTML += "<div class='card-body'>";
        cardHTML += "<img src='assets/images/cards/" + cardname.replace(/ /g, "_") + ".jpg' alt='" + hand[card].name + "' width='100%'>";
        cardHTML += "<p class='card-description'>" + hand[card].description + "</p></div>";
        cardHTML += "<div class='card-info'>";
        cardHTML += "<p class='card-cost'>" + hand[card].cost + "</p>";
        cardHTML += "<p class='card-type'>" + hand[card].type + "</p></div></li>";

        element.append(cardHTML);
    };

    var handlePlayTreasures = function() {
        var currentHand = $('#handPile .card');

        currentHand.each(function() {
            if($(this).hasClass('treasure')) {
                playCard($(this));
            }
        });
    };

    var handlePhaseEnd = function () {
        if(turn.phase === "BUY") {
            var target = turn.next_player;

            this.passTurn(target, function() {
                $(".overlay").slideUp(function() {
                    $(".overlay").remove();
                });

                handCarousel.currentTab = 0;
                $('#playedCards').empty();
                endPhase();
                //gameObj.hasSkippedThisTurn = false;
            });
        } else {
            stopAction();
            endPhase();
        }
    };

    var updateControlListeners = function () {
        handlePlayTreasuresVisibility();

        $('#controls .playCards').on('click', function(e) {
            e.preventDefault();
            flushPlayBuffer();
            e.stopImmediatePropagation();
        });
        $('#controls .endPhase').on('click', function(e) {
            e.preventDefault();
            handlePhaseEnd();
            e.stopImmediatePropagation();
        });

        $("#controls .playTreasures").on("click", function(e) {
            e.preventDefault();
            handlePlayTreasures();
            e.stopImmediatePropagation();
        });

        $('.menu').on('click', function(e) {
            e.preventDefault();

            if(menuOn) {
                $('.settings').slideUp(function () {
                    $('.settings').remove();
                    menuOn = false;
                });
            } else {
                var html = "<div class='settings'>";
                html += "<ul>";
                html += "<li><a href=''>Quit Playing</a></li>";
                html += "</ul>";
                html += "</div>";
                $('body').append(html);
                $('.settings').slideDown();
                menuOn = true;
            }

            e.stopImmediatePropagation();
        });

        $('.info').on('click', function(e) {
            e.preventDefault();

            if(infoModeOn === true) {
                $(this).children().remove();
                $(this).append("<i class='material-icons'>info_outline</i>");
                $('.info-overlay').fadeOut(function() {
                    $('.info-overlay').remove();
                });
                infoModeOn = false;
            } else {
                $(this).children().remove();
                $(this).append("<i class='material-icons'>info</i>");

                var hand = $('#handPile').position();
                var market = $('#mainPile').children().first().position();
                var field = $('#playedCardContainer').children().first().position();
                var controls = $('#controls').children().first().position();
                var counters = $('#topStatus ul').children().first().position();

                $('body').append("<div class='info-overlay handOverlay'><h2>HAND</h2><p>This field contains the current players' hand of cards. Playable cards will be highlighted, unplayable cards will be dimmed.</p></div>");
                $('.handOverlay').css({top: hand.top + 'px', left: hand.left + 14 + 'px'});

                $('body').append("<div class='info-overlay marketOverlay'><h2>MARKET</h2><p>This field contains all of the purchaseable cards. Buyable cards will be highlighted, unbuyable cards will be dimmed.</p></div>");
                $('.marketOverlay').css({top: market.top + 'px', left: market.left + 'px'});

                $('body').append("<div class='info-overlay fieldOverlay'><h2>PLAYING FIELD</h2><p>This field contains all of the cards that have been played this turn.</p></div>");
                $('.fieldOverlay').css({top: field.top + 10 + 'px', left: field.left + 'px'});

                $('body').append("<div class='info-overlay controlsOverlay'><h2>CONTROLS</h2></div>");
                $('.controlsOverlay').css({top: controls.top + 'px', left: controls.left + 'px'});

                $('body').append("<div class='info-overlay countersOverlay'><h2>COUNTERS</h2></div>");
                $('.countersOverlay').css({top: counters.top + 5 + 'px', left: controls.left + 380 + 'px'});

                $('.info-overlay').fadeIn();

                infoModeOn = true;
            }
            e.stopImmediatePropagation();
        });
    };

    var updateCardListeners = function () {
        if(turn.phase === "ACTION") {
            $("#handPile .action, #handPile .curse").on("click", function(e) {
                togglePlayBuffer($(this));
                e.stopImmediatePropagation();
            });
            $("#handPile .treasure").off().append("<div class='dim'></div>");
        } else {
            $("#handPile .treasure").on("click", function(e) {
                togglePlayBuffer($(this));
                e.stopImmediatePropagation();
            });
            $("#handPile .action, #handPile .curse").off().append("<div class='dim'></div>");
        }
        $("#handPile .victory").off().append("<div class='dim'></div>");
    };

    var updateMarketListeners = function() {
        $("#mainPile .wideCard").on('click', function(e) {
            buyCard($(this).children('.wideCard-title').text().toLowerCase().replace(/ /g, "_"));
            e.stopImmediatePropagation();
        });
        $('#treasurePile .wideCard').on('click', function(e) {
            buyCard($(this).children('.wideCard-title').text().toLowerCase());
            e.stopImmediatePropagation();
        });
        $('#victoryPile .wideCard').on('click', function(e) {
            buyCard($(this).children('.wideCard-title').text().toLowerCase());
            e.stopImmediatePropagation();
        });
        $('#trashCursePile .cursePile').on('click', function(e) {
            buyCard($(this).children('.wideCard-title').text().toLowerCase());
            e.stopImmediatePropagation();
        });

        $("#mainPile .wideCard").hover(function (e) {
            $('body').append("<div class='tooltip'></div>");
            var cardname = $(this).children('.wideCard-title').text().toLowerCase().replace(/ /g, "_");
            var pos = $(this).position();
            var actionCards = board.action;
            var cardID;

            for (var card in actionCards) {
                if(actionCards[card].name === cardname) {
                    cardID = card;
                }
            }

            addCard(cardID, actionCards, $('.tooltip'));
            $(".tooltip").css({left: pos.left + 2  +'px', top: pos.top + 84 +'px'});
            e.stopImmediatePropagation();
        }, function (e) {
            $('.tooltip').remove();
            e.stopImmediatePropagation();
        });

        $("#victoryPile .wideCard").hover(function (e) {
            $('body').append("<div class='tooltip'></div>");
            var cardname = $(this).children('.wideCard-title').text().toLowerCase();
            var pos = $(this).position();
            var cards = board.victory;
            var cardID;

            for (var card in cards) {
                if(cards[card].name === cardname) {
                    cardID = card;
                }
            }

            addCard(cardID, cards, $('.tooltip'));
            $(".tooltip").css({left: pos.left + 5  +'px', top: pos.top + 84 +'px'});
            e.stopImmediatePropagation();
        }, function (e) {
            $('.tooltip').remove();
            e.stopImmediatePropagation();
        });

        $("#treasurePile .wideCard").hover(function (e) {
            $('body').append("<div class='tooltip'></div>");
            var cardname = $(this).children('.wideCard-title').text().toLowerCase();
            var pos = $(this).position();
            var cards = board.treasure;
            var cardID;

            for (var card in cards) {
                if(cards[card].name === cardname) {
                    cardID = card;
                }
            }

            addCard(cardID, cards, $('.tooltip'));
            $(".tooltip").css({left: pos.left + 5  +'px', top: pos.top + 84 +'px'});
            e.stopImmediatePropagation();
        }, function (e) {
            $('.tooltip').remove();
            e.stopImmediatePropagation();
        });

        $(".cursePile").hover(function (e) {
            $('body').append("<div class='tooltip'></div>");
            var cardname = $(this).children('.wideCard-title').text().toLowerCase();
            var pos = $(this).position();
            var cards = board.curse;
            var cardID;

            for (var card in cards) {
                if(cards[card].name === cardname) {
                    cardID = card;
                }
            }

            addCard(cardID, cards, $('.tooltip'));
            $(".tooltip").css({left: pos.left + 5  +'px', top: pos.top + 84 +'px'});
            e.stopImmediatePropagation();
        }, function (e) {
            $('.tooltip').remove();
            e.stopImmediatePropagation();
        });
    };

    var flushPlayBuffer = function () {
        for (var i = 0; i < playBuffer.length; i++) {
            playCard($(handContents[playBuffer[i]]));
        }
        playBuffer = [];
    };

    var togglePlayBuffer = function (card) {
        if(playBuffer.indexOf(handContents.index(card)) === -1) {
            card.css('box-shadow', '0px 0px 5px 4px rgba(41, 128, 185, 1)');
            playBuffer.push(handContents.index(card));
        } else {
            card.css('box-shadow', '');
            playBuffer.splice(playBuffer.indexOf(handContents.index(card)), 1);
        }
    };

    var handlePlayTreasuresVisibility = function() {
        if (turn.phase === "ACTION") {
            $("#controls .playTreasures").hide();
        } else {
            $("#controls .playTreasures").show();
        }
    };

    var addCardToField = function(cardname) {
        var html = "<li class='miniCard'>";
        html += "<p class='miniCard-title'>" + cardname + "</p>";
        html += "<img src='assets/images/cards/" + cardname.replace(/ /g, "_") + ".jpg' width='100%'></li>";
        $("#playedCards").append($(html));
    };

    var refreshField = function() {
        $("#playedCards").children().remove();
        for (var card in board.playedcards) {
            addCardToField(board.playedcards[card].name);
        }
        fieldCarousel.addCarousel();
        handCarousel.addCarousel();
    };

    var addCarousels = function() {
        handCarousel = new Dominion.Interface.Carousel($('#handContainer'));
        fieldCarousel = new Dominion.Interface.Carousel($('#playedCardContainer'));
    };

    var updateActionCards = function() {
        var cardDisplay = $('#mainPile li.wideCard');
        for (var card in board.action) {
            cardDisplay.eq(card).children('p:first').text(board.action[card].name.replace(/_/g, " "));
            cardDisplay.eq(card).children('div:first').children('p:first').text(board.action[card].cost);
            cardDisplay.eq(card).children('div:first').children('p:last').text(board.action[card].amount);
            cardDisplay.eq(card).css({
                'background': 'linear-gradient(rgba(127, 140, 141, 0.8), rgba(127, 140, 141, 0.8)), url(assets/images/cards/' + board.action[card].name + '.jpg) no-repeat center center'
            });
        }

        dimAllMarketCards();
        handleActiveMarketCards();
    };

    var dimAllMarketCards = function () {
        $("#mainPile .wideCard").children('.dim').remove();
        $("#treasurePile .wideCard").children('.dim').remove();
        $("#victoryPile .wideCard").children('.dim').remove();
        $("#trashCursePile .wideCard").children('.dim').remove();
        $("#discardDeckPile .wideCard").children('.dim').remove();
        $("#mainPile .wideCard").append("<div class='dim'></div>");
        $("#treasurePile .wideCard").append("<div class='dim'></div>");
        $("#victoryPile .wideCard").append("<div class='dim'></div>");
        $("#trashCursePile .wideCard").append("<div class='dim'></div>");
        $("#discardDeckPile .wideCard").append("<div class='dim'></div>");
    };

    var handleActiveMarketCards = function() {
        if(turn.phase === "BUY") {
            if(turn.buysleft > 0) {
                showAvailibleMarketCards();
            }
        }
    };

    var showAvailibleMarketCards = function() {
        var actionDOM = $("#mainPile .wideCard");
        var treasureDOM = $("#treasurePile .wideCard");
        var victoryDOM = $("#victoryPile .wideCard");
        showAvailibleCards(turn.buypower, board.action, actionDOM);
        showAvailibleCards(turn.buypower, board.treasure, treasureDOM);
        showAvailibleCards(turn.buypower, board.victory, victoryDOM);
        $('.cursePile .dim').remove();
    };

    var showAvailibleCards = function(availibleCoins, pile, cardDOM) {
        for (var card in pile) {
            if(pile[card].cost <= availibleCoins) {
                showAvailibleCard(pile[card], card, cardDOM);
            }
        }
    };

    var showAvailibleCard = function(cardData, cardId, cardDOM) {
        var cardPile = cardDOM.eq(cardId);
        var cardTitle = cardPile.children('.wideCard-title').text().replace(/ /g, "_");

        if(cardData.name === cardTitle.toLowerCase()) {
            cardPile.children('.dim').remove();
        }
    };

    var updatePlayerDisplayNames = function () {
        $('.players').empty();
        $('.players').append('<p>Players</p>');
        for (var player in players) {
            if (turn.player === players[player].displayname){
                $('.players').append("<p class='player active'>" + players[player].displayname + "</p>");
            } else {
                $('.players').append("<p class='player'>" + players[player].displayname + "</p>");
            }
        }
    };

    var updateBoardCounters = function() {
        $('.curseCount').text(board.curse[0].amount);
        $('.copperCount').text(board.treasure[0].amount);
        $('.silverCount').text(board.treasure[1].amount);
        $('.goldCount').text(board.treasure[2].amount);
        $('.estateCount').text(board.victory[0].amount);
        $('.duchyCount').text(board.victory[1].amount);
        $('.provinceCount').text(board.victory[2].amount);
        $('.trashCount').text(board.trash.length);
    };

    var endPhase = function() {
        send({"type": "play", "request": {"action": "endphase"}});
    };

    var playCard = function(card) {
        send({"type": "play", "request": {"action": "playcard", "card": card}});
    };

    var buyCard = function(card) {
        send({"type": "play", "request": {"action": "playcard", "card": card}});
    };

    var selectCard = function(card) {
        send({"type": "play",  "request": {"action": "selectcard", "card": card}});
    };

    var stopAction = function() {
        send({"type": "play", "request": {"action": "stopaction"}});
    };

    return Online;
}(Dominion.Online || {}));
