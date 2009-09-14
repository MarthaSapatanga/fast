var Palette = Class.create(SetListener, /** @lends Palette.prototype */ {

    /**
     * Represents a palette of droppable components of a given type.
     *
     * @constructs
     * @extends SetListener
     */
    initialize: function(/** BuildingBlockSet */ set, /** DropZone */ dropZone,
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
         * Zone to drop components
         * @type DropZone
         * @private @member
         */
        this._dropZone = dropZone;
        
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
     * Starts data retrieval
     * @public
     */
    startRetrievingData: function() {
        this._set.startRetrievingData();
    },

    /**
     * Gets the node of the accordion pane
     * @type DOMNode
     * @public
     */
    getNode: function() {
        return this._node;
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
            'class': 'paletteContent'
        });
        
        this._node.setContent(this._contentNode);
    },

    /**
     * Updates the palette components from building blocks by querying its building block factory.
     * @private
     */
    _updateComponents: function() {
        var descs = this._set.getBuildingBlocks();
  
        $A(descs).each(
            function(desc) {
                if (!this._components.get(desc.uri)) {
                    this._addComponentFor(desc);
                }
            }.bind(this)
        );
    },

    
    /**
     * Adds a new component to the palette
     */
    _addComponentFor: function(/** BuildingBlockDescription */ desc) {
        var component = null;
        
        switch(this._set.getBuildingBlockType()) {
            case Constants.BuildingBlock.SCREEN:
                component = new ScreenComponent(desc, this._dropZone, this._inferenceEngine);
                break;
                
            case Constants.BuildingBlock.DOMAIN_CONCEPT:
                component = new DomainConceptComponent(desc, this._dropZone, this._inferenceEngine);
                break;
                
            default:
                throw "Unsupported building block type";
        }
        this._components.set(desc.uri, component);

        var separator = new Element("div", {"class": "paletteSeparator"});   
        this._contentNode.appendChild(component.getNode());
        this._contentNode.appendChild(separator);
    }
});

// vim:ts=4:sw=4:et:
