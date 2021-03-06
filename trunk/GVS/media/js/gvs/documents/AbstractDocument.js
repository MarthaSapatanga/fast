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
var AbstractDocument = Class.create(ToolbarModel, /** @lends AbstractDocument.prototype */ {
    /**
     * Represents a document and its tab. Subclasses must provide the
     * inner content.
     * @extends ToolbarModel
     * @abstract
     * @constructs
     */
    initialize: function($super, /** String */ title,
        /** Boolean (Optional) */ _isWidgetProvided) {
        $super();

        var isWidgetProvided = Utils.variableOrDefault(_isWidgetProvided, false);

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
        this._tabId = UIDGenerator.generate("tab");


        /**
         * Initial tab content (empty by default)
         * @type DOMNode
         * @private
         */
        this._tabContent = new Element("div", {
            "class": "document"
        });

        /**
         * Actual tab
         * @type dijit.layout.ContentPane
         * @private
         */
        this._tab = null;

        if (!isWidgetProvided) {
            this._tab = new dijit.layout.ContentPane({
                title: this._title,
                id: this._tabId,
                closable: true,
                onClose: this._closeDocument.bind(this)
            }, null);

            this._tab.setContent(this._tabContent);
        } else {
            this._tab = this._getWidget();
        }
    },

    // **************** PUBLIC METHODS **************** //

    /**
     * Gets the tab id.
     * @type String
     */
    getTabId: function() {
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
    getTitle: function() {
        return this._title;
    },

    /**
     * Gets the content node.
     * @type DOMNode
     */
    getNode: function() {
        return this._tabContent;
    },

    /**
     * Method called on del. Might be overloaded
     */
    onKeyPressed: function(/** String */ key) {
        // Do nothing
    },


    /**
     * Implementing menu model interface
     * @type Object
     */
    getMenuElements: function() {
        return {};
    },

    show: function() {
        // Do nothing
    },

    hide: function() {
        // Do nothing
    },

    // **************** PRIVATE METHODS **************** //

    /**
     * Close document event handler.
     * @private
     */
    _closeDocument: function() {
        GVS.getDocumentController().closeDocument(this._tabId);
        return true;
    },

    /**
     * Sets the document title
     * @private
     */
    _setTitle: function(/** String */ title) {
        this._title = title;
        this._tab.attr("title", this._title);
    }
});

// vim:ts=4:sw=4:et:
