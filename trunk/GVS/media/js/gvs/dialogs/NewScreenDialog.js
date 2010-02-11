var NewScreenDialog = Class.create(ConfirmDialog /** @lends NewScreenDialog.prototype */, {
    /**
     * This class handles the dialog
     * to create a new Screen
     * @constructs
     * @extends ConfirmDialog
     */ 
    initialize: function($super) {
        /**
         * Current screen name/version availability
         * @private
         * @type Boolean
         */
        this._available = true;

        $super("New Screen");
    },
    
    
    // **************** PUBLIC METHODS **************** //


    // **************** PRIVATE METHODS **************** //


    /** 
     * initDialogInterface
     * This function creates the dom structure and
     * @private
     * @override
     */
    _initDialogInterface: function (){
 
        this._setHeader("Fulfill Gadget Information", 
                             "Please fulfill the required information in order to " +
                             "create a new screen.");

        var callback = this._scheduleAvailabilityCheck.bind(this);
        var formData = [
            {
                'type':'input', 
                'label': 'Screen Name:',
                'name': 'name', 
                'value': 'New Screen',
                'message': 'Screen cannot be blank',
                'required': true,
                'events': {
                    'keypress': callback
                }
            },
            {
                'type':'input', 
                'label': 'Version:', 
                'name': 'version', 
                'value': '',
                'message': 'Version cannot be blank',
                'events': {
                    'keypress': callback
                }
            },
            {
                'type':'input', 
                'label': 'Tags:',
                'name': 'tags',
                'value': ''
            }
        ];

        this._setContent(formData);
    },

    /**
     * This function updates this dialog taking into account the availability
     * status. For example, if the screen name/version is not available, this
     * dialog will be disabled.
     *
     * @private
     */
    _updateAvailabilityStatus: function() {
        if (this._available) {
            this._setMessage();
        } else {
            this._setMessage('Please, use a diferent Version or Screen Name.',
                             FormDialog.MESSAGE_ERROR);
        }
        this._setDisabled(!this._available);
    },

    /**
     * Callback function
     *
     * @private
     */
    _onAvailabilityCheckSuccess: function(/**XMLHttpRequest*/ transport) {
        var metadata = transport.responseText.evalJSON();
        this._available = metadata.length == 0
        this._updateAvailabilityStatus();
    },

    /**
     * Updates the availability status. This is an asynchronous operation.
     *
     * @private
     */
    _availabilityCheck: function() {
        var name = $F(this._getForm().name);
        var version = $F(this._getForm().version);

        if (name == "" || version == "") {
            this._available = name != "";
            this._updateAvailabilityStatus();
            return;
        }

        var query = {
            "query": {
                "type": "and",
                "operands": [
                    {
                        "type": "field",
                        "condition": "is",
                        "field": "name",
                        "value": name
                    },
                    {
                        "type": "field",
                        "condition": "is",
                        "field": "version",
                        "value": version
                    }
                ]
            }
        }
        PersistenceEngine.sendPost(URIs.screenSearch, null,
                Object.toJSON(query), this, this._onAvailabilityCheckSuccess,
                Utils.onAJAXError);
    },

    /**
     * Callback function.
     *
     * Invalidates current availability info and schedules the retreiving of the
     * availability status.
     *
     * @private
     */
    _scheduleAvailabilityCheck: function(e) {
        // Ignore "control" keys, except "backspace" and "delete"
        if ((e.charCode == 0) && (e.keyCode != 8 && e.keyCode != 46))
            return;

        this._available = false;
        try {
            clearTimeout(this._timeout);
        } catch (e) {}

        this._setMessage('Checking if the screen already exists...');
        this._timeout = setTimeout(this._availabilityCheck.bind(this), 1000);
        this._setDisabled(true);
    },

    /**
     * Overriding onOk handler
     * @override
     * @private
     */
    _onOk: function($super){
        if (this._getFormWidget().validate() && this._available) {
            var name = $F(this._getForm().name);
            var tags = $F(this._getForm().tags).split(/[\s,]+/).without("");
            var version = $F(this._getForm().version);

            var processedTags = Utils.getCatalogueTags($A(tags), null);

            var documentController = GVS.getDocumentController();
            documentController.createScreen(name, processedTags, version);
            
            $super();
        }       
    },

     /**
     * Overriding onCancel handler
     * @override
     * @private
     */
    _onCancel: function($super) {
        try {
            clearTimeout(this._timeout);
        } catch (e) {}

        $super();
    },

    /**
     * Reset method to leave the form as initially
     * @private
     */
    _reset: function ($super) {
        this._getForm().name.value = "New Screen";
        this._getForm().tags.value = "";
        this._getForm().version.value = "";
        this._available = true;
        this._setMessage();
        this._setDisabled(false);
        $super();
    }
});

// vim:ts=4:sw=4:et:
