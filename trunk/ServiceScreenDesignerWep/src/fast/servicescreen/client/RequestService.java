/**
 * Copyright (c) 2008-2011, FAST Consortium
 * 
 * This file is part of FAST Platform.
 * 
 * FAST Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FAST Platform is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with FAST Platform. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Info about members and contributors of the FAST Consortium
 * is available at http://fast.morfeo-project.eu
 *
 **/
package fast.servicescreen.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("request")	//the sub url to request
public interface RequestService extends RemoteService
{
	String sendHttpRequest_GET(String url, HashMap<String, String> headers);
	
	String sendHttpRequest_POST(String url, HashMap<String, String> headers, String body);
	
	String sendHttpRequest_PUT(String url, HashMap<String, String> headers, String body);
	
	String sendHttpRequest_DELETE(String url, HashMap<String, String> headers, String body);
	
	String saveJsFileOnServer(boolean isLocal, String opName, String preHTMLCode, String transCode, String postHTMLCode);
}
