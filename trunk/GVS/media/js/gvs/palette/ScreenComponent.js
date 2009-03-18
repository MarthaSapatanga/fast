var ScreenComponent = Class.create(PaletteComponent, 
    /** @lends ScreenComponent.prototype */ {
        
    /**
     * Palette component of a screen resource.
     * @param ResourceDescription screenResourceDescription
     * @constructs
     * @extends PaletteComponent
     */
    initialize: function($super, screenResourceDescription) {

        $super(screenResourceDescription);

    },


    // **************** PUBLIC METHODS **************** //


    // **************** PRIVATE METHODS **************** //
    

    /**
     * Creates a new screen to be dragged.
     * @type ScreenInstance
     * @override
     */
    _createInstance: function () {
        return new ScreenInstance(this._resourceDescription);
    },
    
    /**
     * Drop event handler for the DragSource
     * @override
     */
    // FIXME: is this method useful?
    onFinish: function(finishedOK, draggedObject) {}
});

// vim:ts=4:sw=4:et:
