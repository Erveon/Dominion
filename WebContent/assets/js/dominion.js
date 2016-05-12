var Dominion = (function() {
    'use strict';

    var start = function() {
        new Dominion.Menu();
    }, gameObj = null;

    return {
        start: start
    };
}());
