Dominion.Menu = (function(Menu) {

    var onlineApi;

    Menu = function() {
        this.setFullpageConfig();
        this.disableScroll();
        this.addListeners();
        this.preventImageDragging();
        this.updateLobby();
    };

    Menu.prototype.setFullpageConfig = function() {
        $('#fullpage').fullpage({
            verticalCentered: false,
            sectionsColor: ['rgba(118, 53, 104, 0.75)', 'rgba(44, 62, 80, 0.75)', 'rgba(118, 53, 104, 0.75)'],
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

    Menu.prototype.addLobby = function(id, name, players, joinable) {
        var classes = joinable ? "lobby" : "lobby nojoin";
        var btnMsg = joinable ? "Join" : "In progress";
        var that = this;

        $("#lobbies table").append(
            "<tr data-id='" + id + "'>" +
            "<td>" + name + "</td>" +
            "<td>" + players + "</td>" +
            "<td><button data-id='" + id + "' class='" + classes + "'>" + btnMsg + "</button></td>" +
            "</tr>"
        );

        $('#' + id).on('click', that.joinLobby);
    };

    Menu.prototype.updateLobbies = function(id, name, players, joinable) {
        var lobbies = $("#lobbies table tr");
        $("#lobbies table tr").each(function () {
            if($(this).attr("data-id") === id) {
                $(this).eq(0).text(name);
                $(this).eq(1).text(players);
                $(this).eq(2).toggleClass(nojoin, !joinable);
            }
        });
    };

    Menu.prototype.clearLobbies = function() {
        $("#lobbies table tr").each(function() {
            if($(this).attr("data-id")) {
                $(this).remove();
            }
        });
    };

    Menu.prototype.removeLobby = function(uuid) {
        $("#lobbies table tr").each(function() {
            if($(this).attr("data-id") && $(this).data("id").equals(uuid)) {
                $(this).remove();
            }
        });
    };

    Menu.prototype.addListeners = function() {
    	var that = this;
        $('#player-amount').on('change', function() {
            $('.player-names').empty();
            that.appendPlayers($(this).val());
        });
        $('button.page-down').on('click', this.moveDown);
        $('button.page-up').on('click', this.moveUp);
        $('.start-game').on('click', function() {
        	that.startGame(that);
        });
        $(".create-online-game").on('click', this.clearLobbies);
    };

    return Menu;
}(Dominion.Menu || {}));
