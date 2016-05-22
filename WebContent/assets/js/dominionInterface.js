Dominion.Interface = (function(Interface) {
    Interface = function() {
        console.log('The interface has been initialized.');
        this.handCarousel = null;
        this.fieldCarousel = null;
        this.carouselAdded = false;
        this.listenersAdded = false;
        this.menuOn = false;
        this.infoModeOn = false;
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

        $('.menu').on('click', function(e) {
            e.preventDefault();

            if(that.menuOn) {
                $('.settings').slideUp(function () {
                    $('.settings').remove();
                    that.menuOn = false;
                });
            } else {
                var html = "<div class='settings'>";
                html += "<ul>";
                html += "<li><a href=''>Quit Playing</a></li>";
                html += "</ul>";
                html += "</div>";
                $('body').append(html);
                $('.settings').slideDown();
                that.menuOn = true;
            }

            e.stopImmediatePropagation();
        });

        $('.info').on('click', function(e) {
            e.preventDefault();

            if(that.infoModeOn === true) {
                $(this).children().remove();
                $(this).append("<i class='material-icons'>info_outline</i>");
                $('.info-overlay').fadeOut(function() {
                    $('.info-overlay').remove();
                });
                that.infoModeOn = false;
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

                that.infoModeOn = true;
            }

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
        var that = this;

        $("#mainPile .wideCard").on('click', function(e) {
            gameObj.buyCard($(this).children('.wideCard-title').text().toLowerCase().replace(/ /g, "_"));
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

        $("#mainPile .wideCard").hover(function (e) {
            $('body').append("<div class='tooltip'></div>");
            var cardname = $(this).children('.wideCard-title').text().toLowerCase().replace(/ /g, "_");
            var pos = $(this).position();
            var actionCards = that.gameData.game.board.action;
            var cardID;

            for (var card in actionCards) {
                if(actionCards[card].name === cardname) {
                    cardID = card;
                }
            }

            that.addCard(cardID, actionCards, $('.tooltip'));
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
            var cards = that.gameData.game.board.victory;
            var cardID;

            for (var card in cards) {
                if(cards[card].name === cardname) {
                    cardID = card;
                }
            }

            that.addCard(cardID, cards, $('.tooltip'));
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
            var cards = that.gameData.game.board.treasure;
            var cardID;

            for (var card in cards) {
                if(cards[card].name === cardname) {
                    cardID = card;
                }
            }

            that.addCard(cardID, cards, $('.tooltip'));
            $(".tooltip").css({left: pos.left + 5  +'px', top: pos.top + 84 +'px'});
            e.stopImmediatePropagation();
        }, function (e) {
            $('.tooltip').remove();
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

    Interface.prototype.showCardSelector = function(response) {
        var that = this;
        if (response.player === this.gameData.game.turn.player) {
            this.determineAction(response);
            $('.overlay').slideDown();
            gameObj.returnToSamePlayer = true;
        } else {
            this.passTurn(response.player, function () {
                $('.overlay').remove();
                that.determineAction(response);
                $('.overlay').show();
            });
        }
    };

    Interface.prototype.determineAction = function(response) {
        switch(response.result) {
            case 'SELECT_CARD_HAND':
                this.selectCardsFromHand(response);
                break;
            case 'SELECT_CARD_BOARD':
                this.selectCardsFromBoard(response);
                break;
            case 'REVEAL':
                this.revealCards(response);
                break;
        }
    };

    Interface.prototype.selectCardsFromHand = function(response) {
        var sourceArray = this.generateHandArray(response.player);
        var message = response.message;
        var force = response.force;
        this.showActionOverlay(sourceArray, message, force);
        this.addSelectorListeners();
    };

    Interface.prototype.selectCardsFromBoard = function(response) {
        var sourceArray = this.generateBoardArray(response.cost);
        var message = response.message;
        var force = response.force;
        this.showActionOverlay(sourceArray, message, force);
        this.addSelectorListeners();
    };

    Interface.prototype.revealCards = function(response){
        var sourceArray = response.reveal;
        var message = response.message;
        var force = response.force;
        this.showActionOverlay(sourceArray, message, force);
    };

    Interface.prototype.showActionOverlay = function(sourceArray, message, force, type) {
        var overlayHTML = "<div class='overlay'></div>";
        var selectorContainer = "<div id='selectorContainer'></div>";
        var leftArrow = "<a href='' class='arrow prev'><i class='material-icons'>chevron_left</i></a>";
        var rightArrow = "<a href='' class='arrow next'><i class='material-icons'>chevron_right</i></a>";
        var selectorHand = "<ul id='selectorPile' class='cardContainer'></ul>";
        var selectorCarousel = null;
        var that = this;
        $('body').append(overlayHTML);
        $('.overlay').append("<h2 class='message'>" + message + "</h2>").append(selectorContainer);
        $('#selectorContainer').append(leftArrow).append(selectorHand).append(rightArrow);
        that.addCardsFromSourceArray(sourceArray);
        selectorCarousel = new Dominion.Interface.Carousel($('#selectorContainer'));
        that.handleSelectButton(force);
    };

    Interface.prototype.generateHandArray = function(targetHand) {
        var hand = [];
        var players = this.gameData.game.players;

        for(var player in players) {
            if(players[player].displayname === targetHand){
                hand = this.gameData.game.players[player].hand;
            }
        }

        return hand;
    };

    Interface.prototype.generateBoardArray = function(maxCost) {
        var board = this.gameData.game.board;
        var boardArray = [board.action, board.treasure, board.victory];
        var buyableCards = [];

        for(var array in boardArray) {
            for(var card in boardArray[array]) {
                if(boardArray[array][card].cost <= maxCost) {
                    buyableCards.push(boardArray[array][card]);
                }
            }
        }

        return buyableCards;
    };

    Interface.prototype.addCardsFromSourceArray = function(sourceArray) {
        for(var item in sourceArray) {
            this.addCard(item, sourceArray, $("#selectorPile"));
        }
    };

    Interface.prototype.handleSelectButton = function(force) {
        var that = this;

        if(force === false) {
            $('.overlay').append("<a class='actionBtn continue' href=''>Continue</a>");
            $('.overlay a.actionBtn').on('click', function (e) {
                e.preventDefault();
                gameObj.stopAction();

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

    Interface.prototype.addSelectorListeners = function(type) {
        $('#selectorPile .card').on('click', function(e) {
            var card = $(this).children().first().children().text();
            gameObj.selectCard(card, $(this));
            e.stopImmediatePropagation();
        });
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

    Interface.prototype.addTooltipCard = function(cardName) {

    };

    Interface.prototype.addCard = function(card, hand, element) {
        var cardname = hand[card].name;
        var cardHTML = "<li class='card " + hand[card].type.toLowerCase() +"'>";
        cardHTML += "<div class='card-header'>";
        cardHTML += "<p class='card-title'>" + cardname.replace(/_/g, " ") + "</p></div>";
        cardHTML += "<div class='card-body'>";
        cardHTML += "<img src='assets/images/cards/" + hand[card].name + ".jpg' alt='" + hand[card].name + "' width='100%'>";
        cardHTML += "<p class='card-description'>" + hand[card].description + "</p></div>";
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
            cardDisplay.eq(card).children('p:first').text(actionCards[card].name.replace(/_/g, " "));
            cardDisplay.eq(card).children('div:first').children('p:first').text(actionCards[card].cost);
            cardDisplay.eq(card).children('div:first').children('p:last').text(actionCards[card].amount);
            cardDisplay.eq(card).css({
                'background': 'linear-gradient(rgba(127, 140, 141, 0.8), rgba(127, 140, 141, 0.8)), url(assets/images/cards/' + actionCards[card].name + '.jpg) no-repeat center center'
            });
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
        var cardTitle = cardPile.children('.wideCard-title').text().replace(/ /g, "_");

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
