var ScreenInstance = Class.create(ComponentInstance,
    /** @lends ScreenInstance.prototype */ {

    /**
     * Screen instance.
     * @constructs
     * @extends ComponentInstance
     */
    initialize: function($super, /**ResourceDescription*/ resourceDescription) {
        $super(resourceDescription);
        var uidGenerator = UIDGeneratorSingleton.getInstance();
        this._id = uidGenerator.generate("screenInstance");
    },

    // **************** PUBLIC METHODS **************** //

    /**
     * Drop event handler for the DragSource
     * @param finishState
     *      True if a new ScreenInstance has
     *      been added to the new zone.
     * @override
     */
    onDragFinish: function(finishState) {
        // FIXME: remove this
        if(finishState) {
            GVSSingleton.getInstance().getDocumentController().getCurrentDocument().addScreen(this);
            UIUtils.onClick(null, this.getView().getId());
            this.getView().getNode().observe('click', UIUtils.onClick);
            //TODO check to the catalogue
            // get the screens from the canvas
            var canvas = [];
            var domainContext = {'tags':null, 'user':null};
            //element list is empty TODO get the actual element list from the palette
            var elements = [];
            if (URIs.CATALOGUE_FLOW=='check'){
                CatalogueSingleton.getInstance().check(canvas, domainContext, elements, 'reachability');
            } else {
                CatalogueSingleton.getInstance().get_screens(canvas, domainContext, elements, 'reachability');
            }
        }
    }

    // **************** PRIVATE METHODS **************** //

});

// vim:ts=4:sw=4:et:
