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
var Logger = Class.create();

Object.extend(Logger, {
    serverError: function(/** XmlHttpRequest */ transport, /** Exception */ e){
        var msg;
        if (e) {
            msg = "JavaScript exception on file #{errorFile} (line: #{errorLine}): #{errorDesc}".interpolate({errorFile: e.fileName, errorLine: e.lineNumber, errorDesc: e});
        } else if (transport.responseXML) {
            msg = transport.responseXML.documentElement.textContent;
        } else {
            try {
                m = JSON.parse(transport.responseText);
                msg = m.message;
            } catch (e) {}
            if(!msg){
                msg = "HTTP Error #{status} - #{text}".interpolate({status: transport.status, text: transport.statusText});
            }
        }
        console.log(msg);
    }

});
