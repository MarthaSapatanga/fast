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
package eu.morfeoproject.fast.catalogue.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import uk.ac.open.kmi.iserve.IServeResponse;
import eu.morfeoproject.fast.catalogue.BuildingBlockException;
import eu.morfeoproject.fast.catalogue.Constants;
import eu.morfeoproject.fast.catalogue.NotFoundException;
import eu.morfeoproject.fast.catalogue.builder.BuildingBlockJSONBuilder;
import eu.morfeoproject.fast.catalogue.model.Action;
import eu.morfeoproject.fast.catalogue.model.BackendService;
import eu.morfeoproject.fast.catalogue.model.Condition;
import eu.morfeoproject.fast.catalogue.model.Form;
import eu.morfeoproject.fast.catalogue.model.Operator;
import eu.morfeoproject.fast.catalogue.model.Pipe;
import eu.morfeoproject.fast.catalogue.model.ScreenComponent;

/**
 * Servlet implementation class ScreenComponentFindCheckServlet
 */
public class ScreenComponentFindCheckServlet extends GenericServlet {
	
	private static final long serialVersionUID = 1L;
       
	/**
     * @see HttpServlet#HttpServlet()
     */
    public ScreenComponentFindCheckServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader reader = request.getReader();
		PrintWriter writer = response.getWriter();
		StringBuffer buffer = new StringBuffer();
		String line = reader.readLine();
		while (line != null) {
			buffer.append(line);
			line = reader.readLine();
		}
		String body = buffer.toString();
		
		try {
			// create JSON representation of the input
			JSONObject input = new JSONObject(body);
			// parses the preconditions
			List<Condition> preconditions = BuildingBlockJSONBuilder.buildConditions(input.getJSONArray("preconditions"));
			// parses the preconditions
			List<Condition> postconditions = BuildingBlockJSONBuilder.buildConditions(input.getJSONArray("postconditions"));
			// parses the canvas
			ArrayList<ScreenComponent> canvas = new ArrayList<ScreenComponent>();
			JSONArray jsonCanvas = input.getJSONArray("canvas");
			for (int i = 0; i < jsonCanvas.length(); i++) {
				URI uri = new URIImpl(jsonCanvas.getJSONObject(i).getString("uri"));
				ScreenComponent sc = (ScreenComponent) getCatalogue().getBuildingBlock(uri);
				if (sc == null || sc.getPrototype() == null)
					throw new BuildingBlockException(uri+" does not exist or may not be a clone of a prototype.");
				canvas.add(sc);
			}
			// parses the domain context
			JSONObject jsonDomainContext = input.getJSONObject("domainContext");
			JSONArray jsonTags = jsonDomainContext.getJSONArray("tags");
			ArrayList<String> tags = new ArrayList<String>();
			for (int i = 0; i < jsonTags.length(); i++)
				tags.add(jsonTags.getString(i));
			StringBuffer sb = new StringBuffer();
			for (String tag : tags)
				sb.append(tag+" ");
			// TODO do something with the user
			String user = jsonDomainContext.getString("user");
			// parses the pipes
			List<Pipe> pipes = BuildingBlockJSONBuilder.buildPipes(null, input.getJSONArray("pipes"));
			// parses the selected item
			Object selectedItem = null;
			if (input.has("selectedItem")) {
				selectedItem = getConditionById(preconditions, input.getString("selectedItem"));
				if (selectedItem == null) {
					selectedItem = getConditionById(postconditions, input.getString("selectedItem"));
					if (selectedItem == null)
						selectedItem = getCatalogue().getScreenComponent(new URIImpl(input.getString("selectedItem")));
				}
			} 
			// extract the array of actions to perform (find, check, iserve)
			Vector<String> actions = new Vector<String>();
			JSONArray actionsArray = input.optJSONArray("actions");
			for (int idx = 0; actionsArray != null && idx < actionsArray.length(); idx++)
				actions.add(actionsArray.getString(idx));			
			boolean find = actions.contains("find");
			boolean check = actions.contains("check");
			boolean iserve = actions.contains("iserve");
			
			// pagination variables
			int offset = input.optInt("offset", 0);
			int limit = input.optInt("limit", -1);
			
			// do the real work
			//-----------------------------
			ArrayList<ScreenComponent> outForms = new ArrayList<ScreenComponent>();
			ArrayList<ScreenComponent> outOperators = new ArrayList<ScreenComponent>();
			ArrayList<ScreenComponent> outBackendServices = new ArrayList<ScreenComponent>();
			ArrayList<IServeResponse> iServeList = new ArrayList<IServeResponse>();
			
			if (find || iserve) {
				// list of conditions to use for the find and iserve actions
				// if a item has been selected, only its conditions will be used, otherwise
				// conditions from all the elements from the canvas will be gathered
				ArrayList<Condition> conList = new ArrayList<Condition>();
				if (selectedItem != null) {
					if (selectedItem instanceof Condition) {
						conList.add((Condition) selectedItem);
					} else if (selectedItem instanceof ScreenComponent) {
						conList.addAll(extractAllConditions((ScreenComponent) selectedItem));
					}
				} else {
			    	conList.addAll(preconditions);
			    	conList.addAll(postconditions);
					for (ScreenComponent sc : canvas) {
				    	conList.addAll(extractAllConditions(sc));
					}
				}
			
				if (find) {
					// ask the catalogue for suggestions, and add the results to the list of forms, operators and services
					int strategy = Constants.PREPOST + Constants.PATTERNS;
					ScreenComponent selected = selectedItem instanceof ScreenComponent ? (ScreenComponent) selectedItem : null;
					List<URI> findResults = getCatalogue().findScreenComponents(canvas, selected, conList, offset, limit, tags, strategy);
					for (URI uri : findResults) {
						ScreenComponent sc = getCatalogue().getScreenComponent(uri);
						if (sc instanceof Form) 				outForms.add(sc);
						else if (sc instanceof Operator) 		outOperators.add(sc);
						else if (sc instanceof BackendService)	outBackendServices.add(sc);
					}
				}
			
				if (iserve) {
					// query iServe for more web services
					iServeList.addAll(getCatalogue().findIServeWS(conList));
				}
			}
				
			// extract pipes which are well defined (precondition and
			// postcondition match)
			ArrayList<Pipe> correctPipeList = new ArrayList<Pipe>();
			if (check) {
				for (Pipe pipe : pipes) {
					if (isPipeCorrect(pipe, preconditions, postconditions))
						correctPipeList.add(pipe);
				}
			}
			
			// check if elements in the canvas + pre/postconditions are reachable
			List<Object> reachableElements = new ArrayList<Object>();
			if (check) {
				reachableElements.addAll(getReachableElements(canvas, preconditions, postconditions, correctPipeList));
			}
			
			// create the JSON output
			JSONObject output = new JSONObject();
			
			// check if the pipes are well defined
			JSONArray jsonPipes = new JSONArray();
			for (Pipe pipe : pipes) {
				JSONObject jsonPipe = pipe.toJSON();
				if (check) {
					jsonPipe.put("correct", correctPipeList.contains(pipe));
					jsonPipe.put("satisfied", reachableElements.contains(pipe));
				}
				jsonPipes.put(jsonPipe);
			}
			output.put("pipes", jsonPipes);

			JSONArray canvasArray = new JSONArray();
			for (ScreenComponent sc : canvas) {
				JSONObject cObject = check ? processComponent(canvas, sc, pipes, reachableElements) : new JSONObject();
				cObject.put("uri", sc.getUri());
				canvasArray.put(cObject);
			}
			output.put("canvas", canvasArray);

			if (find) {
				JSONArray formsArray = new JSONArray();
				for (ScreenComponent sc : outForms) {
					formsArray.put(sc.getUri());
				}
				output.put("forms", formsArray);
	
				JSONArray operatorsArray = new JSONArray();
				for (ScreenComponent sc : outOperators) {
					operatorsArray.put(sc.getUri());
				}
				output.put("operators", operatorsArray);
	
				JSONArray bsArray = new JSONArray();
				for (ScreenComponent sc : outBackendServices) {
					bsArray.put(sc.getUri());
				}
				output.put("backendservices", bsArray);
			}
			
			if (iserve) {
				JSONArray iServeArray = new JSONArray();
				for (IServeResponse iServeResponse : iServeList) {
					iServeArray.put(iServeResponse.toJSON());
				}
				output.put("iserve", iServeArray);
			}
			
			JSONArray preArray = new JSONArray();
			for (Condition con : preconditions) {
				JSONObject cObject = con.toJSON();
				if (check) cObject.put("satisfied", true);
				preArray.put(cObject);
			}
			output.put("preconditions", preArray);
			
			JSONArray postArray = new JSONArray();
			for (Condition con : postconditions) {
				JSONObject cObject = check ? processPostcondition(canvas, con, pipes, reachableElements) : con.toJSON();
				postArray.put(cObject);
			}
			output.put("postconditions", postArray);
		
			if (check && selectedItem != null) {
				JSONArray connectionsOut = new JSONArray();
				List<Pipe> connections = generatePipes(canvas, preconditions, postconditions, selectedItem, pipes);
				for (Pipe pipe : connections)
					connectionsOut.put(pipe.toJSON());
				output.put("connections", connectionsOut);
			}
						
			writer.print(output.toString(2));
			response.setContentType(MediaType.APPLICATION_JSON);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (JSONException e) {
			log.error(e.toString(), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (NotFoundException e) {
			log.error(e.toString(), e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
		} catch (BuildingBlockException e) {
			log.error(e.toString(), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
	}
	
	private Condition getConditionById(List<Condition> conditions, String id) {
		for (Condition condition : conditions) {
			if (condition.getId() != null && condition.getId().equals(id)) {
				return condition;
			}
		}
		return null;
	}

	private boolean isPipeCorrect(Pipe pipe, List<Condition> preconditions, List<Condition> postconditions) throws IOException {
		boolean satisfied = false;
		Condition conFrom, conTo;

		try {
			if (pipe.getBBFrom() == null) {
				conFrom = getConditionById(preconditions, pipe.getConditionFrom());
			} else {
				ScreenComponent sc = getCatalogue().getScreenComponent(pipe.getBBFrom());
				conFrom = sc.getPostcondition(pipe.getConditionFrom());
			}
			if (pipe.getBBTo() == null) {
				conTo = getConditionById(postconditions, pipe.getConditionTo());
			} else {
				ScreenComponent sc = getCatalogue().getScreenComponent(pipe.getBBTo());
				conTo = sc.getPrecondition(pipe.getConditionTo());
			}
			satisfied = isConditionCompatible(conFrom, conTo);
		} catch (NotFoundException e) {
			log.error(e.getMessage(), e);
			return false;
		}
		
		return satisfied;
	}
	
	private JSONObject processPostcondition(List<ScreenComponent> canvas, Condition postcondition, List<Pipe> pipes, List<Object> elements) throws JSONException, IOException {
		JSONObject jsonCon = postcondition.toJSON();
		Pipe pipe = getPipeToPostcondition(postcondition, pipes);
		jsonCon.put("satisfied", pipe == null ? false : elements.contains(pipe));
		return jsonCon;
	}
	
	private JSONObject processComponent(List<ScreenComponent> canvas, ScreenComponent sc, List<Pipe> pipes, List<Object> reachableElements) throws JSONException, IOException {
		JSONObject jsonSc = new JSONObject();
		jsonSc.put("uri", sc.getUri());
		JSONArray actionArray = new JSONArray();
		boolean reachability = sc.getActions().isEmpty() ? true : false;
		for (Action action : sc.getActions()) {
			JSONArray conArray = new JSONArray();
			boolean actionSatisfied = true;
			for (Condition con : action.getPreconditions()) {
				Pipe pipe = getPipeToComponent(sc, action, con, pipes);
				boolean conSatisfied = pipe == null ? false : reachableElements.contains(pipe);
				actionSatisfied = actionSatisfied && conSatisfied;
				JSONObject jsonCon = con.toJSON();
				jsonCon.put("satisfied", conSatisfied);
				conArray.put(jsonCon);
			}
			reachability = reachability || actionSatisfied;
			JSONObject actObject = action.toJSON();
			actObject.put("preconditions", conArray);
			actObject.put("satisfied", actionSatisfied);
			actionArray.put(actObject);
		}
		jsonSc.put("reachability", reachability);
		jsonSc.put("actions", actionArray);
		return jsonSc;
	}
	
	private boolean isConditionCompatible(Condition conA, Condition conB) throws IOException {
		return getCatalogue().isConditionCompatible(conA, conB);
	}
	
	private List<Pipe> generatePipes(
			List<ScreenComponent> canvas,
			List<Condition> preconditions,
			List<Condition> postconditions,
			Object selectedItem,
			List<Pipe> pipes) throws IOException {
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
		
		if (selectedItem instanceof ScreenComponent) {
			ScreenComponent component = (ScreenComponent) selectedItem;
			// look for pipes from <somewhere> to the preconditions of the selected item
			for (Action action : component.getActions()) {
				for (Condition pre : action.getPreconditions()) {
					for (Condition con : preconditions) {
						if (isConditionCompatible(con, pre)) {
							Pipe pipe = new Pipe(null);
							pipe.setBBFrom(null);
							pipe.setConditionFrom(con.getId());
							pipe.setBBTo(component.getUri());
							pipe.setActionTo(action.getName());
							pipe.setConditionTo(pre.getId());
							if (!pipes.contains(pipe)) pipeList.add(pipe);
						}
					}
					for (ScreenComponent sc : canvas) {
						if (sc.equals(component)) {
							// discard selected item
						} else {
							for (Condition con : sc.getPostconditions()) {
								if (isConditionCompatible(con, pre)) {
									Pipe pipe = new Pipe(null);
									pipe.setBBFrom(sc.getUri());
									pipe.setConditionFrom(con.getId());
									pipe.setBBTo(component.getUri());
									pipe.setActionTo(action.getName());
									pipe.setConditionTo(pre.getId());
									if (!pipes.contains(pipe)) pipeList.add(pipe);
								}
							}
						}
					}
				}
			}
			// look for pipes from the postconditions the selected item to <somewhere>
			for (Condition con : component.getPostconditions()) {
				for (Condition post : postconditions) {
					if (isConditionCompatible(con, post)) {
						Pipe pipe = new Pipe(null);
						pipe.setBBFrom(component.getUri());
						pipe.setConditionFrom(con.getId());
						pipe.setBBTo(null);
						pipe.setActionTo(null);
						pipe.setConditionTo(post.getId());
						if (!pipes.contains(pipe)) pipeList.add(pipe);
					}
				}
				for (ScreenComponent sc : canvas) {
					if (sc.equals(component)) break; // discard selected item
					for (Action action : sc.getActions()) {
						for (Condition pre : action.getPreconditions()) {
							if (isConditionCompatible(con, pre)) {
								Pipe pipe = new Pipe(null);
								pipe.setBBFrom(component.getUri());
								pipe.setConditionFrom(con.getId());
								pipe.setBBTo(sc.getUri());
								pipe.setActionTo(action.getName());
								pipe.setConditionTo(pre.getId());
								if (!pipes.contains(pipe)) pipeList.add(pipe);
							}
						}
					}
				}
			}
		} else {
			Condition condition = (Condition) selectedItem;
			if (getConditionById(preconditions, condition.getId()) != null) {
				// look for pipes from the precondition to <somewhere>
				for (ScreenComponent sc : canvas) {
					for (Action action : sc.getActions()) {
						for (Condition pre : action.getPreconditions()) {
							if (isConditionCompatible(pre, condition)) {
								Pipe pipe = new Pipe(null);
								pipe.setBBFrom(null);
								pipe.setConditionFrom(condition.getId());
								pipe.setBBTo(sc.getUri());
								pipe.setActionTo(action.getName());
								pipe.setConditionTo(pre.getId());
								if (!pipes.contains(pipe)) pipeList.add(pipe);
							}
						}
					}
				}
			} else if (getConditionById(postconditions, condition.getId()) != null) {
				// look for pipes from <somewhere> to the postcondition
				for (ScreenComponent sc : canvas) {
					for (Condition con : sc.getPostconditions()) {
						if (isConditionCompatible(con, condition)) {
							Pipe pipe = new Pipe(null);
							pipe.setBBFrom(sc.getUri());
							pipe.setConditionFrom(con.getId());
							pipe.setBBTo(null);
							pipe.setActionTo(null);
							pipe.setConditionTo(condition.getId());
							if (!pipes.contains(pipe)) pipeList.add(pipe);
						}
					}
				}
			}
		}
		
		return pipeList;
	}
	
	private List<Object> getReachableElements(
			List<ScreenComponent> scList,
			List<Condition> preconditions, 
			List<Condition> postconditions,
			List<Pipe> pipes) {
		List<Object> elements = new ArrayList<Object>();

		// pipes from preconditions are reachable
		List<Pipe> reachablePipeList = getPipesFrom(pipes, null);
		
		// screen components without actions, or having an action without
		// preconditions are reachable
		List<ScreenComponent> reachableSCList = new ArrayList<ScreenComponent>();
		for (ScreenComponent sc : scList) {
			boolean reachable = false;
			if (sc.getActions().size() == 0) {
				reachable = true;
			} else {
				for (Action action : sc.getActions()) {
					if (action.getPreconditions().size() == 0) {
						reachable = true;
						break;
					}
				}
			}
			if (reachable) {
				reachableSCList.add(sc);
				reachablePipeList.addAll(getPipesFrom(pipes, sc.getUri().toString()));
			}
		}
		
		// at first, no postcondition is reachable
		List<Condition> reachablePost = new ArrayList<Condition>();
		
		// screen components connected to reachable pipes are also reachable
		List<Pipe> pipesToCheck = new ArrayList<Pipe>(reachablePipeList);
		List<Pipe> nextPipeList = new ArrayList<Pipe>();
		while (pipesToCheck.size() > 0) {
			for (Pipe pipe : pipesToCheck) {
				if (pipe.getBBTo() == null) {
					// it's a postcondition
					Condition post = getConditionById(postconditions, pipe.getConditionTo());
					if (reachablePost.contains(post))
						reachablePost.add(post);
				} else {
					ScreenComponent sc = getScreenComponent(scList, pipe.getBBTo());
					if (!reachableSCList.contains(sc)) {
						reachableSCList.add(sc);
						List<Pipe> toAdd = getPipesFrom(pipes, sc.getUri().toString());
						reachablePipeList.addAll(toAdd);
						nextPipeList.addAll(toAdd);
					}
				}
			}
			// if no new pipes are reachable, the loop finishes
			pipesToCheck.clear();
			pipesToCheck.addAll(nextPipeList);
			nextPipeList.clear();
		}

		// puts all reachable elements together
		elements.addAll(reachablePipeList);
		elements.addAll(reachableSCList);
		elements.addAll(reachablePost);
		
		return elements;
	}
	
	private ScreenComponent getScreenComponent(List<ScreenComponent> scList, URI uri) {
		for (ScreenComponent sc : scList) {
			if (sc.getUri().equals(uri)) return sc;
		}
		return null;
	}
	
	private List<Pipe> getPipesFrom(List<Pipe> pipes, String bbFrom) {
		ArrayList<Pipe> results = new ArrayList<Pipe>();
		for (Pipe pipe : pipes) {
			if (bbFrom == null && pipe.getBBFrom() == null) {
				results.add(pipe);
			} else if (bbFrom != null && pipe.getBBFrom() != null && bbFrom.equals(pipe.getBBFrom().toString())) {
				results.add(pipe);
			}
		}
		return results;
	}
	
	/**
	 * Returns the pipe which connects any other screen component to a precondition within this 
	 * component, null in case there is no pipe connecting it
	 * @param sc
	 * @param action
	 * @param precondition
	 * @param pipes
	 * @return
	 */
	private Pipe getPipeToComponent(ScreenComponent sc, Action action, Condition precondition, List<Pipe> pipes) {
		for (Pipe pipe : pipes) {
			if (pipe.getBBTo() != null 
					&& pipe.getBBTo().equals(sc.getUri())
					&& pipe.getActionTo().equals(action.getName())
					&& pipe.getConditionTo().equals(precondition.getId())) {
				return pipe;
			}
		}
		return null;
	}
	
	/**
	 * Returns the pipe which connects a (screen) postcondition to any other screen component, null in
	 * case there is no pipe connecting it
	 * @param postcondition
	 * @param pipes
	 * @return pipe which connects to the postcondition
	 */
	public Pipe getPipeToPostcondition(Condition postcondition, List<Pipe> pipes) {
		for (Pipe pipe : pipes) {
			if (pipe.getBBTo() == null
					&& pipe.getConditionTo().equals(postcondition.getId())) {
				return pipe;
			}
		}
		return null;
	}

	protected List<Condition> extractAllConditions(ScreenComponent sc) {
		ArrayList<Condition> conList = new ArrayList<Condition>();
		for (Action action : sc.getActions()) {
			conList.addAll(action.getPreconditions());
		}
		conList.addAll(sc.getPostconditions());
		return conList;
	}

}