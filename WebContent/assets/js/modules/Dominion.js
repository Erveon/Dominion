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
                that.generateGameData();
                //console.log(that.GameData.turn);
            }
        );
    };

    Game.prototype.generateGameData = function () {
        var data = {};
        this.Api.doCall({'action': 'info'},
            function (returnData) {
                console.log(returnData);
            }
        );
        return data;
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
