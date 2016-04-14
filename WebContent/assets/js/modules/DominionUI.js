var DominionUI = (function($) {
    'use strict';

    function startGame() {
        var gameConfig = {
            players: getPlayerNames(),
            cardSet: getCardSet()
        };

        var Game = new Dominion.Game(gameConfig);
    }

    var setFullpageConfig = function() {
        $('#fullpage').fullpage({
            verticalCentered: false,
            sectionsColor: ['rgba(118, 53, 104, 0.9)', 'rgba(44, 62, 80, 0.9)', 'rgba(118, 53, 104, 0.9)'],
            controlArrows: false,
            afterSlideLoad: function (anchorLink, index, slideIndex) {
                if (index === 3 && slideIndex === 1) {
                    //window.location = 'play.html';
                }
            }
        });
    };

    var disableScroll = function() {
        $.fn.fullpage.setAllowScrolling(false);
    };

    var moveDown = function() {
        $.fn.fullpage.moveSectionDown();
    };

    var moveUp = function() {
        $.fn.fullpage.moveSectionUp();
    };

    var moveRight = function() {
        $.fn.fullpage.moveSlideRight();
    };

    var moveLeft = function() {
        $.fn.fullpage.moveSlideLeft();
    };

    var updateNameDisplay = function() {
        $('.player-names').empty();
        appendPlayers($(this).val());
    };

    var PlayerNameField = function(playerNo) {
        var name = '<label class="hidden" for="player' + playerNo + '">Name Player ' + playerNo + '</label>';
        name += '<input class="hidden player" id="player' + playerNo + '" type="text" name="player' + playerNo + '">';
        return name;
    };

    var appendPlayers = function(playerAmount) {
        for(var i = 1; i <= playerAmount; i++)
            $('.player-names').append(PlayerNameField(i));
            $('.player-names .hidden').fadeIn().css('display', 'inline-block');
    };

    var addListeners = function() {
        $('#player-amount').on('change', updateNameDisplay);
        $('button.page-down').on('click', moveDown);
        $('button.page-up').on('click', moveUp);
        $('button.page-right').on('click', moveRight);
        $('button.page-left').on('click', moveLeft);
        $('.start-game').on('click', startGame);
    };

    var init = function() {
        setFullpageConfig();
        disableScroll();
        addListeners();
    };

    var preventImageDragging = function () {
        $('img').on('dragstart', function(e) { e.preventDefault(); });
    };

    var getPlayerNames = function() {
        return $('.player').map(function() {
            return $(this).val();
        }).get();
    };

    var getCardSet = function() {
        return $('#card-set').val();
    };

    return {
        init: init,
        preventImageDragging: preventImageDragging,
        getPlayerNames: getPlayerNames,
        getCardSet: getCardSet
    };
}($));