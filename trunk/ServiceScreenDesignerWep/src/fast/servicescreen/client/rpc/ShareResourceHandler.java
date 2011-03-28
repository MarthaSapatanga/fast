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
package fast.servicescreen.client.rpc;

import java.util.HashMap;

import net.sourceforge.htmlunit.corejs.javascript.json.JsonParser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fast.common.client.BuildingBlock;
import fast.common.client.FactPort;
import fast.common.client.FactType;
import fast.servicescreen.client.RequestService;
import fast.servicescreen.client.RequestServiceAsync;

public class ShareResourceHandler {

	//maybe roll out to config-file
	private String gvsUrl = "http://localhost:13337/";
	private String catalogueUrl = "http://localhost:8080/Catalogue-1";
	private String codeBaseUrl = "static/servicescreendesignerwep/";

	private RequestServiceAsync shResService;

	public String share(BuildingBlock res)
	{
		final String resultString = "";

		//url and header
		String url = gvsUrl + "buildingblock/resource";
		final String cookie = "fastgvsid=" + Cookies.getCookie("fastgvsid");
		final HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", cookie);

		//build operator
		String body = "buildingblock=" + buildResource(res);

		//		Window.alert("Resource: " + body);

		//upload to GVS
		shResService = GWT.create(RequestService.class);
		shResService.sendHttpRequest_POST(url, headers, body, new AsyncCallback<String>(){
			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(String result) {
				if(result != null && result != "-1")
				{
					JSONValue resourceVal = JSONParser.parse(result);
					JSONObject resourceObj = resourceVal.isObject();
					JSONValue idVal = resourceObj.get("id");
					JSONNumber idNum = idVal.isNumber();
					String id = idNum.toString();

					if(id != null)
					{
						String shareUrl = gvsUrl + "buildingblock/" + id + "/sharing";
						shResService.sendHttpRequest_POST(shareUrl, headers, "", new AsyncCallback<String>(){
							@Override
							public void onFailure(Throwable caught)
							{
								//Resource couldn't be shared
								System.out.println("Resource couldn't be shared" + caught.getMessage());
							}

							@Override
							public void onSuccess(String result)
							{
								//Resource was shared
								System.out.println("Resource was shared: " + result);
							}
						});
					}
				}
			}
		});

		return resultString;
	}

