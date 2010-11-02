package fast.servicescreen.client.gui.codegen_js;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fast.common.client.BuildingBlock;
import fast.common.client.FASTMappingRule;
import fast.common.client.FactPort;
import fast.common.client.ServiceScreen;
import fast.servicescreen.client.RequestService;
import fast.servicescreen.client.RequestServiceAsync;
import fast.servicescreen.client.gui.RuleUtil;
import fast.servicescreen.client.gui.codegen_js.CodeGenViewer.WrappingType;
import fast.servicescreen.client.gui.parser.Kind;
import fast.servicescreen.client.gui.parser.Operation;
import fast.servicescreen.client.gui.parser.OperationHandler;

/**
 * Use the constructor, it will create the template
 * */
public class CodeGenerator
{
	public BuildingBlock screen = null;
	protected HashMap<String, String> table = null;
	
	//the requested source type
	WrappingType reqType;
	
	protected boolean firstOperation = true;
	
	//Operation names
	protected String trimBoth 	 = "Trim(";
	protected String charsFromTo = "charsFromTo(";
	protected String wordsFromTo = "wordsFromTo(";
	protected String until 		 = "until(";
	protected String _from 		 = "from(";
	
	protected String depth  = "   ";
	protected String depth2 = "      ";
	protected String depth3 = "         ";
	protected String depth4 = "            ";
	protected String depth5 = "               ";
	
	
	public CodeGenerator(BuildingBlock screen)
	{
		this.screen = screen;
		
		//set up the templates for the specific code generator
		setTemplates();
	}
	
	/**
	 * U should not use this constructor
	 * */
	public CodeGenerator(){}
	
	public String resetTemplates()
	{
		//resets the templates
		CodeGenerator tmp = createEmptyGenerator();
		rootTemplate = tmp.rootTemplate;
		prehtml = tmp.prehtml;
		posthtml = tmp.posthtml;
		helperMethods = tmp.helperMethods;
		
		//resets some state variables
		firstOperation = true;
		operationStart = true;
		
		return rootTemplate;
	}
	
	/**
	 * This method returns an empty CodeGenerator.
	 * 
	 * Overwrite this method to create a specific CodeGenerator, 
	 * it will be used to reset the Generator and make the resetTemplates()
	 * method work!
	 * */
	protected CodeGenerator createEmptyGenerator()
	{
		return new CodeGenerator(null);
	}
	
	/**
	 * Code generation order
	 * */
	public String generateJS()
	{
		table = new HashMap<String, String>();		
		
		//Build the input port list
		add_InPorts_toTable();
		
		//Build the request
		add_PreRequest_toTable();
		add_PreRequestReplaces_toTable();
		
		//Build the exRules - feature
		add_Translation_toTable();
		
		//fill founded values into the <keywords> in rootTemplate
		String tmp = rootTemplate;
		rootTemplate = expandTemplateKeys(tmp);
		
		//replace input port in transform method outer root template
		tmp = posthtml;
		posthtml = expandTemplateKeys(tmp);

		return rootTemplate;
	}

	protected void add_PreRequest_toTable()
	{	
		// lookup the gui request text field
		if (screen instanceof ServiceScreen)
		{
			String prerequestText = ((ServiceScreen)screen).getRequestTemplate();
			table.put("<<prerequest>>", prerequestText);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void add_PreRequestReplaces_toTable()
	{	
		// lookup the gui input ports
		FactPort currentInpPort = null;
		String preReqRepText = "";
		String indent = "";
		
		for (Iterator<FactPort> iterator = screen.iteratorOfPreconditions(); iterator.hasNext();)
		{
			currentInpPort = iterator.next();
			
			//build prerequestreplaces
			preReqRepText += indent + "prerequest = prerequest.replace(/<";
			preReqRepText += currentInpPort.get("name");
			preReqRepText += ">/g,encodeURIComponent("; 
			preReqRepText += currentInpPort.get("name");
			preReqRepText += ")); \n ";
			indent = depth2;
		}
		
		//add result lines in the table
		table.put("<<prerequestreplaces>>", preReqRepText);
	}
	
	
	/**
	 * This method currently adds more then one input port, if needed.
	 * Notice: The current understanding of FAST is, that we an�t need more then one input value.
	 * */
	@SuppressWarnings("unchecked")
	protected void add_InPorts_toTable()
	{	
		// lookup the gui input ports
		String inputPortText = "";
		FactPort currentInPort = null;
		
		for (Iterator<FactPort> iterator = screen.iteratorOfPreconditions(); iterator.hasNext();)
		{
			currentInPort = iterator.next();
			
			inputPortText += currentInPort.get("name") + ",";
		}
		
		//if we add one "," to much, cut it here
		if(inputPortText.lastIndexOf(",") == inputPortText.length()-1)
		{
			inputPortText = inputPortText.substring(0, inputPortText.length()-1);
		}

		table.put("<<inputportlist>>", inputPortText);
	}
	
	protected String transCode = "";
	protected String currentTags = "currentTags";
	protected void add_Translation_toTable()
	{
		transCode = "";
		
		//take rootRule
		FASTMappingRule rootRule = (FASTMappingRule) screen.iteratorOfMappingRules().next();
		
		//run threw all rules and append js code. Returns js code    
		transform(rootRule, depth5);
		
		table.put("<<transformationCode>>", transCode);
	}
	
	protected boolean operationStart = true;
	protected void transform(FASTMappingRule rule, String codeIndent)
	{
		boolean hasOpenSqareBracket = false;
		boolean hasOpenForLoop = false;
		
		if(RuleUtil.isCompleteRule(rule) && rule.getOperationHandler() != null)
		{
			//get the current operationList 
			OperationHandler opHandler = (OperationHandler) rule.getOperationHandler();
			Iterator<ArrayList<Operation>> opList_iter = opHandler.getOperationlistIterator();
			ArrayList<Operation> current_opList = null;
			
			String kind = rule.getKind();
						
			if ("createObject".equals(kind))
			{
				String tmpCode = "";
				
				//from to
				String from = rule.getSourceTagname();
				String target = rule.getTargetElemName();
				
				//In first case we have to access from the root Tag
				String curTag = currentTags;
				if(firstOperation)
				{
					firstOperation = false;
					
					curTag = "xmlResponse";
					
					tmpCode += "var indent = ''; \n";
				}
				else
				{
					tmpCode += codeIndent + "indent = indent + '   '; \n";
				}

				//element count
				String lengthName =  from + "_length";
				
				//create element count variable
				tmpCode += codeIndent + "var " + lengthName + " = " + curTag + ".getElementsByTagName('" + from + "').length; \n";
				
				//increment var for loop
				String countVar = from + "_Count";
				
				//cr�ate loop - code				
				tmpCode += 
					//get searched elementsList out of xmlResponse 
					codeIndent + "var " + from + " = " + curTag + ".getElementsByTagName('" + from + "'); \n\n" +
							
					//declare loop rump
					codeIndent + "for(var " + countVar + " = 0; " + countVar + " < " + lengthName + "; ++" + countVar + ")\n" +
					codeIndent + "{\n" +
							
					//declare loop body
					codeIndent + depth + currentTags + " = " + from + ".item(" + countVar + ");\n\n" +
							
					 //adds a current index variable
					codeIndent + depth + "currentCount = " + countVar + "; \n\n";		
				
				hasOpenForLoop = true;
						
				 //adds a 'new object' in the result, jumps over types that are needles in JSON
				if(target.endsWith("List"))
				{
					tmpCode += codeIndent + depth + "result += indent + '\"" + target + "\" : [ \\n'; \n";
					hasOpenSqareBracket = true;
				}
				else
				{
					tmpCode += codeIndent + depth + "result += indent + '{ \\n'; \n\n";
				}

				//overtake loop in real transcode
				transCode += tmpCode;
			}
			
			else if ("fillAttributes".equals(kind))
			{
				String tmpCode;
				
				//Iterate over any opList entry
				while(opList_iter.hasNext())
				{
					tmpCode = "";
					String lastSourceTagname = "";
					String attrName = rule.getTargetElemName();
					
					current_opList = opList_iter.next();
					
					//if constant
					if(current_opList.size() > 0 && current_opList.get(0).kind == Kind.constant)
					{
						//add a constant
						tmpCode += "'" + current_opList.get(0).value + "'";
					}
					//if operations
					else
					{
						Operation lastTagnameOperation = opHandler.getLastTagnameOf(current_opList);
						lastSourceTagname = lastTagnameOperation.value;
						
						//create code for getting the current (working)element list
						tmpCode += trimBoth +  " getValue(currentTags, '" + lastSourceTagname + "') )";
						
						//create code for the hole operation list
						int count = current_opList.indexOf(lastTagnameOperation) + 1;
						for(; count < current_opList.size(); ++count)
						{
							Operation currentOp = current_opList.get(count);
							
							//create code for executing the currentOperation
							tmpCode = createOperationCode(tmpCode, currentOp);
						}
					}
					
					
					//overtake operation code in real transcode
					if(operationStart)
					{
						transCode += codeIndent + "result += indent + '   \"" + attrName + "\" : \"' + " + tmpCode;
						
						operationStart = false;
					}
					else
					{
						transCode += tmpCode;	
					}
					
					
					//if there are more operationLists, add a '+' , else a ';'
					if(opList_iter.hasNext())
					{
						transCode += " + ";
					}
					else
					{
						//means, we reach the last fillAttr. rule!
						if(rule.getParent().getLastOfKids() == rule)
						{
							transCode += " + '\" \\n' + indent + '}, \\n'; \n";
						}
						else
						{
							transCode += " + '\", \\n'; \n";
						}
						
						operationStart = true;
					}
				}
			}
			
			callTransformForKids(rule, codeIndent + depth);
			
			if (hasOpenForLoop)
			{
				transCode += codeIndent + "} \n";
			}
			
			if (hasOpenSqareBracket)
			{
				transCode += codeIndent + "result += ' ]\\n';";
			}
		}
	}
	
	/**
	 * append code for given operation into tmpCode and return that
	 * */
	protected String createOperationCode(String tmpCode, Operation op)
	{
		switch(op.kind)
		{
			case chars: 	tmpCode = charsFromTo + tmpCode + ", ";
							break;
				
			case words:     tmpCode = wordsFromTo + tmpCode + ", ";
							break;
				
			case Param: 	String signs = op.signs;
							if(signs != null && ! "".equals(signs))
							{
								tmpCode += "'" + signs + "', ";
							}
							
							tmpCode += op.value.replace("-", ", ") + ")";
							break;
				
			case from: 		tmpCode = _from + tmpCode + ", ";
							break;

				
			case until: 	tmpCode = until + tmpCode + ", ";
							break;
		}
		
		return tmpCode;
	}
	
	/**
	 * Should call transformation method for any child of given rule
	 * to add code into transCode
	 * */
	@SuppressWarnings("unchecked")
	protected void callTransformForKids(FASTMappingRule rule, String codeIndent)
	{
		for (Iterator<FASTMappingRule> kidIter = rule.iteratorOfKids(); kidIter.hasNext();)
		{
			FASTMappingRule kid = (FASTMappingRule) kidIter.next();
			transform(kid, codeIndent);
		}
	}

	/**
	 * Replace any <key> with a value, if the table contains it.
	 * 
	 * Returns the filled template.
	 * */
	protected String expandTemplateKeys(String template)
	{
		for (String key : table.keySet()) 
		{
			String value = table.get(key);
			// template = template.replaceAll(key, value);
			template = replaceKey(template, key, value);
		}
		
		return template;
	}
	
	
	protected String replaceKey(String text, String key, String value) 
	{
		// find key and replace by value
		String result = text;
		int pos = text.indexOf(key);
		while (pos >= 0)
		{
			result = result.substring(0, pos) + value + result.substring(pos + key.length());
			pos = result.indexOf(key);
		}
		return result;
	}

	protected static RequestServiceAsync service;
	/**
	 * This method send the current template to
	 * an service which writes a js and a html file with it as content. 
	 * */
	public void write_JS_File()
	{
		if (service == null)
		{ 
			service = GWT.create(RequestService.class);
		}

		//send pre - trans - post code to server
		service.saveJsFileOnServer(screen.getName(), prehtml, helperMethods + rootTemplate, posthtml, new AsyncCallback<String>()
				{
					@Override
					public void onSuccess(String result)
					{
						GWT.log("Writing .js file to RequestService succed..", null);
						
						Window.alert(result);
					}

					@Override
					public void onFailure(Throwable caught)
					{
						GWT.log("ERROR while writing .js file to RequestService..", null);
						
						Window.alert(caught.getLocalizedMessage());
					}
				});
	}
	
	
	
	// -------------- the template strings -------------- //
	
	/**
	 * Contains root template and is set up in
	 * setTemplates method
	 * */
	String rootTemplate = 
		//declare method rump 
		depth + "search: function (filter)\n" +
		depth + "{\n" +
		    depth2 + "var <<inputportlist>> = filter.data.<<inputportlist>>;\n" +
		    
			//fill request url
			depth2 + "var prerequest = '<<prerequest>>';\n" +
			
			//should replace inports to real values in runtime!
			depth2 + "<<prerequestreplaces>>\n\n" +
			
			//save the complete url with an xmlHttp request (made for Ajax access to SameDomain Resources)
			depth2 + "var request = prerequest;\n" +
			
			//sending/recieving the request
			depth2 + "//Invoke the service\n" +
			depth2 + "    new FastAPI.Request(request,{\n" +
			depth2 + "        'method':       'get',\n" +
			depth2 + "        'content':      'xml',\n" +
			depth2 + "        'context':      theOperator,\n" +
			depth2 + "        'onSuccess':    theOperator.addToList\n" +
			depth2 + "    });\n\n" +
		depth + "},\n" +
		depth + "\n" +
		//next method rump 
		depth + "addToList: function (transport) \n" +
		depth + "{ \n" +
		    depth2 + "var xmlResponse = transport;\n" +
	        depth2 + "var currentTags = null; \n\n" +
	        depth2 + "var currentCount = null; \n\n" +
	        depth2 + "var result = new String('{ '); \n\n" +
	        depth2 + "<<transformationCode>>\n\n" +
	        depth2 + "result += ' }'; \n" +
		    depth2 + "var jsonResult = JSON.parse(result); \n" +
		    depth2 + "var factResult = {data: {productList: jsonResult}}\n" +
		    depth2 + "if (this.manageData) {\n" +
		    depth2 + "   this.manageData([\"itemList\"], [factResult], [])\n" +
		    depth2 + "}\n" +
		    depth2 + "else {\n" +
		    depth2 + "   document.getElementById('show').value = result;\n" +
		    depth2 + "}\n" +
			depth + "}, \n" +
			depth + "\n" +
			//next method rump
			depth + "onError: function (transport){} \n" +
			depth + "\n" +
		"\n";
	
	/**
	 * contains HTML end
	 * */
	public String posthtml =
		"}\n" + 
		"var _debugger = null;\n" + 
		"function transform (param) {\n" + 
		"   var factParam = {data: {<<inputportlist>>: param}} \n" + 
		"   var result = theOperator.search (factParam); \n" + 
		"}\n" + 
		"\n" + 
		"</script>\n" +
		"</head>\n" +
		"<body>\n" +
		"<form name=f1>\n" +
		"<input type='text' name=t2 value='x' size='50'> \n" +
		"<input type=button value='request and transform' \n" +
		"onclick='this.form.t1.value=transform(this.form.t2.value )'>	\n" +
		"<br><br><br><br> \n" +
		"<textarea name=t1 id='show' cols=70 rows=20> To see the results, press the button above.. </textarea>" +
		"</form>\n" +
		"</body>\n" +
		"</html>\n";
	
	/**
	 *  Contains the HTML header
	 * */
	public String prehtml =
		"<html> \n" +
		"<head> \n" +
		"<meta http-equiv='Content-Type' content='text/html; charset=ISO-8859-1'> \n" +
		"<title>Insert title here</title> \n" +
		"<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:13337/static/1/js/prototype/prototype.js\"></script> \n" +
		"<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:13337/static/1/js/cjson_parse/cjson_parse.js\"></script> \n" +
		"<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:13337/static/1/js/fast/menu.js\"></script> \n" +
		"<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:13337/static/1/js/fast/screenflowEngine.js\"></script> \n" +
		"<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:13337/static/1/js/fast/screenEngine.js\"></script> \n" +
		"<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:13337/static/1/js/fast/buildingblock.js\"></script> \n" +
		"<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:13337/static/1/js/fast/debugger.js\"></script> \n" +
		"<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:13337/static/servicescreendesignerwep/kasselStringUtils.js\"></script> \n" +
		"<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:13337/static/1/js/fastAPI/fastAPI.js\"></script> \n" +
		"<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:13337/static/1/js/fastAPI/fastAPI_player.js\"></script> \n" +
		"<script type='text/javascript'> \n \n" + 
		"  var theOperator = { \n" + 
		"\n";
	
	/**
	 * This method is used to overwrite and set up
	 * templates. In cooperation with string helpermethod, it�s a 
	 * fast way to add .js code functionality.
	 * */
	protected void setTemplates()
	{
		//..
	}
	
	public String buildingBlockTemplate =
		//declare JSON object 
		depth + "{\n" +
			//name
		    depth2 + "\"name\" : \"<<name>>\"\n" +
		    
			//url of the code
			depth2 + "\"code\" : \"<<codeUrl>>\"\n" +
			
			//actions = {name, id, preconditions, uses}
			depth2 + "\"actions\" : {\n" +
			
				//name of the action
				depth3 + "\"name\" : \"<<actionName>>\"\n" +
				
				//preconditions = [{},{...}]
				depth3 + "\"preconditions\" : [\n" +
				
					//a precondition
					depth4 + "{\n" +
					
						//precondition id
						depth5 + "\"id\" : \"<<preconditionID>>\"\n" +
					
						//precondition label
						depth5 + "\"label\" : \"<<preconditionLabel>>\"\n" +
						
						//precondition pattern
						depth5 + "\"pattern\" : \"<<preconditionPattern>>\"\n" +
						
						//precondition positive default = true
						depth5 + "\"positive\" : \"true\"\n" +
					
					depth4 + "}\n" +
					
				depth3 + "]\n" +
				
				//action uses
				depth3 + "\"uses\" : []\n" +
			
			depth2 + "}\n" +
			
			//postconditions= [{},{...}]
			depth2 + "\"postconditions\" : [\n" +
			
				//a postcondition
				depth4 + "{\n" +
			
					//postcondition id
					depth5 + "\"id\" : \"<<postconditionID>>\"\n" +
				
					//postcondition label
					depth5 + "\"label\" : \"<<postconditionLabel>>\"\n" +
					
					//postcondition pattern
					depth5 + "\"pattern\" : \"<<postconditionPattern>>\"\n" +
					
					//postcondition positive default = true
					depth5 + "\"positive\" : \"true\"\n" +
			
				depth4 + "}\n" +
			
			depth2 + "]\n" +
			
			//version can be blank
			depth2 + "\"version\" : \"\"\n" +
			
			//label
			depth2 + "\"label\" : { \"en\" : \"<<label>>\"}\n" +
			
			//description
			depth2 + "\"description\" : \"<<description>>\"\n" +
			
			//external libs that the bb needs to work
			depth2 + "\"libraries\" : []\n" +
			
			//triggers
			depth2 + "\"triggers\" : []\n" +
			
			//tags
			depth2 + "\"tags\" : [<<tags>>]\n" +
			
			//default rights
			depth2 + "\"rights\" : \"http://creativecommons.org/\"\n" +
			
			//default homepage
			depth2 + "\"homepage\" : \"http://fast.morfeo-project.eu/\"\n" +
			
			//"default" icon
			depth2 + "\"icon\" : \"http://demo.fast.morfeo-project.org/gvsdata/images/catalogue/<<name>>icon.jpg\"\n" +
			
			//"default" screenshot
			depth2 + "\"screenshot\" : \"http://www.deri.ie/<<name>>screenshot.jpg\"\n" +
		depth + "}\n" +
		"\n";
	
	public String helperMethods = "";
}


//@SuppressWarnings({ "unchecked", "unused" })
//private void add_outPort_toTable()
//{
//	String outputPortVar = "";
//	String outPutVarType = "";
//	
//	//search outPortName
//	Iterator<FactPort> iterator = screen.iteratorOfPostconditions();
//	FactPort currentOutPort = iterator.next();
//	
//	if(currentOutPort != null && ! iterator.hasNext()/*only one allowed*/)
//	{
//		outputPortVar = "" + currentOutPort.get("name");
//		
//		outPutVarType = (String) currentOutPort.get("factType");	//Should be the outputs type
//	}
//
//	//create code for variable initialization 
//	this.outputPortName = outputPortVar;
//	outputPortVar = depth2 + "var " + outputPortVar + " = new " + outPutVarType + "(); \n";
//	
//	table.put("<<outputport>>", outputPortVar);
//}