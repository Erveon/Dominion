var Dominion = (function($) {
    'use strict';

    var start = function() {
        new Menu();
    };

    var Api = function() {
        this.URL = null;
    };

    Api.prototype.setURL = function(url) {
        this.URL = url;
    };

    Api.prototype.buildCallString = function(data, multi) {
        var callString = this.URL;
        callString += multi ? 'type=mp&' : 'type=local&';
        callString += this.stringifyKeys(data);
        return callString;
    };

    Api.prototype.ajaxCall = function(callString, callback) {
    	var that = this;
        console.log('Calling: ' + callString);
        $.ajax({url: callString})
            .done(function(returnData) {
                that.handleDone(callString, callback, returnData);
            })
            .fail(function(error) {
                console.log(error);
            });
    };

    Api.prototype.stringifyKeys = function(data) {
        var keylist = "";
        for (var key in data)
            keylist += key + '=' + data[key] + '&';
        keylist = keylist.substring(0, keylist.length - 1);
        return keylist;
    };

    Api.prototype.handleDone = function(callString, callback, returnData) {
        if(returnData.response === 'invalid')
            console.log('invalid response for call: ' + callString);
        else
            callback(returnData);
    };

    Api.prototype.doCall = function(data, callback, multi) {
        var callString = this.buildCallString(data, multi);
        this.ajaxCall(callString, callback);
    };

    var Game = function(initData) {
        this.players = initData.players;
        this.cardSet = initData.cardSet;
        this.gameInfo = null;
        this.Api = new Api();
        this.Api.setURL('//localhost:8080/Dominion/api?');
        this.Interface = new Interface();
        this.initGame();
    };

    Game.prototype.addPlayer = function(name) {
        this.Api.doCall({'action': 'addplayer', 'name': name},
            function() {
                console.log(name + ' was added to the game');
            }
        );
    };

    Game.prototype.addAllPlayers = function() {
        for (var playerIndex in this.players)
            this.addPlayer(this.players[playerIndex]);
    };

    Game.prototype.startGame = function () {
        var that = this;
        this.Api.doCall({'action': 'start'},
            function () {
                console.log("Game started!");
                that.updateGameInfo();
            }
        );
    };

    Game.prototype.updateGameInfo = function () {
        var that = this;
        this.Api.doCall({'action': 'info'},
            function (data) {
                that.gameData = data;
                that.Interface.refreshUI(that.gameData);
            }
        );
    };

    Game.prototype.endPhase = function () {
        var that = this;
        this.Api.doCall({'action': 'endphase'},
            function () {
                console.log("Phase Ended!");
                that.updateGameInfo();
            }
        );
    };

    Game.prototype.initGame = function() {
        var that = this;
        this.Api.doCall({'action': 'create'},
            function() {
                console.log('game created');
                that.addAllPlayers();
                that.startGame();
            }
        );
    };

    var Interface = function() {
        console.log('interface created');
    };

    Interface.prototype.updatePlayerDisplayNames = function (data) {
        $('.players').empty();
        $('.players').append('Players&nbsp;');
        for (var player in data.game.players) {
            if (data.game.turn.player === data.game.players[player].displayname){
                $('.players').append("<p class='player active'>" + data.game.players[player].displayname + "</p>");
            } else {
                $('.players').append("<p class='player'>" + data.game.players[player].displayname + "</p>");
            }
        }
    };

    Interface.prototype.updateHand = function (data) {
        var hand;

        for (var player in data.game.players) {
            if (data.game.turn.player === data.game.players[player].displayname) {
                hand = data.game.players[player].hand;
            }
        }

        if (hand !== undefined) {
            //this.loadCards(hand);
            this.addCarousel();
        }
    };

    Interface.prototype.loadCards = function(hand) {
        $('#handPile').empty();
        for (var card in hand) {
            console.log(card[hand]);
            var cardHTML = "<li class='card'>";
            cardHTML += "<div class='card-header'>";
            cardHTML += "<p class='card-title'>" + hand[card].name + "</p></div>";
            cardHTML += "<div class='card-body'>";
            cardHTML += "<img src='assets/images/cards/" + hand[card].name + ".jpg' alt='" + hand[card].name + "' width='100%'>";
            cardHTML += "<div class='card-actionList'>";
            cardHTML += "<ul class='card-actions'>";
            for (var action in hand[card].actions) cardHTML += "<li class='card-action'>" + hand[card].actions[action] + "</li>";
            cardHTML += "</ul></div></div>";
            cardHTML += "<div class='card-info'>";
            cardHTML += "<p class='card-cost'>" + hand[card].cost + "</p>";
            cardHTML += "<p class='card-type'>" + hand[card].type + "</p></div></li>";
            $('#handPile').append(cardHTML);
        }
    };

    Interface.prototype.addCarousel = function () {
        if (($('li.card').length) > 5) {
            $('li.card:lt(5)').css('display', 'inline-block');
            $('#handContainer .prev').css('opacity', '0');
            $('#handContainer .prev').css('cursor', 'default');
            $('#handContainer .next').on('click', function () {
                $('li.card').css('display', 'inline-block');
                $('li.card:lt(5)').css('display', 'none');
            });
        } else {
            $('li.card').css('display', 'inline-block');
            $('#handContainer .arrow').css('opacity', '0');
            $('#handContainer .arrow').css('cursor', 'default');
        }


    };

    Interface.prototype.updatePlayerCounters = function (data) {
        $('span.actionC').text(data.game.turn.actionsleft);
        $('span.buyC').text(data.game.turn.buysleft);
        $('span.coinC').text(data.game.turn.buypower);
    };

    Interface.prototype.updateDeckCounter = function (data) {
        for (var player in data.game.players) {
            if (data.game.turn.player === data.game.players[player].displayname){
                $('.deckcounter').text(data.game.players[player].deck.length);
            }
        }
    };

    Interface.prototype.updateBoardCounters = function (data) {
        $('.curseCount').text(data.game.board.curse.curse);
        $('.copperCount').text(data.game.board.treasure.copper);
        $('.silverCount').text(data.game.board.treasure.silver);
        $('.goldCount').text(data.game.board.treasure.gold);
        $('.estateCount').text(data.game.board.victory.estate);
        $('.duchyCount').text(data.game.board.victory.duchy);
        $('.provinceCount').text(data.game.board.victory.province);
        $('.trashCount').text(data.game.board.trash.length);
    };

    Interface.prototype.updateTurnDisplay = function (data) {
        $('.circle').removeClass('activePhase');
        switch (data.game.turn.phase) {
            case 'ACTION':
                $('.actionDisp').addClass('activePhase');
                console.log('action phase');
                break;
            case 'BUY':
                $('.buyDisp').addClass('activePhase');
                console.log('buy phase');
                break;
            case 'CLEANUP':
                $('.cleanupDisp').addClass('activePhase');
                console.log('cleanup phase');
                break;
        }
    };

    Interface.prototype.refreshUI = function(gameData) {
        this.updatePlayerDisplayNames(gameData);
        this.updatePlayerCounters(gameData);
        this.updateDeckCounter(gameData);
        this.updateBoardCounters(gameData);
        this.updateTurnDisplay(gameData);
        this.updateHand(gameData);
    };

    Interface.prototype.updatePlayerDisplayNames = function (data) {
        $('.players').empty();
        $('.players').append('Players&nbsp;');
        for (var player in data.game.players) {
            if (data.game.turn.player === data.game.players[player].displayname){
                this.addActivePlayer(player, data);
            } else {
                $('.players').append("<p class='player'>" + data.game.players[player].displayname + "</p>");
            }
        }
    };

    Interface.prototype.addActivePlayer = function (player, data) {
        var activePlayer = "<p class='player active'>";
        activePlayer += data.game.players[player].displayname + "</p>";
        $('.players').append(activePlayer);
    };

    var Menu = function() {
        this.setFullpageConfig();
        this.disableScroll();
        this.addListeners();
        this.preventImageDragging();
    };

    Menu.prototype.setFullpageConfig = function() {
        $('#fullpage').fullpage({
            verticalCentered: false,
            sectionsColor: ['rgba(118, 53, 104, 0.75)', 'rgba(44, 62, 80, 0.75)', 'rgba(118, 53, 104, 0.75)'],
            controlArrows: false,
        });
    };

    Menu.prototype.disableScroll = function() {
        $.fn.fullpage.setAllowScrolling(false);
    };

    Menu.prototype.moveDown = function() {
        $.fn.fullpage.moveSectionDown();
    };

    Menu.prototype.moveUp = function() {
        $.fn.fullpage.moveSectionUp();
    };

    Menu.prototype.moveRight = function() {
        $.fn.fullpage.moveSlideRight();
    };

    Menu.prototype.moveLeft = function() {
        $.fn.fullpage.moveSlideLeft();
    };

    Menu.prototype.updateNameDisplay = function() {
        $('.player-names').empty();
        this.appendPlayers($(this).val());
    };

    Menu.prototype.PlayerNameField = function(playerNo) {
        var name = '<label class="hidden" for="player' + playerNo + '">Name Player ' + playerNo + '</label>';
        name += '<input class="hidden player" id="player' + playerNo + '" type="text" name="player' + playerNo + '">';
        return name;
    };

    Menu.prototype.appendPlayers = function(playerAmount) {
        for(var i = 1; i <= playerAmount; i++)
            $('.player-names').append(PlayerNameField(i));
            $('.player-names .hidden').fadeIn().css('display', 'inline-block');
    };

    Menu.prototype.preventImageDragging = function () {
        $('img').on('dragstart', function(e) { e.preventDefault(); });
    };

    Menu.prototype.getPlayerNames = function() {
        return $('.player').map(function() {
            return $(this).val();
        }).get();
    };

    Menu.prototype.getCardSet = function() {
        return $('#card-set').val();
    };

    Menu.prototype.startGame = function(that) {
        new Game(that.generateConfig(that));
    };

    Menu.prototype.generateConfig = function(that) {
    	return {
    		players: that.getPlayerNames(),
    		cardSet: that.getCardSet()
    	};
    };

    Menu.prototype.addListeners = function() {
    	var that = this;
        $('#player-amount').on('change', this.updateNameDisplay);
        $('button.page-down').on('click', this.moveDown);
        $('button.page-up').on('click', this.moveUp);
        $('button.page-right').on('click', this.moveRight);
        $('button.page-left').on('click', this.moveLeft);
        $('.start-game').on('click', function() {
        	that.startGame(that);
        });
    };

    return {
        Api: Api,
        Game: Game,
        Interface: Interface,
        Menu: Menu,
        start: start
    };
}(jQuery));
