var BuildingBlockDocument = Class.create(PaletteDocument, /** @lends BuildingBlockDocument.prototype */ {
    /**
     *
     * @abstract
     * @extends PaletteDocument
     * @constructs
     */
    initialize: function ($super,/** Object */ properties) {
        this._type = properties['type'];
        this._uri = URIs[this._type];
        this._codeURI = properties['code'];
        this._codeInline = properties['codeInline'];

        this._jsonText = new Element("textarea");
        this._jsonContainer = new Element('div', {
            'class': 'codeContainer'
        }).update(this._jsonText);

        this._codeText = new Element("textarea");
        this._codeContainer = new Element('div', {
            'class': 'codeContainer'
        }).update(this._codeText);

        $super("Building Block", properties, new DumbInferenceEngine());
        this._start();
    },

    /**
     * Create the CodeMirror text editors
     * To be called after the document has been added into the GVS
     * CodeMirror needs to be instantiated into Elements already
     * added to the DOM
     */
    createTextEditors: function() {


        var JSONContent = this._getJSONProperties();

        this._jsonEditor = CodeMirror.fromTextArea(this._jsonText, {
                'height': "100%",
                'parserfile': ["tokenizejavascript.js",
                    "parsejavascript.js"],
                'parserConfig': {
                    'json': true
                },
                'stylesheet': ["fast/js/lib/codemirror/css/jscolors.css"],
                'path': "fast/js/lib/codemirror/js/",
                'lineNumbers': true,
                'tabMode': "shift",
                'reindentOnLoad': true,
                'content': JSONContent,
                'saveFunction': this._save.bind(this),
                'onChange': this._propertiesChanged.bind(this)
            });

        var parsers = ["tokenizejavascript.js", "parsejavascript.js"];
        var stylesheet = ["fast/js/lib/codemirror/css/jscolors.css"];
        if (this._type == BuildingBlockDocument.FORM) {
            parsers = ["parsexml.js", "parsecss.js", "tokenizejavascript.js",
                        "parsejavascript.js", "parsehtmlmixed.js"];
            stylesheet = [  "fast/js/lib/codemirror/css/xmlcolors.css",
                            "fast/js/lib/codemirror/css/jscolors.css",
                            "fast/js/lib/codemirror/css/csscolors.css"
                        ];
        }

        this._codeEditor = CodeMirror.fromTextArea(this._codeText, {
                'height': "100%",
                'parserfile': parsers,
                'stylesheet': stylesheet,
                'path': "fast/js/lib/codemirror/js/",
                'lineNumbers': true,
                'tabMode': "shift",
                'reindentOnLoad': true,
                'onChange': function() {
                                this._setDirty(true);
                                this._description.addProperties({
                                    'codeInline': this._codeEditor.getCode()
                                });
                            }.bind(this),
                'initCallback': this._loadCodeText.bind(this)
            });
    },

    // **************** PRIVATE METHODS **************** //

    /**
     * @private
     * @override
     */
    _start: function($super) {
        $super();
        if (this._type == "operator" || this._type == "resource") {
            this._toolbarElements.get('debugger').setEnabled(true);
        }
    },

    _loadCodeText: function(codeEditor) {
        if (this._codeInline) {
            codeEditor.setCode(this._codeInline);
            this._setDirty(false);
        } else {
            if (this._codeURI) {
                this._loadRemoteCodeText(codeEditor);
            } else {
                var codeText = this._getInitialCodeContent(this._type);
                codeEditor.setCode(codeText);
            }
        }
    },

    _loadRemoteCodeText: function(codeEditor) {
        new Ajax.Request('/proxy', {
            method: 'post',
            parameters: {url:this._codeURI, method:'get'},
            onSuccess: function(transport) {
                var codeText = transport.responseText
                codeEditor.setCode(codeText);
                this._setDirty(false);
            }.bind(this),
            onFailure: function() {
                Utils.showMessage("Can not open code of the selected element", {
                'error': true,
                'hide': true});
            }
        });
    },

    /**
     * Returns the areas of the document
     * @override
     * @private
     * @type Hash
     */
    _getAreas: function() {
        // Dropping areas
        var jsonArea = new Area('json',
                                [],
                                null,
                                {splitter: true, region: 'top', minHeight:300});
        var codeArea = new Area('code',
                                [],
                                null,
                                {splitter: true, region: 'center'});
        var preArea = new Area('pre',
                                $A([Constants.BuildingBlock.DOMAIN_CONCEPT]),
                                this._drop.bind(this),
                                {splitter: true, region: 'left', minWidth:100});
        var postArea = new Area('post',
                                $A([Constants.BuildingBlock.DOMAIN_CONCEPT]),
                                this._drop.bind(this),
                                {splitter: true, region: 'right', minWidth:100});

        jsonArea.getNode().appendChild(new Element('div',
                {'class':'title'}).update("Building Block Metainformation"));
        jsonArea.getNode().appendChild(this._jsonContainer);
        codeArea.getNode().appendChild(new Element('div',
                {'class': 'title'}).update("Building Block Code"));
        codeArea.getNode().appendChild(this._codeContainer);
        return $H({
            'json': jsonArea,
            'code': codeArea
        });

    },

    /**
     * @private
     * @override
     */
    _getSets: function() {
        // Palette sets
        var domainConceptSet = new DomainConceptSet(this._tags, Catalogue.
                            getBuildingBlockFactory(Constants.BuildingBlock.DOMAIN_CONCEPT));

        return [];
    },

    /**
     * Gets the document description
     * @override
     * @private
     * @type BuildingBlockDescription
     */
    _getDescription: function(/** Object */ properties) {

        var description;
        if (properties.id) {
            // An existing buildingblock
            description = Object.clone(properties);
        } else {
            // A new buildingblock
            description = {
                'type': this._type,
                'label': {'en-gb': properties.name},
                'name': properties.name,
                'version': properties.version,
                'tags':  this._tags,
                "creator": GVS.getUser().getUserName(),
                "description": {"en-gb": "Please fill the description..."},
                "rights": "http://creativecommons.org/",
                "icon": "http://fast.morfeo-project.eu/icon.png",
                "screenshot": "http://fast.morfeo-project.eu/screenshot.png",
                "homepage": "http://fast.morfeo-project.eu/",
                "libraries": [],
                "actions": [],
                "postconditions": [],
                "triggers": [],
                "parameterTemplate":"",
            };
        }

        return new BuildingBlockDescription(description);
    },

    /*
     * @override
     */
    _onSaveSuccess: function($super, /** XMLHttpRequest */ transport) {
        var data = JSON.parse(transport.responseText);
        if (this._description.getId() == null) {
            this._description.addProperties({
                    "id": data.id,
                    "version": data.version,
                    "creationDate": data.creationDate,
                    "code": data.code
            });
            this._save(false);
        } else {
            $super(transport);
        }
    },

    /**
     * Get the canvas cache for loading
     * @override
     * @private
     * @type String
     */
    _getCanvasCache: function(/** Object */ properties) {
        // TODO
        return null;
    },

    /**
     * Returns the uri of code
     * @private
     * @type URI
     */
    _getCodeURI: function() {
        return this._description['code'];
    },

    /**
     * Returns the save uri
     * @type String
     * @private
     * @override
     */
    _getSaveUri: function() {
        return this._uri;
    },

    /*
     * @override
     */
    _getType: function() {
        return this._type;
    },

    /**
     * Returns the empty palette status
     * @type Object
     * @private
     * @override
     */
    _getEmptyPalette: function() {
        return [];
    },

    /**
     * @private
     */
    _drop: function(area, instance, position) {
        this._addToArea(area, instance, position);

        if (!instance.getId()) {
            instance.setId(UIDGenerator.generate(instance.getTitle()));
        } else {
            UIDGenerator.setStartId(instance.getId());
        }
        instance.setConfigurable(false);

        if (area.getNode().className.include("pre")) {
            instance.setType("pre");
            instance.getView().setReachability({"satisfied": true});
        } else if (area.getNode().className.include("post")) {
            instance.setType("post");
        }
        instance.setEventListener(this);
        instance.enableDragNDrop(area,[area]);
        this._setDirty(true);
        return true;
    },

    /**
     * @private
     */
    _configureToolbar: function() {
        this._addToolbarElement('save', new ToolbarButton(
                'Save the current screenflow',
                'save',
                this._save.bind(this),
                false // disabled by default
            ));
        this._addToolbarElement('properties', new ToolbarButton(
                'Edit Building Block properties',
                'properties',
                this._propertiesDialog.show.bind(this._propertiesDialog),
                true
            ));
        this._addToolbarElement('share', new ToolbarButton(
                'Share the current building block with the community',
                'share',
                this._share.bind(this),
                true
            ));
        this._addToolbarElement('debugger', new ToolbarButton(
                'Debugger the current building block code',
                'debugger',
                function() {
                    this._pendingOperation = function() {
                        var url = URIs.debugger + '?url=' +
                            encodeURIComponent(this._getCodeURI());
                        var title = "BuildingBlockTest " + this.getTitle();
                        var options = 'menubar=no,toolbar=no,width=800,height=600';
                        window.open(url, title, options);
                    }.bind(this);
                    this._save(true);
                }.bind(this),
                false
            ));
    },

    /**
     * Starts the sharing process
     * @private
     * @overrides
     */
     _share: function() {
        if (this._isDirty) {
            this._pendingOperation = this._share.bind(this);
            this._save(false);
        } else {
            var text = new Element('div', {
                'style': 'text-align:center; width: 30em; margin: 0 auto;'
            }).update("You are about to share the building blokc. After that, " +
                     "you will not be able to edit it anymore. " +
                     "You can either close it or create a clone " +
                     "(Save with another name/version)");
            var dialog = new ConfirmDialog("Warning",
                                           ConfirmDialog.CUSTOM,
                                           {
                                            'callback': this._onShareDialogEvent.bind(this),
                                            'contents': text,
                                            'buttons': {
                                                'clone': 'Share and create a clone',
                                                'close': 'Share and close',
                                                'cancel': 'Cancel'
                                                },
                                            });
            dialog.show();
        }
     },

    /**
     * This function will be called whenever an event is triggered in the
     * share dialog
     * @private
     */
    _onShareDialogEvent: function(/** String */ status) {
        if (status != "cancel") {
            var uri = URIs.share.replace("<id>", this._description.getId());
            PersistenceEngine.sendPost(uri, null, null,
                                      {'mine': this, 'status': status},
                                      this._onShareSuccess, Utils.onAJAXError);
        }
    },

    /**
     * On share success
     * @private
     */
    _onShareSuccess: function(/** XMLHttpRequest */ transport) {
        Utils.showMessage("Building block successfully shared", {'hide': true});
        switch(this.status) {
            case 'clone':
                this.mine._saveAs(true);
                break;
            case 'close':
                this.mine._effectiveCloseDocument(ConfirmDialog.DISCARD);
                break;
        }
    },

    /**
     * Callback called whenever the JSON properties editor is changed
     * @private
     */
    _propertiesChanged: function() {
        this._setDirty(true);
        try {
            var jsonContent = cjson_parse(this._jsonEditor.getCode());
            this._description.addProperties(jsonContent);
            Utils.showMessage();
        } catch (e) {
            Utils.showMessage("The properties are not well formed. It will not " +
                              "work", {'error': true});
            this._toolbarElements.get('save').setEnabled(false);
        }
    },

    /**
     * @override
     */
    _onPropertiesChange: function($super) {
        $super();
        this._jsonEditor.setCode(this._getJSONProperties());
        this._jsonEditor.reindent();
    },

    /**
     * Returns the building block properties in JSON-like string
     * @private
     * @type String
     */
    _getJSONProperties: function() {
        var validContents = $H(this._description.toJSON()).clone();
        ["code","codeInline", "id", "creationDate"].each(function(element){
            validContents.unset(element);
        });
        var JSONContent = Object.toJSON(validContents.toObject()).replace(/,\s*"/g, ",\n\"");
        JSONContent = JSONContent.replace(/{/g, "{\n");
        JSONContent = JSONContent.replace(/}/g, "\n}");
        return JSONContent;
    },


    /**
     * This function creates the area containing the canvas
     * and the inspectors
     * @private
     * @override
     */
    _renderCenterContainer: function() {

        var centerContainer = new dijit.layout.BorderContainer({
            design:"headline",
            liveSplitters:"false",
            region:"center"
        });

        this._designContainer.domNode.addClassName('canvas');

        centerContainer.addChild(this._designContainer);

        return centerContainer;
    },

    /**
     * This function returns the initial code content from a given
     * building block
     * @private
     * @type String
     */
    _getInitialCodeContent: function (buildingBlockType) {
        var content;
        switch (buildingBlockType) {
            case BuildingBlockDocument.FORM:
                content =   "<html>\n" +
                            "<head>\n" +
                            "<script type=\"text/javascript\">\n" +
                            "var {{buildingblockId}} = Class.create(BuildingBlock, {\n" +
                            "init: function (){}\n" +
                            "});\n" +
                            "</script>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "</body>\n" +
                            "</html>";
                break;
            case BuildingBlockDocument.OPERATOR:
                content = "operatorFunction: function(inputFacts) {\n" +
                          "}";
                break;
            case BuildingBlockDocument.RESOURCE:
                content = "serviceFunction: function(inputFacts) { \n" +
                          "}";
                break;
            default:
                content = "";
        }

        return content;
    },

    _findCheckCallback: function() {
        // Do nothing
    },

    _refreshReachability: function() {
        // Do nothing
    }
});
BuildingBlockDocument.FORM = "form";
BuildingBlockDocument.OPERATOR = "operator";
BuildingBlockDocument.RESOURCE = "resource";

/**
 * This class is included here due to PaletteDocument restrictions,
 * which need an instance of an InferenceEngine. As this document
 * does not need any kind of inference, the class does nothing
 */
var DumbInferenceEngine = Class.create({
    initialize: function(){},
    findCheck: function(){},
    addReachabilityListener: function(){},
    removeReachabilityListener: function(){}
});

// vim:ts=4:sw=4:et:
