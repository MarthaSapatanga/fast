var {{name}} = Class.create(BuildingBlock,{
    /** @constructs */
    initialize: function($super, screenId, buildingBlockId, context){
		$super(screenId, buildingBlockId, context);
    },
    
    manageData: function(triggers, addedFacts, deletedFacts){
    	ScreenEngineFactory.getInstance(this.screenId).manageData(triggers, addedFacts,deletedFacts, this.buildingBlockId);
    },
    
    {{code|safe}}
    
});
    