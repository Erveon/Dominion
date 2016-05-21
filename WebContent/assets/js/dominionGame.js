Dominion.Game = (function(Game) {
    Game = function(initData) {
        this.Api = new Dominion.Api('//localhost:8080/Dominion/api?', true);
        this.players = initData.players;
        this.cardSet = initData.cardSet;
        this.gameData = null;
        this.Interface = null;
        this.playingAction = false;
        this.isMp = false;
        this.returnToSamePlayer = true;
        this.cardsSelected = 0;
        this.initGame();
        gameObj = this;
    };

    Game.prototype.addPlayer = function(name) {
        this.Api.doCall({'action': 'addplayer', 'name': name}, this.isMp,
            function() {
                console.log('Player ' + name + ' was added to the game.');
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
            function() {
                console.log("The game has been started.");
                that.Interface = new Dominion.Interface();
                that.updateGameInfo();
            }
        );
    };

    Game.prototype.buyCard = function(card) {
        var that = this;
        this.Api.doCall({'action': 'buycard', 'card': card}, this.isMp,
            function() {
                console.log("Card has been purchased.");
                that.updateGameInfo();
            }
        );
    };

    Game.prototype.playCard = function(card) {
        var cardToPlay = card.children().first().children().text();
        var that = this;

        this.Api.doCall({'action': 'playcard', 'card': cardToPlay}, this.isMp,
            function(data) {
                console.log("CARD PLAY RESPONSE: ", data);
                if (data.response == "OK") {
                    that.Interface.addCardToField(card);
                    if(data.result !== "DONE") {
                        that.playingAction = true;
                        that.Interface.showCardSelector(data);
                    } else {
                        that.updateGameInfo();
                    }
                }
            }
        );
    };

    Game.prototype.updateGameInfo = function () {
        var that = this;
        this.Api.doCall({'action': 'info'}, this.isMp,
            function (data) {
                that.gameData = data;
                that.Interface.setGameData(that.gameData);
                that.handlePhaseSkip();
                that.Interface.refreshUI();
            }
        );
    };

    Game.prototype.addCardSet = function () {
        var that = this;
        this.Api.doCall({'action': 'setconfig', 'key': 'setcardset', 'value' : 'test'}, this.isMp);
    };

    Game.prototype.checkHandForActions = function () {
        var currentPlayerHand = this.fetchCurrentPlayerHand();
        var containsActions = false;

        for(var card in currentPlayerHand) {
            if(currentPlayerHand[card].type.substring(0, 6) === "ACTION") {
                containsActions = true;
            }
        }

        return containsActions;
    };

    Game.prototype.fetchCurrentPlayerHand = function () {
        var currentPlayerName = this.gameData.game.turn.player;
        var playersInGame = this.gameData.game.players;
        var currentPlayerHand = [];

        for (var player in playersInGame) {
            if(playersInGame[player].displayname === currentPlayerName) {
                currentPlayerHand = playersInGame[player].hand;
            }
        }

        return currentPlayerHand;
    };

    Game.prototype.handlePhaseSkip = function () {
        if(this.gameData.game.turn.phase === "ACTION") {
            if(this.checkHandForActions() === false && !this.playingAction) {
                this.Interface.handlePhaseEnd();
            } else if(this.gameData.game.turn.actionsleft === 0 && !this.playingAction) {
                this.Interface.handlePhaseEnd();
            }
        }
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

    Game.prototype.selectCard = function(card, element) {
        var that = this;
        this.Api.doCall({'action': 'selectcard', 'card': card}, this.isMP,
            function (data) {
                console.log('Card Selected: ', card);
                console.log('Card Select Response', data);
                that.handleSelect(data, that);
                element.remove();
            }
        );
    };

    Game.prototype.handleSelect = function(data, that) {
        that.cardsSelected++;

        if (data.result === "DONE") {
            if (this.returnToSamePlayer === true) {
                $('.overlay').slideUp(function() {
                    $('.overlay').remove();
                });
                that.playingAction = false;
                that.handlePhaseSkip();
                this.returnToSamePlayer = false;
            } else {
                that.Interface.passTurn(that.gameData.game.turn.player, function() {
                    $('.overlay').slideUp(function() {
                        $('.overlay').remove();
                    });
                    that.playingAction = false;
                    that.handlePhaseSkip();
                });
            }

            that.updateGameInfo();
        }

        if (that.cardsSelected === data.max) {
            that.cardsSelected = 0;
            that.Interface.showCardSelector(data);
        }
    };

    Game.prototype.initGame = function() {
        var that = this;
        this.Api.doCall({'action': 'create'}, this.isMp,
            function() {
                console.log('The game has been created.');
                that.addCardSet();
                that.addAllPlayers(function() {
                    that.startGame();
                });
            }
        );
    };

    return Game;
}(Dominion.Game || {}));
