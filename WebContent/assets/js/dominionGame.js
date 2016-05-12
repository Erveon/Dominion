Dominion.Game = (function(Game) {
    Game = function(initData) {
        this.Api = new Dominion.Api('//localhost:8080/Dominion/api?', true);
        this.players = initData.players;
        this.cardSet = initData.cardSet;
        this.gameInfo = null;
        this.Interface = null;
        this.isMp = false;
        this.initGame();
        gameObj = this;
    };

    Game.prototype.addPlayer = function(name) {
        this.Api.doCall({'action': 'addplayer', 'name': name}, this.isMp,
            function() {
                console.log(name + ' was added to the game');
            }
        );
    };

    Game.prototype.addAllPlayers = function(callback) {
        for (var playerIndex in this.players) {
            this.addPlayer(this.players[playerIndex]);
        }

        if (callback) {
            callback();
        }
    };

    Game.prototype.startGame = function () {
        var that = this;
        this.Api.doCall({'action': 'start'}, this.isMp,
            function () {
                console.log("Game started!");
                that.updateGameInfo();
                that.Interface = new Dominion.Interface();
            }
        );
    };

    Game.prototype.playCard = function(card) {
        this.Api.doCall({'action': 'playcard', 'card': card}, this.isMp);
    };

    Game.prototype.updateGameInfo = function () {
        var that = this;
        this.Api.doCall({'action': 'info'}, this.isMp,
            function (data) {
                that.gameData = data;
                that.Interface.refreshUI(that.gameData);
            }
        );
    };

    Game.prototype.addCardSet = function () {
        var that = this;
        this.Api.doCall({'action': 'setconfig', 'key': 'setcardset', 'value' : 'test'}, this.isMp);
    };

    Game.prototype.endPhase = function () {
        var that = this;
        this.Api.doCall({'action': 'endphase'}, this.isMp,
            function () {
                console.log("Phase Ended!");
                that.updateGameInfo();
            }
        );
    };

    Game.prototype.initGame = function() {
        var that = this;
        this.Api.doCall({'action': 'create'}, this.isMp,
            function() {
                console.log('game created');
                that.addCardSet();
                that.addAllPlayers(function() {
                    that.startGame();
                });
            }
        );
    };

    return Game;
}(Dominion.Game || {}));