	//assembles the request body
	private String buildResource(BuildingBlock res)
	{	
		String result = "";		
		JSONObject resource = new JSONObject();

		//name
		String name = res.getName();
		JSONString resName = new JSONString(name);
		resource.put("name", resName);
		
		//label as seen in GVS
		JSONObject resLabel = new JSONObject();
		resLabel.put("en-gb", new JSONString(res.getName()));
		resource.put("label", resLabel);

		//code
		String codeUrl = codeBaseUrl + res.getName() + "Op.js";
		JSONString resCode = new JSONString(/*codeUrl*/"http://127.0.0.1:8080/ServiceDesignerWep/servicescreendesignerwep/F1DriversPerSeasonOp.js");
		resource.put("code", resCode);

		//version - left blank will let the gvs generate it
		JSONString resVersion = new JSONString("");
		resource.put("version", resVersion);

		//actions TODO tg adjust
		JSONArray resActions = new JSONArray();
		JSONObject actions1 = new JSONObject();
		//actions.name
		actions1.put("name", new JSONString("search"));
		//actions.preconditions [id,label,pattern,positive] TODO tg adjust
		FactPort preCondition = (FactPort)res.iteratorOfPreconditions().next();
		JSONArray preConds = new JSONArray();
		JSONObject preCond1 = new JSONObject();
		//actions.preconditions.id
		preCond1.put("id", new JSONString("filter"));		
		//actions.preconditions.label
		JSONObject preCond1Label = new JSONObject();
		preCond1Label.put("en-gb", new JSONString("A filter"));
		preCond1.put("label", preCond1Label);
		//actions.preconditions.pattern
		//		String preCond1Pattern = "?" + preCondition.getName() + " " + preCondition.getUri() + " " + ;
		String preCond1Pattern = "?eFilter http://www.w3.org/1999/02/22-rdf-syntax-ns#type http://localhost:8080/Catalogue-1/concepts/kassel/f1season";
		preCond1.put("pattern", new JSONString(preCond1Pattern));
		//actions.preconditions.positive
		preCond1.put("positive", new JSONString("true"));
		preConds.set(0, preCond1);

		//		//preconditions[id,label,pattern,positive]
		//		JSONArray preConds = new JSONArray();
		//		int index = 0;
		//		for (Iterator<FactPort> iterator = screen.iteratorOfPreconditions(); iterator.hasNext();) {
		//			FactPort preCond = (FactPort) iterator.next();
		//			//name
		//			String preCondStringName = preCond.getName();
		//			if( preCondStringName == null )
		//			{
		//				preCondStringName = "";
		//			}
		//			JSONString preCondName = new JSONString(preCondStringName);
		//			//factType
		//			String preCondStringFactType = preCond.getFactType();
		//			if( preCondStringFactType == null )
		//			{
		//				preCondStringFactType = "";
		//			}
		//			JSONString preCondFactType = new JSONString(preCondStringFactType);
		//			//exampleValue
		//			String preCondStringExampleValue = preCond.getExampleValue();
		//			if( preCondStringExampleValue == null )
		//			{
		//				preCondStringExampleValue = "";
		//			}
		//			JSONString preCondExampleValue = new JSONString(preCondStringExampleValue);
		//
		//			JSONObject inPort = new JSONObject();
		//			inPort.put("name", preCondName);
		//			inPort.put("factType", preCondFactType);
		//			inPort.put("exampleValue", preCondExampleValue);
		//			
		//			preConds.set(index, inPort);
		//			index++;
		//		}
		actions1.put("preconditions", preConds);
		//actions.uses
		actions1.put("uses", new JSONArray());
		resActions.set(0, actions1);
		resource.put("actions", resActions);

		//postconditions: [id, label, pattern, positive] TODO tg adjust
		FactPort postCondition = (FactPort)res.iteratorOfPostconditions().next();
		JSONArray postConds = new JSONArray();
		JSONArray innerPostConds = new JSONArray();
		JSONObject postCond1 = new JSONObject();
		//actions.preconditions.id
		postCond1.put("id", new JSONString("list"));		
		//actions.preconditions.label
		JSONObject postCond1Label = new JSONObject();
		postCond1Label.put("en-gb", new JSONString("A driver list"));
		postCond1.put("label", postCond1Label);
		//actions.preconditions.pattern
		String postCond1Pattern = "?f1driverlist http://www.w3.org/1999/02/22-rdf-syntax-ns#type http://localhost:8080/Catalogue-1/concepts/kassel/f1driverlist";
		postCond1.put("pattern", new JSONString(postCond1Pattern));
		//actions.preconditions.positive
		postCond1.put("positive", new JSONString("true"));
		innerPostConds.set(0, postCond1);
		postConds.set(0, innerPostConds);
		resource.put("postconditions", postConds);

		//		index = 0;
		//		for (Iterator<FactPort> iterator = screen.iteratorOfPostconditions(); iterator.hasNext();) {
		//			FactPort postCond = (FactPort) iterator.next();
		//			//name
		//			String postCondStringName = postCond.getName();
		//			if( postCondStringName == null )
		//			{
		//				postCondStringName = "";
		//			}
		//			JSONString postCondName = new JSONString(postCondStringName);
		//			//factType
		//			String postCondStringFactType = postCond.getFactType();
		//			if( postCondStringFactType == null )
		//			{
		//				postCondStringFactType = "";
		//			}
		//			JSONString postCondFactType = new JSONString(postCondStringFactType);
		//			//exampleValue
		//			String postCondStringExampleValue = postCond.getExampleValue();
		//			if( postCondStringExampleValue == null )
		//			{
		//				postCondStringExampleValue = "";
		//			}
		//			JSONString postCondExampleValue = new JSONString(postCondStringExampleValue);
		//
		//			JSONObject outPort = new JSONObject();
		//			outPort.put("name", postCondName);
		//			outPort.put("factType", postCondFactType);
		//			outPort.put("exampleValue", postCondExampleValue);
		//
		//			postConds.set(index, outPort);
		//			index++;
		//		}
		//		resource.put("postconditions", postConds);

		//description TODO tg
		JSONObject resDescription = new JSONObject();
		resDescription.put("en-gb", new JSONString("This is a " + res.getName()));
		resource.put("description", resDescription);

		//uri TODO tg unnecessary!
//		JSONString resUri = new JSONString("http://localhost:8080/Catalogue-1/services/230");
//		resource.put("uri", resUri);


		//the external libs that the BB needs to work
		JSONArray resLibs = new JSONArray();
		resource.put("libraries", resLibs);

		//triggers
		JSONArray resTriggers = new JSONArray();
		resource.put("triggers", resTriggers);

		//tags TODO tg will be helpful when searching
		JSONArray resTags = new JSONArray();
		resource.put("tags", resTags);

		//rights - default rights
		JSONString resRights = new JSONString("http://creativecommons.org/");
		resource.put("rights", resRights);

		//homepage - default homepage
		JSONString resHomepage = new JSONString("http://fast.morfeo-project.eu/");
		resource.put("homepage", resHomepage);

		//icon - default icon
		JSONString resIcon = new JSONString("http://demo.fast.morfeo-project.org/gvsdata/images/catalogue/" + name + "icon.jpg");
		resource.put("icon", resIcon);

		//screenshot - default screenshot
		JSONString resScreenshot = new JSONString("http://www.deri.ie/" + name + "screenshot.jpg");
		resource.put("screenshot", resScreenshot);

		result = resource.toString();

		return result;
	}
}