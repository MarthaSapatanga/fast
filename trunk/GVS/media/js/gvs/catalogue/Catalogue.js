/**
 * Local catalogue
 * @constructs
 * @static
 */
var Catalogue = Class.create();

// **************** STATIC ATTRIBUTES **************** //

Object.extend(Catalogue, {
    /**
     * BuildingBlock factories
     * @type Hash
     * @private
     */
    _factories: {
        'screen': new ScreenFactory(),
        'screenflow': new ScreenflowFactory(),
        'domainConcept': new DomainConceptFactory(),
        'form': new FormFactory(),
        'resource': new ResourceFactory(),
        'operator': new OperatorFactory()
    }
});

// **************** STATIC METHODS ******************* //

Object.extend(Catalogue, {

    // **************** PUBLIC METHODS **************** //

    /**
     * Gets a building block factory for a given type of building blocks
     * @type BuildingBlockFactory
     * @public
     */
    getBuildingBlockFactory: function(/** String */buildingBlockType) {
        return this._factories[buildingBlockType];
    },

    getFacts: function() {
        var onSuccess = function(response) {
            var factMetadata = response.responseText.evalJSON();
            FactFactory.setFacts(factMetadata);
        };
        var onError = function(response, e) {
            console.error(e);
        };

        PersistenceEngine.sendGet(URIs.catalogueGetFacts, this, onSuccess, onError);
    },

    getDomainConcepts: function() {
        var onDConceptsSuccess = function(response){
            var domainConceptMetadata = response.responseText.evalJSON();
            this.getBuildingBlockFactory(Constants.BuildingBlock.DOMAIN_CONCEPT).
                updateBuildingBlockDescriptions(domainConceptMetadata.domainConcepts);
            var paletteController = GVS.getDocumentController().getCurrentDocument().
                getPaletteController();
            var domainConceptPalette = paletteController.
                getPalette(Constants.BuildingBlock.DOMAIN_CONCEPT);
            domainConceptPalette.paintComponents();
        };
        var onDConceptsError = function(response, e) {
            console.error(e);
        };

        PersistenceEngine.sendGet(URIs.catalogueGetDomainConcepts,
            this, onDConceptsSuccess, onDConceptsError);
    }
});

// vim:ts=4:sw=4:et:
