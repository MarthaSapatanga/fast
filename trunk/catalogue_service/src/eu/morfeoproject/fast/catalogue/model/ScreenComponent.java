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
package eu.morfeoproject.fast.catalogue.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;

import eu.morfeoproject.fast.catalogue.vocabulary.FGO;
import eu.morfeoproject.fast.catalogue.vocabulary.FOAF;

public abstract class ScreenComponent extends BuildingBlock {

	private URI code;
	private List<Action> actions;
	private List<Library> libraries;
	private List<Condition> postconditions;
	private List<String> triggers;

    protected ScreenComponent(URI uri) {
		super(uri);
		this.actions = new ArrayList<Action>();
		this.libraries = new ArrayList<Library>();
		this.postconditions = new ArrayList<Condition>();
		this.triggers = new ArrayList<String>();
	}

	public URI getCode() {
		return code;
	}

	public void setCode(URI code) {
		this.code = code;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public List<Library> getLibraries() {
		return libraries;
	}

	public void setLibraries(List<Library> libraries) {
		this.libraries = libraries;
	}
	
	public List<Condition> getPostconditions() {
		return postconditions;
	}

	public void setPostconditions(List<Condition> postconditions) {
		this.postconditions = postconditions;
	}

	public List<String> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<String> triggers) {
		this.triggers = triggers;
	}
	
	public Condition getPrecondition(String name) {
		for (Action action : this.actions) {
			for (Condition condition : action.getPreconditions()) {
				if (condition.getId() != null 
						&& condition.getId().equals(name)) {
					return condition;
				}
			}
		}
		return null;
	}
	
	public Condition getPostcondition(String name) {
		for (Condition condition : this.postconditions) {
			if (condition.getId() != null 
					&& condition.getId().equals(name)) {
				return condition;
			}
		}
		return null;
	}

	@Override
	public Model toRDF2GoModel() {
		Model model = super.toRDF2GoModel();

		URI scUri = this.getUri();
		
		// code
		if (this.getCode() != null) {
			model.addStatement(scUri, FGO.hasCode, this.getCode());
		}
		
		// actions
		for (Action action : getActions()) {
			URI actionUri = action.getUri();
			// FIXME: is this the place to assign URI to actions? rethink!!
			if (actionUri == null) {
				actionUri = model.createURI(scUri + "/actions/" + action.getName());
				action.setUri(actionUri);
			}
			model.addStatement(scUri, FGO.hasAction, actionUri);
			model.addStatement(action.getUri(), RDFS.label, action.getName());
			// preconditions
			for (Condition con : action.getPreconditions()) {
				URI conUri = con.getUri();
				// FIXME: is this the place to assign URI to actions? rethink!!
				if (conUri == null) {
					conUri = model.createURI(actionUri + "/preconditions/" + con.getId());
					con.setUri(conUri);
				}
				model.addStatement(actionUri, FGO.hasPreCondition, conUri);
				model.addStatement(conUri, FGO.hasPatternString, model.createDatatypeLiteral(con.getPatternString(), XSD._string));
				model.addStatement(conUri, FGO.isPositive, model.createDatatypeLiteral(new Boolean(con.isPositive()).toString(), XSD._boolean));
				for (String key : con.getLabels().keySet()) {
					model.addStatement(conUri, RDFS.label, model.createLanguageTagLiteral(con.getLabels().get(key), key));
				}
			}
			// uses
			for (String id : action.getUses().keySet()) {
				BlankNode useNode = model.createBlankNode();
				model.addStatement(actionUri, FGO.uses, useNode);
				model.addStatement(useNode, RDFS.label, model.createPlainLiteral(id));
				model.addStatement(useNode, FOAF.Document, action.getUses().get(id));
			}
		}
		
		// libraries
		for (Library library : getLibraries()) {
			BlankNode libNode = model.createBlankNode();
			model.addStatement(scUri, FGO.hasLibrary, libNode);
			model.addStatement(libNode, FGO.hasLanguage, model.createPlainLiteral(library.getLanguage()));
			model.addStatement(libNode, FGO.hasCode, library.getSource());
		}
		
		// postconditions
		for (Condition con : getPostconditions()) {
			URI conUri = con.getUri();
			// FIXME: is this the place to assign URI to actions? rethink!!
			if (conUri == null) {
				conUri = model.createURI(scUri + "/postconditions/" + con.getId());
				con.setUri(conUri);
			}
			model.addStatement(scUri, FGO.hasPostCondition, conUri);
			model.addStatement(conUri, FGO.hasPatternString, model.createDatatypeLiteral(con.getPatternString(), XSD._string));
			model.addStatement(conUri, FGO.isPositive, model.createDatatypeLiteral(new Boolean(con.isPositive()).toString(), XSD._boolean));
			for (String key : con.getLabels().keySet()) {
				model.addStatement(conUri, RDFS.label, model.createLanguageTagLiteral(con.getLabels().get(key), key));
			}
		}
		
		// triggers
		for (String trigger : getTriggers()) {
			model.addStatement(scUri, FGO.hasTrigger, model.createDatatypeLiteral(trigger, XSD._string));
		}
		
		return model;
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = super.toJSON();

		// code
		json.put("code", getCode() == null ? JSONObject.NULL : getCode().toString());
		
		// actions
		JSONArray actionsArray = new JSONArray();
		for (Action action : getActions()) {
			actionsArray.put(action.toJSON());
		}
		json.put("actions", actionsArray);
		
		// libraries
		JSONArray librariesArray = new JSONArray();
		for (Library library : getLibraries()) {
			librariesArray.put(library.toJSON());
		}
		json.put("libraries", librariesArray);
		
		// postconditions
		JSONArray postArray = new JSONArray();
		for (Condition condition : getPostconditions()) {
			postArray.put(condition.toJSON());
		}
		json.put("postconditions", postArray);
		
		// triggers
		JSONArray triggersArray = new JSONArray();
		for (String trigger : getTriggers()) {
			triggersArray.put(trigger);
		}
		json.put("triggers", triggersArray);

		return json;
	}

}