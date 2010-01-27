package eu.morfeoproject.fast.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.morfeoproject.fast.catalogue.DuplicatedResourceException;
import eu.morfeoproject.fast.catalogue.NotFoundException;
import eu.morfeoproject.fast.catalogue.OntologyInvalidException;
import eu.morfeoproject.fast.catalogue.ResourceException;
import eu.morfeoproject.fast.model.Precondition;

/**
 * Servlet implementation class PreconditionServlet
 */
public class PreconditionServlet extends GenericServlet {
	private static final long serialVersionUID = 1L;

	static Logger logger = LoggerFactory.getLogger(PreconditionServlet.class);
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PreconditionServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		String format = request.getHeader("accept") != null ? request.getHeader("accept") : MediaType.APPLICATION_JSON;
		String[] chunks = request.getRequestURI().split("/");
		String id = chunks[chunks.length-1];
		if (id.equalsIgnoreCase("preconditions")) id = null;
		
		if (id == null) {
			// List the members of the collection
			logger.info("Retrieving all preconditions");
			try {
				if (format.equals(MediaType.APPLICATION_RDF_XML)) {
					response.setContentType(MediaType.APPLICATION_RDF_XML);
					Model model = RDF2Go.getModelFactory().createModel();
					try {
						model.open();
						for (Precondition s : CatalogueAccessPoint.getCatalogue().listPreconditions()) {
							Model preModel = s.createModel();
							for (String ns : preModel.getNamespaces().keySet())
								model.setNamespace(ns, preModel.getNamespace(ns));
							model.addModel(preModel);
							preModel.close();
						}
						model.writeTo(writer, Syntax.RdfXml);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						model.close();
					}
				} else { //if (format.equals(MediaType.APPLICATION_JSON)) {
					response.setContentType(MediaType.APPLICATION_JSON);
					JSONArray pres = new JSONArray();
					for (Precondition s : CatalogueAccessPoint.getCatalogue().listPreconditions())
						pres.put(s.toJSON());
					writer.print(pres.toString(2));
				}
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (JSONException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			}
		} else {
			// Retrieve the addressed member of the collection
			String uri = request.getRequestURL().toString();
			Precondition s = CatalogueAccessPoint.getCatalogue().getPrecondition(new URIImpl(uri));
			if (s == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "The resource "+uri+" has not been found.");
			} else {
				try {
					if (format.equals(MediaType.APPLICATION_RDF_XML)) {
							response.setContentType(MediaType.APPLICATION_RDF_XML);
							Model preModel = s.createModel();
							preModel.writeTo(writer, Syntax.RdfXml);
							preModel.close();
					} else {
						response.setContentType(MediaType.APPLICATION_JSON);
						writer.print(s.toJSON().toString(2));
					}				
					response.setStatus(HttpServletResponse.SC_OK);
				} catch (JSONException e) {
					e.printStackTrace();
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
				}
			}
		}
		writer.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader reader = request.getReader();
		PrintWriter writer = response.getWriter();
		String format = request.getHeader("accept") != null ? request.getHeader("accept") : MediaType.APPLICATION_JSON;
		StringBuffer buffer = new StringBuffer();
		String line = reader.readLine();
		while (line != null) {
			buffer.append(line);
			line = reader.readLine();
		}
		String body = buffer.toString();

		// Create a new entry in the collection where the ID is assigned automatically by 
		// the collection and it is returned.
		try {
			JSONObject json = new JSONObject(body);
			Precondition pre = parsePrecondition(json, null);
			try {
				CatalogueAccessPoint.getCatalogue().addPreOrPost(pre);
				if (format.equals(MediaType.APPLICATION_RDF_XML)) {
					response.setContentType(MediaType.APPLICATION_RDF_XML);
					Model preModel = pre.createModel();
					preModel.writeTo(writer, Syntax.RdfXml);
					preModel.close();
				} else {
					response.setContentType(MediaType.APPLICATION_JSON);
					writer.print(pre.toJSON().toString(2));
				}
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (DuplicatedResourceException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			} catch (OntologyInvalidException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			} catch (ResourceException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			}
		} catch (JSONException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}	
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader reader = request.getReader();
		PrintWriter writer = response.getWriter();
		String format = request.getHeader("accept") != null ? request.getHeader("accept") : MediaType.APPLICATION_JSON;
		String[] chunks = request.getRequestURI().split("/");
		String id = chunks[chunks.length-1];
		if (id.equalsIgnoreCase("screens")) id = null;
		StringBuffer buffer = new StringBuffer();
		String line = reader.readLine();
		while (line != null) {
			buffer.append(line);
			line = reader.readLine();
		}
		String body = buffer.toString();
		
		if (id == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "An ID must be specified.");
		} else {
			// Update the addressed member of the collection or create it with a defined ID.
			String uri = request.getRequestURL().toString();
			try {
				JSONObject json = new JSONObject(body);
				Precondition pre = parsePrecondition(json, new URIImpl(uri));
				CatalogueAccessPoint.getCatalogue().updatePreOrPost(pre);
				if (format.equals(MediaType.APPLICATION_RDF_XML)) {
					response.setContentType(MediaType.APPLICATION_RDF_XML);
					Model preModel = pre.createModel();
					preModel.writeTo(writer, Syntax.RdfXml);
					preModel.close();
				} else {
					response.setContentType(MediaType.APPLICATION_JSON);
					writer.print(pre.toJSON().toString(2));
				}
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (JSONException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			} catch (NotFoundException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "The resource "+uri+" has not been found.");
			} catch (ResourceException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			}
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] chunks = request.getRequestURI().split("/");
		String id = chunks[chunks.length-1];
		if (id.equalsIgnoreCase("screens")) id = null;
		
		if (id == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "An ID must be specified.");
		} else {
			// Delete the addressed member of the collection.
			String uri = request.getRequestURL().toString();
			try {
				CatalogueAccessPoint.getCatalogue().removePreOrPost(new URIImpl(uri));
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (NotFoundException e) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "The resource "+uri+" has not been found.");
			}
		}
	}

}
