var Palette = Class.create(SetListener, /** @lends Palette.prototype */ {

    /**
     * Represents a palette of droppable components of a given type.
     *
     * @constructs
     * @extends SetListener
     */
    initialize: function(/** BuildingBlockSet */ set, /** Array */ dropZones,
            /** InferenceEngine */ inferenceEngine) {
        /**
         * @private @member
         * @type InferenceEngine
         */
        this._inferenceEngine = inferenceEngine;

        /**
         * Building block set
         * @type BuildingBlockSet
         * @private @member
         */
        this._set = set;

        /**
         * Zones to drop components
         * @type Array
         * @private @member
         */
        this._dropZones = dropZones;

        /**
         * Collection of components the palette offers.
         * @type Hash   Hash of URI to PaletteComponent
         * @private @member
         */
        this._components = new Hash();

        /**
         * Accordion pane node.
         * @type DOMNode
         * @private @member
         */
        this._node = null;

        /**
         * Palette content
         * @type DOMNode
         * @private @member
         */
        this._contentNode = null;

        this._renderUI();
        this._set.setListener(this);
    },

    // **************** PUBLIC METHODS **************** //

    /**
     * Gets the node of the accordion pane
     * @type DOMNode
     * @public
     */
    getNode: function() {
        return this._node;
    },

    /**
     * Gets the node of the contents
     * @type DOMNode
     * @public
     */
    getContentNode: function() {
        return this._contentNode;
    },

    /**
     * This function will be called whenever
     * the set of building blocks changes
     * @overrides
     */
    setChanged: function () {
        this._updateComponents();
    },

    getBuildingBlockSet: function() {
        return this._set;
    },

    /**
     * All uris of all the components
     */
    getComponentUris: function() {
        var uris = [];
        this._set.getBuildingBlocks().each(function(buildingBlock) {
            uris.push({
                uri: buildingBlock.uri
            });
        });
        return uris;
    },

    // **************** PRIVATE METHODS **************** //

    /**
     * Creates the GUI stuff that shows the content: components and separators.
     * @type DOMNode
     * @private
     */
    _renderUI: function() {
        this._node = new dijit.layout.AccordionPane({
            'title':this._set.getBuildingBlockName(),
            'class':'paletteElement'
        });

        this._contentNode = new Element('div', {
            'class':'paletteContent'
        });

        this._searchBox = new PaletteSearchBox();
        this._searchBox.addEventListener(this._filterComponents.bind(this));
        this._node.setContent(this._searchBox.getDOMNode());

        this._searchBox.getDOMNode().insert({after:this._contentNode});
    },

    /**
     * Updates the palette components from building blocks by querying its building block factory.
     * @private
     */
    _updateComponents: function() {
        var descs = $A(this._set.getBuildingBlocks());
        var sortDescs = descs.sortBy(function(desc){ return desc.getTitle() });

        var newComponent = false;
        var component;
        var lastComponent;
        for (var i=0, desc; desc = sortDescs[i]; i++) {
            component = this._components.get(desc.uri);
            if (!component) {
                component = this._buildComponentFor(desc);
                if (lastComponent) {
                    lastComponent.insert({after: component.getNode()});
                } else {
                    this._contentNode.appendChild(component.getNode())
                }
                component.getNode().insert({after: new Element("div", {"class": "paletteSeparator"})});
                newComponent = true;
            }
            lastComponent = component.getNode().next();
        }

        this._filterComponents();
        if (newComponent) {
            Utils.showMessage("Building blocks loaded", {'hide': true});
        }
    },

    /**
     * Hiden component if not match filter
     */
    _filterComponents: (function() {
        function matchFacts(svalue, facts){
            if (!facts) { return false }
            return facts.any(function(fact){
                return fact.textContent.toLowerCase().match(svalue)
            });
        }

        return function(input) {
            var svalue = this._searchBox.getValue().toLowerCase();
            var slots = this._contentNode.select('.slot');
            for(var i = 0; i < slots.length; i++) {
                var slot = slots[i];
                if (svalue.blank() ||
                    slot.select('.slotTitle').first().textContent.toLowerCase().match(svalue)||
                    matchFacts(svalue, slot.select('.contentLayer'))
                ) {
                    slot.show();
                } else {
                    slot.hide();
                }
            }
        }
    })(),

    /**
     * Build a new component
     */
    _buildComponentFor: function(/** BuildingBlockDescription */ desc) {
        var component = null;

        switch(this._set.getBuildingBlockType()) {
            case Constants.BuildingBlock.SCREEN:
                component = new ScreenComponent(desc, this._dropZones, this._inferenceEngine);
                break;

            case Constants.BuildingBlock.DOMAIN_CONCEPT:
                component = new DomainConceptComponent(desc, this._dropZones, this._inferenceEngine);
                break;

            case Constants.BuildingBlock.FORM:
                component = new FormComponent(desc, this._dropZones, this._inferenceEngine);
                break;

            case Constants.BuildingBlock.RESOURCE:
                component = new ResourceComponent(desc, this._dropZones, this._inferenceEngine);
                break;

            case Constants.BuildingBlock.OPERATOR:
                component = new OperatorComponent(desc, this._dropZones, this._inferenceEngine);
                break;

            default:
                throw "Unsupported building block type";
        }
        this._components.set(desc.uri, component);

        return component;
    }
});

// vim:ts=4:sw=4:et:
