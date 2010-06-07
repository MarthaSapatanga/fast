var ResourceInstance = Class.create(ComponentInstance,
    /** @lends ResourceInstance.prototype */ {

    /**
     * Pre or Post instance.
     * @constructs
     * @extends ComponentInstance
     */
    initialize: function($super, /** BuildingBlockDescription */ description,
            /** InferenceEngine */ inferenceEngine) {
        $super(description, inferenceEngine);

        /**
         * Terminals for screen design
         * @type Array
         * @private
         */
        this._terminals = new Hash();

    },

    // **************** PUBLIC METHODS **************** //

    /**
     * Creates the terminal
     */
    createTerminals: function(/** Function */ handlers) {
        var options = {
            'direction':[0,1],
            'offsetPosition': {
                'top': -6,
                'left': 4
            },
            'wireConfig': {
                'drawingMethod': 'arrows'
            }
        };
        var actionTerminals;
        this._buildingBlockDescription.actions.each(function(action) {
        	actionTerminals = new Hash();
            action.preconditions.each(function(pre) {
                  options.ddConfig = {
                     'type': 'input',
                     'allowedTypes': ['output']
                  };
                var node = this._view.getConditionNode(pre.id, action.name);
                var terminal = new Terminal(node, options, this, pre.id, action.name);
                terminal.onWireHandler(handlers);
                actionTerminals.set(pre.id, terminal);
            }.bind(this));
            this._terminals.set(action.name, actionTerminals);
        }.bind(this));

        var posts;
        if (this._buildingBlockDescription.postconditions &&
                this._buildingBlockDescription.postconditions[0] instanceof Array) {
            posts = this._buildingBlockDescription.postconditions[0];
        } else {
            posts = this._buildingBlockDescription.postconditions;
        }

        var postconditionTerminals = new Hash();
        posts.each(function(post) {
            options.alwaysSrc = true;
            options.ddConfig = {
                 'type': 'output',
                 'allowedTypes': ['input']
            };
            var node = this._view.getConditionNode(post.id, "postconditions");
            var terminal = new Terminal(node, options, this, post.id);
            terminal.onWireHandler(handlers);
            postconditionTerminals.set(post.id, terminal);
        }.bind(this));
        this._terminals.set("postconditions", postconditionTerminals);
    },

    /**
     * Gets a terminal from an id
     * @type Terminal
     */
    getTerminal: function(/** String */ id, /** String (Optional) */ _action) {
        var terminal = null;
        if (_action) {
            var actionTerminals = this._terminals.get(_action);
            if (actionTerminals) {
                terminal = actionTerminals.get(id);
            }
        } else {
            terminal = this._terminals.get("postconditions").get(id);
        }
        return terminal;
    },

    /**
     * Destroy the instance
     * @override
     */
    destroy: function($super, /** Boolean */ removeFromServer) {
        if (this._terminals) {
            this._terminals.values().each(function(terminalGroup){
                terminalGroup.values().each(function(terminal){
                    terminal.destroy();
                });
            });
        }
        $super();
    },

    /**
     * On position update
     * @override
     */
    onUpdate: function(/** Number */ x, /** Number */ y) {
        if (this._terminals) {
            this._terminals.values().each(function(terminalGroup) {
                terminalGroup.values().each(function(terminal) {
                    terminal.updatePosition();
                });
            });
        }
    },

    // **************** PRIVATE METHODS **************** //
    /**
     * Creates a new View instance for the component
     * @type BuildingBlockView
     * @override
     */
    _createView: function () {
        return new ResourceView(this._buildingBlockDescription);
    }

});

// vim:ts=4:sw=4:et:
