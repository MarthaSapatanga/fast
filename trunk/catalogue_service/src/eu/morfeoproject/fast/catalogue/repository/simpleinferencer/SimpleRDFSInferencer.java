/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2008.
 *
 * Licensed under the Aduna BSD-style license.
 */
package eu.morfeoproject.fast.catalogue.repository.simpleinferencer;

import org.openrdf.sail.NotifyingSail;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.helpers.NotifyingSailWrapper;
import org.openrdf.sail.inferencer.InferencerConnection;

/**
 * Forward-chaining RDF Schema inferencer, using the rules from the <a
 * href="http://www.w3.org/TR/2004/REC-rdf-mt-20040210/">RDF Semantics
 * Recommendation (10 February 2004)</a>. This inferencer can be used to add
 * RDF Schema semantics to any Sail that returns {@link InferencerConnection}s
 * from their {@link Sail#getConnection()} method.
 */
public class SimpleRDFSInferencer extends NotifyingSailWrapper {

	/*--------------*
	 * Constructors *
	 *--------------*/

	public SimpleRDFSInferencer() {
		super();
	}

	public SimpleRDFSInferencer(NotifyingSail baseSail) {
		super(baseSail);
	}

	/*---------*
	 * Methods *
	 *---------*/

	@Override
	public SimpleRDFSInferencerConnection getConnection()
		throws SailException
	{
		try {
			InferencerConnection con = (InferencerConnection)super.getConnection();
			return new SimpleRDFSInferencerConnection(con);
		}
		catch (ClassCastException e) {
			throw new SailException(e.getMessage(), e);
		}
	}

	/**
	 * Adds axiom statements to the underlying Sail.
	 */
	@Override
	public void initialize()
		throws SailException
	{
		super.initialize();

		SimpleRDFSInferencerConnection con = getConnection();
		try {
			con.addAxiomStatements();
			con.commit();
		}
		finally {
			con.close();
		}
	}
}