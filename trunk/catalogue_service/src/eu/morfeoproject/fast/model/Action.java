package eu.morfeoproject.fast.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Action {

	private String name;
	private List<Condition> preconditions;
	private List<String> uses;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Condition> getPreconditions() {
		if (preconditions == null)
			preconditions = new ArrayList<Condition>();
		return preconditions;
	}

	public void setPreconditions(List<Condition> preconditions) {
		this.preconditions = preconditions;
	}

	
	public List<String> getUses() {
		if (uses == null)
			uses = new ArrayList<String>();
		return uses;
	}

	public void setUses(List<String> uses) {
		this.uses = uses;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		try {
			if (getName() == null)
				json.put("name", JSONObject.NULL);
			else
				json.put("name", getName());
			JSONArray preArray = new JSONArray();
			for (Condition condition : getPreconditions())
				preArray.put(condition.toJSON());
			json.put("preconditions", preArray);
			// uses
			JSONArray usesArray = new JSONArray();
			for (String use : getUses())
				usesArray.put(use);
			json.put("uses", usesArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

}
