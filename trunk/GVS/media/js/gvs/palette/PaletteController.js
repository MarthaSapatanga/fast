var PaletteController = Class.create(
    /** @lends PaletteController.prototype */ {

    /**
     * Manages a set of palettes.
     * @constructs
     */
    initialize: function(/** String */ docId) {
        /**
         * List of available palettes
         * @type {Hash}
         * @private
         */
        this._palettes = {};
        this._containerNode = null;
        
        var uidGenerator = UIDGeneratorSingleton.getInstance();
        this._paletteId = uidGenerator.generate("palette");

        // TODO: create all the palettes
        var screenPalette = new Palette("screen", docId);
        var connectorPalette = new Palette("connector", docId);
        var domainConceptPalette = new Palette("domainConcept", docId);
        this._palettes["screen"] = screenPalette;
        this._palettes["connector"] = connectorPalette;
        this._palettes["domainConcept"] = domainConceptPalette;
        
        this.getNode().addChild(screenPalette.getNode());
        this.getNode().addChild(connectorPalette.getNode());
        this.getNode().addChild(domainConceptPalette.getNode());
    },

    // **************** PUBLIC METHODS **************** //

    /**
     * Updates each palette
     */
    updatePalettes: function () {
        $H(this._palettes).each (function (pair){
            pair.value.updateComponents();
        });
    },
    
    /**
     * Paints each palette from each factory
     */
    paintPalettes: function () {
        $H(this._palettes).each (function (pair){
            pair.value.paintComponents();
        });
    },

    getPalette: function (/** String */ type) {
        return this._palettes[type];
    },
    
    getNode: function() {
        if(this._containerNode == null){
            var palettePane = new dijit.layout.AccordionContainer({
                "id":this._paletteId,
                "class":"palettePane",
                "region":"left",
                "minSize":"170",
                "maxSize":"300",
                "splitter":"true",
                "livesplitters":"false"
                });
            this._containerNode = palettePane;
        }
        return this._containerNode;
    }

    // **************** PRIVATE METHODS **************** //
    
});

// vim:ts=4:sw=4:et: 
