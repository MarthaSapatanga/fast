var Debugger = Class.create(/** @lends Debugger.prototype */ {
    /**
     * Class that handles all the debugging process
     * @constructs
     */
    initialize: function(debugLevel) {

        /**
         * Debug level in logging|debug (debug is more complete than logging)
         * @private
         * @type String
         */
        this._debugLevel = debugLevel;

        /**
         * Node of the Debugger area, in case Firebug is not
         * installed
         * @type DOMNode
         * @private
         */
        this._debuggerNode = new Element("div", {
            "id": "debugger"
        });
        document.body.appendChild(this._debuggerNode);
        document.body.addClassName(this._debugLevel);

        /**
         * Number of logging groups opened
         * @private
         * @type Number
         */
        this._indentLevel = 0;


        /**
         * Flag for testing purposes, remove when deploying
         * @private
         * @type Boolean
         */
        this._testing = false;


        this._initKB();
        this._initConsole();
    },

    /**
     * Adds a new fact to the fact base
     */
    addFact: function(fact) {
        var updated = this._KB.addFact(fact);
        var text = "added";
        if (updated) {
            text = "updated";
        }
        this._showObject(fact, "Fact %s: %s", text, fact.uri);
    },

    /**
     * Removes a fact from the fact base
     */
    removeFact: function(fact) {
        this._showObject(fact, "Fact removed: %s", fact.uri);
        this._KB.removeFact(fact.uri);
    },

    /**
     * Adds a screen into the screenflow
     */
    addScreen: function(screen) {
        this._showObject(screen, "Screen added: %s", screen.title);
    },

    /**
     * Init an AJAX call
     */
    request: function(url, options) {
        // Ensure the log is in the top
        for (var i=0; i < this._indentLevel; i++) {
            this._logger.groupEnd();
            this._indentLevel--;
        }

        this._indentLevel++;
        this._logger.groupCollapsed("Remote request");

        this._logger.dir({
            "method": options.method,
            "url": url
        });
    },

    /**
     * Ends the ajax call (to be called after request)
     */
    onRequestSuccess: function(data, type) {
        this._logger.dir({"Response" : data});

        this._indentLevel++;
        this._logger.groupCollapsed("Details");
        if (type == "xml") {
            this._logger.dirxml(data);
        } else {
            this._logger.dir(data);
        }
        // Two groups must be closed
        this._indentLevel--;
        this._logger.groupEnd();
        this._indentLevel--;
        this._logger.groupEnd();
    },

    // ************** PRIVATE METHODS ************* //

    /**
     * Initializes the console to fake firebugs one, in case it does not exist
     * @private
     */
    _initConsole: function() {

        if (this._testing || window.console === undefined) {
            this._logger = new Logger(this._debuggerNode);
            document.body.addClassName("no-firebug");
        } else {
            this._logger = window.console;
        }
    },

    /**
     * Init the KB, in case debug is enabled
     */
    _initKB: function() {
        if (this._debugLevel == "debug") {
            this._KB = new KnowledgeBase(this._debuggerNode);
        } else {
            // Dumb KB
            this._KB = {
                addFact: Prototype.emptyFunction,
                removeFact: Prototype.emptyFunction
            }
        }
    },

    /**
     * Shows an object in the console, wrapped in a group
     * @private
     */
    _showObject: function(_object /* , title ... */) {
        var titleArgs = Array.prototype.slice.call(arguments, 1);
        this._logger.groupCollapsed.apply(this._logger, titleArgs);
        this._logger.dir(_object);
        this._logger.groupEnd();
    }
});


