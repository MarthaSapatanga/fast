var WelcomeDocument = Class.create(AbstractDocument,
    /** @lends WelcomeDocument.prototype */ {

    /**
     * WelcomeDocument is the initial document and allows the user to access
     * to the most important GVS features as starting point.
     * @constructs
     * @extends AbstractDocument
     */
    initialize: function($super) {
        $super("Welcome!");
        this._populate ();
    },



    // **************** PUBLIC METHODS **************** //

    // **************** PRIVATE METHODS **************** //


    /**
     * Constructs the document content.
     * @private
     */
    _populate: function(){
        var content = this.getNode();

        content.addClassName("welcome");

        var logo = new Element('img', {
            'src': URIs.logoFast
        });
        content.appendChild(logo);
        var documentTitle = new Element ("div", {"class": "documentTitle"}).
            update("Welcome to the Gadget Visual Storyboard!");
        content.appendChild(documentTitle);
        var welcomeIntro = new Element ("div", {"id": "intro"}).
            update("Choose your desired action:");
        content.appendChild (welcomeIntro);

        var gvs = GVSSingleton.getInstance();
        var newScreenflowButton = new dijit.form.Button({
            label: "New Screenflow",
            onClick: function() { gvs.action("newScreenflow"); }}
        );
        var openScreenflowButton = new dijit.form.Button({
            label: "Open Existing Screenflow...",
            onClick: function() { gvs.action("openScreenflow"); }}
        );
        var addScreenButton = new dijit.form.Button({
            label: "Add Screen to the catalogue...",
            onClick: function() { gvs.action("addScreen"); }}
        );

        var buttonsContainer = new Element("div");
        buttonsContainer.appendChild(newScreenflowButton.domNode);
        buttonsContainer.appendChild(new Element("br"));
        buttonsContainer.appendChild(openScreenflowButton.domNode);
        buttonsContainer.appendChild(new Element("br"));
        buttonsContainer.appendChild(addScreenButton.domNode);
        content.appendChild(buttonsContainer);
    }
});

// vim:ts=4:sw=4:et:
