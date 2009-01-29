package eu.morfeoproject.fast.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;

public class Screen {
	
	private URI uri;
	private String label;
//	private Map<String, String> label;
	private URI creator;
	private String description;
	private URI rights;
	private String version;
    private Date creationDate;
    private URI icon;
    private URI screenshot;
    private URI homepage; 
	private List<URI> domainContext;
	private List<Condition> preconditions;
	private List<Condition> postconditions;
	
	protected Screen(URI uri) {
		this.uri = uri;
//		this.label = new HashMap<String, String>();
	}
	
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

//	public String getLabel(String language) {
//		return label.get(language);
//	}
//
//	public void setLabel(String language, String label) {
//		this.label.put(language, label);
//	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public URI getCreator() {
		return creator;
	}

	public void setCreator(URI creator) {
		this.creator = creator;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public URI getRights() {
		return rights;
	}

	public void setRights(URI rights) {
		this.rights = rights;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public URI getIcon() {
		return icon;
	}

	public void setIcon(URI icon) {
		this.icon = icon;
	}

	public URI getScreenshot() {
		return screenshot;
	}

	public void setScreenshot(URI screenshot) {
		this.screenshot = screenshot;
	}

	public URI getHomepage() {
		return homepage;
	}

	public void setHomepage(URI homepage) {
		this.homepage = homepage;
	}

	public List<URI> getDomainContext() {
		if (domainContext == null)
			domainContext = new ArrayList<URI>();
		return domainContext;
	}

	public void setDomainContext(List<URI> domainContext) {
		this.domainContext = domainContext;
	}

	public List<Condition> getPreconditions() {
		if (preconditions == null)
			preconditions = new ArrayList<Condition>();
		return preconditions;
	}

	public void setPreconditions(List<Condition> preconditions) {
		this.preconditions = preconditions;
	}

	public List<Condition> getPostconditions() {
		if (postconditions == null)
			postconditions = new ArrayList<Condition>();
		return postconditions;
	}

	public void setPostconditions(List<Condition> postconditions) {
		this.postconditions = postconditions;
	}
	
	public String toString() {
		return getUri().toString();
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		try {
			if (getUri() == null)
				json.put("uri", JSONObject.NULL);
			else
				json.put("uri", getUri().toString());
			if (getLabel() == null)
				json.put("label", JSONObject.NULL);
			else
				json.put("label", getLabel().toString());
			if (getDescription() == null)
				json.put("description", JSONObject.NULL);
			else
				json.put("description", getDescription().toString());
			if (getCreator() == null)
				json.put("creator", JSONObject.NULL);
			else
				json.put("creator", getCreator().toString());
			if (getRights() == null)
				json.put("rights", JSONObject.NULL);
			else
				json.put("rights", getRights().toString());
			if (getVersion() == null)
				json.put("version", JSONObject.NULL);
			else
				json.put("version", getVersion());
			if (getCreationDate() == null)
				json.put("creationDate", JSONObject.NULL);
			else
				json.put("creationDate", getCreationDate().toString());
			if (getIcon() == null)
				json.put("icon", JSONObject.NULL);
			else
				json.put("icon", getIcon().toString());
			if (getScreenshot() == null)
				json.put("screenshot", JSONObject.NULL);
			else
				json.put("screenshot", getScreenshot().toString());
			JSONArray domainContext = new JSONArray();
			for (URI domain : getDomainContext())
				domainContext.put(domain.toString());
			json.put("domainContext", domainContext);
			if (getHomepage() == null)
				json.put("homepage", JSONObject.NULL);
			else
				json.put("homepage", getHomepage().toString());
			JSONArray preconditions = new JSONArray();
			for (Condition con : getPreconditions()) {
				StringBuffer sb = new StringBuffer();
				for (Statement st : con.getStatements())
					sb.append(st.getSubject() + " " + st.getPredicate() + " " + st.getObject() + " . ");
				preconditions.put(sb.toString());
			}				
			json.put("preconditions", preconditions);
			JSONArray postconditions = new JSONArray();
			for (Condition con : getPostconditions()) {
				StringBuffer sb = new StringBuffer();
				for (Statement st : con.getStatements())
					sb.append(st.getSubject() + " " + st.getPredicate() + " " + st.getObject() + " . ");
				postconditions.put(sb.toString());
			}				
			json.put("postconditions", postconditions);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
//	public boolean equals(Screen s) {
//		return compareTo(s) == 0;
//	}
//
//	@Override
//	public int compareTo(Object arg0) {
//		if (arg0 instanceof Screen) {
//			Screen s = (Screen)arg0;
//			return s.getUri().compareTo(getUri());
//		} else {
//			return -1;
//		}
//	}
}
