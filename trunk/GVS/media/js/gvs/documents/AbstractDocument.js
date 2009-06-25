var AbstractDocument = Class.create( /** @lends AbstractDocument.prototype */ {
    /**
     * Represents a document and its tab. Subclasses must provide the 
     * inner content.
     * @abstract
     * @constructs
     */ 
    initialize: function(/** String */ title) {
        var uidGenerator = UIDGeneratorSingleton.getInstance();

        /**
         * Accepted building blocks
         * @type {String[]}
         * @private
         */
        this._validBuildingBlocks = [];
        
        /**
         * Tab title
         * @type String
         * @private
         */
        this._title = title;
        
        /**
         * Actual Tab Id
         * @type String
         * @private
         */
        this._tabId = uidGenerator.generate("tab");
        
        /**
         * Tab content id
         * @type String
         * @private
         */
        this._tabContentId = uidGenerator.generate("tabContent");
        
        /**
         * Initial tab content (empty by default)
         * @type DOMNode
         * @private
         */
        this._tabContent = new Element("div", {
            "id":    this._tabContentId,
            "class": "document"
        });        
        
        /**
         * Actual tab
         * @type DOMNode
         * @private
         */
        this._tab = new dijit.layout.ContentPane({
            title: this._title,
            id: this._tabId,
            closable: true
        }, null);
        
        /**
         * Document Type
         * @type String
         * @private
         */       
        this._documentType=null;
        
        /**
         * Palette Controller
         * @type PaletteController
         * @private
         */ 
        this._paletteController = new PaletteController(this.getTabId());
        
        this._tab.setContent(this._tabContent);
    },
    

    // **************** PUBLIC METHODS **************** //
    

    /**
     * Returns the list of valid building blocks for the document type
     * @type {String[]}
     */
    getValidBuildingBlocks: function () {
        return this._validBuildingBlocks;
    },

    /**
     * Gets the palette controller
     * @type PaletteController
     * @public
     */
    getPaletteController: function () {
        return this._paletteController;
    },

    /**
     * Gets the tab id.
     * @type String
     */
    getTabId: function () {
        return this._tabId;
    },
    
    
    /**
     * Gets the tab node.
     * @type DOMNode
     */
    getTab: function() {
        return this._tab;
    },
    
    
    /**
     * Gets the title.
     * @type String
     */
    getTitle: function () {
        return this._title;
    },

    /**
     * Gets the content node.
     * @type DOMNode
     */
    getContent: function () {
        return this._tabContent;
    },
    
    
    /**
     * Gets the content id.
     * @type String
     */
    getContentId: function () {
        return this._tabContentId;
    },
    
    /**
     * Gets the document type.
     * @type String
     */
    getDocumentType: function () {
        return this._documentType;
    },

    /**
     * Select this tab.
     */
    select: function() {
        dijit.byId("documentContainer").selectChild(this._tabId);
    }


    // **************** PRIVATE METHODS **************** //
});

// vim:ts=4:sw=4:et:
