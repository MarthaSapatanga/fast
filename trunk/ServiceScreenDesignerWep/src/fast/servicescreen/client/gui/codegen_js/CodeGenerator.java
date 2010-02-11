package fast.servicescreen.client.gui.codegen_js;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fast.FASTMappingRule;
import fast.FactPort;
import fast.servicescreen.client.RequestService;
import fast.servicescreen.client.RequestServiceAsync;
import fast.servicescreen.client.ServiceScreenDesignerWep;
import fast.servicescreen.client.gui.RuleUtil;
import fast.servicescreen.client.gui.ExtendedRuleParser.Kind;
import fast.servicescreen.client.gui.ExtendedRuleParser.Operation;
import fast.servicescreen.client.gui.ExtendedRuleParser.OperationHandler;

/**
 * Use the constructor to create the first template
 * */
public class CodeGenerator
{
	private ServiceScreenDesignerWep designer = null;
	private HashMap<String, String> table = null;
	private String rootTemplate = "";
	private boolean writeFile_result = true;
	private String endbrakets = "";
	
	@SuppressWarnings("unused")
	private String outputPortName = "";	//use later to generate translation code
	
	//Operation names
	private String trimBoth 	= "Trim(";
	private String charsFromTo 	= "charsFromTo(";
	private String wordsFromTo 	= "wordsFromTo(";
	private String until 		= "until(";
	private String _from 		= "from(";
	
	private boolean firstTime = true;
	
	/**
	 * The constructor creates the first template
	 * */
	public CodeGenerator(ServiceScreenDesignerWep designer)
	{
		this.designer = designer;
		
		setStartingRootTemplate();
	}
	
	public String setStartingRootTemplate()
	{
		//fill the rootTemplate
		rootTemplate =
			
			//declare method rump 
			"function transform(<<inputportlist>>) \n" +
			"{\n" +
			
			//fill request url
			"   var prerequest = '<<prerequest>>'; \n" +
			"\n" +
			
			//should replace inports to real values in runtime!
			"  <<prerequestreplaces>>" +
			"\n" +
			
			//save the complete url with an xmlHttp request (made for Ajax access to SameDomain Resources)
			"  var request = prerequest; \n" +
			"\n" +
			
			//sending/recieving the request
			"  <<sendrequest>> \n" +
			"\n" +
			
			//the outputPort variable
//			"  <<outputport>> \n" +
			"\n" +
			
			//declare method end
			"}\n";

		//resets some variables
		endbrakets = "";
		firstTime = true;
		operationStart = true;
		
		return rootTemplate;
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
		
		//Build the request - code
		add_SendRequest_toTable();

		//Build the outport (setup this.outputPortName, too)
		add_outPort_toTable();
		
		//Build the exRules - feature
		add_Translation_toTable();
		
		
		//load the current template text into root template (for user changes, delete it later)
		rootTemplate = designer.codeGenViewer.get_templateShow_Text();
		
		//fill founded values into the <keywords> in rootTemplate
		rootTemplate = expandTemplateKeys(rootTemplate);

		return rootTemplate;
	}

	private void add_PreRequest_toTable()
	{	
		// lookup the gui request text field
		String prerequestText = designer.serviceScreen.getRequestTemplate();
		table.put("<<prerequest>>", prerequestText);
	}
	
	@SuppressWarnings("unchecked")
	private void add_PreRequestReplaces_toTable()
	{	
		// lookup the gui input ports
		FactPort currentInpPort = null;
		String preReqRepText = "";
		
		for (Iterator<FactPort> iterator = designer.serviceScreen.iteratorOfPreconditions(); iterator.hasNext();)
		{
			currentInpPort = iterator.next();
			
			//build prerequestreplaces
			preReqRepText += "prerequest = prerequest.replace(/<";
			preReqRepText += currentInpPort.get("name");
			preReqRepText += ">/g,"; 
			preReqRepText += currentInpPort.get("name");
			preReqRepText += "); \n ";
		}
		
		//add result lines in the table
		table.put("<<prerequestreplaces>>", preReqRepText);
	}
	
