Dominion.Interface = (function(Interface) {
    Interface = function() {
        console.log('The interface has been initialized.');
        this.handCarousel = null;
        this.fieldCarousel = null;
        this.carouselAdded = false;
        this.listenersAdded = false;
        this.gameData = null;
        this.handContents = null;
        this.playBuffer = [];
    };

    Interface.prototype.updatePlayerDisplayNames = function () {
        var data = this.gameData;
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

    Interface.prototype.updateHand = function () {
        var hand;
        var data = this.gameData;

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

    Interface.prototype.updateControlListeners = function () {
        var that = this;

        $('#controls .playCards').on('click', function(e) {
            e.preventDefault();
            that.flushPlayBuffer();
            e.stopImmediatePropagation();
        });
        $('#controls .endPhase').on('click', function(e) {
            e.preventDefault();
            that.handlePhaseEnd();
            e.stopImmediatePropagation();
        });
    };

    Interface.prototype.updateCardListeners = function () {
        var that = this;
        if(this.gameData.game.turn.phase === "ACTION") {
            $("#handPile .action, #handPile .curse").on("click", function(e) {
                that.togglePlayBuffer($(this));
                e.stopImmediatePropagation();
            });
            $("#handPile .treasure").off().append("<div class='dim'></div>");
        } else {
            $("#handPile .treasure").on("click", function(e) {
                that.togglePlayBuffer($(this));
                e.stopImmediatePropagation();
            });
            $("#handPile .action, #handPile .curse").off().append("<div class='dim'></div>");
        }
        $("#handPile .victory").off().append("<div class='dim'></div>");
    };

    Interface.prototype.updateMarketListeners = function() {
        $("#mainPile .wideCard").on('click', function(e) {
            gameObj.buyCard($(this).children('.wideCard-title').text().toLowerCase());
            e.stopImmediatePropagation();
        });
        $('#treasurePile .wideCard').on('click', function(e) {
            gameObj.buyCard($(this).children('.wideCard-title').text().toLowerCase());
            e.stopImmediatePropagation();
        });
        $('#victoryPile .wideCard').on('click', function(e) {
            gameObj.buyCard($(this).children('.wideCard-title').text().toLowerCase());
            e.stopImmediatePropagation();
        });
        $('#trashCursePile .cursePile').on('click', function(e) {
            gameObj.buyCard($(this).children('.wideCard-title').text().toLowerCase());
            e.stopImmediatePropagation();
        });
    };

    Interface.prototype.handleListenerAccess = function () {
        if(this.gameData.game.turn.phase === "ACTION") {
            $("#handPile .action, #handPile .curse").on("click", this.toggleBuffer);
        } else {
            $("#handPile .victory, #handPile .treasure").on("click", this.toggleBuffer);
        }
    };

    Interface.prototype.handlePhaseEnd = function () {
        if(this.gameData.game.turn.phase === "BUY") {
            var target = this.gameData.game.turn.next_player;
            var that = this;

            this.passTurn(target, function() {
                $(".overlay").slideUp(function() {
                    $(".overlay").remove();
                });

                that.handCarousel.currentTab = 0;
                $('#playedCards').empty();
                gameObj.endPhase();
            });
        } else {
            gameObj.endPhase();
        }
    };

    Interface.prototype.flushPlayBuffer = function () {
        for (var i = 0; i < this.playBuffer.length; i++) {
            gameObj.playCard($(this.handContents[this.playBuffer[i]]));
        }
        this.playBuffer = [];
        gameObj.updateGameInfo();
    };

    Interface.prototype.togglePlayBuffer = function (card) {
        if(this.playBuffer.indexOf(this.handContents.index(card)) === -1) {
            card.css('box-shadow', '0px 0px 5px 4px rgba(41, 128, 185, 1)');
            this.playBuffer.push(this.handContents.index(card));
        } else {
            card.css('box-shadow', '');
            this.playBuffer.splice(this.playBuffer.indexOf(this.handContents.index(card)), 1);
        }
    };

    Interface.prototype.addCardToField = function(card) {
        var cardName = card.children().first().children().text();
        var html = "<li class='miniCard'>";
        html += "<p class='miniCard-title'>" + cardName + "</p>";
        html += "<img src='assets/images/cards/" + cardName + ".jpg' width='100%'></li>";
        $("#playedCards").append($(html));
        card.remove();
    };

    Interface.prototype.showCardSelector = function(playResponse) {
        var overlayHTML = "<div class='overlay'></div>";
        var selectorContainer = "<div id='selectorContainer'></div>";
        var leftArrow = "<a href='' class='arrow prev'><i class='material-icons'>chevron_left</i></a>";
        var rightArrow = "<a href='' class='arrow next'><i class='material-icons'>chevron_right</i></a>";
        var selectorHand = "<ul id='selectorPile' class='cardContainer'></ul>";
        var that = this;

        if (playResponse.player === this.gameData.game.turn.player) {
            $('body').append(overlayHTML);
            $('.overlay').append("<h2 class='message'></h2>").append(selectorContainer);
            $('#selectorContainer').append(leftArrow).append(selectorHand).append(rightArrow);
            that.appendSelectorCards(playResponse);
            that.handleSelectButton(playResponse);
            var selectorCarousel = new Dominion.Interface.Carousel($('#selectorContainer'));
            selectorCarousel.addCarousel();
            $('.overlay').slideDown();
            gameObj.returnToSamePlayer = true;
        } else {
            this.passTurn(playResponse.player, function () {
                $('.overlay').remove();
                $('body').append(overlayHTML);
                $('.overlay').append("<h2 class='message'></h2>").append(selectorContainer);
                $('#selectorContainer').append(leftArrow).append(selectorHand).append(rightArrow);
                that.appendSelectorCards(playResponse);
                that.handleSelectButton(playResponse);
                var selectorCarousel = new Dominion.Interface.Carousel($('#selectorContainer'));
                selectorCarousel.addCarousel();
                $('.overlay').fadeIn();
            });
        }
    };

    Interface.prototype.handleSelectButton = function(playResponse) {
        var that = this;

        if(playResponse.force === false) {
            $('.overlay').append("<a class='actionBtn continue' href=''>Continue</a>");
            $('.overlay a').on('click', function (e) {
            e.preventDefault();


            if (gameObj.returnToSamePlayer === true) {
                $('.overlay').slideUp(function() {
                    gameObj.updateGameInfo();
                    $('.overlay').remove();
                });
                gameObj.returnToSamePlayer = false;
            } else {
                that.passTurn(that.gameData.game.turn.player, function() {
                    $('.overlay').slideUp(function() {
                        gameObj.updateGameInfo();
                        $('.overlay').remove();
                    });
                    gameObj.playingAction = false;
                });
            }


            e.stopImmediatePropagation();
        });
        }


    };

    Interface.prototype.appendSelectorCards = function(playResponse) {
        this.selectCardFromHand(playResponse.player);
        $('.message').text(playResponse.message);
        this.addSelectorListeners();
    };

    Interface.prototype.addSelectorListeners = function() {
        $('#selectorPile .card').on('click', function() {
            var card = $(this).children().first().children().text();
            gameObj.selectCard(card, $(this));
        });
    };

    Interface.prototype.selectCardFromHand = function(target) {
        var hand = [];
        var players = this.gameData.game.players;

        for(var player in players) {
            if(players[player].displayname === target){
                hand = this.gameData.game.players[player].hand;
            }
        }

        for(var card in hand) {
            this.addCard(card, hand, $("#selectorPile"));
        }
    };

    Interface.prototype.refreshField = function() {
        this.fieldCarousel.addCarousel();
        this.handCarousel.addCarousel();
    };

    Interface.prototype.passTurn = function(target, endAction) {
        var html = "<div class='overlay'>";
        html += "<div class='overlay-title'>";
        html += "<h2>Give the controls to " + target + ", please.</h2>";
        html += "</div>";
        html += "<a class='actionBtn continue' href=''>Continue</a>";
        html += "</div>";
        $('body').append(html);
        $('.continue').on('click', function(e) {
            e.preventDefault();
            endAction();
        });
        $('.overlay').slideDown();
    };

    Interface.prototype.loadCards = function(hand) {
        $('#handPile').empty();

        for (var card in hand) {
            this.addCard(card, hand, $("#handPile"));
        }

        if (!this.carouselAdded) {
            this.addCarousels();
            this.carouselAdded = true;
        }

        this.handContents = $("ul#handPile").children();
    };

    Interface.prototype.addCard = function(card, hand, element) {
        var cardHTML = "<li class='card " + hand[card].type.toLowerCase() +"'>";

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

        element.append(cardHTML);
    };

    Interface.prototype.addCarousels = function() {
        this.handCarousel = new Dominion.Interface.Carousel($('#handContainer'));
        this.fieldCarousel = new Dominion.Interface.Carousel($('#playedCardContainer'));
    };

    Interface.prototype.updatePlayerCounters = function () {
        var data = this.gameData;
        $('span.actionC').text(data.game.turn.actionsleft);
        $('span.buyC').text(data.game.turn.buysleft);
        $('span.coinC').text(data.game.turn.buypower);
    };

    Interface.prototype.updateDeckCounter = function () {
        var data = this.gameData;
        for (var player in data.game.players) {
            if (data.game.turn.player === data.game.players[player].displayname){
                $('.deckcounter').text(data.game.players[player].deck.length);
            }
        }
    };

    Interface.prototype.updateBoardCounters = function () {
        var data = this.gameData;
        $('.curseCount').text(data.game.board.curse[0].amount);
        $('.copperCount').text(data.game.board.treasure[0].amount);
        $('.silverCount').text(data.game.board.treasure[1].amount);
        $('.goldCount').text(data.game.board.treasure[2].amount);
        $('.estateCount').text(data.game.board.victory[0].amount);
        $('.duchyCount').text(data.game.board.victory[1].amount);
        $('.provinceCount').text(data.game.board.victory[2].amount);
        $('.trashCount').text(data.game.board.trash.length);
    };

    Interface.prototype.updateActionCards = function() {
        var data = this.gameData;
        var cardDisplay = $('#mainPile li.wideCard');
        var actionCards = data.game.board.action;
        for (var card in actionCards) {
            cardDisplay.eq(card).children('p:first').text(actionCards[card].name);
            cardDisplay.eq(card).children('div:first').children('p:first').text(actionCards[card].cost);
            cardDisplay.eq(card).children('div:first').children('p:last').text(actionCards[card].amount);
        }

        this.dimAllMarketCards();
        this.handleActiveMarketCards();
    };

    Interface.prototype.dimAllMarketCards = function () {
        $("#mainPile .wideCard").children('.dim').remove();
        $("#treasurePile .wideCard").children('.dim').remove();
        $("#victoryPile .wideCard").children('.dim').remove();
        $(".cursePile").children('.dim').remove();
        $("#mainPile .wideCard").append("<div class='dim'></div>");
        $("#treasurePile .wideCard").append("<div class='dim'></div>");
        $("#victoryPile .wideCard").append("<div class='dim'></div>");
        $(".cursePile").append("<div class='dim'></div>");
    };

    Interface.prototype.handleActiveMarketCards = function() {
        var buys = this.gameData.game.turn.buysleft;

        if(this.gameData.game.turn.phase === "BUY") {
            if(buys > 0) {
                this.showAvailibleMarketCards();
            }
        }
    };

    Interface.prototype.showAvailibleMarketCards = function () {
        var availibleCoins = this.gameData.game.turn.buypower;
        var actionPile = this.gameData.game.board.action;
        var treasurePile = this.gameData.game.board.treasure;
        var victoryPile = this.gameData.game.board.victory;
        var actionDOM = $("#mainPile .wideCard");
        var treasureDOM = $("#treasurePile .wideCard");
        var victoryDOM = $("#victoryPile .wideCard");
        this.showAvailibleCards(availibleCoins, actionPile, actionDOM);
        this.showAvailibleCards(availibleCoins, treasurePile, treasureDOM);
        this.showAvailibleCards(availibleCoins, victoryPile, victoryDOM);
    };

    Interface.prototype.showAvailibleCards = function(availibleCoins, pile, cardDOM) {
        for (var card in pile) {
            if(pile[card].cost <= availibleCoins) {
                this.showAvailibleCard(pile[card], card, cardDOM);
            }
        }
    };

    Interface.prototype.showAvailibleCard = function(cardData, cardId, cardDOM) {
        var cardPile = cardDOM.eq(cardId);
        var cardTitle = cardPile.children('.wideCard-title').text();

        if(cardData.name === cardTitle.toLowerCase()) {
            cardPile.children('.dim').remove();
        }
    };

    Interface.prototype.updateTurnDisplay = function () {
        var data = this.gameData;
        $('.circle').removeClass('activePhase');
        switch (data.game.turn.phase) {
            case 'ACTION':
                $('.actionDisp').addClass('activePhase');
                $('#controls .endPhase').text('End Action Phase');
                break;
            case 'BUY':
                $('.buyDisp').addClass('activePhase');
                $('#controls .endPhase').text('End Turn');
                break;
        }
    };

    Interface.prototype.setGameData = function (gameData) {
        this.gameData = gameData;
    };

    Interface.prototype.refreshUI = function() {
        this.updatePlayerDisplayNames();
        this.updatePlayerCounters();
        this.updateDeckCounter();
        this.updateBoardCounters();
        this.updateTurnDisplay();
        this.updateHand();
        this.updateActionCards();
        this.refreshField();
        this.updateControlListeners();
        this.updateCardListeners();
        this.updateMarketListeners();
        console.log('A refresh has been completed.');
    };

    Interface.prototype.addActivePlayer = function (player, data) {
        var activePlayer = "<p class='player active'>";
        activePlayer += data.game.players[player].displayname + "</p>";
        $('.players').append(activePlayer);
    };

    return Interface;
}(Dominion.Interface || {}));
