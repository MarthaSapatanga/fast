package eu.morfeoproject.fast.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;

public class Condition {
	
	private String patternString;
	private List<Statement> pattern;
	private String scope;
	private Map<String, String> labels;
	
	public String getPatternString() {
		return patternString;
	}
	
	public void setPatternString(String patternString) {
		this.patternString = patternString;
	}
	
	public List<Statement> getPattern() {
		if (pattern == null)
			pattern = new ArrayList<Statement>();
		return pattern;
	}

	public void setPattern(List<Statement> pattern) {
		this.pattern = pattern;
	}
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Map<String, String> getLabels() {
		if (labels == null)
			labels = new HashMap<String, String>();
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		Condition condition = (Condition)o;
		if (this.getPattern().size() == condition.getPattern().size()) {
			Model condModel = RDF2Go.getModelFactory().createModel();
			condModel.open();
			condModel.addAll(this.getPattern().iterator());
			
			// create the ASK sparql query for a condition
	    	String queryStr = "ASK {";
	    	for (Statement st : condition.getPattern()) {
				String su = (st.getSubject() instanceof BlankNode) ? st.getSubject().toString() : st.getSubject().toSPARQL();
				String ob = (st.getObject() instanceof BlankNode) ? st.getObject().toString() : st.getObject().toSPARQL();
				queryStr = queryStr.concat(su+" "+st.getPredicate().toSPARQL()+" "+ob+" . ");
	    	}
	    	queryStr = queryStr.concat("}");

	    	return condModel.sparqlAsk(queryStr);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return patternString;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		try {
			if (getPatternString() == null)
				json.put("pattern", JSONObject.NULL);
			else
				json.put("pattern", getPatternString());
			if (getScope() == null)
				json.put("scope", JSONObject.NULL);
			else
				json.put("scope", getScope());
			if (getLabels() == null || getLabels().isEmpty())
				json.put("label", JSONObject.NULL);
			else {
				JSONObject jsonLabels = new JSONObject();
				for (String key : getLabels().keySet())
					jsonLabels.put(key, getLabels().get(key));
				json.put("label", jsonLabels);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
}
