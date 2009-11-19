package eu.morfeoproject.fast.model;

import java.util.List;

import org.ontoware.rdf2go.model.Statement;

public class FastModelFactory {
	
	public static ScreenFlow createScreenFlow() {
		return new ScreenFlow(null);
	}
	
	public static Screen createScreen() {
		return new Screen(null);
	}
	
	public static FormElement createFormElement() {
		return new FormElement(null);
	}
	
	public static Operator createOperator() {
		return new Operator(null);
	}
	
	public static BackendService createBackendService() {
		return new BackendService(null);
	}

	public static Precondition createPrecondition() {
		return new Precondition(null);
	}
	
	public static Postcondition createPostcondition() {
		return new Postcondition(null);
	}
	
	public static Condition createCondition() {
		return new Condition();
	}
	
	public static Condition createCondition(List<Statement> pattern) {
		Condition condition = new Condition();
		for (Statement st : pattern)
			condition.getPattern().add(st);
		return condition;
	}
	
	public static Pipe createPipe() {
		return new Pipe();
	}
	
}
