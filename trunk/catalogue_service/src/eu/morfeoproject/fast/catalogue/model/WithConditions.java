package eu.morfeoproject.fast.catalogue.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;

import eu.morfeoproject.fast.catalogue.vocabulary.FGO;

public abstract class WithConditions extends BuildingBlock {

	private List<List<Condition>> preconditions;
	private List<List<Condition>> postconditions;

	protected WithConditions(URI uri) {
		super(uri);
	}

	public List<List<Condition>> getPreconditions() {
		if (preconditions == null)
			preconditions = new ArrayList<List<Condition>>();
		return preconditions;
	}

	public void setPreconditions(List<List<Condition>> preconditions) {
		this.preconditions = preconditions;
	}

	public List<List<Condition>> getPostconditions() {
		if (postconditions == null)
			postconditions = new ArrayList<List<Condition>>();
		return postconditions;
	}

	public void setPostconditions(List<List<Condition>> postconditions) {
		this.postconditions = postconditions;
	}
	
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = super.toJSON();

		JSONArray preconditions = new JSONArray();
		for (List<Condition> conditionList : getPreconditions()) {
			JSONArray conditionArray = new JSONArray();
			for (Condition con : conditionList)
				conditionArray.put(con.toJSON());
			preconditions.put(conditionArray);
		}
		json.put("preconditions", preconditions);
	
		JSONArray postconditions = new JSONArray();
		for (List<Condition> conditionList : getPostconditions()) {
			JSONArray conditionArray = new JSONArray();
			for (Condition con : conditionList)
				conditionArray.put(con.toJSON());
			postconditions.put(conditionArray);
		}
		json.put("postconditions", postconditions);

		return json;
	}
	
	@Override
	public Model toRDF2GoModel() {
		Model model = super.toRDF2GoModel();

		URI bbUri = this.getUri();
		for (List<Condition> conList : this.getPreconditions()) {
			BlankNode bag = model.createBlankNode();
			model.addStatement(bag, RDF.type, RDF.Bag);
			model.addStatement(bbUri, FGO.hasPreCondition, bag);
			int i = 1;
			for (Condition con : conList) {
				BlankNode c = model.createBlankNode();
				model.addStatement(bag, RDF.li(i++), c);
				model.addStatement(c, FGO.hasPatternString, model.createDatatypeLiteral(con.getPatternString(), XSD._string));
				model.addStatement(c, FGO.isPositive, model.createDatatypeLiteral(new Boolean(con.isPositive()).toString(), XSD._boolean));
				model.addStatement(c, FGO.hasId, model.createDatatypeLiteral(con.getId(), XSD._string));
				for (String key : con.getLabels().keySet())
					model.addStatement(c, RDFS.label, model.createLanguageTagLiteral(con.getLabels().get(key), key));
			}
		}
		for (List<Condition> conList : this.getPostconditions()) {
			BlankNode bag = model.createBlankNode();
			model.addStatement(bag, RDF.type, RDF.Bag);
			model.addStatement(bbUri, FGO.hasPostCondition, bag);
			int i = 1;
			for (Condition con : conList) {
				BlankNode c = model.createBlankNode();
				model.addStatement(bag, RDF.li(i++), c);
				model.addStatement(c, FGO.hasPatternString, model.createDatatypeLiteral(con.getPatternString(), XSD._string));
				model.addStatement(c, FGO.isPositive, model.createDatatypeLiteral(new Boolean(con.isPositive()).toString(), XSD._boolean));
				model.addStatement(c, FGO.hasId, model.createDatatypeLiteral(con.getId(), XSD._string));
				for (String key : con.getLabels().keySet())
					model.addStatement(c, RDFS.label, model.createLanguageTagLiteral(con.getLabels().get(key), key));
			}
		}
		
		return model;
	}

}
