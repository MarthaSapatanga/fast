var ConfirmDialog = Class.create(FormDialog, /** @lends ConfirmDialog.prototype */ {
    /**
     * This class handles dialogs
     * @abstract
     * @extends FormDialog
     * @constructs
     * @param buttons
     *     ['ok_cancel' (default) | 'ok']
     */ 
    initialize: function($super, /** String */ title, /** String */ buttons) {
        $super({
            'title': title,
            'style': 'display:none;'
        });

        //Initializing buttons
        switch(buttons) {
            case 'ok':
                this._addButton ('Ok',     this._onOk.bind(this));
                break;
            
            case 'ok_cancel':
            default:
                this._addButton ('Ok',     this._onOk.bind(this));
                this._addButton ('Cancel', this._onCancel.bind(this));
                break;
        }
    },
    

    // **************** PUBLIC METHODS **************** //

    
    
    // **************** PRIVATE METHODS **************** //
    
    /** 
     * onOK
     * This function is called when ok button is pressed (if any)
     * @private
     */
    _onOk: function(){
        this._hide();
    },
    
    /** 
     * onCancel
     * This function is called when ok button is pressed (if any)
     * @private
     */
    _onCancel: function(){
        this._hide();
    }
});

// vim:ts=4:sw=4:et:
