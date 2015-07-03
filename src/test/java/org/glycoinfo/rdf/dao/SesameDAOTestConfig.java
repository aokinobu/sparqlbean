package org.glycoinfo.rdf.dao;

import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.scint.ClassHandler;
import org.glycoinfo.rdf.utils.TripleStoreProperties;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.spring.RepositoryConnectionFactory;
import org.openrdf.spring.SesameConnectionFactory;
import org.openrdf.spring.SesameTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class SesameDAOTestConfig {
	
    @Value("${triplestore.sesame.url}")
    private String url;
	
	@Bean
	SparqlDAO getSparqlDAO() {
		return new SparqlDAOSesameImpl();
	}

	@Bean
	TripleStoreProperties getTripleStoreProperties() {
		return new TripleStoreProperties();
	}
	
	@Bean
	SesameConnectionFactory getSesameConnectionFactory() {
		return new RepositoryConnectionFactory(getRepository());
	}

	@Bean
	public Repository getRepository() {
		return new HTTPRepository(url);
	}
	
//  <bean id="transactionManager" class="org.openrdf.spring.SesameTransactionManager">
//  <constructor-arg ref="repositoryConnectionFactory"/>
//</bean>
	@Bean
	SesameTransactionManager transactionManager() throws RepositoryException {
		return new SesameTransactionManager(getSesameConnectionFactory());
	}

	//  <bean id="repositoryManager" class="org.openrdf.repository.manager.LocalRepositoryManager"
//          init-method="initialize">
//        <constructor-arg value="."/>
//    </bean>
//	LocalRepositoryManager repositoryManager() throws RepositoryException {
//		LocalRepositoryManager lrm = new LocalRepositoryManager(new File("/tmp/data.dat"));
//		lrm.initialize();
//		return lrm;
//	}
}