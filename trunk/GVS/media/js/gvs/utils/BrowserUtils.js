/**
 * <p>This class implements the Singleton Design Pattern to make sure there is 
 * only one instance of the class BrowserUtilsSingleton.
 *
 * <p> It should be accessed as follows.
 *
 * @constructor
 * @example
 * var gvs = BrowserUtilsSingleton.getInstance();
 */ 
var BrowserUtilsSingleton = function () {

    /**
     * Singleton instance
     * @private @member
     */
    var _instance = null;


    var BrowserUtils = Class.create(
        /** @lends BrowserUtilsSingleton-BrowserUtils.prototype */ {
        
        /**
         * This class provides some utility functions in a browser independent
         * fashion.
         * @constructs
         */
        initalize: function (){},
        

        // **************** PUBLIC METHODS **************** //


        /**
         * Gets browser window height
         * @type Integer
         */
        getHeight : function () {
            var newHeight=window.innerHeight; //Non-IE (Firefox and Opera)
              
            if (document.documentElement &&
                    document.documentElement.clientHeight) {
              //IE 6+ in 'standards compliant mode'
              newHeight = document.documentElement.clientHeight;

            } else if( document.body && document.body.clientHeight ) {
              //IE 4 compatible and IE 5-7 'quirk mode'
              newHeight = document.body.clientHeight;
            }

            return newHeight;
        },
        

        /**
         * Gets browser window width
         * @type Integer
         */
        getWidth : function(){  
            var newWidth=window.innerWidth; //Non-IE (Firefox and Opera)
          
            if (document.documentElement &&
                    (document.documentElement.clientWidth ||
                     document.documentElement.clientHeight)) {
                //IE 6+ in 'standards compliant mode'
                newWidth = document.documentElement.clientWidth;
            } else if (document.body && 
                    (document.body.clientWidth || document.body.clientHeight)) {
                //IE 4 compatible and IE 5-7 'quirk mode'
                newWidth = document.body.clientWidth;
            }

            return newWidth;
        },
            

        /**
         * Determines if a button code is the left one
         * (right button for left-handed people).
         * @type Boolean
         */
        isLeftButton : function(button) {
            
            if (button == 0 || (this.isIE() && button == 1)) {
                return true;
            } else {
                return false;   
            }
        },
    
        
        /**
         * Is the browser Internet Explorer?
         * @type Boolean
         */
        isIE : function(){ 
            if (/MSIE (\d+\.\d+);/.test(navigator.userAgent)) { //test for MSIE x.x;  
                return true;
            } else {
                return false;       
            }
        }
    });

    return new function() {
        /**
         * Returns the singleton instance
         * @type BrowserUtils
         */
        this.getInstance = function() {
            if (_instance == null) {
                _instance = new BrowserUtils();
            }
            return _instance;
        }
    }
    
}();

// vim:ts=4:sw=4:et:
