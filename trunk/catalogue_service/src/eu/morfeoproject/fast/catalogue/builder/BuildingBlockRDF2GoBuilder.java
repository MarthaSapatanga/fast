/**
 * Copyright (c) 2008-2011, FAST Consortium
 * 
 * This file is part of FAST Platform.
 * 
 * FAST Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FAST Platform is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with FAST Platform. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Info about members and contributors of the FAST Consortium
 * is available at http://fast.morfeo-project.eu
 *
 **/
package eu.morfeoproject.fast.catalogue.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commontag.CTag;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

import eu.morfeoproject.fast.catalogue.BuildingBlockException;
import eu.morfeoproject.fast.catalogue.InvalidBuildingBlockTypeException;
import eu.morfeoproject.fast.catalogue.model.Action;
import eu.morfeoproject.fast.catalogue.model.BackendService;
import eu.morfeoproject.fast.catalogue.model.BuildingBlock;
import eu.morfeoproject.fast.catalogue.model.Condition;
import eu.morfeoproject.fast.catalogue.model.Form;
import eu.morfeoproject.fast.catalogue.model.Library;
import eu.morfeoproject.fast.catalogue.model.Operator;
import eu.morfeoproject.fast.catalogue.model.Pipe;
import eu.morfeoproject.fast.catalogue.model.Screen;
import eu.morfeoproject.fast.catalogue.model.ScreenComponent;
import eu.morfeoproject.fast.catalogue.model.ScreenFlow;
import eu.morfeoproject.fast.catalogue.model.Trigger;
import eu.morfeoproject.fast.catalogue.model.WithConditions;
import eu.morfeoproject.fast.catalogue.model.factory.BuildingBlockFactory;
import eu.morfeoproject.fast.catalogue.util.DateFormatter;
import eu.morfeoproject.fast.catalogue.vocabulary.CTAG;
import eu.morfeoproject.fast.catalogue.vocabulary.DC;
import eu.morfeoproject.fast.catalogue.vocabulary.DOAP;
import eu.morfeoproject.fast.catalogue.vocabulary.FGO;
import eu.morfeoproject.fast.catalogue.vocabulary.FOAF;

public class BuildingBlockRDF2GoBuilder {

	static final Log log = LogFactory.getLog(BuildingBlockRDF2GoBuilder.class);

	public static ScreenFlow buildScreenFlow(Model model) {
		ScreenFlow sf = null;
		try {
			sf = (ScreenFlow) retrieveScreenFlow(model);
		} catch (InvalidBuildingBlockTypeException e) {
			log.error("The building block type is not valid: "+e, e);
		}
		return sf;
	}
	
	public static Screen buildScreen(Model model) {
		Screen screen = null;
		try {
			screen = (Screen) retrieveScreen(model);
		} catch (InvalidBuildingBlockTypeException e) {
			log.error("The building block type is not valid: "+e, e);
		} catch (BuildingBlockException e) {
			log.error(""+e, e);
		}
		return screen;
	}

	public static Form buildForm(Model model) {
		Form form = null;
		try {
			form = (Form) retrieveScreenComponent(FGO.Form, model);
		} catch (InvalidBuildingBlockTypeException e) {
			log.error("The building block type is not valid: "+e, e);
		}
		return form;
	}
	
	public static Operator buildOperator(Model model) {
		Operator operator = null;
		try {
			operator = (Operator) retrieveScreenComponent(FGO.Operator, model);
		} catch (InvalidBuildingBlockTypeException e) {
			log.error("The building block type is not valid: "+e, e);
		}
		return operator;
	}
	
	public static BackendService buildBackendService(Model model) {
		BackendService bs = null;
		try {
			bs = (BackendService) retrieveScreenComponent(FGO.BackendService, model);
		} catch (InvalidBuildingBlockTypeException e) {
			log.error("The building block type is not valid: "+e, e);
		}
		return bs;
	}
	
	private static BuildingBlock retrieveBuildingBlock(URI type, Model model) throws InvalidBuildingBlockTypeException {
		// create the resource of the given type
		BuildingBlock bb = BuildingBlockFactory.createBuildingBlock(type);
		// extract the URI for the building block
		ClosableIterator<Statement> it = model.findStatements(Variable.ANY, RDF.type, type);
		URI bbUri = null;
		if (it.hasNext()) bbUri = it.next().getSubject().asURI(); it.close();
		if (bbUri == null) return null;
		// fill the information about the resource
		bb.setUri(bbUri);
		bb.setId(bbUri.toString().substring(bbUri.toString().lastIndexOf("/") + 1));
		ClosableIterator<Statement> cIt = model.findStatements(bbUri, Variable.ANY, Variable.ANY);
		if (!cIt.hasNext()) // the resource does not exist
			return null;
		for ( ; cIt.hasNext(); ) {
			Statement st = cIt.next();
			URI predicate = st.getPredicate();
			Node object = st.getObject();
			if (predicate.equals(FGO.hasPrototype)) {
				bb.setTemplate(object.asURI());
			} else if (predicate.equals(RDFS.label)) {
				if (object instanceof LanguageTagLiteral) {
					LanguageTagLiteral label = object.asLanguageTagLiteral();
					bb.getLabels().put(label.getLanguageTag(), label.getValue());
				}
			} else if (predicate.equals(DC.description)) {
				LanguageTagLiteral description = object.asLanguageTagLiteral();
				bb.getDescriptions().put(description.getLanguageTag(), description.getValue());
			} else if (predicate.equals(DC.creator)) {
				bb.setCreator(object.asURI());
			} else if (predicate.equals(DC.rights)) {
				bb.setRights(object.asURI());
			} else if (predicate.equals(DOAP.revision)) {
				bb.setVersion(object.asDatatypeLiteral().getValue());
			} else if (predicate.equals(DC.date)) {
				bb.setCreationDate(DateFormatter.parseDateISO8601(object.asDatatypeLiteral().getValue()));
			} else if (predicate.equals(FGO.hasIcon)) {
				bb.setIcon(object.asURI());
			} else if (predicate.equals(FGO.hasScreenshot)) {
				bb.setScreenshot(object.asURI());
			} else if (predicate.equals(CTAG.tagged)) {
				CTag tag = null;
				URI tagType = null;
				HashMap<String, String> tagLabels = new HashMap<String, String>();
				URI tagMeans = null;
				Date tagTaggingDate = null;
				BlankNode bnTag = object.asBlankNode();
				ClosableIterator<Statement> tagIt = model.findStatements(bnTag, Variable.ANY, Variable.ANY);
				for ( ; tagIt.hasNext(); ) {
					Statement tagSt = tagIt.next();
					URI tagPredicate = tagSt.getPredicate();
					Node tagObject = tagSt.getObject();
					if (tagPredicate.equals(RDF.type)) {
						tagType = tagObject.asURI();
					} else if (tagPredicate.equals(CTAG.means)) {
						tagMeans = tagObject.asURI();
					} else if (tagPredicate.equals(CTAG.label)) {
						if (tagObject instanceof LanguageTagLiteral) {
							LanguageTagLiteral label = tagObject.asLanguageTagLiteral();
							tagLabels.put(label.getLanguageTag(), label.getValue());
						}
					} else if (tagPredicate.equals(CTAG.taggingDate)) {
						tagTaggingDate = DateFormatter.parseDateISO8601(tagObject.asDatatypeLiteral().getValue());
					}
				}
				tag = BuildingBlockFactory.createTag(tagType);
				tag.getLabels().putAll(tagLabels);
				tag.setMeans(tagMeans);
				tag.setTaggingDate(tagTaggingDate);
				bb.getTags().add(tag);
			} else if (predicate.equals(FOAF.homepage)) {
				bb.setHomepage(object.asURI());
			} else if (predicate.equals(FGO.hasParameterTemplate)) {
				bb.setParameterTemplate(object.asDatatypeLiteral().getValue());
			}
		}
		cIt.close();
		
		return bb;
	}

	private static ScreenFlow retrieveScreenFlow(Model model) throws InvalidBuildingBlockTypeException {
		ScreenFlow sf = (ScreenFlow) retrieveWithConditions(FGO.ScreenFlow, model);
		
		if (sf != null) {
			// find all the info related to a screen-flow
			ClosableIterator<Statement> sfIt = model.findStatements(sf.getUri(), Variable.ANY, Variable.ANY);
			for ( ; sfIt.hasNext(); ) {
				Statement st = sfIt.next();
				URI predicate = st.getPredicate();
				Node object = st.getObject();
			    if (predicate.equals(FGO.contains)) {
			    	sf.getBuildingBlocks().add(object.asURI());
			    }
			}
			sfIt.close();
		}
		
		return sf;
	}
	
	private static Screen retrieveScreen(Model model) throws InvalidBuildingBlockTypeException, BuildingBlockException {
		Screen screen = (Screen) retrieveWithConditions(FGO.Screen, model);
		
		if (screen != null) {
			ClosableIterator<Statement> cIt;
			
			// if 'code' is specified, the screen is returned with its value
			cIt = model.findStatements(screen.getUri(), FGO.hasCode, Variable.ANY);
			if (cIt.hasNext()) {
				screen.setCode(cIt.next().getObject().asURI());
				return screen;
			}
			
			// the screen is defined in terms of its 'building blocks', 'pipes' and 'triggers'
			screen.setBuildingBlocks(new LinkedList<URI>());
			screen.setPipes(new LinkedList<Pipe>());
			screen.setTriggers(new LinkedList<Trigger>());
			cIt = model.findStatements(screen.getUri(), FGO.contains, Variable.ANY);
			for ( ; cIt.hasNext(); ) {
				Statement stmt = cIt.next();
				URI predicate = stmt.getPredicate();
				Node object = stmt.getObject();
				if (predicate.equals(FGO.contains)) {
					// FIXME: this is not the best way to do it, but in the model I cannot know which the type of the elements
					String strObj = object.toString();
					if (strObj.contains("/forms/") || strObj.contains("/operators/") || strObj.contains("/services/")) {
						screen.getBuildingBlocks().add(object.asURI());
					} else if (strObj.contains("/pipes/")) {
						screen.getPipes().add(retrievePipe(screen, object.asURI(), model));
					} else if (strObj.contains("/triggers/")) {
						screen.getTriggers().add(retrieveTrigger(screen, object.asURI(), model));
					}
				}
			}
			cIt.close();
		}
		
		return screen;
	}
	
	private static WithConditions retrieveWithConditions(URI type, Model model) throws InvalidBuildingBlockTypeException {
		if (!type.equals(FGO.ScreenFlow) && !type.equals(FGO.Screen))
			throw new InvalidBuildingBlockTypeException("Only ScreenFlow and Screen types are allowed.");
		
		// create the resource of the given type
		WithConditions withConditions = (WithConditions) retrieveBuildingBlock(type, model);
		if (withConditions != null) {
			withConditions.setPreconditions(new ArrayList<Condition>());
			withConditions.setPostconditions(new ArrayList<Condition>());
			// fill the conditions of the building block
			ClosableIterator<Statement> cIt = model.findStatements(withConditions.getUri(), Variable.ANY, Variable.ANY);
			for ( ; cIt.hasNext(); ) {
				Statement st = cIt.next();
				URI predicate = st.getPredicate();
				Node object = st.getObject();
				if (predicate.equals(FGO.hasPreCondition)) {
					withConditions.getPreconditions().add(retrieveCondition(object.asURI(), model));
				} else if (predicate.equals(FGO.hasPostCondition)) {
					withConditions.getPostconditions().add(retrieveCondition(object.asURI(), model));
				}
			}
			cIt.close();
		}
		
		return withConditions;
	}
	
	private static ScreenComponent retrieveScreenComponent(URI type, Model model) throws InvalidBuildingBlockTypeException {
		if (!type.equals(FGO.Form) && !type.equals(FGO.Operator) && !type.equals(FGO.BackendService))
			throw new InvalidBuildingBlockTypeException(type+" is not a valid screen component type.");
		
		ScreenComponent sc = (ScreenComponent) retrieveBuildingBlock(type, model);
		if (sc != null) {
			// find all the info related to a screen component
			ClosableIterator<Statement> cIt = model.findStatements(sc.getUri(), Variable.ANY, Variable.ANY);
			for ( ; cIt.hasNext(); ) {
				Statement scStmt = cIt.next();
				URI scPre = scStmt.getPredicate();
				Node scObj = scStmt.getObject();
				if (scPre.equals(FGO.hasAction)) {
					Action action = new Action(scObj.asURI());
					ClosableIterator<Statement> actionIt = model.findStatements(scObj.asURI(), Variable.ANY, Variable.ANY);
					for ( ; actionIt.hasNext(); ) {
						Statement actionSt = actionIt.next();
						URI acPre = actionSt.getPredicate();
						Node acObj = actionSt.getObject();
						if (acPre.equals(RDFS.label)) {
							action.setName(acObj.asLiteral().getValue());
						} else if (acPre.equals(FGO.hasPreCondition)) {
							action.getPreconditions().add(retrieveCondition(acObj.asURI(), model));
						} else if (acPre.equals(FGO.uses)) {
							ClosableIterator<Statement> useIt = model.findStatements(acObj.asBlankNode(), Variable.ANY, Variable.ANY);
							String useId = null;
							URI useUri = null;
							for ( ; useIt.hasNext(); ) {
								Statement useSt = useIt.next();
								URI usePre = useSt.getPredicate();
								if (usePre.equals(RDFS.label)) {
									useId = useSt.getObject().asLiteral().getValue();
								} else if (usePre.equals(FOAF.Document)) {
									useUri = useSt.getObject().asURI();
								}
							}
							useIt.close();
							if (useId != null && useUri != null) {
								action.getUses().put(useId, useUri);
							}
						}
					}
					actionIt.close();
					sc.getActions().add(action);
				} else if (scPre.equals(FGO.hasPostCondition)) {
					sc.getPostconditions().add(retrieveCondition(scObj.asURI(), model));
				} else if (scPre.equals(FGO.hasCode)) {
					sc.setCode(scObj.asURI());
				} else if (scPre.equals(FGO.hasTrigger)) {
					sc.getTriggers().add(scObj.asDatatypeLiteral().getValue());
				} else if (scPre.equals(FGO.hasLibrary)) {
					Library library = new Library();
					ClosableIterator<Statement> libIt = model.findStatements(scObj.asBlankNode(), Variable.ANY, Variable.ANY);
					for ( ; libIt.hasNext(); ) {
						Statement s = libIt.next();
						URI p = s.getPredicate();
						Node o = s.getObject();
						if (p.equals(FGO.hasLanguage)) {
							library.setLanguage(o.asLiteral().getValue());
						} else if (p.equals(FGO.hasCode)) {
							library.setSource(o.asURI());
						}
					}
					libIt.close();
					sc.getLibraries().add(library);
				}
			}
			cIt.close();
		}
		
		return sc;
	}
	
	/*
	 * Each condition will have only one pattern at most
	 */
	private static Condition retrieveCondition(URI conUri, Model model) {
		Condition condition = BuildingBlockFactory.createCondition(conUri);
		condition.setId(conUri.toString().substring(conUri.toString().lastIndexOf("/") + 1));
		ClosableIterator<Statement> cIt = model.findStatements(conUri, Variable.ANY, Variable.ANY);
		for ( ; cIt.hasNext(); ) {
			Statement st = cIt.next();
			URI predicate = st.getPredicate();
			Node object = st.getObject();
			if (predicate.equals(RDFS.label)) {
				if (object instanceof LanguageTagLiteral) {
					LanguageTagLiteral label = object.asLanguageTagLiteral();
					condition.getLabels().put(label.getLanguageTag(), label.getValue());
				}
			} else if (predicate.equals(FGO.isPositive)) {
				condition.setPositive(Boolean.parseBoolean(object.asDatatypeLiteral().getValue()));
			} else if (predicate.equals(FGO.hasPatternString)) {
				condition.setPatternString(object.asDatatypeLiteral().getValue());
			}
		}
		cIt.close();
		return condition;
	}
	
	private static Pipe retrievePipe(Screen screen, URI pipeURI, Model model) {
		Pipe pipe = BuildingBlockFactory.createPipe(screen, pipeURI);
		ClosableIterator<Statement> cIt = model.findStatements(pipeURI, Variable.ANY, Variable.ANY);
		for ( ; cIt.hasNext(); ) {
			Statement stmt = cIt.next();
			URI predicate = stmt.getPredicate();
			Node object = stmt.getObject();
			if (predicate.equals(FGO.from)) {
				String str = object.asURI().toString();
				int pCur = 0;
				if ((pCur = str.indexOf("/preconditions/")) > -1) {
					pipe.setBBFrom(null);
					pipe.setConditionFrom(str.substring(pCur + 15));
				} else {
					pCur = str.indexOf("/postconditions/");
					pipe.setBBFrom(model.createURI(str.substring(0, pCur)));
					pipe.setConditionFrom(str.substring(pCur + 16));
				}
			} else if (predicate.equals(FGO.to)) {
				String str = object.asURI().toString();
				int pCur = 0, aCur = 0;
				if ((pCur = str.indexOf("/postconditions/")) > -1) {
					pipe.setBBTo(null);
					pipe.setActionTo(null);
					pipe.setConditionTo(str.substring(pCur + 16));
				} else {
					aCur = str.indexOf("/actions/");
					pCur = str.indexOf("/preconditions/");
					pipe.setBBTo(model.createURI(str.substring(0, aCur)));
					pipe.setActionTo(str.substring(aCur + 9, str.indexOf('/', aCur + 9)));
					pipe.setConditionTo(str.substring(pCur + 15));
				}
			}
		}
		cIt.close();
		return pipe;
	}

	private static Trigger retrieveTrigger(Screen screen, URI triggerURI, Model model) {
		Trigger trigger = BuildingBlockFactory.createTrigger(screen, triggerURI);
		ClosableIterator<Statement> cIt = model.findStatements(triggerURI, Variable.ANY, Variable.ANY);
		for ( ; cIt.hasNext(); ) {
			Statement stmt = cIt.next();
			URI predicate = stmt.getPredicate();
			Node object = stmt.getObject();
			if (predicate.equals(FGO.from)) {
				String str = object.asURI().toString();
				int pCur = str.indexOf("/triggers/");
				URI uri = new URIImpl(str.substring(0, pCur));
				String name = str.substring(pCur + 10);
				if (uri.equals(screen.getUri())) uri = null;

				trigger.setBBFrom(uri);
				trigger.setNameFrom(name);
			} else if (predicate.equals(FGO.to)) {
				String str = object.asURI().toString();
				int pCur = str.indexOf("/actions/");
				URI uri = new URIImpl(str.substring(0, pCur));
				String action = str.substring(pCur + 9);
				if (uri.equals(screen.getUri())) uri = null;

				trigger.setBBTo(uri);
				trigger.setActionTo(action);
			}
		}
		cIt.close();
		return trigger;
	}

}