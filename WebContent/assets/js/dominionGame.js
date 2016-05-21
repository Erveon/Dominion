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

    Game.prototype.startGame = function (players, cardset) {
        var that = this;
        var playerString = this.constructPlayerString(players);
        this.Api.doCall({'action': 'setup', 'cardset': cardset, 'players': playerString}, this.isMp,
            function() {
                console.log("The game has been started.");
                that.Interface = new Dominion.Interface();
                that.updateGameInfo(function() {
                    that.Interface.passTurn(that.gameData.game.turn.player, function() {
                        $('#config').remove();
                        $('#game').show();
                        $('.overlay').slideUp(function() {
                            $('.overlay').remove();
                        });
                    });
                });
            }
        );
    };

    Game.prototype.constructPlayerString = function(players) {
        var playerString = "";

        for(var player in players) {
            playerString += players[player];
            playerString += "¤";
        }

        return playerString.substring(0, playerString.length - 1);
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

    Game.prototype.updateGameInfo = function (callback) {
        var that = this;
        this.Api.doCall({'action': 'info'}, this.isMp,
            function (data) {
                that.gameData = data;
                that.Interface.setGameData(that.gameData);
                that.handlePhaseSkip();
                that.Interface.refreshUI();

                if(callback) {
                    callback();
                }
            }
        );
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
            }

            if(this.gameData.game.turn.actionsleft === 0 && !this.playingAction) {
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
                if(data.response === "OK") {
                    element.remove();
                }
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
                that.startGame(that.players, that.cardSet);
            }
        );
    };

    return Game;
}(Dominion.Game || {}));
