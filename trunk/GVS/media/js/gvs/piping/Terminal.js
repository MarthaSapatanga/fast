/*...............................licence...........................................
 *
 *    (C) Copyright 2011 FAST Consortium
 *
 *     This file is part of FAST Platform.
 *
 *     FAST Platform is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FAST Platform is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with FAST Platform.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Info about members and contributors of the FAST Consortium
 *     is available at
 *
 *     http://fast.morfeo-project.eu
 *
 *...............................licence...........................................*/
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
 */
var Terminal = function(/** DOMNode */ conditionNode, /** Object */ options,
                        /** ComponentInstance */ instance,
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
        'title': this._conditionNode.title
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
    var extendedOptions = {};
    extendedOptions = Object.extend(extendedOptions, options);
    extendedOptions.wireConfig = Object.extend(wireConfig, options.wireConfig);

    WireIt.Terminal.call(this, this._terminalNode, extendedOptions);

    /**
     * Instance in where the terminal is
     * @type ComponentInstance
     * @private
     */
    this._instance = instance;

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
    getBuildingBlockUri: function() {
        // FIXME: extrange situation for prepost instances,
        // when building
        var uri;
        if (this._instance.constructor == PrePostInstance) {
            uri = null;
        } else {
            uri = this._instance.getUri();
        }
        return uri;
    },

    /**
     * Returns the resourceId
     * @type String
     */
    getBuildingBlockId: function() {
        // FIXME: extrange situation for prepost instances,
        // when building
        var id;
        if (this._instance.constructor == PrePostInstance) {
            id = "";
        } else {
            id = this._instance.getId();
        }
        return id;
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
     * Returns the instance
     * @type ComponentInstance
     */
    getInstance: function() {
        return this._instance;
    },

    /**
     * Destroy the terminal
     */
    destroy: function() {
        this.removeAllWires();
        this.remove();
        this._terminalNode.parentNode.removeChild(this._terminalNode);
    },

    /**
     * Updates the position when the container is moving
     */
    updatePosition: function() {
        this._recalculatePosition();
        this.redrawAllWires();
    },

    /**
     * Adds a handler listening for the connection or deconnection of wires
     */
    onWireHandler: function(/** Hash */ handlers) {
    	var context = {};
    	context = Object.extend(context, handlers);
    	context['refTerminal'] = this;

        this.eventAddWire.subscribe(this._wireAddHandler.bind(context));
        this.eventRemoveWire.subscribe(this._wireRemoveHandler.bind(context));
    },

    /**
     * Sets the visibility of the terminal
     */
    setVisible: function(/** Boolean */ visible) {
        var style = {
            'visibility': visible ? 'visible': 'hidden'
        };
        this.el.setStyle(style);
    },

     // **************** PRIVATE METHODS **************** //


     /**
      * Recalculates the position of the terminal
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
     * Handler called whenever a new wire is added to the terminal
     * @private
     */
    _wireAddHandler: function(/** Event */ event, /** Array */ params) {
    	var wire = params[0];
    	if (wire.terminal1.parentEl && wire.terminal2.parentEl) {
    		if (wire.terminal1 == this.refTerminal) {
    			this.onPipeCreation(wire);
    		}
    	} else if (wire.terminal1 == this.refTerminal) {
    		this.onPipeCreationStart(wire, wire.terminal1);
    	} else if (wire.terminal2 == this.refTerminal) {
    		this.onPipeCreationStart(wire, wire.terminal2);
    	}
    },


    /**
     * Handler called whenever a wire is removed from the terminal
     * @private
     */
    _wireRemoveHandler: function(/** Event */ event, /** Array */ params) {
    	var wire = params[0];
    	if (wire.terminal1.parentEl && wire.terminal2.parentEl) {
    		if (wire.terminal1 == this.refTerminal) {
    			this.onPipeDeletion(wire);
    		}
    	} else if (wire.terminal1 == this.refTerminal || wire.terminal2 == this.refTerminal) {
    		this.onPipeCreationCancel(wire);
    	}
    }
});
