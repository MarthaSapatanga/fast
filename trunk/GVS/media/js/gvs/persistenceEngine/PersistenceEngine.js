/* 
*     (C) Copyright 2008 Telefonica Investigacion y Desarrollo
*     S.A.Unipersonal (Telefonica I+D)
*
*     This file is part of Morfeo EzWeb Platform.
*
*     Morfeo EzWeb Platform is free software: you can redistribute it and/or modify
*     it under the terms of the GNU Affero General Public License as published by
*     the Free Software Foundation, either version 3 of the License, or
*     (at your option) any later version.
*
*     Morfeo EzWeb Platform is distributed in the hope that it will be useful,
*     but WITHOUT ANY WARRANTY; without even the implied warranty of
*     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*     GNU Affero General Public License for more details.
*
*     You should have received a copy of the GNU Affero General Public License
*     along with Morfeo EzWeb Platform.  If not, see <http://www.gnu.org/licenses/>.
*
*     Info about members and contributors of the MORFEO project
*     is available at
*
*     http://morfeo-project.org
 */


var PersistenceEngineFactory = function () {

    // *********************************
    // SINGLETON INSTANCE
    // *********************************
    var instance = null;

    function PersitenceEngine () {

        // ****************
        // PUBLIC METHODS 
        // ****************
        PersitenceEngine.prototype.sendGet = function (url, context, successHandler, errorHandler, requestHeaders) {
            new Ajax.Request(url, {
                method: 'get',
                parameters: arguments[4],
                onSuccess: successHandler.bind(context),
                //onFailure: errorHandler.bind(context),
                //onException: errorHandler.bind(context),
				requestHeaders: requestHeaders
            });
        }

        PersitenceEngine.prototype.sendPost = function (url, params, body, context, successHandler, errorHandler, requestHeaders) {
            new Ajax.Request(url, {
                method: 'post',
                parameters: params,
                postBody: body,
                onSuccess: successHandler.bind(context),
                onFailure: errorHandler.bind(context),
                onException: errorHandler.bind(context),
				requestHeaders: requestHeaders
            });
        }

        PersitenceEngine.prototype.sendDelete = function (url, context, successHandler, errorHandler, requestHeaders){
            new Ajax.Request(url, {
                method: 'delete',
                onSuccess: successHandler.bind(context),
                onFailure: errorHandler.bind(context),
                onException: errorHandler.bind(context),
				requestHeaders: requestHeaders
            });
        }

        PersitenceEngine.prototype.sendUpdate = function (url, params, body, context, successHandler, errorHandler, requestHeaders){
        	//FIXME: If body -> it sends post
        	new Ajax.Request(url, {
                method: 'put',
                parameters: params,
                postBody: body,
                onSuccess: successHandler.bind(context),
                onFailure: errorHandler.bind(context),
                onException: errorHandler.bind(context),
				requestHeaders: requestHeaders
            });
        }
    }

    // *********************************
    // SINGLETON GET INSTANCE
    // *********************************
    return new function() {
        this.getInstance = function() {
            if (instance == null) {
                instance = new PersitenceEngine();
            }
            return instance;
        }
    }

}();