	@SuppressWarnings("unchecked")
	private void add_InPorts_toTable()
	{	
		// lookup the gui input ports
		String inputPortText = "";
		FactPort currentInPort = null;
		
		for (Iterator<FactPort> iterator = designer.serviceScreen.iteratorOfPreconditions(); iterator.hasNext();)
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
	
	@SuppressWarnings("unchecked")
	private void add_outPort_toTable()
	{
		String outputPortVar = "";
		String outPutVarType = "";
		
		//search outPortName
		Iterator<FactPort> iterator = designer.serviceScreen.iteratorOfPostconditions();
		FactPort currentOutPort = iterator.next();
		
		if(currentOutPort != null && ! iterator.hasNext()/*only one allowed*/)
		{
			outputPortVar = "" + currentOutPort.get("name");
			
			outPutVarType = (String) currentOutPort.get("factType");	//Should be the outputs type
		}

		//create code for variable initialization 
		this.outputPortName = outputPortVar;
		outputPortVar = "var " + outputPortVar + " = new " + outPutVarType + "(); \n";
		
		table.put("<<outputport>>", outputPortVar);
	}

	private void add_SendRequest_toTable()
	{
		String sendRequest =
			"var xmlHttp = null; \n" + 
			"  var xmlResponse = null; \n" + 
			"  try \n" +
			"  { \n" + 
			"     xmlHttp = new XMLHttpRequest(); \n" + 
			"  } \n" +
			"  catch(e) \n" +
			"  { \n" + 
			"      try \n" +
			"      { \n" + 
			"          xmlHttp  = new ActiveXObject('Microsoft.XMLHTTP'); \n" + 
			"      } \n" +
			"      catch(e) \n" +
			"      { \n" + 
			"            try \n" +
			"            { \n" + 
			"                  xmlHttp  = new ActiveXObject('Msxml2.XMLHTTP'); \n" + 
			"            } \n" +
			"            catch(e) \n" +
			"            { \n" + 
			"                  xmlHttp  = null; \n" + 
			"            } \n" + 
			"      } \n" + 
			"  } \n" + 
			"\n" + 
			"  if (xmlHttp) \n" +
			"  { \n" + 
			"      xmlHttp.open('GET', '" + requestServlet_URL + "' + replaceEscapeCharacter(request), true); \n" + 
			"      xmlHttp.onreadystatechange = function () { \n" + 
			"            if (xmlHttp.readyState == 4) \n" +
			"            { \n" + 
			"                  xmlResponse = xmlHttp.responseXML; \n" + 
			"\n" +
			"					var currentTags = null; \n\n" +
			"					var currentCount = null; \n\n" +
			"					var result = new String(''); \n\n" +
			"				   <<transformationCode>> \n" +

			endbrakets +
			
			//"				result = result.replace(/./g, '\\\n');\n" +	//breaking the lines in the result
			
			"				document.getElementById('show').value = 'result: ' + result; \n" + 
			"            } \n" + 
			"   } } } \n" + 
			"\n" +
			"      xmlHttp.send(null); \n" + 
			"\n" +
			"      return 'waiting for response...'; \n" + 
			"  } \n";

		
		//reset endBrakets
		endbrakets = "";
		
		//add result in the table
		table.put("<<sendrequest>>", sendRequest);
	}
	
	private String transCode = "";	// should only be added ONE time
	private String currentTags = "currentTags";
	private void add_Translation_toTable()
	{
		transCode = "";
		
		//take rootRule
		FASTMappingRule rootRule = (FASTMappingRule) designer.serviceScreen.iteratorOfMappingRules().next();
		
		//run threw all rules and append js code. Returns js cdoe    
		transform(rootRule);
		
		table.put("<<transformationCode>>", transCode);
	}

	private boolean operationStart = true;
	private void transform(FASTMappingRule rule)
	{
		if(RuleUtil.isCompleteRule(rule))
		{
			//get the current operationList 
			OperationHandler opHandler = rule.getOperationHandler();
			Iterator<ArrayList<Operation>> opList_iter = opHandler.getOperationlistIterator();
			ArrayList<Operation> current_opList = null;
			
			String kind = rule.getKind();
			
			if ("createObject".equals(kind))
			{
				String tmpCode = "";
				
				//from to
				String from = rule.getSourceTagname();
				String target = rule.getTargetElemName();
				
				String curTag = currentTags;
				if(firstTime)
				{
					firstTime = false;	//reset by setStartTemplate()
					
					curTag = "xmlResponse";		//In first case we access from as current (root)Tag
				}

				
				String lengthName =  from + "_length";
				
				//count of elements to iterate
				tmpCode += "var " + lengthName + " = " + curTag + ".getElementsByTagName('" + from + "').length; \n";
				
				//count var for loop
				String countVar = from + "_Count";
				
				//cr�ate loop - code				
				tmpCode += 
							//get searched elementsList out of xmlResponse 
							"var " + from + " = " + curTag + ".getElementsByTagName('" + from + "'); \n\n" +
							
							"for(var " + countVar + " = 0; " + countVar + " < " + lengthName + "; ++" + countVar + ")\n" +
							"{\n" +
							currentTags + " = " + from + ".item(" + countVar + ")  ;\n\n" +
							
							"currentCount = " + countVar + ";\n" + 	//adds a current index variable 
							
							"result += '" + target + "Object - '; \n" +	//adds a 'new object' in the result
							
							"\n\n";
							
							endbrakets += "} \n";	//manages end breaktes
				
				//overtake in real transcode
				transCode += tmpCode;
			}
			else if ("fillAttributes".equals(kind))
			{
				//watch the example for loops
				
				String tmpCode;
				
				//Iterate over any opList entry
				while(opList_iter.hasNext())
				{
					tmpCode = "";
					
					String lastSourceTagname = "";
					
					current_opList = opList_iter.next();
					
					if(current_opList.size() > 0 && current_opList.get(0).kind == Kind.constant)
					{
						//add a constant
						tmpCode += "'" + current_opList.get(0).value + "'";
					}
					else
					{
						Operation lastTagnameOperation = opHandler.getLastTagnameOf(current_opList);
						lastSourceTagname = lastTagnameOperation.value;
						
						//create code for getting the element/elementsItem
						tmpCode += trimBoth +  currentTags + ".getElementsByTagName('"
								+ lastSourceTagname + "').item(0).textContent)";
						
						//create code for rest of operation list
						int count = current_opList.indexOf(lastTagnameOperation) + 1;
						for(; count < current_opList.size(); ++count)
						{
							Operation currentOp = current_opList.get(count);
							
							//create code for executing the currentOperation
							tmpCode = createOperationCode(tmpCode, currentOp);
						}
					}
					
					//write into transCode
					if(operationStart)
					{
						transCode += "result += '" + lastSourceTagname + "Attribute - ' + " + tmpCode;
						
						operationStart = false;
					}
					else
					{
						transCode += tmpCode;	
					}
					
					if(opList_iter.hasNext())
					{
						transCode += " + ";
					}
					else
					{
						transCode += "; \n";
						
						operationStart = true;
					}
				}
			}
			else if("dummy".equals(kind))
			{
				//simply no handling?
			}
			
			callTransformForKids(rule);
		}
	}
	
	/**
	 * append code for given operation into tmpCode and return that
	 * */
	private String createOperationCode(String tmpCode, Operation op)
	{
		Kind kind = op.kind;
		
		switch(kind)
		{
			case chars: 	tmpCode =  charsFromTo + tmpCode + ", ";
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
	private void callTransformForKids(FASTMappingRule rule)
	{
		for (Iterator<FASTMappingRule> kidIter = rule.iteratorOfKids(); kidIter.hasNext();)
		{
			FASTMappingRule kid = (FASTMappingRule) kidIter.next();
			transform(kid);
		}
	}

	/**
	 * Replace any <key> with a value, if the table contains it.
	 * 
	 * Returns the filled template.
	 * */
	private String expandTemplateKeys(String template)
	{
		for (String key : table.keySet()) 
		{
			String value = table.get(key);
			template = template.replaceAll(key, value);
		}
		
		return template;
	}
	
	/**
	 * This method send the current template to
	 * an service which writes a js file with it as content. 
	 * */
	public boolean write_JS_File()
	{
		// Instantiate service and set up its target url
		RequestServiceAsync service = GWT.create(RequestService.class);

		//send pre - trans - post code to server
		service.saveJsFileOnServer(preHtml + rootTemplate + postHtml, new AsyncCallback<String>()
				{
					@Override
					public void onSuccess(String result)
					{
						if(RuleUtil.getBool(result))
						{
							//If onSucces, and result = "true", return true
							writeFile_result = true;
						}
						else
						{
							writeFile_result = false;
						}
					}

					@Override
					public void onFailure(Throwable caught)
					{
						writeFile_result = false;
					}
				});

		return writeFile_result;
	}

	
	/**
	 * The path to the standalone GET Proxy Servlet
	 * 
	 * Add a Param before sending. Param should be the url u want to request,
	 * but fill with escape code first:
	 * url = http://open.api.sandbox.ebay.com/shopping?appid=KasselUn-efea-4b93-9505-5dc2ef1ceecd&version=517&callname=FindItems&ItemSort=EndTime&QueryKeywords=USB&responseencoding=XML
	 * 
	 * -> with URL Escape Codes: / -> %2F, = -> %3D, ? = %3F, & -> %26, : -> %3A
	 * xxx = http%3A%2F%2Fopen.api.sandbox.ebay.com%2Fshopping%3Fappid%3DKasselUn-efea-4b93-9505-5dc2ef1ceecd%26version%3D517%26callname%3DFindItems%26ItemSort%3DEndTime%26QueryKeywords%3DUSB%26responseencoding%3DXML
	 * 
	 * to do this, there is a replaceEscapeCharacter method in this class!
	 * */
	private final String requestServlet_URL = "http://localhost:8080/servicescreendesignerwep/requestServlet?url=";  
	
	/**
	 * contains html end
	 * */
	private String postHtml =
		"</script>\n" +
		"	</head>\n" +
		"		<body>\n" +
		"			<form name=f1>\n" +
		"				<input type='text' name=t2 value='Harry' size='50'> \n" +
		"				<input type=button value='request and transform' \n" +
		"					onclick='this.form.t1.value=transform(this.form.t2.value)'>	\n" +
		"				<br><br><br><br> \n" +
		"				RESULT:	\n" +
		"				<input type=text name=t1 id='show' value='press the button above..' size=200>\n" +
		"			</form>\n" +
		"		</body>\n" +
		"	</html>\n";
	
	/**
	 *  Contains the html header and all javascript util functions
	 * */
	private String preHtml =
		"<html> \n" +
		"<head> \n" +
		"    <meta http-equiv='Content-Type' content='text/html; charset=ISO-8859-1'> \n" +
		"	  <title>Insert title here</title> \n" +
		"	<script type='text/javascript'> \n \n" +

		"	function from(str, sign, sepNr) \n" +
		"	{	 \n" +
		"		var tmp = new String(Trim(str)); \n" +
		"		var save = ''; \n" +

		"		if (sepNr < 1) \n" +
		"		{ \n" +
		"			sepNr = 1; \n" +
		"		} \n" +

		"		while (tmp.indexOf(sign) != -1 && sepNr > 0) \n" +
		"		{ \n" +
		"			save = tmp.substring(tmp.indexOf(sign), tmp.indexOf(sign) \n" +
		"					+ sign.length); \n" +

		"			tmp = tmp.substring(tmp.indexOf(sign) + sign.length, tmp.length); \n" +

		"			sepNr--; \n" +
		"		} \n" +

		"		tmp = save + tmp; \n" +

		"		return tmp; \n" +
		"	} \n \n" +

		"	function until(str, sign, sepNr) \n" +
		"	{ \n" +
		"		var tmp = new String(Trim(str)); \n" +
		"		var res = ''; \n" +
		"		var length = sign.length; \n" +

		"		if(sepNr < 1) \n" +
		"		{ \n" +
		"			sepNr = 1; \n" +
		"		} \n" +

		"		while(tmp.indexOf(sign) != -1 && sepNr > 0) \n" +
		"		{ \n" +
		"			res += tmp.substring(0, tmp.indexOf(sign) + length); \n" +

		"		tmp = tmp.substring(tmp.indexOf(sign) + length, tmp.length); \n" +

		"			sepNr--; \n" +
		"		} \n" +

		"		return res; \n" +
		"	} \n \n" +

		"	function charsFromTo(str, from, to) \n" +
		"	{ \n" +
		"		var tmp = new String(Trim(str)); \n" +
		"		var res = ''; \n" +
		
		"		if(from < 1) \n" +
		"		{ \n" +
		"			from = 1; \n" +
		"		} \n" +
		
		"		if(to > tmp.length) \n" +
		"		{ \n" +
		"			to = tmp.length; \n" +
		"		} \n" +
		
		"		for(from; from <= to; from++) \n" +
		"		{ \n" +
		"			res += charAt(tmp, from); \n" +
		"		} \n" +
		
		"		return res; \n" +
		"	} \n \n" +
		
		"	function charAt(str, index) \n" +
		"	{ \n" +
		"		var res = ''; \n" +
		
		"		if(index < 1) \n" +
		"		{ \n" +
		"			index = 1; \n" +
		"		} \n" +
		"		else if(index > str.length) \n" +
		"		{ \n" +
		"			index = str.length; \n" +
		"		} \n" +
		
		"		index = index - 1; \n" +
		
		"		res = str.charAt(index); \n" +
		
		"		return res; \n" +
		"	} \n \n" +
		
		"	function wordsFromTo(str, from, to) \n" +
		"	{ \n" +
		"		var tmp = new String(Trim(str)); \n" +
		"		var res = ''; \n" +
		
		"		var _split = tmp.split(' '); \n" +
		"		var length = _split.length; \n" +
		
		"		if(from < 1) \n" +
		"		{ \n" +
		"			from = 1; \n" +
		"		} \n" +
		
		"		if(to > length) \n" +
		"		{ \n" +
		"			to = length; \n" +
		"		} \n" +
		
		"		for(from; from <= to; from++) \n" +
		"		{ \n" +
		"			res =  res + wordAt(str, from) + ' '; \n" +
		"		} \n" +
		
		"		return res; \n" +
		"	} \n \n" +
		
		"	function wordAt(str, nr) \n" +
		"	{ \n" +
		"		var res = new String(Trim(str)); \n" +
		"		var _split = res.split(' '); \n" +
		"		var length = _split.length; \n" +
		
		"		nr = nr -1; \n" +
		
		"		if(nr < 0 || nr >= length) \n" +
		"		{ \n" +
		"			nr = length-1; \n" +
		"		} \n" +
		
		"		res = _split[nr]; \n" +
		
		"		return res; \n" +
		"	} \n \n" +
		
		"	function Trim(str) \n" +
		"	{ \n" +
		"		return RTrim(LTrim(str)); \n" +
		"	} \n" +
		
		"	function LTrim(str) \n" +
		"	{ \n" +
		"		var whitespace = new String(' '); \n" +

		"		var s = new String(str); \n" +
		"		if (whitespace.indexOf(s.charAt(0)) != -1) \n" +
		"		{ \n" +
		"			var j=0, i = s.length; \n" +

		"			while (j < i && whitespace.indexOf(s.charAt(j)) != -1) \n" +
		"				j++; \n" +

		"			s = s.substring(j, i); \n" +
		"		} \n" +
		"		return s; \n" +
		"	} \n \n" +
		
		"	function RTrim(str) \n" +
		"	{ \n" +
		"		var whitespace = new String(' '); \n" +

		"		var s = new String(str); \n" +
		"		if (whitespace.indexOf(s.charAt(s.length-1)) != -1) \n" +
		"		{ \n" +
		"			var i = s.length - 1; \n" +

		"			while (i >= 0 && whitespace.indexOf(s.charAt(i)) != -1) \n" +
		"				i--; \n" +

		"			s = s.substring(0, i+1); \n" +
		"		} \n" +
		
		"		return s; \n" +
		"	} \n" +
		"\n" +
		
		"   function replaceEscapeCharacter(url)\n" +
		"   {\n" +
		"		url = url.replace(/\\//g, '%2F'); \n" +
	    "		url = url.replace(/=/g, '%3D'); \n" +
	    "		url = url.replace(/\\?/g, '%3F'); \n" +
	    "		url = url.replace(/&/g, '%26'); \n" +
	    "		url = url.replace(/:/g, '%3A'); \n" +
	    "		return url; \n" +
	    "    } \n\n";

	
	
	
	//Getters, Setters, Helpers
	
	/**
	 * Returns the current template
	 * */
	public String getCurrentTemplate()
	{
		return this.rootTemplate;
	}
	
	/**
	 * Set up the current template
	 * */
	public void setCurrentTemplate(String text)
	{
		this.rootTemplate = text;
	}
}