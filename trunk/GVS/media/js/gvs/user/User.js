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
var User = Class.create( /** @lends User.prototype */ {
    /**
     * This class handles the user account and properties
     * It is used directly by the GVS
     * @constructs
     */
    initialize: function() {
        /**
         * @type String
         * @private @member
         */
        this._userName = null;

        /**
         * @type String
         * @private @member
         */
        this._firstName = null;

        /**
         * @type String
         * @private @member
         */
        this._lastName = null;

        /**
         * @type String
         * @private @member
         */
        this._email = null;

        /**
         * @type String
         * @private @member
         */
        this._ezWebURL = null;

        // Initializes the Cookie for storing local preferences
        Cookie.init({"name": "GVSUserData", "expires": 10000});


        /**
         * Defines the magic level of the catalogue.
         * Magic stands for:
         *    <li> Automatic reordering of the building blocks
         * @type Boolean
         * @private
         */
        this._catalogueMagic = Cookie.getData("catalogueMagic") || false;

        /**
         * Do you want iserve integration?
         * @private
         * @type Boolean
         */
        this._iServe = Cookie.getData("iserve") || false;

        //Bring the user from the server
        this._getUser();
    },


    // **************** PUBLIC METHODS **************** //

    /**
     * getUserName
     * @type String
     */
    getUserName: function () {
        return this._userName;
    },

    /**
     * getUserName
     * @type String
     */
    getFirstName: function () {
        return this._firstName;
    },

    /**
     * getUserName
     * @type String
     */
    getLastName: function () {
        return this._lastName;
    },

    /**
     * getEmail
     * @type String
     */
    getEmail: function () {
        return this._email;
    },

    /**
     * getEzWebURL
     * @type String
     */
    getEzWebURL: function () {
        return this._ezWebURL;
    },

    /**
     * getRealName
     * @type String
     */
    getRealName: function () {
        return this._firstName + " " + this._lastName;
    },

    /**
     * @type Boolean
     */
    getCatalogueMagic: function() {
        return this._catalogueMagic;
    },

    /**
     * @param Boolean
     */
    setCatalogueMagic: function(catalogueMagic) {
        this._catalogueMagic = catalogueMagic;
        Cookie.setData("catalogueMagic", catalogueMagic);
    },

    /**
     * @type Boolean
     */
    getiServe: function() {
        return this._iServe;
    },

    /**
     * @param Boolean
     */
    setiServe: function(iServe) {
        this._iServe = iServe;
        Cookie.setData("iserve", iServe);
    },

    /**
     * This function updates the user object
     * based on the data passed
     */
    update: function (/** Hash */ userData){
        //TODO empty data control
        this._firstName = userData.firstName;
        this._lastName = userData.lastName;
        this._email = userData.email;
        this.setCatalogueMagic((userData.catalogueMagic !== undefined));
        this.setiServe((userData.iServe !== undefined));

        this._ezWebURL = userData.ezWebURL || "";

        if (this._ezWebURL != "" && this._ezWebURL[this._ezWebURL.length-1] != '/') {
            this._ezWebURL += '/';
        }

        URIs.ezweb = this._ezWebURL;

        //Send it to the server
        this._updateUser();
    },
    // **************** PRIVATE METHODS **************** //

    /**
     *  This function gets the user from the server
     *  @private
     */
    _getUser: function (){
        /**
         * onSuccess handler
         * @private
         */
        var onSuccess = function (/** XmlHttpRequest */ response){
            var remoteUser = JSON.parse(response.responseText);

            this._userName = remoteUser.user.username;
            this._firstName = remoteUser.user.first_name;
            this._lastName = remoteUser.user.last_name;
            this._email = remoteUser.user.email;
            this._ezWebURL = remoteUser.profile.ezweb_url;

            if (this._ezWebURL != "" && this._ezWebURL[this._ezWebURL.length-1] != '/') {
                this._ezWebURL += '/';
            }

            URIs.ezweb = this._ezWebURL;
        }
        /**
         * onError handler
         * @private
         */
        var onError = function (/** XmlHttpRequest */ response, /** Exception */ e){
            Logger.serverError (response, e);
        }

        PersistenceEngine.sendGet(URIs.userPreferences, this, onSuccess, onError);
    },
    /**
     *  This function updates the user to the server
     *  @private
     */
    _updateUser: function (){

        /**
         * onSuccess handler
         * @private
         */
        var onSuccess = function (/** XmlHttpRequest */ response){
        }

        /**
         * onError handler
         * @private
         */
        var onError = function (/** XmlHttpRequest */ response){
            Logger.serverError (response, e);
        }

        var object = {
            'first_name': this._firstName,
            'last_name': this._lastName,
            'email': this._email,
            'ezweb_url': this._ezWebURL
        };
        var preferences = {preferences :Object.toJSON(object)};

        PersistenceEngine.sendUpdate(URIs.userPreferences, preferences, null,
                                this, onSuccess, onError);
    }

});

// vim:ts=4:sw=4:et:
