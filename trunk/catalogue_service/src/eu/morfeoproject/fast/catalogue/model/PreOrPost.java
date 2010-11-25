package eu.morfeoproject.fast.catalogue.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;

import eu.morfeoproject.fast.catalogue.vocabulary.DC;
import eu.morfeoproject.fast.catalogue.vocabulary.FGO;

public abstract class PreOrPost extends BuildingBlock {
	
	private List<Condition> conditions;

	protected PreOrPost(URI uri) {
		super(uri);
		this.conditions = new ArrayList<Condition>();
	}
	
	public List<Condition> getConditions() {
		return conditions;
	}
	
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	
	public Model toRDF2GoModel() {
		super.toRDF2GoModel();
		Model model = RDF2Go.getModelFactory().createModel();
		model.open();
		model.setNamespace("dc", DC.NS_DC.toString());
		model.setNamespace("FGO", FGO.NS_FGO.toString());
		
		BlankNode bag = model.createBlankNode();
		model.addStatement(bag, RDF.type, RDF.Bag);
		model.addStatement(getUri(), FGO.hasCondition, bag);
		int i = 1;
		for (Condition con : conditions) {
			BlankNode c = model.createBlankNode();
			model.addStatement(bag, RDF.li(i++), c);
			model.addStatement(c, FGO.hasPatternString, model.createDatatypeLiteral(con.getPatternString(), XSD._string));
			model.addStatement(c, FGO.isPositive, model.createDatatypeLiteral(new Boolean(con.isPositive()).toString(), XSD._boolean));
//			model.addStatement(c, FGO.hasId, model.createDatatypeLiteral(con.getId(), XSD._string)); TODO: is it needed here for pre/postconditions??
			for (String key : con.getLabels().keySet())
				model.addStatement(c, RDFS.label, model.createLanguageTagLiteral(con.getLabels().get(key), key));
		}

		return model;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("uri", getUri() == null ? JSONObject.NULL : getUri().toString());
		json.put("id", getId() == null ? JSONObject.NULL : getId().toString());
		JSONArray conditions = new JSONArray();
		for (Condition con : getConditions())
			conditions.put(con.toJSON());
		json.put("conditions", conditions);
		return json;
	}

}
