var ComponentInstance = Class.create(DragSource,
    /** @lends ComponentInstance.prototype */ {

    /**
     * This class is an instance of a palette component
     * in the Document area
     * @constructs
     * @extends DragSource
     */
    initialize: function($super, /**BuildingBlockDescription*/ buildingBlockDescription,
             /** InferenceEngine */ inferenceEngine) {
        $super();

        /**
         * BuildingBlock description this class is instance of
         * @type BuildingBlockDescription
         * @private @member
         */
        this._buildingBlockDescription = buildingBlockDescription;

        /**
         * Identification of the instance inside its container
         * @type String
         * @private
         */
        this._id = null;

        /**
         * Orientation of the instance inside its container
         * @type Integer
         * @private
         */
        this._orientation = 0;


        /**
         * Title of the instance
         * @private
         * @type String
         */
        this._title = null;


        /**
         * Original BuildingBlock URI
         * @private
         * @type String
         */
         this._originalUri = null;

        /**
         * BuildingBlock description graphical representation
         * @type BuildingBlockView
         * @private
         */
        this._view = this._createView();

        this._menu = new MenuOptions(this._view.getNode());
        this._menu.addOption('Delete', function(){
            if (this.document && this.document.deleteInstance) {
                this.document.deleteInstance(this);
            }
        }.bind(this));

        /**
         * BuildingBlock params
         * @type String
         * @private
         */
        this._params = '{}';

        /**
         * Inference engine to receive reachability updates
         * @type InferenceEngine
         * @private
         */
        this._inferenceEngine = inferenceEngine;

        /**
         * Event listener
         * @type Object
         * @private
         */
        this._listener = null;

        this.document = null;

    },

    // **************** PUBLIC METHODS **************** //

    /**
     * Somehow something the user can comprehend
     * Implementing TableModel interface
     * @type String
     */
    getTitle: function() {
        if (!this._title) {
            this._title = this._buildingBlockDescription.getTitle();
        }
        return this._title;
    },

    /**
     * This function returns an array of lines representing the
     * key information of the building block, in order to be shown in
     * a table
     * Implementing TableModel interface
     * @type Hash
     */
    getInfo: function() {
        var info = new Hash();

        info.set('Title', this.getTitle());
        info.set('Description', this._buildingBlockDescription.description['en-gb']);
        info.set('Tags', this._buildingBlockDescription.tags.collect(function(tag) {
                return tag.label['en-gb'];
            }).join(", "));

        var params = document.createElement('div');
        var paramsText = this.getParams();
        var parameterized = paramsText.strip().length > 0;
        if (parameterized) {
            try {
                var json = json = cjson_parse(paramsText);

                parameterized = false;
                for (var i in json) {
                    params.addClassName('json-icon-bg');
                    parameterized = true;
                    break;
                }
            } catch (e) {
                params.addClassName('text-icon-bg');
            }
        }

        if (!parameterized) {
            var span = document.createElement('span');
            span.addClassName('triggerInfo');
            span.appendChild(document.createTextNode('No parameterized'));
            params.appendChild(span);
        }

        var paramsDialog = new ParamsDialog(this.getTitle(),
                               this.getParams(),
                               this._buildingBlockDescription.parameterTemplate,
                               this.setParams.bind(this));
        params.appendChild(paramsDialog.getButtonNode());

        info.set('Parameters', params);
        return info;
    },

    /**
     * Create a copy in the catalogue for the inferencing
     */
    createCatalogueCopy: function(eventHandler) {
        var body = {
            "uri": this._buildingBlockDescription.uri
        };
        var context = {
            "mine": this,
            "eventHandler": eventHandler
        };
        PersistenceEngine.sendPost(URIs.catalogueCopy, null, Object.toJSON(body), context,
                                    this._onCopySuccess, Utils.onAJAXError);
    },

    /**
     * Sets the uri of the instance, after copying.
     */
    setCopyUri: function(uri) {
        var originalUri = this._buildingBlockDescription.uri;
        var bb = this._buildingBlockDescription.clone();
        bb.uri = uri;

        this._buildingBlockDescription = bb;
        this.setOriginalUri(originalUri);
        this._inferenceEngine.addReachabilityListener(this.getUri(),
                                                            this._view);
    },


    /**
     * Adds event listener
     */
    setEventListener: function(/** Object */ listener) {
        this._listener = listener;
        this.document = listener;
    },

    /**
     * Returns the uri of the instance
     * @type String
     */
    getUri: function() {
        return this._buildingBlockDescription.uri;
    },

    /**
     * Sets the URI of the original building block
     */
    setOriginalUri: function(/** String */ uri) {
        this._originalUri = uri;

        this.setId(this.getUri());
    },

    /**
     * @type String
     */
    getOriginalUri: function() {
        return this._originalUri;
    },

    /**
     * Returns the node that can be clicked to start a drag-n-drop operation.
     * @type DOMNode
     * @override
     */
    getHandlerNode: function() {
        return this._view.getNode();
    },

    /**
     * Returns the node that is going to be moved in drag-n-drop operation.
     * @type DOMNode
     * @override
     */
    getDraggableObject: function() {
        return this;
    },

     /**
     * Returns the root node
     * @type DOMNode
     * @public
     */
    getView: function() {
        return this._view;
    },

     /**
     * return the dom element for use in the prototype.js functions
     * @type DOMNode
     * @public
     */
    toElement: function() {
        return this._view.toElement();
    },

    /**
     * Gets the component orientation
     * @type Integer
     * @public
     */
    getOrientation: function() {
        return this._orientation;
    },

    /**
     * Sets the component orientation
     * @params Integer
     * @public
     */
    setOrientation: function(/** Object */ orientation) {
        this._orientation = orientation;
    },

    /**
     * Gets the component params
     * @type Object
     * @public
     */
    getParams: function() {
        return this._params;
    },

    /**
     * Sets the component params
     * @params Object
     * @public
     */
    setParams: function(/** Object */ params) {
        this._params = params;

        if (this._listener && this._listener.modified) {
            this._listener.modified(this);
        }
    },

    /**
     * Sets the title of the instance
     * @public
     */
    setTitle: function(/** String */ title) {
        this._title = title;
        this._view.setTitle(title);

        if (this._listener && this._listener.modified) {
            this._listener.modified(this);
        }
    },

    /**
     * Returns the building block description
     * @public
     */
    getBuildingBlockDescription: function() {
        return this._buildingBlockDescription;
    },


    /**
     * Returns the building block type of this class
     * @public
     */
    getBuildingBlockType: function() {
        return this._buildingBlockType;
    },


    /**
     * Gets the id
     */
    getId: function() {
        return this._id;
    },


    /**
     * Sets the id
     */
    setId: function(id) {
        this._id = id;
    },

    /**
     * Deletes the instance in the catalogue
     */
    deleteInstance: function() {
        if (this._originalUri) {
            var uri = this._buildDeleteUri();
            PersistenceEngine.sendDelete(uri, this, Prototype.emptyFunction, Utils.onAJAXError);
        }
    },

    /**
     * Destroys the view
     * @public
     */
    destroy: function() {
        this._inferenceEngine.removeReachabilityListener(this._buildingBlockDescription.uri, this._view);
        this._view.destroy();
        this._view = null;
    },

    /**
     * On position update
     */
    onUpdate: function(/** Number */ x, /** Number */ y) {},

    /**
     * Drop event handler for the DragSource
     * @param changingZone
     *      True if a new Instance has
     *      been added to the new zone.
     * @override
     */
    onFinish: function(changingZone, /** Object */ position) {
        if (changingZone) {
            this._view.addEventListener (function(event){
                event.stop();
                this._onClick();
            }.bind(this),'mousedown');
            this._view.addEventListener (function(event){
                event.stop();
            },'click');
            this._view.addEventListener (function(event){
                event.stop();
                this._onDoubleClick(event);
            }.bind(this),'dblclick');
        } else {
            if (this._listener && this._listener.positionUpdated) {
                this._listener.positionUpdated(this, position);
            }
        }
        this.onUpdate();
    },

    /**
     * This function returns a list with all the
     * preconditions of the instance,
     * ready to be set in the FactPane
     * @type Array
     */
    getPreconditionTable: function(/** Hash */ reachability) {
        if (reachability != null) {
            var conditions = this._buildingBlockDescription.getPreconditionsList();
            return $A(conditions).map(function(condition){
                return this._getConditionItem(condition, reachability);
            }.bind(this));
        } else {
            return [];
        }
    },

    /**
     * This function returns a list with all the
     * postconditions of the instance,
     * ready to be set in the FactPane
     * @type Array
     */
    getPostconditionTable: function(/** Boolean */ reachability) {
        if (reachability != null) {
            var conditions = this._buildingBlockDescription.getPostconditionsList();
            return $A(conditions).map(function(condition){
                return this._getConditionItem(condition, reachability);
            }.bind(this));
        } else {
            return [];
        }
    },

    /**
     * set position
     */
    setPosition: function(position) {
        this._view.setPosition(position);
    },

    // **************** PRIVATE METHODS **************** //

    /**
     * Creates a new View instance for the component
     * @type BuildingBlockView
     * @abstract
     */
    _createView: function () {
        throw "Abstract Method invocation: ComponentInstance::_createView";
    },

    /**
     * This function is called when the attached view is clicked
     * must be overriden by descendants
     * @private
     */
    _onClick: function (){
        if (this._listener && this._listener.elementClicked) {
            this._listener.elementClicked(this);
        }
    },

    /**
     * This function is called when the attached view is dbl-clicked
     * must be overriden by descendants
     * @private
     */
    _onDoubleClick: function (/** Event */ event){
        if (this._listener && this._listener.elementDblClicked) {
            this._listener.elementDblClicked(this, event);
        }
    },


    /**
     * _OnCopySuccess
     * @private
     */
    _onCopySuccess: function(transport) {
        var response = JSON.parse(transport.responseText);

        // Cloning the BBDescription to assign it the new URI
        this.mine.setCopyUri(response.clone);
        this.eventHandler();

    },


    /**
     * Gets a line of the list
     * @private
     * @type String
     */
    _getConditionItem: function(/** Object */ condition, /** Object */ reachability) {
        var uri = FactFactory.getFactUri(condition);

        var fact = FactFactory.getFactIcon(condition, "embedded").getNode();
        if (reachability.constructor == Hash) {
            Utils.setSatisfeabilityClass(fact, reachability.get(uri));
        } else {
            Utils.setSatisfeabilityClass(fact, reachability);
        }

        if (condition.positive === false) {
            fact.addClassName('negative');
        }

        var description = condition.label['en-gb'];

        return [fact, description, uri];
    },

    /*
     * @private
     * @type String
     */
    _buildDeleteUri: function() {
        if (this.getId()) {
            var originalParts = this.getId().split("/");
            var parts = [
                originalParts[originalParts.length - 3],
                originalParts[originalParts.length - 2],
                originalParts[originalParts.length - 1]
            ];
            var uri = parts.join("/");
            return URIs.catalogueDeleteCopy.replace("<id>", uri);
        } else {
            return "";
        }
    }

});

// vim:ts=4:sw=4:et:
