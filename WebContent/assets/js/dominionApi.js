//API module responsible for handling the interactions between the front-end and the API.
Dominion.Api = (function(Api) {
    "use strict";

    //Pointer to the API object globally accessible in the module.
    var that = null;

    //Api constructor
    Api = function(url, allowDebugging) {
        this.url = url;
        this.callString = "";
        this.allowDebugging = allowDebugging;
        that = this; //Assignment of globally accessible pointer.
    };

    //Private method responsible for creating the url used in the AJAX request.
    var createUrl = function(data, isMultiplayer) {
        that.callString = "";
        that.callString += that.url;
        that.callString += isMultiplayer ? "type=mp&" : "type=local&";
        that.callString += stringifyKeys(data);
    };

    //Private method responsible for turning the data object into a string
    var stringifyKeys = function(data) {
        var dataString = "";

        for(var key in data) {
            dataString += key + "=" + data[key] + "&";
        }

        return dataString.substring(0, dataString.length - 1); //Trim the leading &.
    };

    //Private method responsible for handling invalid responses from the API.
    var catchInvalidResponse = function(url, returnData) {
        if(returnData.response === "invalid") {
            console.log("invalid response for call: " + url);
            console.log("reason:" + returnData.reason);
        }
    };

    //Public method responsible for carrying out AJAX requests to the API.
    Api.prototype.doCall = function(data, multi, callback) {
        createUrl(data, multi);

        if (that.allowDebugging) {
            console.log("Outgoing AJAX call: " + this.callString);
        }

        $.ajax(this.callString)
            .done(function(returnData) {
                if(that.allowDebugging) {
                    catchInvalidResponse(that.callString, returnData);
                }

                if(callback) {
                    callback(returnData);
                }
            })
            .fail(function(error) {
                console.log(error);
            });
    };

    return Api;
}(Dominion.Api || {}));
