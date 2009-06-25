var ScreenFactory = Class.create(BuildingBlockFactory,
    /** @lends ScreenFactory.prototype */ {

    /**
     * Factory of screen building blocks.
     * @constructs
     * @extends BuildingBlockFactory
     */
    initialize: function($super) {
        $super();

        this._buildingBlockType = 'screen';
        this._buildingBlockName = 'Screens';
        this._buildingBlockDescriptions = [];
    },

    // **************** PUBLIC METHODS **************** //


    updateBuildingBlockDescriptions: function (screenDescriptions) {

        var screen_metadata = screenDescriptions;
        for (var i=0; i<screen_metadata.length ; i++) {
            this._buildingBlockDescriptions.push(new ScreenDescription (screen_metadata[i]));
        }
    },

    getBuildingBlocks: function (/** Array*/ screenURIs) {
        var screenDescriptions = [];

        for (var i=0; i<screenURIs.length ; i++) {
            for (var j=0; j<this._buildingBlockDescriptions.length ; j++) {
                if (screenURIs[i]==this._buildingBlockDescriptions[j].uri)
                {
                    screenDescriptions.push(this._buildingBlockDescriptions[j]);
                    break;
                }
            }
        }
        return screenDescriptions;
    }


    // **************** PRIVATE METHODS **************** //
});

// vim:ts=4:sw=4:et: 
