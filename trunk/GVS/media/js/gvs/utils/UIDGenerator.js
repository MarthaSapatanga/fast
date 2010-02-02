/**
 * Unique ID Generator
 * @constructs
 */
var UIDGenerator = Class.create();

// **************** STATIC ATTRIBUTES **************** //

Object.extend(UIDGenerator, {
    /**
     * Next available ids
     * @type Object
     * @private
     */
    _nextIds: new Object()
});

// **************** STATIC METHODS ******************* //

Object.extend(UIDGenerator, {

    /**
     * Returns a valid new DOM Id
     * @param String element  Type of element that needs the identifier
     * @type String
     */
    generate: function (/** String */ element) {
        var sanitized = element.replace(new RegExp('\\s', 'g'), "")
                                .replace("_","");
        var nextId = this._nextIds[sanitized];

        if (!nextId){
            nextId = 1;
        }

        this._nextIds[sanitized] = nextId + 1;

        return sanitized + "_" + nextId;
    },

    /**
     * Sets the initial id for a given name
     */
    setStartId: function(/** String */ id) {
        var pieces = id.split("_");
        var name = pieces[0];
        var lastId = parseInt(pieces[1]);
        if (!this._nextIds[name] || this._nextIds[name] <= lastId) {
            this._nextIds[name] = lastId + 1;
        }
    }
});
        

// vim:ts=4:sw=4:et: 
