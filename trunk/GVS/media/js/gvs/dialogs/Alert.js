/**
 * <p>This class implements the Singleton Design Pattern to make sure there is
 * only one instance of the class Preferences.
 *
 * <p> It should be accessed as follows.
 *
 * @constructor
 * @example
 * var alert = AlertSingleton.getInstance();
 */
var AlertSingleton = function() {

    /**
     * Singleton instance
     * @private @member
     */
    var _instance = null;


    var Alert = Class.create(FormDialog,
        /** @lends AlertSingleton-Alert.prototype */ {

        /**
         * Alert dialog
         * @constructs
         * @extends FormDialog
         */
        initialize: function($super) {
            $super({
               'title': 'Warning',
               'style': 'display:none'
            });
            this._contentNode.addClassName("systemDialog");

            this._addButton ('Ok', function(){
                this._dialog.hide();
            }.bind(this));
        },
        /**
         * This function shows a message
         */
        show: function  ($super, /** String */ message){
            this._contentNode.update(message);

            $super();
        },
        /**
         * @override
         * @private
         */
        _initDialogInterface: function() {
            // Do nothing
        }
    });

    return new function(){
        /**
         * Returns the singleton instance
         * @type Preferences
         */
        this.getInstance = function(){
            if (_instance == null) {
                _instance = new Alert();
            }
            return _instance;
        }
    }
}();

// Browser alert dialog override
if (document.getElementById) {
    window.alert = function(msg) {
        AlertSingleton.getInstance().show(msg);
        console.log(msg);
    }
}


// vim:ts=4:sw=4:et:
