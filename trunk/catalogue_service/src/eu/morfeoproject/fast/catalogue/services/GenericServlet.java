package eu.morfeoproject.fast.catalogue.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.morfeoproject.fast.catalogue.buildingblocks.Action;
import eu.morfeoproject.fast.catalogue.buildingblocks.BackendService;
import eu.morfeoproject.fast.catalogue.buildingblocks.CTag;
import eu.morfeoproject.fast.catalogue.buildingblocks.Concept;
import eu.morfeoproject.fast.catalogue.buildingblocks.Condition;
import eu.morfeoproject.fast.catalogue.buildingblocks.FastModelFactory;
import eu.morfeoproject.fast.catalogue.buildingblocks.Form;
import eu.morfeoproject.fast.catalogue.buildingblocks.Library;
import eu.morfeoproject.fast.catalogue.buildingblocks.Operator;
import eu.morfeoproject.fast.catalogue.buildingblocks.Pipe;
import eu.morfeoproject.fast.catalogue.buildingblocks.Postcondition;
import eu.morfeoproject.fast.catalogue.buildingblocks.Precondition;
import eu.morfeoproject.fast.catalogue.buildingblocks.Resource;
import eu.morfeoproject.fast.catalogue.buildingblocks.Screen;
import eu.morfeoproject.fast.catalogue.buildingblocks.ScreenComponent;
import eu.morfeoproject.fast.catalogue.buildingblocks.ScreenDefinition;
import eu.morfeoproject.fast.catalogue.buildingblocks.ScreenFlow;
import eu.morfeoproject.fast.catalogue.buildingblocks.Trigger;
import eu.morfeoproject.fast.catalogue.util.DateFormatter;

public abstract class GenericServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	protected void redirectToFormat(HttpServletRequest request, HttpServletResponse response, String format) throws IOException {
		String extension = MediaType.getExtension(format);
		extension = extension == null ? MediaType.getExtension(MediaType.APPLICATION_JSON) : extension;
		String url = request.getRequestURL().toString();
		url = (url.charAt(url.length() - 1) == '/') ? url.concat(extension) : url.concat("/"+extension);
		response.sendRedirect(url);
	}
	
	@SuppressWarnings("unchecked")
	protected void parseConcept(Concept concept, JSONObject json, URI uri) throws JSONException, IOException {
		if (uri != null)
			concept.setUri(uri);
		if (json.has("subClassOf")) {
			concept.setSubClassOf(new URIImpl(json.getString("subClassOf")));
		}
		if (json.has("label")) {
			JSONObject jsonLabels = json.getJSONObject("label");
			Iterator<String> labels = jsonLabels.keys();
			for ( ; labels.hasNext(); ) {
				String key = labels.next();
				concept.getLabels().put(key, jsonLabels.getString(key));
			}
		}
		if (json.has("description")) {
			JSONObject jsonDescriptions = json.getJSONObject("description");
			Iterator<String> descriptions = jsonDescriptions.keys();
			for ( ; descriptions.hasNext(); ) {
				String key = descriptions.next();
				concept.getDescriptions().put(key, jsonDescriptions.getString(key));
			}
		}
		// parse the tags
		JSONArray aTag = json.getJSONArray("tags");
		ArrayList<CTag> tags = new ArrayList<CTag>(aTag.length());
		for (int i = 0; i < aTag.length(); i++) {
			CTag tag = new CTag();
			JSONObject oTag = aTag.getJSONObject(i);
			if (oTag.has("means") && !oTag.isNull("means") && oTag.getString("means") != "")
				tag.setMeans(new URIImpl(oTag.getString("means")));
			if (oTag.has("label")){
				JSONObject jsonLabels = oTag.getJSONObject("label");
				Iterator<String> labels = jsonLabels.keys();
				for ( ; labels.hasNext(); ) {
					String key = labels.next();
					tag.getLabels().put(key, jsonLabels.getString(key));
				}
			}
			if (oTag.has("taggingDate") && !oTag.isNull("taggingDate") && oTag.getString("taggingDate") != "")
				tag.setTaggingDate(DateFormatter.parseDateISO8601(oTag.getString("taggingDate")));
			tags.add(tag);
		}
		concept.setTags(tags);
	}
	
	@SuppressWarnings("unchecked")
	protected void parseResource(Resource resource, JSONObject jsonResource, URI uri) throws JSONException, IOException {
		if (uri != null)
			resource.setUri(uri);
		if (jsonResource.has("label")) {
			JSONObject jsonLabels = jsonResource.getJSONObject("label");
			Iterator<String> labels = jsonLabels.keys();
			for ( ; labels.hasNext(); ) {
				String key = labels.next();
				resource.getLabels().put(key, jsonLabels.getString(key));
			}
		}
		if (jsonResource.has("description")) {
			JSONObject jsonDescriptions = jsonResource.getJSONObject("description");
			Iterator<String> descriptions = jsonDescriptions.keys();
			for ( ; descriptions.hasNext(); ) {
				String key = descriptions.next();
				resource.getDescriptions().put(key, jsonDescriptions.getString(key));
			}
		}
		resource.setCreator(new URIImpl(CatalogueAccessPoint.getCatalogue().getServerURL()+"/users/"+jsonResource.getString("creator")));
		resource.setRights(new URIImpl(jsonResource.getString("rights")));
		resource.setVersion(jsonResource.getString("version"));
		if (jsonResource.has("creationDate") && !jsonResource.isNull("creationDate") && jsonResource.getString("creationDate") != "")
			resource.setCreationDate(DateFormatter.parseDateISO8601(jsonResource.getString("creationDate")));
		resource.setIcon(new URIImpl(jsonResource.getString("icon")));
		resource.setScreenshot(new URIImpl(jsonResource.getString("screenshot")));
		resource.setHomepage(new URIImpl(jsonResource.getString("homepage")));
		resource.setId(jsonResource.getString("id"));
		resource.setName(jsonResource.getString("name"));
		// parse the tags
		JSONArray aTag = jsonResource.getJSONArray("tags");
		ArrayList<CTag> tags = new ArrayList<CTag>(aTag.length());
		for (int i = 0; i < aTag.length(); i++) {
			CTag tag = new CTag();
			JSONObject oTag = aTag.getJSONObject(i);
			if (oTag.has("means") && !oTag.isNull("means") && oTag.getString("means") != "")
				tag.setMeans(new URIImpl(oTag.getString("means")));
			if (oTag.has("label")){
				JSONObject jsonLabels = oTag.getJSONObject("label");
				Iterator<String> labels = jsonLabels.keys();
				for ( ; labels.hasNext(); ) {
					String key = labels.next();
					tag.getLabels().put(key, jsonLabels.getString(key));
				}
			}
			if (oTag.has("taggingDate") && !oTag.isNull("taggingDate") && oTag.getString("taggingDate") != "")
				tag.setTaggingDate(DateFormatter.parseDateISO8601(oTag.getString("taggingDate")));
			tags.add(tag);
		}
		resource.setTags(tags);
		if (jsonResource.has("parameterTemplate"))
			resource.setParameterTemplate(jsonResource.getString("parameterTemplate"));
	}

	protected ScreenFlow parseScreenFlow(JSONObject jsonScreenFlow, URI uri) throws JSONException, IOException {
		ScreenFlow screenFlow = FastModelFactory.createScreenFlow();

		// fill common properties of the resource
		parseResource(screenFlow, jsonScreenFlow, uri);

		JSONArray resources = jsonScreenFlow.getJSONArray("contains");
		for (int i = 0; i < resources.length(); i++)
			screenFlow.getResources().add(new URIImpl(resources.getString(i)));
		return screenFlow;
	}

	protected Screen parseScreen(JSONObject jsonScreen, URI uri) throws JSONException, IOException, ParseScreenException {
		Screen screen = FastModelFactory.createScreen();

		// fill common properties of the resource
		parseResource(screen, jsonScreen, uri);

		// preconditions
		JSONArray preArray = jsonScreen.getJSONArray("preconditions");
		ArrayList<List<Condition>> preconditions = new ArrayList<List<Condition>>();
		for (int i = 0; i < preArray.length(); i++)
			preconditions.add(parseConditions(preArray.getJSONArray(i)));
		screen.setPreconditions(preconditions);
		// postconditions
		JSONArray postArray = jsonScreen.getJSONArray("postconditions");
		ArrayList<List<Condition>> postconditions = new ArrayList<List<Condition>>();
		for (int i = 0; i < postArray.length(); i++)
			postconditions.add(parseConditions(postArray.getJSONArray(i)));
		screen.setPostconditions(postconditions);
		// code
		if (jsonScreen.has("code") && jsonScreen.has("definition")) {
			throw new ParseScreenException("Either 'code' or 'definition' must be specified, but not both.");
		} else if (jsonScreen.has("code")) {
			if (jsonScreen.getString("code").equalsIgnoreCase("null"))
				throw new ParseScreenException("'code' cannot be null.");
			screen.setCode(new URIImpl(jsonScreen.getString("code")));
		} else if (jsonScreen.has("definition")) {
			ScreenDefinition sDef = parseScreenDefinition(jsonScreen.getJSONObject("definition"));
			screen.setDefinition(sDef);
		} else {
			throw new ParseScreenException("Either 'code' or 'definition' must be specified.");
		}
		return screen;
	}
	
	protected ScreenDefinition parseScreenDefinition(JSONObject jsonDef) throws JSONException {
		ScreenDefinition definition = new ScreenDefinition();
		// building blocks
		JSONArray bbArray = jsonDef.getJSONArray("buildingblocks");
		for (int i = 0; i < bbArray.length(); i++) {
			JSONObject bb = bbArray.getJSONObject(i);
			definition.getBuildingBlocks().put(bb.getString("id"), new URIImpl(bb.getString("uri")));
		}
		// pipes
		JSONArray pipeArray = jsonDef.getJSONArray("pipes");
		for (int i = 0; i < pipeArray.length(); i++) {
			JSONObject jsonPipe = pipeArray.getJSONObject(i);
			JSONObject pipeFrom = jsonPipe.getJSONObject("from");
			JSONObject pipeTo = jsonPipe.getJSONObject("to");
			Pipe pipe = FastModelFactory.createPipe();
			pipe.setIdBBFrom(!pipeFrom.isNull("buildingblock") ? pipeFrom.getString("buildingblock") : null);
			pipe.setIdConditionFrom(!pipeFrom.isNull("condition") ? pipeFrom.getString("condition") : null);
			pipe.setIdBBTo(!pipeTo.isNull("buildingblock") ? pipeTo.getString("buildingblock") : null);
			pipe.setIdConditionTo(!pipeTo.isNull("condition") ? pipeTo.getString("condition") : null);
			pipe.setIdActionTo(!pipeTo.isNull("action") ? pipeTo.getString("action") : null);
			definition.getPipes().add(pipe);
		}
		// triggers
		JSONArray triggerArray = jsonDef.getJSONArray("triggers");
		for (int i = 0; i < triggerArray.length(); i++) {
			JSONObject jsonTrigger = triggerArray.getJSONObject(i);
			JSONObject triggerFrom = jsonTrigger.getJSONObject("from");
			JSONObject triggerTo = jsonTrigger.getJSONObject("to");
			Trigger trigger = new Trigger();
			trigger.setIdBBFrom(!triggerFrom.isNull("buildingblock") ? triggerFrom.getString("buildingblock") : null);
			trigger.setNameFrom(!triggerFrom.isNull("name") ? triggerFrom.getString("name") : null);
			trigger.setIdBBTo(!triggerTo.isNull("buildingblock") ? triggerTo.getString("buildingblock") : null);
			trigger.setIdActionTo(!triggerTo.isNull("action") ? triggerTo.getString("action") : null);
			definition.getTriggers().add(trigger);
		}
		return definition;
	}
	
	protected Precondition parsePrecondition(JSONObject jsonPre, URI uri) throws JSONException, IOException {
		Precondition pre = FastModelFactory.createPrecondition();
		if (uri != null)
			pre.setUri(uri);
		pre.setId(jsonPre.getString("id"));
		pre.setName(jsonPre.getString("name"));
		// conditions
		JSONArray conditionsArray = jsonPre.getJSONArray("conditions");
		pre.setConditions(parseConditions(conditionsArray));
		return pre;
	}
	
	protected Postcondition parsePostcondition(JSONObject jsonPost, URI uri) throws JSONException, IOException {
		Postcondition post = FastModelFactory.createPostcondition();
		if (uri != null)
			post.setUri(uri);
		post.setId(jsonPost.getString("id"));
		post.setName(jsonPost.getString("name"));
		// conditions
		JSONArray conditionsArray = jsonPost.getJSONArray("conditions");
		post.setConditions(parseConditions(conditionsArray));
		return post;
	}
	
	protected Form parseForm(JSONObject jsonForm, URI uri) throws JSONException, IOException {
		Form fe = FastModelFactory.createForm();
		parseScreenComponent(fe, jsonForm, uri);
		return fe;
	}

	protected Operator parseOperator(JSONObject jsonOp, URI uri) throws JSONException, IOException {
		Operator op = FastModelFactory.createOperator();
		parseScreenComponent(op, jsonOp, uri);
		return op;
	}

	protected BackendService parseBackendService(JSONObject jsonBs, URI uri) throws JSONException, IOException {
		BackendService bs = FastModelFactory.createBackendService();
		parseScreenComponent(bs, jsonBs, uri);
		return bs;
	}
	
	protected Action parseAction(JSONObject jsonAction) throws JSONException, IOException {
		Action action = FastModelFactory.createAction();
		
		// name
		if (jsonAction.get("name") != null)
			action.setName(jsonAction.getString("name"));
		// preconditions
		JSONArray preArray = jsonAction.getJSONArray("preconditions");
		action.setPreconditions(parseConditions(preArray));
		// uses
		if (jsonAction.get("uses") != null) {
			JSONArray usesArray = jsonAction.getJSONArray("uses");
			for (int i = 0; i < usesArray.length(); i++) {
				JSONObject useObject = usesArray.getJSONObject(i);
				action.getUses().put(useObject.getString("id"), new URIImpl(useObject.getString("uri")));
			}
		}

		return action;
	}
	
	protected Library parseLibrary(JSONObject jsonLibrary) throws JSONException, IOException {
		Library library = new Library();
		
		// name
		if (jsonLibrary.get("language") != null)
			library.setLanguage(jsonLibrary.getString("language"));
		// source
		if (jsonLibrary.get("source") != null && !jsonLibrary.getString("source").equalsIgnoreCase("null"))
			library.setSource(new URIImpl(jsonLibrary.getString("source")));

		return library;
	}
	
	protected void parseScreenComponent(ScreenComponent sc, JSONObject jsonSc, URI uri) throws JSONException, IOException {
		// fill common properties of the resource
		parseResource(sc, jsonSc, uri);
		
		// actions
		JSONArray actionsArray = jsonSc.getJSONArray("actions");
		for (int i = 0; i < actionsArray.length(); i++)
			sc.getActions().add(parseAction(actionsArray.getJSONObject(i)));
		// postconditions
		JSONArray postArray = jsonSc.getJSONArray("postconditions");
		ArrayList<List<Condition>> postconditions = new ArrayList<List<Condition>>();
		for (int i = 0; i < postArray.length(); i++)
			postconditions.add(parseConditions(postArray.getJSONArray(i)));
		sc.setPostconditions(postconditions);
		// code
		if (jsonSc.get("code") != null && !jsonSc.getString("code").equalsIgnoreCase("null"))
			sc.setCode(new URIImpl(jsonSc.getString("code")));
		// libraries
		JSONArray librariesArray = jsonSc.getJSONArray("libraries");
		for (int i = 0; i < librariesArray.length(); i++)
			sc.getLibraries().add(parseLibrary(librariesArray.getJSONObject(i)));
		// triggers
		if (jsonSc.get("triggers") != null) {
			JSONArray triggersArray = jsonSc.getJSONArray("triggers");
			for (int i = 0; i < triggersArray.length(); i++)
				sc.getTriggers().add(triggersArray.getString(i));
		}
	}

	/**
	 * Every statement of the conditions has to follow this rules:
	 * <ul>
	 *   <li>Subject must be a variable</li>
	 *   <li>Predicate must be a URI</li>
	 *   <li>Object can be a variable or a URI</li>
	 * </ul>
	 * @param conditionsArray
	 * @return
	 * @throws JSONException
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	protected List<Condition> parseConditions(JSONArray conditionsArray) throws JSONException, IOException {
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		for (int i = 0; i < conditionsArray.length(); i++) {
			JSONObject cJson = conditionsArray.getJSONObject(i);
			Condition c = FastModelFactory.createCondition();
			if (cJson.has("id") && !cJson.isNull("id") && cJson.getString("id") != "") // optional
				c.setId(cJson.getString("id"));
			boolean positive = cJson.has("positive") ? cJson.getBoolean("positive") : true;
			c.setPositive(positive);
			if (cJson.get("label") != null) {
				JSONObject jsonLabels = cJson.getJSONObject("label");
				Iterator<String> labels = jsonLabels.keys();
				for ( ; labels.hasNext(); ) {
					String key = labels.next();
					c.getLabels().put(key, jsonLabels.getString(key));
				}
			}
			String patternString = cJson.getString("pattern");
			c.setPatternString(patternString);
			// only create the triples for the pattern in case of the condition is positive
			if (positive) {
				c.setPattern(CatalogueAccessPoint.getCatalogue().patternToStatements(patternString));
			}
			conditions.add(c);
		}
		return conditions;
	}
	

	protected List<Pipe> parsePipes(JSONArray pipesArray) throws JSONException, IOException {
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();
		for (int i = 0; i < pipesArray.length(); i++) {
			JSONObject pJson = pipesArray.getJSONObject(i);
			Pipe p = FastModelFactory.createPipe();
			JSONObject fromJson = pJson.getJSONObject("from");
			p.setIdBBFrom(fromJson.getString("buildingblock").equals("null") ? null : fromJson.getString("buildingblock"));
			p.setIdConditionFrom(fromJson.getString("condition"));
			JSONObject toJson = pJson.getJSONObject("to");
			p.setIdBBTo(toJson.getString("buildingblock").equals("null") ? null : toJson.getString("buildingblock"));
			p.setIdConditionTo(toJson.getString("condition"));
			String action = toJson.has("action") ? (toJson.getString("action").equals("null") ? null : toJson.getString("action")) : null;
			p.setIdActionTo(action);
			pipes.add(p);
		}
		return pipes;
	}
	
	/**
	 * Transforms a set of statements referring to a certain subject into a 
	 * JSON object, where every predicate will be a key and every object
	 * will be a value.
	 * NOTE: all them will be treated as referring to the same subject
	 * @param statemets
	 * @return
	 */
	protected JSONObject statements2JSON(Set<Statement> statements) {
		JSONObject result = new JSONObject();
		for (Iterator<Statement> it = statements.iterator(); it.hasNext(); ) {
			try {
				Statement st = it.next();
				if (!st.getPredicate().asURI().equals(RDF.type))
					result.accumulate(st.getPredicate().toString(), st.getObject().toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
}