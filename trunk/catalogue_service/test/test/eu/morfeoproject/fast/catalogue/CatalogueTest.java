package test.eu.morfeoproject.fast.catalogue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.ontoware.rdf2go.model.node.URI;

import eu.morfeoproject.fast.catalogue.Catalogue;
import eu.morfeoproject.fast.catalogue.buildingblocks.BackendService;
import eu.morfeoproject.fast.catalogue.buildingblocks.Condition;
import eu.morfeoproject.fast.catalogue.buildingblocks.Form;
import eu.morfeoproject.fast.catalogue.buildingblocks.Operator;
import eu.morfeoproject.fast.catalogue.buildingblocks.Postcondition;
import eu.morfeoproject.fast.catalogue.buildingblocks.Screen;
import eu.morfeoproject.fast.catalogue.buildingblocks.ScreenComponent;
import eu.morfeoproject.fast.catalogue.services.CatalogueAccessPoint;
import eu.morfeoproject.fast.catalogue.vocabulary.FGO;

public class CatalogueTest extends TestCase {

	private Catalogue catalogue;
	
	public CatalogueTest(String name) {
		super(name);
	}
	 
	protected void setUp() throws Exception {
        super.setUp();
		catalogue = CatalogueAccessPoint.getCatalogue();
		catalogue.clear();
	}
	
	protected void tearDown() throws Exception {
        super.tearDown();
	}
	
	public void check() { 
		Assert.assertTrue(catalogue.check()); 
	}
	
	public void createForm() throws Exception {
		Form f1 = (Form) TestUtils.buildBB(catalogue.getServerURL(), "form", "data/json/forms/amazonList.json");
		catalogue.addForm(f1);
		Form f2 = catalogue.getForm(f1.getUri());
		Assert.assertTrue(f1.equals(f2));
		Assert.assertEquals(f1.getActions().size(), f2.getActions().size());
		Assert.assertEquals(f1.getPostconditions().size(), f2.getPostconditions().size());
		Assert.assertEquals(f1.getTriggers().size(), f2.getTriggers().size());
	}

	public void createOperator() throws Exception {
		Operator op1 = (Operator) TestUtils.buildBB(catalogue.getServerURL(), "operator", "data/json/operators/amazonEbayFilter.json");
		catalogue.addOperator(op1);
		Operator op2 = catalogue.getOperator(op1.getUri());
		Assert.assertTrue(op1.equals(op2));
		Assert.assertEquals(op1.getActions().size(), op2.getActions().size());
		Assert.assertEquals(op1.getPostconditions().size(), op2.getPostconditions().size());
		Assert.assertEquals(op1.getTriggers().size(), op2.getTriggers().size());
	}

	public void createBackendService() throws Exception {
		BackendService bs1 = (BackendService) TestUtils.buildBB(catalogue.getServerURL(), "backendservice", "data/json/backendservices/amazonSearchService.json");
		catalogue.addBackendService(bs1);
		BackendService bs2 = catalogue.getBackendService(bs1.getUri());
		Assert.assertTrue(bs1.equals(bs2));
		Assert.assertEquals(bs1.getActions().size(), bs2.getActions().size());
		Assert.assertEquals(bs1.getPostconditions().size(), bs2.getPostconditions().size());
		Assert.assertEquals(bs1.getTriggers().size(), bs2.getTriggers().size());
	}

	public void createPostcondition() throws Exception {
		Postcondition p1 = (Postcondition) TestUtils.buildBB(catalogue.getServerURL(), "postcondition", "data/json/postconditions/searchCriteria.json");
		catalogue.addPreOrPost(p1);
		Postcondition p2 = catalogue.getPostcondition(p1.getUri());
		Assert.assertTrue(p1.equals(p2));
		Assert.assertEquals(p1.getConditions().size(), p2.getConditions().size());
	}
	
	public void createScreen1() throws Exception {
		Screen s1 = (Screen) TestUtils.buildBB(catalogue.getServerURL(), "screen", "data/json/screens/amazonList.json");
		catalogue.addScreen(s1);
		Screen s2 = catalogue.getScreen(s1.getUri());
		Assert.assertTrue(s1.equals(s2));
		Assert.assertEquals(s1.getPreconditions().size(), s2.getPreconditions().size());
		Assert.assertEquals(s1.getPostconditions().size(), s2.getPostconditions().size());
		Assert.assertEquals(s1.getDefinition().getBuildingBlocks().size(), s2.getDefinition().getBuildingBlocks().size());
		Assert.assertEquals(s1.getDefinition().getPipes().size(), s2.getDefinition().getPipes().size());
		Assert.assertEquals(s1.getDefinition().getTriggers().size(), s2.getDefinition().getTriggers().size());
	}

	public void createScreen2() throws Exception {
		Screen s1 = (Screen) TestUtils.buildBB(catalogue.getServerURL(), "screen", "data/json/screens/amazonListCode.json");
		catalogue.addScreen(s1);
		Screen s2 = catalogue.getScreen(s1.getUri());
		Assert.assertTrue(s1.equals(s2));
		Assert.assertEquals(s1.getPreconditions().size(), s2.getPreconditions().size());
		Assert.assertEquals(s1.getPostconditions().size(), s2.getPostconditions().size());
		Assert.assertEquals(s1.getCode(), s2.getCode());
	}

	public void findAndCheck1() throws Exception {
		Form form = (Form) TestUtils.buildBB(catalogue.getServerURL(), "form", "data/json/forms/amazonList.json");
		BackendService service = (BackendService) TestUtils.buildBB(catalogue.getServerURL(), "backendservice", "data/json/backendservices/amazonSearchService.json");
		catalogue.addForm(form);
		catalogue.addBackendService(service);
		ArrayList<Condition> conList = new ArrayList<Condition>();
		HashSet<ScreenComponent> all = new HashSet<ScreenComponent>();
		all.add(form);
		Set<URI> results = catalogue.findScreenComponents(null, conList, all, 0, -1, new HashSet<String>(), FGO.BackendService);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(service.getUri(), results.toArray()[0]);
	}
	
	public static Test suite(){
	    TestSuite suite = new TestSuite();
	    suite.addTest(new CatalogueTest("check"));
	    suite.addTest(new CatalogueTest("createForm"));
	    suite.addTest(new CatalogueTest("createOperator"));
	    suite.addTest(new CatalogueTest("createBackendService"));
	    suite.addTest(new CatalogueTest("createPostcondition"));
	    suite.addTest(new CatalogueTest("createScreen1"));
	    suite.addTest(new CatalogueTest("createScreen2"));
	    suite.addTest(new CatalogueTest("findAndCheck1"));
		return suite;
	}
	
}
