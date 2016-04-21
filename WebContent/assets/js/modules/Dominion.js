var Dominion = (function($) {
    'use strict';

    var dominionApi = '//localhost:8080/Dominion/api?';

    var Api = function(Api) {
        this.Api = Api;
    };

    Api.prototype.doCall = function(callData, callback, multi) {
        var call = this.Api;
        call += multi ? 'type=mp&' : 'type=local&';

        for (var key in callData)
            call += key + '=' + callData[key] + '&';

        call = call.substring(0, call.length - 1);

        console.log('Calling: ' + call);

        $.ajax({url: call})
            .done(function(returnData) {
                if(returnData.response === 'invalid')
                    console.log('invalid response for call: ' + call);
                else
                    callback(returnData);
            })
            .fail(function(error) {
                console.log(error);
            });
    };

    var Game = function(initData) {
        this.players = initData.players;
        this.cardSet = initData.cardSet;
        this.Api = new Api(dominionApi);
        this.gameData = null;
        this.currentPlayer = null;
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
                that.refreshUI();
            }
        );
    };

    Game.prototype.refreshUI = function () {
    	var that = this;
        this.Api.doCall({'action': 'info'},
            function (data) {
        		that.updatePlayerDisplayNames(data);
        		that.updatePlayerCounters(data);
        		that.updateDeckCounter(data);
                that.updateBoardCounters(data);
                that.updateTurnDisplay(data);
                that.addListeners();
                console.log('addlisteners called');
        	}
        );
    };

    Game.prototype.addListeners= function () {
        $('.endPhase').click(function () {
            console.log('endPhase called');
            this.endPhase();
        });
    }

    Game.prototype.updatePlayerDisplayNames = function (data) {
    	for (var player in data.game.players) {
    		if (data.game.turn.player === data.game.players[player].displayname){
    			$('.players').append("<p class='player active'>" + data.game.players[player].displayname + "</p>");
    		} else { 
    			$('.players').append("<p class='player'>" + data.game.players[player].displayname + "</p>");
    		}
    	}
    };

    Game.prototype.updatePlayerCounters = function (data) {
    	$('span.actionC').text(data.game.turn.actionsleft);
    	$('span.buyC').text(data.game.turn.buysleft);
    	$('span.coinC').text(data.game.turn.buypower);
    };

    Game.prototype.updateDeckCounter = function (data) {
    	for (var player in data.game.players) {
    		if (data.game.turn.player === data.game.players[player].displayname){
    			$('.deckcounter').text(data.game.players[player].deck.length);
    		}
    	}
    };

    Game.prototype.updateBoardCounters = function (data) {
        $('.curseCount').text(data.game.board.curse.curse);
        $('.copperCount').text(data.game.board.treasure.copper);
        $('.silverCount').text(data.game.board.treasure.silver);
        $('.goldCount').text(data.game.board.treasure.gold);
        $('.estateCount').text(data.game.board.victory.estate);
        $('.duchyCount').text(data.game.board.victory.duchy);
        $('.provinceCount').text(data.game.board.victory.province);
    };
    
    Game.prototype.updateTurnDisplay = function (data) {
    	switch (data.game.turn.phase) {
    		case 'ACTION':
    			$('.actionDisp').addClass('.activePhase');
    			break;
    		case 'BUY':
    			$('.buyDisp').addClass('.buyPhase');
    			break;
    		case 'CLEANUP':
    			$('.cleanupDisp').addClass('.cleanupPhase');
    			break;
     	}
    };

    Game.prototype.endPhase = function () {
        var that = this;
        this.Api.doCall({'action': 'endphase'},
            function () {
                console.log("Phase Ended!");
                that.refreshUI();
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

    return {
        Game: Game
    };
}($));
