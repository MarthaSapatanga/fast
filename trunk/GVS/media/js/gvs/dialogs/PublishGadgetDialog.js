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
var PublishGadgetDialog = Class.create(ConfirmDialog /** @lends PublishGadgetDialog.prototype */, {
    /**
     * This class handles a dialog
     * able to publish a created gadget into a mashup platform
     * @constructs
     * @extends ConfirmDialog
     */
    initialize: function($super) {
        $super("Publish Gadget", ConfirmDialog.OK);

        /**
         * Publication Info
         * @private
         * @type Object
         */
        this._publication = null;

        /**
         * Hash containing the references to all the buttons
         * @type Hash
         * @private
         */
        this._buttons = new Hash();

        /**
         * Standalone Dialog
         * @type StandaloneEmbeddingDialog
         * @private
         */
        this._standaloneDialog = null;
    },

    // **************** PUBLIC METHODS **************** //


    /**
     * show
     * @override
     */
    show: function ($super, /** Object */ publication) {
        this._publication = publication;
        this._initDialogInterface();
        $super();
    },

    // **************** PRIVATE METHODS **************** //

    /**
     * Publishes the created gadget
     * @private
     */
    _publishGadget: function(/** dijit.form.Button */ button, /** Hash */ options) {
        var publicationUrl = options.url;
        if (GlobalOptions.isLocalStorage) {
            if (options.mashupPlatform == 'ezweb') {
                publicationUrl = URIs.ezweb + "interfaces/gadget?template_uri=" + options.url;
            } else if (options.mashupPlatform == 'igoogle') {
                if (options.destination=='directory') {
                    publicationUrl = "http://www.google.com/ig/submit?url=" + options.url;
                } else if (options.destination=='personal'){
                    publicationUrl = "http://www.google.com/ig/adde?moduleurl=" + options.url;
                }
            } else if (options.mashupPlatform == 'orkut') {
                publicationUrl = "http://sandbox.orkut.com/Main#MyApps?appUrl=" + options.url;
            }
        }
        this._deploy(button, options, publicationUrl);
    },

    /**
     * This function deploy a gadget to EzWeb
     * @private
     */
    _deploy: function(/** domNode */ buttonNode, /** Hash */ options, /**String*/ url) {
        var button = dijit.byId(buttonNode.id);
        if (options.disableAfterPublishing){
            button.attr("label", options.doneButtonLabel);
            button.attr("disabled", true);
        }
        if (options.mashupPlatform == 'standalone'){
            if (!this._standaloneDialog){
                this._standaloneDialog = new StandaloneEmbeddingDialog(this._publication, url);
            } else {
                this._standaloneDialog.updateDialog(this._publication, url);
            }
            this._standaloneDialog.show();
        } else {
            window.open(url);
        }
        console.log(url);
    },

    /**
     * initDialogInterface
     * This function creates the dom structure
     * @private
     * @override
     */
    _initDialogInterface: function () {
        var dom = new Element('div');

        var title = new Element('h2', {
            'class': 'detailsTitle'
        }).update("Publish Gadget");
        dom.appendChild(title);


        var tableData = [
            {'className': 'tableHeader',
             'fields': [{
                    'className': 'left',
                    'node': 'Mashup platform'
                 },{
                    'className': 'left',
                    'node': ''
                 }]
            }
        ];

        var gadgets = new Hash(this._publication.gadgets);

        var hasGadget = false;
        gadgets.each(function(gadget) {
                var destinations = PublishGadgetDialog.GADGET_DESTINATIONS.get(gadget.key);
                destinations.each(function(destination) {
                    if (gadget.value) {
                        destination.url = gadget.value;
                        tableData.push(this._createPlatformRow(destination));
                        hasGadget = true;
                    }
                }.bind(this));
            }.bind(this));

        var contents = new Element('div', {
            'class': 'deployment'
        });
        dom.appendChild(contents);

        if (hasGadget) {
            var table = new Element('table');
            contents.appendChild(table);
            this._fillTable(table, tableData);
        } else {
            contents.update("Upps! There are no available gadgets");
        }
        this._setContent(dom);
    },

    /**
     * This function create a row for a destination platform
     * @private
     */
    _createPlatformRow: function (/** Hash */ options) {
        return {'className': '',
            'fields': [{
                'className': 'mashup',
                'node': options.title + ' [<a href="' + options.url + '" target="blank">' + options.urlLabel + '</a>]'
             },{
                'className': '',
                'node': this._buttons.set(options.id, new dijit.form.Button({
                    'label': options.buttonLabel,
                    'onClick': function(e) {
                                this._publishGadget(e.element(), options);
                            }.bind(this)
                })).domNode
             }]
         };
    },

    /**
     * This function fills a table with data
     * @private
     */
    _fillTable: function(/** DOMNode */ tableNode, /** Array */ data) {
        data.each(function(row) {
            var tr = new Element('tr', {
                'class': row.className
            });
            row.fields.each(function(field) {
                var td = new Element('td', {
                    'class': field.className
                }).update(field.node);
                tr.appendChild(td);
            });
            tableNode.appendChild(tr);
        });
    }
});

//STATIC ATTRIBUTES
PublishGadgetDialog.GADGET_DESTINATIONS = new Hash();
PublishGadgetDialog.GADGET_DESTINATIONS.set('ezweb',
        [
             {	id: 'ezweb',
                 mashupPlatform: 'ezweb',
                title: 'EzWeb',
                urlLabel: 'Template',
                buttonLabel: 'Publish it!',
                disableAfterPublishing: true,
                doneButtonLabel: 'Done'
            }
        ]);
PublishGadgetDialog.GADGET_DESTINATIONS.set('google',
        [
             {	id: 'igoogleDirectory',
                mashupPlatform: 'igoogle',
                destination: 'directory',
                title: 'iGoogle Directory',
                urlLabel: 'Template',
                buttonLabel: 'Publish it!',
                disableAfterPublishing: true,
                doneButtonLabel: 'Done'
            },
            {	id: 'igooglePersonal',
                mashupPlatform: 'igoogle',
                destination: 'personal',
                title: 'iGoogle Personal Page',
                urlLabel: 'Template',
                buttonLabel: 'Publish it!',
                disableAfterPublishing: true,
                doneButtonLabel: 'Done'
            },
            {	id: 'orkut',
                mashupPlatform: 'orkut',
                title: 'Orkut',
                urlLabel: 'Template',
                buttonLabel: 'Publish it!',
                disableAfterPublishing: true,
                doneButtonLabel: 'Done'
            }
        ]);
PublishGadgetDialog.GADGET_DESTINATIONS.set('player',
        [
            {	id: 'standalone',
                mashupPlatform: 'standalone',
                title: 'Standalone',
                urlLabel: 'HTML',
                buttonLabel: 'Embed it!',
                disableAfterPublishing: false
            }
        ]);
PublishGadgetDialog.GADGET_DESTINATIONS.set('standalone',
        [
            {	id: 'standalone',
                mashupPlatform: 'standalone',
                title: 'Standalone',
                urlLabel: 'HTML',
                buttonLabel: 'Embed it!',
                disableAfterPublishing: false
            }
        ]);
PublishGadgetDialog.GADGET_DESTINATIONS.set('beemboard',
        [
            {	id: 'beemboard',
                mashupPlatform: 'beemboard',
                title: 'Beemboard',
                urlLabel: 'HTML',
                buttonLabel: 'Publish it!',
                disableAfterPublishing: true,
                doneButtonLabel: 'Done'
            }
        ]);

// vim:ts=4:sw=4:et:
