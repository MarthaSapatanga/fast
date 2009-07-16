package eu.morfeoproject.fast.restserver;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.morfeoproject.fast.catalogue.Catalogue;
import eu.morfeoproject.fast.catalogue.DuplicatedScreenException;
import eu.morfeoproject.fast.catalogue.NotFoundException;
import eu.morfeoproject.fast.catalogue.OntologyInvalidException;
import eu.morfeoproject.fast.catalogue.OntologyReadonlyException;
import eu.morfeoproject.fast.model.Screen;
import eu.morfeoproject.fast.vocabulary.FGO;

/**
 * 
 * @author irivera
 */
public class ScreenRestlet extends CatalogueRestlet {

	final Logger logger = LoggerFactory.getLogger(ScreenRestlet.class);

	public ScreenRestlet(Catalogue catalogue) throws RepositoryException {
		super();
		this.catalogue = catalogue;
	}

	@Override
	public void handle(Request request, Response response) {
		String id = (String) request.getAttributes().get("id");
		if (request.getMethod().equals(Method.GET)) {
			if (id == null) {
				// List the members of the collection
				logger.info("Retrieving all screens");
				JSONArray screens = new JSONArray();
				for (Screen s : catalogue.listScreens())
					screens.put(s.toJSON());
				try {
					response.setEntity(screens.toString(2), MediaType.APPLICATION_JSON);
					response.setStatus(Status.SUCCESS_OK);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				}
			} else {
				// Retrieve the addressed member of the collection
				logger.info("Retrieving screen "+id);
				Screen s = catalogue.getScreen(catalogue.getTripleStore().createURI(FGO.Screen+id));
				if (s == null)
					response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "The resource "+FGO.Screen+id+" has not been found.");
				else {
					try {
						response.setEntity(s.toJSON().toString(2), MediaType.APPLICATION_JSON);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					response.setStatus(Status.SUCCESS_OK);
				}
			}
		} else if (request.getMethod().equals(Method.PUT) && id != null) {
			// Update the addressed member of the collection or create it with a defined ID.
			try {
				JSONObject json = new JSONObject(request.getEntity().getText());
				Screen screen = parseScreen(json, id);
				catalogue.updateScreen(screen);
				response.setEntity(screen.toJSON().toString(2), MediaType.APPLICATION_JSON);
				response.setStatus(Status.SUCCESS_OK);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "The resource "+FGO.Screen+id+" has not been found.");
			} catch (OntologyReadonlyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (OntologyInvalidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		} else if (request.getMethod().equals(Method.POST)) {
			// Create a new entry in the collection where the ID is assigned automatically by 
			// the collection and it is returned.
			try {
				JSONObject json = new JSONObject(request.getEntity().getText());
//				json.remove("uri"); // FIXME unnecessary, get the screen URI from the URL
				Screen screen = parseScreen(json, null);
				try {
					catalogue.addScreen(screen);
					response.setEntity(screen.toJSON().toString(2), MediaType.APPLICATION_JSON);
					response.setStatus(Status.SUCCESS_OK);
				} catch (DuplicatedScreenException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				} catch (OntologyInvalidException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				} catch (OntologyReadonlyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "The resource "+FGO.Screen+id+" has not been found.");
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}	
		} else if (request.getMethod().equals(Method.DELETE) && id != null) {
			// Delete the addressed member of the collection.
			try {
				catalogue.removeScreen(new URIImpl(FGO.Screen+id));
				response.setStatus(Status.SUCCESS_OK);
			} catch (NotFoundException e1) {
				response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "The resource "+FGO.Screen+id+" has not been found.");
			}
		} else {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}
		
}
