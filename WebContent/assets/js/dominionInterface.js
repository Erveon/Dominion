Dominion.Interface = (function(Interface) {
    Interface = function() {
        console.log('interface created');
        this.handCarousel = null;
        this.fieldCarousel = null;
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

            if(hand.length <= 3) {
                this.handCarousel.hidePrevArrow();
                this.handCarousel.hideNextArrow();
            } else {
                this.handCarousel.addCarousel();
            }
        }
    };

    Interface.prototype.addListeners = function () {
        var that = this;
        $('#handPile .card').on('click', function() {
            console.log('cardPlay called');
            //that.openCardInfo($(this));
            that.playCard($(this));
        });
        console.log('eventlisteners added');
    };

    Interface.prototype.playCard = function(card) {
        gameObj.playCard(card.children().first().children().text());
        this.addCardToField(card);
        card.remove();
    };

    Interface.prototype.addCardToField = function(card) {
        var cardName = card.children().first().children().text();
        var html = "<li class='miniCard'>";
        html += "<p class='miniCard-title'>" + cardName + "</p>";
        html += "<img src='assets/images/cards/" + cardName + ".jpg' width='100%'></li>";
        $("#playedCards").append($(html));
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
            $('#handPile').append(cardHTML);
        }

        this.addListeners();
        this.addCarousels();
    };

    Interface.prototype.addCarousels = function() {
        this.handCarousel = new Dominion.Interface.Carousel($('#handContainer'));
        //this.fieldCarousel = new Carousel();
        this.handCarousel.addCarousel();
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
        this.updateActionCards(gameData);
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

    return Interface;
}(Dominion.Interface || {}));
