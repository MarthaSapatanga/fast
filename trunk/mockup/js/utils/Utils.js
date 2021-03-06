
/**
* MORFEO Project
* http://morfeo-project.org
*
* Component: FAST
*
* (C) Copyright 2008 Telefónica Investigación y Desarrollo
*     S.A.Unipersonal (Telefónica I+D)
*
* Info about members and contributors of the MORFEO project
* is available at:
*
*   http://morfeo-project.org/
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*
* If you want to use this software an plan to distribute a
* proprietary application in any way, and you are not licensing and
* distributing your source code under GPL, you probably need to
* purchase a commercial license of the product.  More info about
* licensing options is available at:
*
*   http://morfeo-project.org/
*/
function openWindowWithPost(url, name, keys, values, windowFeatures){
	var newWindow = window.open('', name, windowFeatures);
	if (!newWindow) return false;
	var html = "";
	html += "<html><head></head><body><form id='formid' method='post' action='" + url + "'>";
	if (keys && values && (keys.length == values.length)){
		for (var i=0; i < keys.length; i++){
			html += "<input type='hidden' name='" + keys[i] + "' value='" + values[i] + "'/>";
		}
	}
	html += "</form><script type='text/javascript'>document.getElementById(\"formid\").submit()</script></body></html>";
	newWindow.document.write(html);
	return newWindow;
}
