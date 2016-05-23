var Dominion = (function() {
    'use strict';

    var start = function() {
        var api = new Dominion.Api('//localhost:8080/Dominion/api?', true);
        api.doCall({'action': 'info'}, false);
        new Dominion.Menu();
    }, gameObj = null;

    return {
        start: start
    };
}());
