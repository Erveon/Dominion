Dominion.Interface = (function(Interface) {
    Interface = function() {
        console.log('The interface has been initialized.');
        this.handCarousel = null;
        this.fieldCarousel = null;
        this.carouselAdded = false;
        this.gameDataObj = null;
        this.handContents = null;
        this.playBuffer = [];
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
            this.loadCards(hand);
        }

        this.handCarousel.addCarousel();
    };

    Interface.prototype.addListeners = function () {
        var that = this;
        $('#handPile .card').on('click', function(e) {
            e.preventDefault();
            that.togglePlayBuffer($(this));
        });
        $('#controls .playCards').on('click', function(e) {
            e.preventDefault();
            that.flushPlayBuffer();
        });
        $('#controls .endPhase').on('click', function(e) {
            e.preventDefault();
        });
        $('#controls .endTurn').on('click', function(e) {
            e.preventDefault();
        });

    };

    Interface.prototype.flushPlayBuffer = function () {
        for (var i = 0; i < this.playBuffer.length; i++) {
            this.playCard($(this.handContents[this.playBuffer[i]]));
        }
        this.playBuffer = [];
    };

    Interface.prototype.togglePlayBuffer = function (card) {
        this.handContents = $("ul#handPile").children();

        if(this.playBuffer.indexOf(this.handContents.index(card)) === -1) {
            card.css('box-shadow', '0px 0px 3px 1px rgba(0, 0, 255, 0.9)');
            this.playBuffer.push(this.handContents.index(card));
        } else {
            card.css('box-shadow', '');
            this.playBuffer.splice(this.playBuffer.indexOf(this.handContents.index(card)), 1);
        }
    };

    Interface.prototype.playCard = function(card) {
        if(gameObj.playCard(card.children().first().children().text())) {
            this.addCardToField(card);
            card.remove();
        }
    };

    Interface.prototype.addCardToField = function(card) {
        var cardName = card.children().first().children().text();
        var html = "<li class='miniCard'>";
        html += "<p class='miniCard-title'>" + cardName + "</p>";
        html += "<img src='assets/images/cards/" + cardName + ".jpg' width='100%'></li>";
        $("#playedCards").append($(html));
        this.refreshField();
        this.refreshUI(this.gameDataObj);
    };

    Interface.prototype.refreshField = function() {
        this.fieldCarousel.addCarousel();
        this.handCarousel.addCarousel();
    };

    /*Interface.prototype.openCardInfo = function(currentCard) {
        var html = "<div class='overlay'>";
        html += "<div class='overlay-title'>";
        html += "<h2>" + currentCard.children('p.card-title').text();
        html += "</div>";
        html += "</div>";
        $('body').append(html);
        $('.overlay').fadeIn();
        console.log('openCardInfo executed');
    };*/

    // NEEDS MORE A E S T H E T I C S
    Interface.prototype.loadCards = function(hand) {
        $('#handPile').empty();

        for (var card in hand) {
            var cardHTML = "<li class='card'>";
            cardHTML += "<div class='card-header'>";
            cardHTML += "<p class='card-title'>" + hand[card].name + "</p></div>";
            cardHTML += "<div class='card-body'>";
            cardHTML += "<img src='assets/images/cards/" + hand[card].name + ".jpg' alt='" + hand[card].name + "' width='100%'>";
            cardHTML += "<div class='card-actionList'>";
            cardHTML += "<ul class='card-actions'>";
            for (var action in hand[card].actions) {
                cardHTML += "<li class='card-action'>" + hand[card].actions[action] + "</li>";
            }
            cardHTML += "</ul></div></div>";
            cardHTML += "<div class='card-info'>";
            cardHTML += "<p class='card-cost'>" + hand[card].cost + "</p>";
            cardHTML += "<p class='card-type'>" + hand[card].type + "</p></div></li>";
            $('#handPile').append(cardHTML);
        }

        this.addListeners();
        if (!this.carouselAdded) {
            this.addCarousels();
            this.carouselAdded = true;
        }
    };

    Interface.prototype.addCarousels = function() {
        this.handCarousel = new Dominion.Interface.Carousel($('#handContainer'));
        this.fieldCarousel = new Dominion.Interface.Carousel($('#playedCardContainer'));
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
        $('.curseCount').text(data.game.board.curse[0].amount);
        $('.copperCount').text(data.game.board.treasure[0].amount);
        $('.silverCount').text(data.game.board.treasure[1].amount);
        $('.goldCount').text(data.game.board.treasure[2].amount);
        $('.estateCount').text(data.game.board.victory[0].amount);
        $('.duchyCount').text(data.game.board.victory[1].amount);
        $('.provinceCount').text(data.game.board.victory[2].amount);
        $('.trashCount').text(data.game.board.trash.length);
    };

    Interface.prototype.updateActionCards = function(data) {
        var cardDisplay = $('#mainPile li.wideCard');
        var actionCards = data.game.board.action;
        for (var card in actionCards) {
            cardDisplay.eq(card).children('p:first').text(actionCards[card].name);
            cardDisplay.eq(card).children('div:first').children('p:first').text(actionCards[card].cost);
            cardDisplay.eq(card).children('div:first').children('p:last').text(actionCards[card].amount);
        }
    };

    Interface.prototype.updateTurnDisplay = function (data) {
        $('.circle').removeClass('activePhase');
        switch (data.game.turn.phase) {
            case 'ACTION':
                $('.actionDisp').addClass('activePhase');
                break;
            case 'BUY':
                $('.buyDisp').addClass('activePhase');
                break;
            case 'CLEANUP':
                $('.cleanupDisp').addClass('activePhase');
                break;
        }
    };

    Interface.prototype.refreshUI = function(gameData) {
        this.gameDataObj = gameData;
        this.updatePlayerDisplayNames(gameData);
        this.updatePlayerCounters(gameData);
        this.updateDeckCounter(gameData);
        this.updateBoardCounters(gameData);
        this.updateTurnDisplay(gameData);
        this.updateHand(gameData);
        this.updateActionCards(gameData);
    };

    Interface.prototype.addActivePlayer = function (player, data) {
        var activePlayer = "<p class='player active'>";
        activePlayer += data.game.players[player].displayname + "</p>";
        $('.players').append(activePlayer);
    };

    return Interface;
}(Dominion.Interface || {}));
