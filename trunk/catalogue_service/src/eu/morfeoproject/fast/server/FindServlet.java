package eu.morfeoproject.fast.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.morfeoproject.fast.catalogue.NotFoundException;
import eu.morfeoproject.fast.model.Resource;

/**
 * Servlet implementation class FindServlet
 */
public class FindServlet extends GenericServlet {
	private static final long serialVersionUID = 1L;
    
	final Logger logger = LoggerFactory.getLogger(FindServlet.class);
	
	boolean recursive = false; // TODO delete this

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Entering FIND operation...");
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
		
		try {
			// create JSON representation of the input
			JSONObject input = new JSONObject(body);
			// parse the canvas
			HashSet<Resource> canvas = new HashSet<Resource>();
			JSONArray jsonCanvas = input.getJSONArray("canvas");
			for (int i = 0; i < jsonCanvas.length(); i++) {
				URI uri = new URIImpl(((JSONObject)jsonCanvas.get(i)).getString("uri"));
				Resource r = CatalogueAccessPoint.getCatalogue().getResource(uri);
				if (r == null) 
					throw new NotFoundException("Resource "+uri+" does not exist.");
				canvas.add(r); 
			}
			// parse the domain context
			JSONObject jsonDomainContext = input.getJSONObject("domainContext");
			JSONArray jsonTags = jsonDomainContext.getJSONArray("tags");
			HashSet<String> tags = new HashSet<String>();
			for (int i = 0; i < jsonTags.length(); i++)
				tags.add(jsonTags.getString(i));
			StringBuffer sb = new StringBuffer();
			for (String tag : tags)
				sb.append(tag+" ");
			// TODO do something with the user
			String user = jsonDomainContext.getString("user");
			// parse the elements
			JSONArray jsonElements = input.getJSONArray("elements");
			for (int i = 0; i < jsonElements.length(); i++) {
				URI uri = new URIImpl(((JSONObject)jsonElements.get(i)).getString("uri"));
				Resource r = CatalogueAccessPoint.getCatalogue().getResource(uri);
				if (r == null)
					throw new NotFoundException("Screen "+uri+" does not exist.");
				canvas.add(r); 
			}

			// make the call to the catalogue
			Set<URI> results = null;
			if (!this.recursive)
				results = CatalogueAccessPoint.getCatalogue().find(canvas, true, true, 0, 100000, tags);
//			else
//				results = CatalogueAccessPoint.getCatalogue().findRecursive(canvas, true, true, 0, 100000, tags);

			// write the results in the output
			JSONArray output = new JSONArray();
			for (Iterator<URI> it = results.iterator(); it.hasNext(); ) {
				URI r = it.next();
				output.put(r);
//				logger.info("[MATCH] "+r);
			}
			writer.print(output.toString(2));
			response.setContentType(MediaType.APPLICATION_JSON);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} catch (NotFoundException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
		logger.info("...Exiting FIND operation");
	}

}
