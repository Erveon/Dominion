Dominion.Menu = (function(Menu) {
    Menu = function() {
        this.setFullpageConfig();
        this.disableScroll();
        this.addListeners();
        this.preventImageDragging();
    };

    Menu.prototype.setFullpageConfig = function() {
        $('#fullpage').fullpage({
            verticalCentered: false,
            sectionsColor: ['rgba(44, 62, 80, 0.75)', 'rgba(118, 53, 104, 0.75)'],
            controlArrows: false,
        });
    };

    Menu.prototype.disableScroll = function() {
        $.fn.fullpage.setAllowScrolling(false);
    };

    Menu.prototype.moveDown = function() {
        $.fn.fullpage.moveSectionDown();
    };

    Menu.prototype.moveUp = function() {
        $.fn.fullpage.moveSectionUp();
    };

    Menu.prototype.moveRight = function() {
        $.fn.fullpage.moveSlideRight();
    };

    Menu.prototype.moveLeft = function() {
        $.fn.fullpage.moveSlideLeft();
    };

    Menu.prototype.PlayerNameField = function(playerNo) {
        var name = '<label class="hidden" for="player' + playerNo + '">Name Player ' + playerNo + '</label>';
        name += '<input class="hidden player" id="player' + playerNo + '" type="text" name="player' + playerNo + '">';
        return name;
    };

    Menu.prototype.appendPlayers = function(playerAmount) {
        for(var i = 1; i <= playerAmount; i++) {
            $('.player-names').append(this.PlayerNameField(i));
            $('.player-names .hidden').fadeIn().css('display', 'inline-block');
        }
    };

    Menu.prototype.preventImageDragging = function () {
        $('img').on('dragstart', function(e) { e.preventDefault(); });
    };

    Menu.prototype.getPlayerNames = function() {
        return $('.player').map(function() {
            return $(this).val();
        }).get();
    };

    Menu.prototype.getCardSet = function() {
        return $('#card-set').val();
    };

    Menu.prototype.startGame = function(that) {
        new Dominion.Game(that.generateConfig(that));
    };

    Menu.prototype.generateConfig = function(that) {
    	return {
    		players: that.getPlayerNames(),
    		cardSet: that.getCardSet()
    	};
    };

    Menu.prototype.addListeners = function() {
    	var that = this;
        $('#player-amount').on('change', function() {
            $('.player-names').empty();
            that.appendPlayers($(this).val());
        });
        $('button.page-down').on('click', this.moveDown);
        $('button.page-up').on('click', this.moveUp);
        $('button.page-right').on('click', this.moveRight);
        $('button.page-left').on('click', this.moveLeft);
        $('.start-game').on('click', function() {
        	that.startGame(that);
        });
    };

    return Menu;
}(Dominion.Menu || {}));
