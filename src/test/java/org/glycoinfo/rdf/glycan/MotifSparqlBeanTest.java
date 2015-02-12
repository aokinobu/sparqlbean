package org.glycoinfo.rdf.glycan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.glycoinfo.batch.glyconvert.ConvertInsertSparql;
import org.glycoinfo.batch.glyconvert.ConvertSelectSparql;
import org.glycoinfo.conversion.GlyConvert;
import org.glycoinfo.conversion.wurcs.GlycoctToWurcsConverter;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SesameDAOTestConfig;
import org.glycoinfo.rdf.dao.SparqlDAO;
import org.glycoinfo.rdf.dao.SparqlDAOSesameImpl;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.glycan.wurcs.MotifSelectSparql;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.qos.logback.classic.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MotifSparqlBeanTest.class)
@Configuration
@EnableAutoConfiguration
public class MotifSparqlBeanTest {

	public static Logger logger = (Logger) LoggerFactory
			.getLogger(MotifSparqlBeanTest.class);

	@Bean
	MotifSelectSparql getMotifSelectSparql() {
		return new MotifSelectSparql();
	}

//	@Bean
//	ConvertInsertSparql getConvertInsertSparql() {
//		ConvertInsertSparql convert = new ConvertInsertSparql();
//		convert.setGraphBase("http://glytoucan.org/rdf/demo/0.7");
//		return convert;
//	}

	@Test
	public void testSelectSparql() throws SparqlException {
		logger.debug(getMotifSelectSparql().getSparql());
		assertEquals("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "PREFIX toucan:  <http://www.glytoucan.org/glyco/owl/glytoucan#>\n"
				+ "SELECT ?SaccharideURI ?PrimaryId ?GlycoSequenceURI ?Sequence\n"
				+ "FROM <http://glytoucan.org/rdf/demo/0.2>\n"
				+ "FROM <http://glytoucan.org/rdf/demo/0.3/wurcs>\n"
				+ " WHERE {?SaccharideURI a glycan:glycan_motif .\n"
				+ "?SaccharideURI toucan:has_primary_id ?PrimaryId .\n"
				+ "?SaccharideURI glycan:has_glycosequence ?GlycoSequenceURI .\n"
				+ "?GlycoSequenceURI glycan:has_sequence ?Sequence .\n"
				+ "?GlycoSequenceURI glycan:in_carbohydrate_format glycan:carbohydrate_format_wurcs\n}\n", getMotifSelectSparql().getSparql());
	}
	
//	@Test
//	public void testInsertSparql() {
//		ConvertInsertSparql convert = getConvertInsertSparql();
//		SparqlEntity se = new SparqlEntity();
//		se.setValue("SaccharideURI", "<testSaccharideURI>");
//		se.setValue("SequenceURI", "<testSequenceURI>");
//		se.setValue("ConvertedSequence", "testConvertedSequence!");
//		convert.setSparqlEntity(se);
//		assertEquals("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
//				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
//				+ "PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
//				+ "PREFIX glytoucan:  <http://www.glytoucan.org/glyco/owl/glytoucan#>\n"
//				+ "INSERT INTO\n"
//				+ "GRAPH <http://glytoucan.org/rdf/demo/0.7wurcs>\n"
//				+ "USING <http://glytoucan.org/rdf/demo/0.2>\n"
//				+ "USING <http://glytoucan.org/rdf/demo/msdb/8>\n"
//				+ "USING <http://purl.jp/bio/12/glyco/glycan/ontology/0.18>\n"
//				+ "USING <http://www.glytoucan.org/glyco/owl/glytoucan>\n"
//				+ "{ <testSaccharideURI> glycan:has_glycosequence <testSequenceURI> .\n"
//				+ "<testSequenceURI> glycan:has_sequence \"testConvertedSequence!\"^^xsd:string .\n"
//				+ "<testSequenceURI> glycan:in_carbohydrate_format glycan:carbohydrate_format_wurcs .\n"
//				+ "<testSequenceURI> glytoucan:is_glycosequence_of <testSaccharideURI> .\n }\n", convert.getSparql());
//	}
}