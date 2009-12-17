/**
 * This class handles the creation of pipes based on WireIt.Terminal
 * and WireIt.Wire classes. Due to the use of these classes, which are
 * not Prototype compliant classes, the syntax of the class is quite 
 * different than usual
 * The terminal object will be attached to each pre or post condition
 * of the instances (specifically, to the condition nodes of their 
 * respective view)
 * 
 * @constructs
 * @extends WireIt.Terminal
 */
var Terminal = function(/** DOMNode */ conditionNode, /** Object */ options, 
                        /** String */ resourceUri, /** String */ resourceId,
                        /** String */ conditionId, /** String(optional) */ action) {
    
    /**
     * @private
     * @type DOMNode
     */
    this._conditionNode = conditionNode;
    
    /**
     * @private
     * @type DOMNode
     */
    this._terminalNode = new Element('div',
    {
        'class': this._conditionNode.title
    });
    
    var style = {
        'position':'absolute',
        'width': '1px',
        'height': '1px',
        'z-index': '50'
    };
    this._terminalNode.setStyle(style);  
    this._recalculatePosition();
    document.body.appendChild(this._terminalNode); 
   
    var wireConfig = {
            'width': 2,
            'borderwidth': 2,
            'color': '#EAEAEA',
            'bordercolor': '#808080'
    }
    extendedOptions = {};
    extendedOptions = Object.extend(extendedOptions, options);
    extendedOptions.wireConfig = Object.extend(wireConfig, options.wireConfig);
         
    WireIt.Terminal.call(this, this._terminalNode, extendedOptions);


    /**
     * The uri of the resource
     * @private
     * @type String
     */
    this._resourceUri = resourceUri;

    /**
     * This is the id of the resource (operator, service, form)
     * that contains the terminal
     * @private
     * @type String
     */
    this._resourceId = resourceId;
    
    /**
     * This is the id of the condition inside the
     * resource that contains the terminal
     * @private
     * @type String
     */
    this._conditionId = conditionId;

    /**
     * This is the action
     * @private
     * @type String
     */
    this._action = action ? action: "";
} 

// Inheriting all methods
Object.extend(Terminal.prototype, WireIt.Terminal.prototype);

Object.extend(Terminal.prototype, /** @lends Terminal.prototype */ { 

    
    // **************** PUBLIC METHODS **************** //

    /**
     * Returns the resourceUri
     * @type String
     */
    getBuildingblockUri: function() {
        return this._resourceUri;
    },
    
    /**
     * Returns the resourceId
     * @type String
     */
    getBuildingblockId: function() {
        return this._resourceId;
    },

    /**
     * Returns the conditionId
     * @type String
     */
    getConditionId: function() {
        return this._conditionId;
    },

    /**
     * Returns the action
     * @type String
     */
    getActionId: function() {
        return this._action;
    },
    
    /**
     * Destroy the terminal
     */
    destroy: function() {
        this._terminalNode.parentNode.removeChild(this._terminalNode);
        this.removeAllWires();
        this.remove();
    },
    
    /**
     * Updates the position when the container is moving
     */
    updatePosition: function() {
        this._recalculatePosition();
        this.redrawAllWires();        
    },
    
    /**
     * Adds a handler for the conection or deconection of wires
     */
    onWireHandler: function(/** Function */ handler) {
        var context = {
            'handler': handler
        }
        this.eventAddWire.subscribe(this._wireAddHandler.bind(context));
        this.eventRemoveWire.subscribe(this._wireRemoveHandler.bind(context));    
    },
    
     // **************** PRIVATE METHODS **************** //
     /**
      * @private
      */
    _recalculatePosition: function() {
        var position = Utils.getPosition(this._conditionNode);
        var style = {
            'top': position.top + 'px',
            'left':  position.left + 'px'
        }     
        this._terminalNode.setStyle(style);
    },
    /**
     * @private
     */
    _wireAddHandler: function(/** Event */ event, /** Array */ params) {
        this.handler(event, params, true);    
    },
    /**
     * @private
     */
    _wireRemoveHandler: function(/** Event */ event, /** Array */ params) {
        this.handler(event, params, false);    
    }
});