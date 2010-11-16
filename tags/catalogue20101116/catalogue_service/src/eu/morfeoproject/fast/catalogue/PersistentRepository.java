package eu.morfeoproject.fast.catalogue;

import java.io.File;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.NotifyingSail;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 	
 * @author Ismael Rivera
 */
public class PersistentRepository {
	
	final Logger logger = LoggerFactory.getLogger(PersistentRepository.class);
	
	static final String defaultIndexes = "spoc, posc, sopc, psoc, ospc, opsc";
	static final int defaultInferencer = TripleStore.FORWARD_CHAINING_RDFS_INFERENCER;
	
	public static Repository getHTTPRepository(String sesameServer, String repositoryID) throws RepositoryException, RepositoryConfigException {
		RepositoryManager manager = RemoteRepositoryManager.getInstance(sesameServer);
		Repository repository = null;
		repository = manager.getRepository(repositoryID);
		repository.initialize();
		return repository;
	}
	
	public static Repository getLocalRepository(File dir, String indexes) throws RepositoryException {
		//RepositoryManager manager = new LocalRepositoryManager(dir);
		//manager.getRepository(arg0);
		if (indexes == null) indexes = defaultIndexes;
		NotifyingSail sail = new NativeStore(dir, indexes);
		Repository repository = new SailRepository(sail);
		repository.initialize();
		return repository;
	}
	
	public static Repository getLocalRepository(File dir) throws RepositoryException {
		return PersistentRepository.getLocalRepository(dir, defaultIndexes);
	}
	
}
