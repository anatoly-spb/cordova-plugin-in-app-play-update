var exec = cordova.require('cordova/exec');

var InAppPlayUpdate = function() {
    console.log('InAppPlayUpdate instanced');
};

InAppPlayUpdate.prototype.show = function(msg, onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'InAppPlayUpdate', 'show', [msg]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = InAppPlayUpdate;
}