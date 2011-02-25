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
var PrePostInstance = Class.create(ComponentInstance,
    /** @lends PrePostInstance.prototype */ {

    /**
     * Pre or Post instance.
     * @constructs
     * @extends ComponentInstance
     */
    initialize: function($super, /** BuildingBlockDescription */ domainConceptDescription,
            /** InferenceEngine */ inferenceEngine, /** Boolean (Optional) */ _isConfigurable) {

        $super(domainConceptDescription.clone(), inferenceEngine);

        if (!this._buildingBlockDescription.pattern) {
            this._buildingBlockDescription.pattern = "?x " +
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#type " +
                this._buildingBlockDescription.uri;
        }


        if (!this._buildingBlockDescription.label) {
            this._buildingBlockDescription.label = {
                'en-gb': this._buildingBlockDescription.title
            };
        }

        if (this._buildingBlockDescription.id) {
            this._id = this._buildingBlockDescription.id;
        }

        /**
         * @type DomainConceptDialog
         * @private @member
         */
        this._dialog = null;


        /**
         * @private @member
         * @type Function
         */
        this._changeHandler = null;


        /**
         * Terminal for screen design
         * @type Terminal
         * @private
         */
        this._terminal = null;

        /**
         * It states if the instance
         * can be configured by the user
         * @type Boolean
         * @private
         */
        this._isConfigurable = Utils.variableOrDefault(_isConfigurable, true);
    },

    // **************** PUBLIC METHODS **************** //

    /**
     * This function returns the relevant info
     * to the properties table
     * Implementing TableModel interface
     * @overrides
     */
    getInfo: function() {
        var info = new Hash();
        info.set('Title', this.getTitle());
        info.set('Uri', this._buildingBlockDescription.uri);
        info.set('Type', this.getType());
        if (this._buildingBlockDescription.properties) {
            info.set('EzWeb Binding', this._buildingBlockDescription.properties.ezweb.binding);
            info.set('Friendcode', this._buildingBlockDescription.properties.ezweb.friendcode);
            info.set('Variable Name',this._buildingBlockDescription.properties.ezweb.variableName);
        }
        return info;
    },

    /**
     * Returning the type in {pre|post}
     */
    getType: function() {
        return this._buildingBlockDescription.type;
    },


    /**
     * Returns an object representing
     * the fact
     * @type Object
     */
    toJSONForScreen: function() {
        return {
                'label': this._buildingBlockDescription.label,
                'pattern': this._buildingBlockDescription.pattern,
                'positive': true,
                'uri': this._buildingBlockDescription.uri,
                'id': this.getId(),
                'type': this._buildingBlockDescription.type
            };
    },


    /**
     * Returns an object representing
     * the fact
     * @type Object
     */
    toJSONForScreenflow: function() {
        return {
            'conditions': [this._getCondition()],
            'id': this.getId(),
            'semantics': this._buildingBlockDescription.uri,
            'binding': this._buildingBlockDescription.properties.ezweb.binding,
            'friendcode': this._buildingBlockDescription.properties.ezweb.friendcode,
            'variableName': this._buildingBlockDescription.properties.ezweb.variableName,
            'label': this.getTitle(),
            'type': this._buildingBlockDescription.type,
            'uri': this._buildingBlockDescription.uri
        };
    },


    /**
     * Returns an object representing
     * the fact
     * @type Object
     * @private
     */
    toJSONForCatalogue: function() {
        return this._getCondition();
    },

    /**
     * @override
     */
    getUri: function() {
        return this._id;
    },

    /**
     * Set the type in pre | post
     */
    setType: function(/** String */ type) {
        this._buildingBlockDescription.type = type;
        if (this._isConfigurable) {
            var data = {
                'label': this.getTitle()
            };
            if (this._buildingBlockDescription.properties) {
                data = Object.extend(data, this._buildingBlockDescription.properties.ezweb);
            } else {
                if (this._buildingBlockDescription.binding) {
                    data = Object.extend (data, {
                        'binding': this._buildingBlockDescription.binding,
                        'variableName': this._buildingBlockDescription.variableName,
                        'friendcode': this._buildingBlockDescription.friendcode
                    });
                } else {
                     data = Object.extend(data, {
                        'binding': this._buildingBlockDescription.type == 'pre' ? 'slot' : 'event',
                        'variableName':
                        this._buildingBlockDescription.getUriShortcut().replace(/\s+/gi,""),
                        'friendcode': this._buildingBlockDescription.getUriShortcut().replace(/\s+/gi, "")
                    });
                }

            }
            this._onChange(data);
        } else {
            this._onClick();
        }
    },

    /**
     * Sets the configurable status
     */
    setConfigurable: function(/** Boolean */ configurable) {
        this._isConfigurable = configurable;
    },

    /**
     * Due to the slopy catalogue implementation, uri changes can
     * be notified via handler.
     * @public
     */
    setChangeHandler: function(/** Function */ handler) {
        this._changeHandler = handler;
    },

    /**
     * This function shows the dialog to change
     * the instance properties
     */
    showPreviewDialog: function () {
        if (this._isConfigurable) {
            if (!this._dialog) {
                this._dialog = new PrePostDialog(this._onChange.bind(this),
                                                 this._buildingBlockDescription);
            }
            this._dialog.show();
        }
    },

    /**
     * Returns a list the
     * information about the instance
     * ready to be set in the FactPane
     * @type Array
     */
    getConditionTable: function() {
        var fact = FactFactory.getFactIcon(this._getCondition(), "embedded").getNode();
        var reachable;
        if (this._reachable != null) {
            reachable = this._reachable;
        } else {
            reachable = this._view.getReachability();
        }
        Utils.setSatisfeabilityClass(fact, reachable);

        return [fact, this.getTitle(), this._buildingBlockDescription.uri];
    },

    /**
     * Creates the terminal
     */
    createTerminal: function(/** Function (Optional) */ _handler) {
        var options = {
            'direction':[],
            'offsetPosition': {},
            'wireConfig': {
                'drawingMethod': 'arrows'
            }
        };
        if (this._buildingBlockDescription.type == 'pre') {
            options.alwaysSrc = true;
            options.direction = [1,0];
            options.offsetPosition = {
                'top': 9,
                'left': 26
            };
            options.ddConfig = {// A precondition in screen design is an output
                                // (data to be consumed inside the screen)
                'type': 'output',
                'allowedTypes': ['input']
            };
        } else {
            options.direction = [-1,0];
            options.offsetPosition = {
                'top': 9,
                'left': -8
            };
            options.ddConfig = { // Viceversa
                'type': 'input',
                'allowedTypes': ['output']
            };
        }

        this._terminal = new Terminal(this._view.getNode(), options, this,
                                        this.getId());
        if (this._buildingBlockDescription.type == 'pre') {
            this._terminal.onWireHandler(_handler);
        }
    },

    /**
     * Gets the terminal
     * @type Terminal
     */
    getTerminal: function() {
        return this._terminal;
    },

    /**
     * Destroy the instance
     * @override
     */
    destroy: function($super, /** Boolean */ removeFromServer) {
        if (this._terminal) {
            this._terminal.destroy();
            this._terminal = null;
        }
        $super();
    },

    /**
     * On position update
     * @override
     */
    onUpdate: function(/** Number */ x, /** Number */ y) {
        if (this._terminal) {
            this._terminal.updatePosition();
        }
    },

    // **************** PRIVATE METHODS **************** //
    /**
     * Creates a new View instance for the component
     * @type BuildingBlockView
     * @override
     */
    _createView: function () {
        return new DomainConceptView(this._buildingBlockDescription);
    },

    /**
     * This function is called when the attached view is dbl-clicked
     * @private
     * @override
     */
    _onDoubleClick: function (/** Event */ event) {
        this.showPreviewDialog();
    },

    /**
     * Returns an object representing
     * a single fact
     * @type Object
     * @private
     */
    _getCondition: function() {
        return {
                'label': {
                    'en-gb': this.getTitle()
                },
                'pattern': this._buildingBlockDescription.pattern,
                'positive': true,
                'uri': this._buildingBlockDescription.uri,
                'id': this._id
            };
    },


    /**
     * This function is called when the dialog is saved
     * @private
     */
    _onChange: function (/** Object */ data) {
        this._buildingBlockDescription.title = data.label;
        if (data.binding) {
            this._buildingBlockDescription.properties = {
                'ezweb': {
                    'binding': data.binding,
                    'variableName': data.variableName,
                    'friendcode': data.friendcode
                }
            };
        }
        this._onClick();
    }

});

// vim:ts=4:sw=4:et:
