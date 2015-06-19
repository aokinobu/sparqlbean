package org.glycoinfo.rdf.scint;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.glycoinfo.rdf.SelectSparql;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SesameDAOTestConfig;
import org.glycoinfo.rdf.dao.SparqlDAO;
import org.glycoinfo.rdf.dao.SparqlDAOSesameImpl;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.glycan.GlycoSequenceSelectSparql;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import scala.reflect.internal.Trees.Select;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ScintTest.class, SesameDAOTestConfig.class})
@ComponentScan(basePackages = ("org.glycoinfo.rdf.scint"))
@Configuration
@EnableAutoConfiguration
public class ScintTest {

	public static Logger logger = (Logger) LoggerFactory
			.getLogger(ScintTest.class);
	
	@Autowired
	@Qualifier(value = "ClassHandler")
	ClassHandler classHandler;
	
	@Autowired
	SparqlDAO sparqlDAO;
	
	@Autowired
	@Qualifier(value = "scint")
	SelectScint selectScint;

	@Autowired
	@Qualifier(value = "insertscint")
	InsertScint insertScint;
	
	
	@Bean(name = "ClassHandler")
	ClassHandler getClassHandler() throws SparqlException {
		ClassHandler scint = new ClassHandler("schema", "http://schema.org/", "Person");
		return scint; 
	}
	
	@Bean(name = "scint")
	SelectScint getSelectScint() throws SparqlException {
		SelectScint select = new SelectScint();
		select.setClassHandler(classHandler);
		return select;
	}

	@Bean(name = "insertscint")
	InsertScint getInsertScint() throws SparqlException {
		InsertScint insert = new InsertScint();
		insert.setClassHandler(classHandler);
		return insert;
	}

	@Test
	public void testGetDomain() throws SparqlException {
		logger.debug("" + classHandler.getDomains());
	}
	
	@Test
	public void testSelectDomain() throws SparqlException {
		SparqlEntity sparqlentity = new SparqlEntity();
		sparqlentity.setValue("familyName", "Aoki");
		sparqlentity.setValue("givenName", "Nobu");
		sparqlentity.setValue("email", "");
		selectScint.setSparqlEntity(sparqlentity);
		logger.debug(selectScint.getSparql());
		List<SparqlEntity> results = sparqlDAO.query(selectScint);
		
		for (SparqlEntity result : results) {
			Assert.assertEquals("Aoki", result.getValue("familyName"));
			Assert.assertEquals("Nobu", result.getValue("givenName"));
			Assert.assertEquals("support@glytoucan.org", result.getValue("email"));
		}
	}

	@Test
	public void testInsertDomain() throws SparqlException {
		SparqlEntity sparqlentity = new SparqlEntity();
		sparqlentity.setValue("familyName", "Aoki");
		sparqlentity.setValue("givenName", "Nobu");
		sparqlentity.setValue("email", "support@glytoucan.org");
		insertScint.setSparqlEntity(sparqlentity);
		logger.debug(insertScint.getSparql());
		sparqlDAO.insert(insertScint);
	}
}