// Logger class to replace firebug in case it does not be installed
var Logger = Class.create({

    /**
     * @constructs
     */
    initialize: function(debuggerNode) {
        this._loggerNode = new Element("div", {
            "style": "margin-top:3px"
        });


        var clearButton = new Element("a", {
            "class": "clear",
            "href": "javascript:"
        }).update("Clear");
        clearButton.observe("click", function(){
            this._loggerNode.update();
            this._currentLevel = this._loggerNode;
        }.bind(this));

        var loggerContainer = new Element("div", {
            "class": "logger"
        });
        loggerContainer.appendChild(clearButton);
        loggerContainer.appendChild(this._loggerNode);

        debuggerNode.appendChild(loggerContainer);

        /**
         * Current DOM level of the log writing
         * Necessary to allow tree hierarchy
         * @private
         * @type DOMNode
         */
        this._currentLevel = this._loggerNode;
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
var KnowledgeBase = Class.create({
    initialize: function(parentNode) {

        this._facts = new Hash();
        this._factShortcuts = new Hash();

        this._kbContent = new Element("div", {
            "class": "kbContent"
        });
        var kbNode = new Element("div", {
            "class": "kb"
        });

        var title = new Element("div", {
            "class": "title"
        }).update("Fact Base");

        kbNode.appendChild(title);
        kbNode.appendChild(this._kbContent);
        parentNode.appendChild(kbNode);
    },

    /**
     * Adds a new fact to the KB
     */
    addFact: function(fact) {
        oldfact = this._facts.get(fact.uri);
        var factIcon = this._createFact(fact);
        this._facts.set(fact.uri, {
            "fact": fact,
            "node": factIcon
        });
        if (oldfact) {
            this._kbContent.insertBefore(factIcon, oldfact.node);
            this._kbContent.removeChild(oldfact.node);

            factIcon.setStyle({"fontWeight":"bold"});
            setTimeout(function(){
                factIcon.setStyle({"fontWeight": "normal"});
            }, 1000);
        } else {
            this._kbContent.appendChild(factIcon);
        }
        return oldfact ? true : false;
    },

    /**
     * Removes a fact from the KB
     */
    removeFact: function(factUri) {
        this._kbContent.removeChild(this._facts.get(factUri).node);
        this._facts.unset(factUri);
    },

    /**
     * Creates the fact node
     */
    _createFact: function(fact) {
        var factNode = new Element("div",{
            "style": "overflow: auto"
        });

        // Remove fact
        var removeFactNode = new Element("div", {
            "class": "removeFact"
        });
        removeFactNode.observe("click", function(){
            ScreenflowEngineFactory.getInstance().manageFacts([],[fact.uri]);
        }.bind(this));
        removeFactNode.observe("mouseover", function(){
            removeFactNode.setStyle({"cursor": "pointer"});
        });

        factNode.appendChild(removeFactNode);

        // Fact Shortcut
        var factShortcut = this._createFactShortcut(fact.uri);
        var factShortcutNode = new Element("div", {
            "class": "fact"
        }).update(factShortcut);

        var factDetails = this._createFactDetails(fact);
        factShortcutNode.observe("mouseover", function(e) {
            var node = Event.element(e);
            var left = node.cumulativeOffset()[0] - factDetails.getWidth() - 5;
            factDetails.setStyle({
                "display": "block",
                "top": node.cumulativeOffset()[1] + "px",
                "left": left + "px",
            });
        });
        factShortcutNode.observe("mouseout", function(e) {
            factDetails.setStyle({
                "display": "none"
            });
        });
        factNode.appendChild(factShortcutNode);


        // Fact Identifier
        var identifier = new Element("div", {
            "class": "factIdentifier"
        }).update(this._getFactIdentifier(fact.uri));
        factNode.appendChild(identifier);

        return factNode;
    },

    /**
     * Creates the fact shortcut
     */
    _createFactShortcut: function(uri) {
        var shortcut = this._factShortcuts.get(uri);
        if (shortcut) {
            return shortcut;
        }

        var identifier = this._getFactIdentifier(uri);

        //Let's try with capital letters...
        var letters = identifier.match(/[A-Z]/g);
        if (letters && letters.length > 1) { //More than one capital letter
            //try only with 2 letters
            //Put the second letter in lower case
            //letters[1]= letters[1].toLowerCase();
            shortcut = letters.slice(0, 2).join("");
            if (this._factShortcuts.values().indexOf(shortcut) == -1) {
                this._factShortcuts.set(uri, shortcut);
                return shortcut;
            }
        } else {
            //Let's try with the first two letters
            identifier[1]= identifier[1].toLowerCase();
            shortcut = identifier.slice(0,2);
            if (this._factShortcuts.values().indexOf(shortcut) == -1) {
                this._factShortcuts.set(uri, shortcut);
                return shortcut;
            }
        }
        shortcut = identifier.slice(0,1);
        this._factShortcuts.set(uri, shortcut);
        return shortcut;
    },

    _createFactDetails: function(fact) {
        var container = new Element("div", {
            "class": "factDetails"
        });

        var factTitle = new Element("div", {
            "class": "title"
        }).update(fact.uri);

        var factData = new Element("div", {
            "class": "data"
        }).update(Object.toJSON(fact.data));

        container.appendChild(factTitle);
        container.appendChild(factData);

        document.body.appendChild(container);
        return container;
    },

    /**
     * Returns the fact "Identifier"
     */
    _getFactIdentifier: function(uri) {
        var identifier = "";
        var pieces = uri.split("#");
        if (pieces.length > 1){
            identifier = pieces[1];
        } else { //The uri has not identifier, try the last part of the url
            pieces = uri.split("/");
            identifier = pieces[pieces.length - 1];
        }
        identifier = identifier.substr(0, 1).toUpperCase() + identifier.substr(1);
        return identifier;
    }
});
