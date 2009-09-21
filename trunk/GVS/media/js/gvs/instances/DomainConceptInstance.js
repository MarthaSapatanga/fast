var DomainConceptInstance = Class.create(ComponentInstance,
    /** @lends DomainConceptInstance.prototype */ {

    /**
     * Domain concept instance.
     * @constructs
     * @extends ComponentInstance
     */
    initialize: function($super, /**BuildingBlockDescription*/ domainConceptDescription, 
            /** DropZone */ dropZone, /** InferenceEngine */ inferenceEngine) {
        $super(domainConceptDescription, dropZone, inferenceEngine);
        
        /**
         * @type DomainConceptDialog
         * @private @member
         */
        this._dialog = new DomainConceptDialog(this._onChange.bind(this), this.getTitle());
    },

    // **************** PUBLIC METHODS **************** //

    /**
     * Somehow something the user can comprehend
     * @override
     */
    getTitle: function() {
        return this._buildingBlockDescription.title; 
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
    _onDoubleClick: function (/** Event */ event){
        this._dialog.show();
    },
    
    /**
     * This function is called when the dialog is saved
     * @private
     */
    _onChange: function (/** Hash */ data) {
        this._buildingBlockDescription.addProperties(data);
        
        // TODO: notify
    }
});

// vim:ts=4:sw=4:et:
