var PrePostDialog = Class.create(ConfirmDialog /** @lends PrePostDialog.prototype */, {
    /**
     * This class handles the dialog
     * to update the *-condition properties
     * @constructs
     * @extends ConfirmDialog
     */ 
    initialize: function($super,
            /** Function */ onChangeCallback, /** String */ label) {
        $super("Pre/Post Condition");
        
        /**
         * @type String
         * @private @member
         */
        this._label = label;

        this._onChangeCallback = onChangeCallback;
    },
    
    
    // **************** PUBLIC METHODS **************** //

    // **************** PRIVATE METHODS **************** //


    /** 
     * initDialogInterface
     * This function creates the dom structure and
     * @private
     * @overrides
     */
    _initDialogInterface: function (){
 
        this._setHeader("Check domain concept details", 
                             "Please fulfill the required information in order to" +
                             " set up the Domain Concept");

        var formData = [
            {
                'type':'comboBox',
                'label': 'Type:',
                'name': 'type',
                'value': 'undefined',
                'options': [{
                    'value': 'undefined',
                    'label': 'Choose a type...'
                }, {
                    'value': 'pre',
                    'label': 'Precondition'
                }, {
                    'value': 'post',
                    'label': 'Postcondition'
                }],
                'events': {
                    'change': this._onTypeChange.bind(this),
                    'click':  this._removeUndefinedStatus.bind(this)               
                }
            },
            {
                'type': 'input',
                'label': 'Label:',
                'name': 'label',
                'value': this._label,
                'required': true
            },
            {
                'type': 'title',
                'value': 'EzWeb properties'
            },
            {
                'type': 'comboBox',
                'label': 'Binding:',
                'name': 'binding',
                'value': 'undefined',
                'options': [{
                    'value': 'undefined',
                    'label': 'Choose type first...'
                }]
            },
            {
                'type': 'input',
                'label': 'Variable name:',
                'name': 'varname',
                'value': this._label.replace(" ",""),
                'required': true
            },
            {
                'type': 'input',
                'label': 'Friendcode:',
                'name': 'friendcode',
                'value': this._label.replace(" ",""),
                'required': true
            }
        ];
        
        this._setContent(formData);               
        
    },
    /**
     * Overriding onOk handler
     * @override
     * @private
     */
    _onOk: function($super){
        this._onChangeCallback(this._getForm().serialize({'hash':true}));
        $super();
    },
 
    /**
     * Function called when the type of domain concept changes
     * @private
     */    
    _onTypeChange: function() {
        
        var bindings = new Array();
        switch ($F(this._getForm().type)) {
            case 'pre':
                bindings.push({'value':'slot','label':'Slot'});
                bindings.push({'value':'pref','label':'User Preference'});
                bindings.push({'value':'context','label':'Platform Context'});
                break;
            case 'post':
                bindings.push({'value':'event','label':'Event'});
                break;
            default: 
                break;
        }
        var bindingNode = $(this._getForm().binding);
        bindingNode.update("");
        bindings.each(function(binding) {
           var optionNode = new Element('option', {
                             'value': binding.value
                        }).update(binding.label);
           bindingNode.appendChild(optionNode);                      
        });                    
    },
    /**
     * This function removes the undefined status
     * from the type combobox
     * @private
     */
    _removeUndefinedStatus: function() {
        //Removing the initial status
        if ($F(this._getForm().type) == 'undefined') {
            var typeCombobox = $(this._getForm().type);
            typeCombobox.update("");
            typeNode = new Element('option', {
                             'value': 'pre'
                        }).update("Precondition");
            typeCombobox.appendChild(typeNode);
            typeNode = new Element('option', {
                             'value': 'post'
                        }).update("Postcondition");
            typeCombobox.appendChild(typeNode);
            this._onTypeChange();
        }
    }
});

// vim:ts=4:sw=4:et:
