package eu.morfeoproject.fast.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.morfeoproject.fast.model.Condition;
import eu.morfeoproject.fast.model.Event;
import eu.morfeoproject.fast.model.FastModelFactory;
import eu.morfeoproject.fast.model.Screen;
import eu.morfeoproject.fast.model.Slot;
import eu.morfeoproject.fast.util.FormatterUtil;

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

	@SuppressWarnings("unchecked")
	protected Screen parseScreen(JSONObject jsonScreen, URI id) throws JSONException, IOException {
		Screen screen = FastModelFactory.createScreen();
		if (id != null)
			screen.setUri(id);
		if (jsonScreen.get("label") != null) {
			JSONObject jsonLabels = jsonScreen.getJSONObject("label");
			Iterator<String> labels = jsonLabels.keys();
			for ( ; labels.hasNext(); ) {
				String key = labels.next();
				screen.getLabels().put(key, jsonLabels.getString(key));
			}
		}
		if (jsonScreen.get("description") != null) {
			JSONObject jsonDescriptions = jsonScreen.getJSONObject("description");
			Iterator<String> descriptions = jsonDescriptions.keys();
			for ( ; descriptions.hasNext(); ) {
				String key = descriptions.next();
				screen.getDescriptions().put(key, jsonDescriptions.getString(key));
			}
		}
		if (jsonScreen.get("creator") != null)
			screen.setCreator(new URIImpl(jsonScreen.getString("creator")));
		if (jsonScreen.get("rights") != null)
			screen.setRights(new URIImpl(jsonScreen.getString("rights")));
		if (jsonScreen.get("version") != null)
			screen.setVersion(jsonScreen.getString("version"));
		if (jsonScreen.get("creationDate") != null)
			screen.setCreationDate(FormatterUtil.parseDateISO8601(jsonScreen.getString("creationDate")));
		if (jsonScreen.get("icon") != null)
			screen.setIcon(new URIImpl(jsonScreen.getString("icon")));
		if (jsonScreen.get("screenshot") != null)
			screen.setScreenshot(new URIImpl(jsonScreen.getString("screenshot")));
		JSONObject domainContext = jsonScreen.getJSONObject("domainContext");
		JSONArray tags = domainContext.getJSONArray("tags");
		for (int i = 0; i < tags.length(); i++)
			screen.getDomainContext().getTags().add(tags.getString(i));
		URI user = new URIImpl(domainContext.getString("user"));
		screen.getDomainContext().setUser(user);
		if (jsonScreen.get("homepage") != null)
			screen.setHomepage(new URIImpl(jsonScreen.getString("homepage")));
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
		if (jsonScreen.get("code") != null && !jsonScreen.getString("code").equalsIgnoreCase("null"))
			screen.setCode(new URIImpl(jsonScreen.getString("code")));
		return screen;
	}
	
	protected Slot parseSlot(JSONObject jsonSlot, URI id) throws JSONException, IOException {
		Slot slot = FastModelFactory.createSlot();
		if (id != null)
			slot.setUri(id);
		// conditions
		JSONArray conditionsArray = jsonSlot.getJSONArray("conditions");
		slot.setConditions(parseConditions(conditionsArray));
		return slot;
	}
	
	protected Event parseEvent(JSONObject jsonEvent, URI id) throws JSONException, IOException {
		Event event = FastModelFactory.createEvent();
		if (id != null)
			event.setUri(id);
		// conditions
		JSONArray conditionsArray = jsonEvent.getJSONArray("conditions");
		event.setConditions(parseConditions(conditionsArray));
		return event;
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
		HashMap<String, BlankNode> blankNodes = new HashMap<String, BlankNode>();
		for (int i = 0; i < conditionsArray.length(); i++) {
			JSONObject cJson = conditionsArray.getJSONObject(i);
			Condition c = FastModelFactory.createCondition();
			String scope = cJson.getString("scope");
			c.setScope(scope);
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
			// only create the triples for the pattern in case of the scope
			// is 'design time' or 'both'
			if (scope.equalsIgnoreCase("design time") || scope.equalsIgnoreCase("both")) {
				ArrayList<Statement> stmts = new ArrayList<Statement>();
				StringTokenizer tokens = new StringTokenizer(patternString);//, " . ");
				for ( ; tokens.hasMoreTokens(); ) {
					String subject = tokens.nextToken();
					String predicate = tokens.nextToken();
					String object = tokens.nextToken();
					if (tokens.hasMoreTokens())
						tokens.nextToken(); // discard the .
					// gets if exists or creates the subject
					BlankNode subjectNode = blankNodes.get(subject);
					if (subjectNode == null) {
						subjectNode = CatalogueAccessPoint.getCatalogue().getTripleStore().createBlankNode();
						blankNodes.put(subject, subjectNode);
					}
					// creates a URI or BlankNode for the object
					Node objectNode;
					try {
						objectNode = new URIImpl(object);
					} catch (IllegalArgumentException e) { 
						objectNode = blankNodes.get(object);
						if (objectNode == null) {
							objectNode = CatalogueAccessPoint.getCatalogue().getTripleStore().createBlankNode();
							blankNodes.put(subject, subjectNode);
						}
					}
					Statement st = CatalogueAccessPoint.getCatalogue().getTripleStore().createStatement(subjectNode, new URIImpl(predicate), objectNode);
					stmts.add(st);
				}
				c.setPattern(stmts);
			}
			conditions.add(c);
		}
		return conditions;
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
