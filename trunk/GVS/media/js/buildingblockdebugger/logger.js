var Logger = Class.create({

    /**
     * @constructs
     */
    initialize: function(/* element */ debuggerNode) {
        this._loggerNode = new Element("div", {
            "style": "margin-top:3px"
        });

        var title = new Element("div", {
            "class": "title"
        }).update("Event Log");

        var clearButton = new Element("div", {
            "class": "clear",
        }).update("Clear");
        clearButton.observe("click", function(){
            this._loggerNode.update();
            this._currentLevel = this._loggerNode;
        }.bind(this));

        var loggerContainer = this.domNode = new Element("div", {
            "class": "logger"
        });
        loggerContainer.appendChild(title);
        loggerContainer.appendChild(clearButton);
        loggerContainer.appendChild(this._loggerNode);

        if (debuggerNode) {
            debuggerNode.appendChild(loggerContainer);
        }

        /**
         * Current DOM level of the log writing
         * Necessary to allow tree hierarchy
         * @private
         * @type DOMNode
         */
        this._currentLevel = this._loggerNode;
    },

    toElement: function() {
        return this.domNode;
    },

    log: function(/* arguments ... */) {
        this._writeMessage("log", arguments);
    },

    debug: function(/* arguments ... */) {
        this._writeMessage("debug", arguments);
    },

    info: function(/* arguments ... */) {
        this._writeMessage("info", arguments);
    },

    warn: function(/* arguments ... */){
        this._writeMessage("warn", arguments);
    },

    error: function(/* arguments ... */) {
        this._writeMessage("error", arguments);
    },

    assert: function(/** Boolean */ expression /*, arguments ... */) {
        if (expression == false) {
            args = Array.prototype.slice.call(arguments, 1);
            this._error.apply(this, args);
        }
    },

    dir: function(object) {
       switch(object.constructor) {
            case Array:
                for (var i=0; i < object.length; i++) {
                    if (object[i].constructor == Object ||
                        (object[i].constructor == Array && object[i].length > 0)) {
                        this._createGroup(true, [i.toString(), ": ", this._print(object[i])]);
                        this.dir(object[i]);
                        this.groupEnd();
                    } else {
                        this.log(i, ": ", this._print(object[i]));
                    }
                }
                break;
            case Object:
                $H(object).keys().each(function(key) {
                    if (object[key].constructor == Object ||
                        (object[key].constructor == Array && object[key].length > 0)) {
                        this._createGroup(true, [key, ": ", this._print(object[key])]);
                        this.dir(object[key]);
                        this.groupEnd();
                    } else {
                        this.log(key, ": ", this._print(object[key]));
                    }
                }, this);
                break;
            default:
                this.log(this._print(object));
       }
    },

    dirxml: function(xmlObject){
        this.log((new XMLSerializer()).serializeToString(xmlObject));
    },

    group: function(/* arguments ... */) {
       this._createGroup(false, arguments);
    },

    groupCollapsed: function(/* arguments ... */){
       this._createGroup(true, arguments);
    },

    groupEnd: function(){
        if (this._currentLevel != this._loggerNode) {
            this._currentLevel = this._currentLevel.parentNode;
        }
    },

    table: function(){},

    // ************** PRIVATE METHODS ************ //

    /**
     * @private
     */
    _writeMessage: function(type, args) {
        if (args.length > 0) {
            var entry = new Element("div", {
                "class": "entry " + type
            });
            var message = this._getComputedText(args);
            var messageNode = new Element("span", {
                "class": "message"
            });
            messageNode.textContent = message;
            entry.appendChild(messageNode);

            this._currentLevel.appendChild(entry);
        }
    },

    /**
     * @private
     */
    _createGroup: function(collapsed, args) {
        var newGroup = new Element("div", {
            "class": "group"
        });
        newGroup.setStyle({
            "display": collapsed ? "none" : "block"
        });
        var button = new Element("div", {
            "class": "groupButton " + (collapsed ? "plus" : "minus")
        }).update("+");

        button.observe("click", function(e) {
            var nextCollapsed = newGroup.style.display == "block" ? true : false;
            newGroup.setStyle({
                "display": nextCollapsed ? "none" : "block"
            });
            if (nextCollapsed) {
                button.removeClassName("minus");
                button.addClassName("plus");
                button.update("+");
            } else {
                button.removeClassName("plus");
                button.addClassName("minus");
                button.update("-");
            }
        });

        var entry = new Element("div", {
            "class": "entry groupTitle"
        });
        entry.appendChild(button);
        var messageNode = new Element("span", {
            "class": "message"
        }).update(this._getComputedText(args));

        entry.appendChild(messageNode);
        this._currentLevel.appendChild(entry);
        this._currentLevel.appendChild(newGroup);

        this._currentLevel = newGroup;
    },

    /**
     * @private
     */
    _getComputedText: function(args) {
        var message = args[0];

        for (i=1; i < args.length; i++) {
            if (message.match(/%[s,i,d,f]/)) {
                message = message.replace(/%[s,i,d,f]/, args[i]);
            } else {
                message += args[i];
            }
        }
        return message;
    },

    _print: function(element) {
        var result;
        switch (element.constructor) {
            case Array:
                result = "[";
                for (var i=0; i < element.length; i++) {
                    result += this._print(element[i]);
                    if (i != (element.length - 1)) {
                        result += ", ";
                    }
                }
                result += "]";
                break;
            case Object:
                result = "Object {...}";
                break;
            case Function:
                result = "function()";
                break;
            default:
                result = element;
        }
        return result;
    }
});
