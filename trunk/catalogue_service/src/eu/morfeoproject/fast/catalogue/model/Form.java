package eu.morfeoproject.fast.catalogue.model;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.morfeoproject.fast.catalogue.vocabulary.FGO;

public class Form extends ScreenComponent {
	
	public Form(URI uri) {
		super(uri);
	}
	
	@Override
	public Model toRDF2GoModel() {
		Model model = super.toRDF2GoModel();
		model.addStatement(getUri(), RDF.type, FGO.Form);
		return model;
	}
	
}
