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
var PlanInstance = Class.create(ComponentInstance,
    /** @lends PlanInstance.prototype */ {

    /**
     * Plan instance.
     * @constructs
     * @extends ComponentInstance
     */
    initialize: function($super, /** Array */ plan,
            /** Array */ dropZones, /** InferenceEngine */ inferenceEngine) {

        /**
         * The plan
         * @type Array
         * @private
         */
        this._plan = plan;

        $super([], dropZones, inferenceEngine);
    },

    // **************** PUBLIC METHODS **************** //

    /**
     * Somehow something the user can comprehend
     * @override
     */
    getTitle: function() {
        return "";
    },
    /**
     * @override
     */
    getInfo: function() {
        return new Hash();
    },

    /**
     * @override
     */
    getUri: function() {
        return "";
    },

    /**
     * Returns the plan
     * @type Array
     */
    getPlanElements: function() {
        return this._plan;
    },

    // **************** PRIVATE METHODS **************** //
    /**
     * Creates a new View instance for the component
     * @type BuildingBlockView
     * @override
     */
    _createView: function () {
        return new PlanView(this._plan);
    }
});

// vim:ts=4:sw=4:et:
