/*...............................licence...........................................
 *
 *    (C) Copyright 2011 FAST Consortium
 *
 *     This file is part of FAST Platform.
 *
 *     FAST Platform is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FAST Platform is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with FAST Platform.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Info about members and contributors of the FAST Consortium
 *     is available at
 *
 *     http://fast.morfeo-project.eu
 *
 *...............................licence...........................................*/
var ScreenFactory = Class.create(BuildingBlockFactory,
    /** @lends ScreenFactory.prototype */ {

    /**
     * Factory of screen building blocks.
     * @constructs
     * @extends BuildingBlockFactory
     */
    initialize: function($super) {
        $super();

         /**
         * Hash table (organized by URI)
         * containing all the BB descriptions
         * @type Hash
         * @private @member
         */
        this._buildingBlockDescriptions = new Hash();

    },

    // **************** PUBLIC METHODS **************** //
    /**
     * Gets the type of building block this factory mades.
     * @type String
     * @override
     */
    getBuildingBlockType: function (){
        return Constants.BuildingBlock.SCREEN;
    },


    /**
     * Gets building block descriptions by URI
     * @type {BuildingBlockDescription[]}
     * @override
     */
    getBuildingBlocks: function (/** Array */ uris, _includeCopies) {
        var includeCopies = Utils.variableOrDefault(_includeCopies, true);
        var result = new Array();
        var validUris = $A(uris);
        if (!includeCopies) {
            validUris = validUris.findAll(function(uri) {
                return (uri.indexOf("clones") == -1);
            });
        }
        validUris.each(function(uri){
            if(this._buildingBlockDescriptions.get(uri)) {
                result.push(this._buildingBlockDescriptions.get(uri));
            } else {
                throw "Ooops. Something went wrong. " +
                      "ScreenFactory::getBuildingBlocks";
            }
        }.bind(this));
        return result;
    },

    /**
     * This function retrieves the pending elements from the serverside
     * catalogue
     */
    cacheBuildingBlocks: function (/** Array */ uris, /** Function */ callback){
        //URIs not already retrieved
        var pendingURIs = new Array();
        $A(uris).each (function (uri){
            if (!this._buildingBlockDescriptions.get(uri)){
                pendingURIs.push (uri);
            }
        }.bind(this));

        if (pendingURIs.size() > 0) {
            var postData = Object.toJSON(pendingURIs);
            PersistenceEngine.sendPost(URIs.catalogueGetMetadata,
                null, postData,
                {
                    'mine': this,
                    'callback': callback
                },
                this._onSuccess, Utils.onAJAXError);
        } else {
            callback();
        }
    },

    /**
     * Gets a new instance of the type of the factory
     * @override
     * @type ScreenInstance
     */
    getInstance: function(/** BuildingBlockDescription */description, /** InferenceEngine */ engine) {
        return new ScreenInstance(description, engine);
    },

    // **************** PRIVATE METHODS **************** //



    /**
     * Callback function
     */
    _onSuccess: function(/**XMLHttpRequest*/ transport) {
        var metadata = transport.responseText.evalJSON();
        //update the Screen Factory
        this.mine._updateBuildingBlockDescriptions(metadata.screens);
        //call the callback function passed as argument
        this.callback();
    },

    /**
     * This function creates the different screen Descriptions
     * @private
     */
    _updateBuildingBlockDescriptions: function (/** Array */ screenDescriptions) {

        for (var i=0; i< screenDescriptions.length ; i++) {
            this._buildingBlockDescriptions.set(screenDescriptions[i].uri,
                                        new ScreenDescription (screenDescriptions[i]));
        }
    }
});

// vim:ts=4:sw=4:et:
