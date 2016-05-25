var Dominion = (function() {
    'use strict';

    var start = function() {
        var api = new Dominion.Api('//localhost:8080/Dominion/api?', true);
        api.doCall({'action': 'info'}, false);
        var menu = new Dominion.Menu();
        var onlineApi = new Dominion.Online();
    }, gameObj = null;

    return {
        start: start
    };
}());
