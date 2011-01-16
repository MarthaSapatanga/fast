package uk.ac.open.kmi.iserve;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ontoware.rdf2go.model.node.URI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.morfeoproject.fast.catalogue.util.URLInputSource;
import eu.morfeoproject.fast.catalogue.util.URLUTF8Encoder;

public class IServeClient {

	protected final Log log = LogFactory.getLog(this.getClass());

	private String iServeURL;
	
	public IServeClient() {}
	
	public IServeClient(IServeConfiguration config) {
		this.iServeURL = config.get("server");
	}
	
	public List<IServeResponse> query(List<URI> classes) {
		if (this.iServeURL == null || this.iServeURL.equals(""))
			throw new RuntimeException("iServe URL has not been configured. Please, set up an iServe URL.");
		
		LinkedList<IServeResponse> results = new LinkedList<IServeResponse>();
		
		String queryTemplate = 
			"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
			"PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>\n" +
			"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n" +
			"PREFIX wl:<http://www.wsmo.org/ns/wsmo-lite#>\n" +
			"PREFIX sawsdl:<http://www.w3.org/ns/sawsdl#>\n" +
			"PREFIX rest:<http://www.wsmo.org/ns/hrests#>\n" +
			"PREFIX msm:<http://cms-wg.sti2.org/ns/minimal-service-model#>\n" +
			"SELECT DISTINCT ?s ?def ?address WHERE {\n" +
			"  { ?s msm:hasOperation ?o . ?o msm:hasInput ?in . ?in msm:hasPart ?p1 . ?p1 msm:hasPart ?p2 . ?p2 msm:hasPart ?p3 . ?p3 sawsdl:modelReference <class> . }  UNION \n" +
			"  { ?s msm:hasOperation ?o . ?o msm:hasInput ?in . ?in msm:hasPart ?p1 . ?p1 msm:hasPart ?p2 . ?p2 sawsdl:modelReference <class> . } UNION \n" +
			"  { ?s msm:hasOperation ?o . ?o msm:hasInput ?in . ?in msm:hasPart ?p1 . ?p1 sawsdl:modelReference <class> . } UNION \n" +
			"  { ?s msm:hasOperation ?o . ?o msm:hasInput ?in . ?in sawsdl:modelReference <class> . } UNION \n" +
			"  { ?s msm:hasOperation ?o . ?o sawsdl:modelReference <class> . } . " +
			"  OPTIONAL { ?s rdfs:isDefinedBy ?def } . \n" +
			"  OPTIONAL { ?o rest:hasAddress ?address } } \n";
		
		for (URI classUri : classes) {
			String query = queryTemplate.replaceAll("<class>", classUri.toSPARQL());
			if (log.isDebugEnabled()) log.debug(query);
			try {
				URLInputSource inSource = new URLInputSource(new URL(this.iServeURL+"?query="+URLUTF8Encoder.encode(query)));
				InputStream inStream = inSource.getInputStream();
				
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(inStream);
				doc.getDocumentElement().normalize();
				NodeList nodeList = doc.getElementsByTagName("result");
				
				if (log.isDebugEnabled()) log.debug(nodeList.getLength()+" services found.");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					NodeList children = node.getChildNodes();
					IServeResponse result = new IServeResponse();
					for (int j = 0; j < children.getLength(); j++) {
						result.put(children.item(j).getAttributes().getNamedItem("name").getNodeValue(), children.item(j).getTextContent());
					}
					results.add(result);
					if (log.isDebugEnabled()) log.debug(result.toString());
				}
			} catch (IOException e) {
				log.error(e.toString(), e);
			} catch (ParserConfigurationException e) {
				log.error(e.toString(), e);
			} catch (SAXException e) {
				log.error(e.toString(), e);
			}
		}
		
		return results;
	}
}