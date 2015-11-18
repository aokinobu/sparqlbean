package org.glycoinfo.rdf.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.glycoinfo.batch.search.wurcs.SubstructureSearchSparql;
import org.glycoinfo.client.MSdbClient;
import org.glycoinfo.conversion.error.ConvertException;
import org.glycoinfo.mass.MassInsertSparql;
import org.glycoinfo.rdf.InsertSparql;
import org.glycoinfo.rdf.SelectSparql;
import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlDAO;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.dao.SparqlEntityFactory;
import org.glycoinfo.rdf.dao.VirtSesameDAOTestConfig;
import org.glycoinfo.rdf.glycan.ContributorInsertSparql;
import org.glycoinfo.rdf.glycan.ContributorNameSelectSparql;
import org.glycoinfo.rdf.glycan.DatabaseSelectSparql;
import org.glycoinfo.rdf.glycan.GlycoSequenceInsertSparql;
import org.glycoinfo.rdf.glycan.ResourceEntryInsertSparql;
import org.glycoinfo.rdf.glycan.Saccharide;
import org.glycoinfo.rdf.glycan.SaccharideInsertSparql;
import org.glycoinfo.rdf.glycan.SaccharideSelectSparql;
import org.glycoinfo.rdf.glycan.msdb.MSInsertSparql;
import org.glycoinfo.rdf.glycan.wurcs.GlycoSequenceResourceEntryContributorSelectSparql;
import org.glycoinfo.rdf.glycan.wurcs.GlycoSequenceToWurcsSelectSparql;
import org.glycoinfo.rdf.glycan.wurcs.MonosaccharideSelectSparql;
import org.glycoinfo.rdf.glycan.wurcs.MotifSequenceSelectSparql;
import org.glycoinfo.rdf.glycan.wurcs.WurcsRDFInsertSparql;
import org.glycoinfo.rdf.glycan.wurcs.WurcsRDFMSInsertSparql;
import org.glycoinfo.rdf.scint.ClassHandler;
import org.glycoinfo.rdf.scint.InsertScint;
import org.glycoinfo.rdf.scint.SelectScint;
import org.glycoinfo.rdf.service.impl.ContributorProcedureRdf;
import org.glycoinfo.rdf.service.impl.GlycanProcedureConfig;
import org.glycoinfo.rdf.service.impl.MailService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {GlycanProcedureTest.class, VirtSesameDAOTestConfig.class, GlycanProcedureConfig.class})
//@ComponentScan(basePackages = {"org.glycoinfo.rdf.service", "org.glycoinfo.rdf.scint"})
//@ComponentScan(basePackages = {"org.glycoinfo.rdf"}, excludeFilters={
//		  @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=Configuration.class)})
@Configuration
@EnableAutoConfiguration
public class GlycanProcedureTest {
	public static Logger logger = (Logger) LoggerFactory
			.getLogger(GlycanProcedureTest.class);
	
	@Autowired
	SparqlDAO sparqlDAO;
	
	private static final String graph = "http://rdf.glytoucan.org";
	
	@Bean(name = "selectscintperson")
	SelectScint getSelectPersonScint() throws SparqlException {
		SelectScint select = new SelectScint();
		
		select.setClassHandler(getPersonClassHandler());
		return select;
	}

	@Bean(name = "insertscintperson")
	InsertScint getInsertPersonScint() throws SparqlException {
		InsertScint insert = new InsertScint("http://rdf.glytoucan.org/users");
		insert.setClassHandler(getPersonClassHandler());
		return insert;
	}
	
	@Bean(name = "selectscintregisteraction")
	SelectScint getSelectRegisterActionScint() throws SparqlException {
		SelectScint select = new SelectScint();
		select.setClassHandler(getRegisterActionClassHandler());
		return select;
	}

	@Bean(name = "insertscintregisteraction")
	InsertScint getInsertRegisterActionScint() throws SparqlException {
		InsertScint insert = new InsertScint("http://rdf.glytoucan.org/users");
		insert.setClassHandler(getRegisterActionClassHandler());
		return insert;
	}
	
	@Bean
	ClassHandler getPersonClassHandler() throws SparqlException {
		ClassHandler ch = new ClassHandler("schema", "http://schema.org/", "Person");
		ch.setSparqlDAO(sparqlDAO);
		return ch; 
	}
	
	ClassHandler getRegisterActionClassHandler() throws SparqlException {
		ClassHandler ch = new ClassHandler("schema", "http://schema.org/", "RegisterAction");
		ch.setSparqlDAO(sparqlDAO);
		return ch; 
	}
	
	ClassHandler getDateTimeClassHandler() throws SparqlException {
		ClassHandler ch = new ClassHandler("schema", "http://schema.org/", "DateTime");
		ch.setSparqlDAO(sparqlDAO);
		return ch; 
	}
	
	@Autowired
	GlycanProcedure glycanProcedure;

	@Bean(name = "glycanProcedure")
	@Scope("prototype")
	GlycanProcedure getGlycanProcedure() throws SparqlException {
		GlycanProcedure glycan = new org.glycoinfo.rdf.service.impl.GlycanProcedure();
		return glycan;
	}
	
	@Bean
	SaccharideInsertSparql getSaccharideInsertSparql() {
		SaccharideInsertSparql sis = new SaccharideInsertSparql();
		sis.setGraph(graph);
		return sis;
	}
	
	@Bean(name = "contributorProcedure")
	ContributorProcedure getContributorProcedure() throws SparqlException {
		ContributorProcedure cp = new ContributorProcedureRdf();
		return cp;
	}

	@Bean
	ContributorInsertSparql getContributorInsertSparql() {
		ContributorInsertSparql c = new ContributorInsertSparql();
		c.setGraph(graph);
		return c;
	}
	
	@Bean
	ContributorNameSelectSparql getContributorNameSelectSparql() {
		ContributorNameSelectSparql selectbyNameContributor = new ContributorNameSelectSparql();
		selectbyNameContributor.setFrom("FROM <" + graph + ">");
		return selectbyNameContributor;
	}
	
	@Bean
	ResourceEntryInsertSparql getResourceEntryInsertSparql() {
		ResourceEntryInsertSparql resourceEntryInsertSparql = new ResourceEntryInsertSparql();
		SparqlEntity se = new SparqlEntity();
		se.setValue(ResourceEntryInsertSparql.Database, "glytoucan");
		resourceEntryInsertSparql.setSparqlEntity(se);
		resourceEntryInsertSparql.setGraph(graph);
		return resourceEntryInsertSparql;
	}
	
	@Bean
	@Scope("prototype")
	SelectSparql glycoSequenceContributorSelectSparql() {
		GlycoSequenceResourceEntryContributorSelectSparql sb = new GlycoSequenceResourceEntryContributorSelectSparql();
		sb.setFrom("FROM <" + graph + ">\nFROM <http://rdf.glytoucan.org/sequence/wurcs>\nFROM <http://rdf.glytoucan.org/mass>");
		return sb;
	}
	
	@Bean
	WurcsRDFInsertSparql wurcsRDFInsertSparql() {
		WurcsRDFInsertSparql wrdf = new WurcsRDFInsertSparql();
		wrdf.setSparqlEntity(new SparqlEntity());
		wrdf.setGraph("http://rdf.glytoucan.org/sequence/wurcs");
		return wrdf;
	}
	
	@Bean
	InsertSparql glycoSequenceInsert() {
		GlycoSequenceInsertSparql gsis = new GlycoSequenceInsertSparql();
		gsis.setSparqlEntity(new SparqlEntity());
		gsis.setGraph(graph);
		return gsis;
	}

	@Bean
	MassInsertSparql massInsertSparql() {
		MassInsertSparql mass = new MassInsertSparql();
		mass.setGraphBase(graph);
		return mass;
	}
	
	@Bean
	SelectSparql listAllGlycoSequenceContributorSelectSparql() {
		GlycoSequenceResourceEntryContributorSelectSparql sb = new GlycoSequenceResourceEntryContributorSelectSparql();
		sb.setFrom("FROM <http://rdf.glytoucan.org>\nFROM <http://rdf.glytoucan.org/sequence/wurcs>\nFROM <http://rdf.glytoucan.org/mass>");
		return sb;
	}

	@Bean
	MotifSequenceSelectSparql motifSequenceSelectSparql() {
		MotifSequenceSelectSparql select = new MotifSequenceSelectSparql();
//		select.setFrom("FROM <http://rdf.glytoucan.org>\nFROM <http://rdf.glytoucan.org/sequence/wurcs>");
		return select;
	}
	
	@Bean
	SubstructureSearchSparql substructureSearchSparql() {
		SubstructureSearchSparql ssb = new SubstructureSearchSparql();
		return ssb;
	}
	
	@Bean
	WurcsRDFMSInsertSparql wurcsRDFMSInsertSparql() {
		WurcsRDFMSInsertSparql wrdf = new WurcsRDFMSInsertSparql();
		wrdf.setGraph("http://rdf.glytoucan.org/wurcs/ms");
		return wrdf;
	}
	
	@Bean
	SaccharideSelectSparql saccharideSelectSparql() {
		SaccharideSelectSparql select = new SaccharideSelectSparql();
		select.setFrom("FROM <http://rdf.glytoucan.org>\n");
		return select;
	}

	
	@Bean
	MailService mailService() {
		return new MailService();
	}
	
	@Bean
	MSdbClient msdbClient() {
		return new MSdbClient();
	}
	
	@Bean
	MonosaccharideSelectSparql monosaccharideSelectSparql() {
		MonosaccharideSelectSparql sb = new MonosaccharideSelectSparql();
		sb.setFrom(sb.getFrom() + "FROM <http://rdf.glytoucan.org>\n");
		return sb;
	}
	
	@Bean
	public MSInsertSparql msInsertSparql() {
		MSInsertSparql wrss = new MSInsertSparql();
		wrss.setGraph("http://rdf.glytoucan.org/msdb");
		return wrss;
	}
	
	@Autowired
	DatabaseSelectSparql databaseSelectSparql;
	
//	@Test(expected=SparqlException.class)
//	public void testInsufficientUser() throws SparqlException {
//		SparqlEntity se = new SparqlEntity();
//		se.setValue("id", "person456");
//		userProcedure.setSparqlEntity(se);
//		userProcedure.addUser();
//	}
	
	/*
	 * RES
1b:b-dglc-HEX-1:5
2s:n-acetyl
3b:b-dgal-HEX-1:5
LIN
1:1d(2+1)2n
2:1o(4+1)3d
	 */
	@Test
	public void testSearch() throws SparqlException, ConvertException {
		String sequence = "RES\n" +
				"1b:b-dglc-HEX-1:5\n" +
				"2s:n-acetyl\n"
				+ "3b:b-dgal-HEX-1:5\n"
				+ "LIN\n"
				+ "1:1d(2+1)2n\n"
				+ "2:1o(4+1)3d";
		SparqlEntity se = glycanProcedure.searchBySequence(sequence);
		
		logger.debug(se.getValue(GlycoSequenceToWurcsSelectSparql.AccessionNumber));
		logger.debug(se.getValue(GlycanProcedure.Image));
		logger.debug(se.getValue(GlycoSequenceToWurcsSelectSparql.Sequence));
		Assert.assertNotNull(se.getValue(GlycoSequenceToWurcsSelectSparql.AccessionNumber));
		Assert.assertNotNull(se.getValue(GlycanProcedure.Image));
		Assert.assertNotNull(se.getValue(GlycoSequenceToWurcsSelectSparql.Sequence));
		Assert.assertEquals("G00055MO", se.getValue(GlycoSequenceToWurcsSelectSparql.AccessionNumber));
	}
	
	@Test
	public void testSearchG00031MO() throws SparqlException, ConvertException {
		// ne
//		data-wurcs="WURCS=2.0/2,2,1/[a2112h-1a_1-5_2*NCC/3=O][a2112h-1b_1-5]/1-2/a3-b1" >
	
//test
//		data-wurcs="WURCS=2.0/2,2,1/[22112h-1a_1-5_2*NCC/3=O][12112h-1b_1-5]/1-2/a3-b1">
//		data-wurcs="WURCS=2.0/2,2,1/[22112h-1a_1-5_2*NCC/3=O][12112h-1b_1-5]/1-2/a3-b1">
		String sequence = "RES\n"
				+ "1b:a-dgal-HEX-1:5\n"
				+ "2s:n-acetyl\n"
				+ "3b:b-dgal-HEX-1:5\n"
				+ "LIN\n"
				+ "1:1d(2+1)2n\n"
				+ "2:1o(3+1)3d";
		SparqlEntity se = glycanProcedure.searchBySequence(sequence);

		logger.debug(se.getValue(GlycoSequenceToWurcsSelectSparql.AccessionNumber));
		logger.debug(se.getValue(GlycanProcedure.Image));
		logger.debug(se.getValue(GlycoSequenceToWurcsSelectSparql.Sequence));
		Assert.assertNotNull(se.getValue(GlycoSequenceToWurcsSelectSparql.AccessionNumber));
		Assert.assertNotNull(se.getValue(GlycanProcedure.Image));
		Assert.assertNotNull(se.getValue(GlycoSequenceToWurcsSelectSparql.Sequence));
		Assert.assertEquals("G00031MO", se.getValue(GlycoSequenceToWurcsSelectSparql.AccessionNumber));
	}

	
	@Test
	public void testHash() {
		String sequence="RES\n"
				+ "1b:x-dglc-HEX-1:5|1:a\n"
				+ "2b:b-dgal-HEX-1:5\n"
				+ "LIN\n"
				+ "1:1o(4+1)2d";
		String hashtext = DigestUtils.md5Hex(sequence);
		Assert.assertEquals("e06b141de8d13adfa0c3ad180b9eae06", hashtext);
		hashtext = DigestUtils.md5Hex("WURCS=2.0/4,4,3/[u2122h][a2112h-1b_1-5][a2112h-1a_1-5][a2112h-1b_1-5_2*NCC/3=O]/1-2-3-4/a4-b1_b3-c1_c3-d1");
		Assert.assertEquals("497ea4c9a0680f9aa7d6541dca211967", hashtext);
		logger.debug(hashtext);

		//		WURCS=2.0/4,4,3/[u2122h_2*NCC/3=O_6*OSO/3=O/3=O_?*OSO/3=O/3=O][a1212A-1a_1-5_2*OSO/3=O/3=O][a2122h-1b_1-5_2*NCC/3=O_6*OSO/3=O/3=O_?*OSO/3=O/3=O][a1212A-1a_1-5]/1-2-3-4/a4-b1_b4-c1_c4-d1
		hashtext = DigestUtils.md5Hex("WURCS=2.0/4,4,3/[u2122h_2*NCC/3=O_6*OSO/3=O/3=O_?*OSO/3=O/3=O][a1212A-1a_1-5_2*OSO/3=O/3=O][a2122h-1b_1-5_2*NCC/3=O_6*OSO/3=O/3=O_?*OSO/3=O/3=O][a1212A-1a_1-5]/1-2-3-4/a4-b1_b4-c1_c4-d1");
		logger.debug(hashtext);
		Assert.assertEquals("331ebfcfc29a997790a7a4f1671a9882", hashtext);
	}
	
	@Test(expected=SparqlException.class)
	public void testRegisterNew() throws SparqlException, NoSuchAlgorithmException, ConvertException {
		
		String sequence="WURCS=2.0/4,4,3/[u2122h][a2112h-1b_1-5][a2112h-1a_1-5][a2112h-1b_1-5_2*NCC/3=O]/1-2-3-4/a4-b1_b3-c1_c3-d1";

//		glycanProcedure.setSequence(sequence);
//		glycanProcedure.setContributorId("testname");
		String se = glycanProcedure.register(sequence, "testname");

		/*
		PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>
			PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>
			SELECT distinct ?glycoseq ?accessionNo ?sequence 
			#?WURCS_label  
			?res
			#?Contributor
			WHERE {
			       ?s a glycan:saccharide .
			       ?s glytoucan:has_primary_id ?accessionNo .
			       ?s glytoucan:has_primary_id "G03828HN" .
			       ?s glycan:has_glycosequence ?glycoseq .
			       ?glycoseq glycan:has_sequence ?sequence .
			#       ?glycoseq rdfs:label ?WURCS_label .
			       ?glycoseq glycan:in_carbohydrate_format glycan:carbohydrate_format_wurcs .
			        ?s glycan:has_resource_entry ?res .
			#        ?res a glycan:resource_entry ;
			#        glytoucan:date_registered ?ContributionTime ;
			#         glytoucan:contributor ?c .
			#        ?c foaf:name ?Contributor .
			}*/
		logger.debug(se);
		String query = "PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>\n"
				+ "SELECT distinct ?glycoseq ?accessionNo ?sequence\n"
				+ "?WURCS_label\n"
				+ "?res\n"
				+ "?Contributor\n"
				+ "WHERE {\n"
				+ "?s a glycan:saccharide .\n"
				+ "?s glytoucan:has_primary_id ?accessionNo .\n"
				+ "?s glytoucan:has_primary_id \"" + se + "\" .\n"
				+ "?s glycan:has_glycosequence ?glycoseq .\n"
				+ "?glycoseq glycan:has_sequence ?sequence .\n"
				+ "?glycoseq rdfs:label ?WURCS_label .\n"
				+ "?glycoseq glycan:in_carbohydrate_format glycan:carbohydrate_format_wurcs .\n"
				+ "?s glycan:has_resource_entry ?res .\n"
				+ "?res a glycan:resource_entry ;\n"
				+ "glytoucan:date_registered ?ContributionTime ;\n"
				+ "glytoucan:contributor ?c .\n"
				+ "?c foaf:name ?Contributor .}";

		List<SparqlEntity> results = sparqlDAO.query(new SelectSparqlBean(query));
		Assert.assertTrue(results.size()>0);
		SparqlEntity first = results.iterator().next();
		logger.debug(first.getValue("glycoseq"));
		logger.debug(first.getValue("accessionNo"));
		logger.debug(first.getValue("Contributor"));
//		Assert.assertNotNull(se);
	}
	
	@Test
	public void testSequenceScope() throws SparqlException, NoSuchAlgorithmException {
		SparqlEntity se = glycanProcedure.searchByAccessionNumber("G00026MO");
		logger.debug(se.getValue("Mass"));
		se = glycanProcedure.searchByAccessionNumber("G00031MO");
		logger.debug(se.getValue("Mass"));
//		Assert.assertNotNull(se);
	}
	
	@Test
	@Transactional
	public void testRegisterNew3() throws SparqlException, NoSuchAlgorithmException, ConvertException {
		String sequence = "RES\\n"
				+ "1b:x-dglc-HEX-1:5\\n"
				+ "2b:x-dglc-HEX-1:5\\n"
				+ "3b:x-dglc-HEX-1:5\\n"
				+ "4s:n-acetyl\\n"
				+ "5s:n-acetyl\\n"
				+ "LIN\\n"
				+ "1:1o(-1+1)2d\\n"
				+ "2:2o(-1+1)3d\\n"
				+ "3:3d(2+1)4n\\n"
				+ "4:1d(2+1)5n\\n";
//		String sequence = "RES\n"
//				+ "1b:b-dglc-HEX-1:5\n"
//				+ "2s:n-acetyl\n"
//				+ "3b:b-dgal-HEX-1:5\n"
//				+ "LIN\n"
//				+ "1:1d(2+1)2n\n"
//				+ "2:1o(4+1)3d";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setSequence(sequence);
		SparqlEntity se = glycanProcedure.searchBySequence(sequence);

		logger.debug(se.getValue(GlycoSequenceToWurcsSelectSparql.AccessionNumber));
		logger.debug(se.getValue(GlycanProcedure.ResultSequence));
		Assert.assertNotNull(se.getValue(GlycoSequenceToWurcsSelectSparql.AccessionNumber));
		logger.debug(se.getValue(GlycanProcedure.FromSequence));

		String wurcs = se.getValue(GlycanProcedure.ResultSequence);
		
		logger.debug("wurcs:>" + wurcs + "<");
//		glycanProcedure.setContributorId("test");
		
//		glycanProcedure.setSequence(sequence);
		String id = glycanProcedure.register(sequence, "254");
		logger.debug("searching with id:>" + id + "<");
		se = glycanProcedure.searchByAccessionNumber(id);
		
		Assert.assertNotNull(se.getValue("Mass"));

		logger.debug(se.toString());
	}
	
//	@Test
//	@Transactional
	public void testRegisterG98132BH() throws SparqlException, NoSuchAlgorithmException, ConvertException {
		String sequence = "RES\\n"
				+ "1b:x-dglc-HEX-1:5\\n"
				+ "2s:n-acetyl\\n"
				+ "3b:b-dglc-HEX-1:5\\n"
				+ "4s:n-acetyl\\n"
				+ "5b:b-dman-HEX-1:5\\n"
				+ "6b:a-dman-HEX-1:5\\n"
				+ "7b:b-dglc-HEX-1:5\\n"
				+ "8s:n-acetyl\\n"
				+ "9b:b-dgal-HEX-1:5\\n"
				+ "10b:b-dglc-HEX-1:5\\n"
				+ "11s:n-acetyl\\n"
				+ "12b:b-dgal-HEX-1:5\\n"
				+ "13b:a-dman-HEX-1:5\\n"
				+ "14b:b-dglc-HEX-1:5\\n"
				+ "15s:n-acetyl\\n"
				+ "16b:a-lgal-HEX-1:5|6:d\\n"
				+ "LIN\\n"
				+ "1:1d(2+1)2n\\n"
				+ "2:1o(4+1)3d\\n"
				+ "3:3d(2+1)4n\\n"
				+ "4:3o(4+1)5d\\n"
				+ "5:5o(3+1)6d\\n"
				+ "6:6o(2+1)7d\\n"
				+ "7:7d(2+1)8n\\n"
				+ "8:7o(4+1)9d\\n"
				+ "9:6o(4+1)10d\\n"
				+ "10:10d(2+1)11n\\n"
				+ "11:10o(4+1)12d\\n"
				+ "12:5o(6+1)13d\\n"
				+ "13:13o(2|6+1)14d\\n"
				+ "14:14d(2+1)15n\\n"
				+ "15:1o(6+1)16d\\n";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setSequence(sequence);
		SparqlEntity se = glycanProcedure.searchBySequence(sequence);

		logger.debug(se.getValue(GlycoSequenceToWurcsSelectSparql.AccessionNumber));
		logger.debug(se.getValue(GlycanProcedure.ResultSequence));
		Assert.assertNotNull(se.getValue(GlycoSequenceToWurcsSelectSparql.AccessionNumber));
		logger.debug(se.getValue(GlycanProcedure.FromSequence));

		String wurcs = se.getValue(GlycanProcedure.ResultSequence);
		
		logger.debug("wurcs:>" + wurcs + "<");
//		glycanProcedure.setContributorId("test");
		
//		glycanProcedure.setSequence(sequence);
		String id = glycanProcedure.register(sequence, "test");
		se = glycanProcedure.searchByAccessionNumber(id);
		Assert.assertNotNull(se.getValue("Mass"));
		
		logger.debug(se.toString());
	}
	
	@Test
	@Transactional
	public void testRegisterG65696SL() throws SparqlException, ConvertException
	{
//		GlycoCT:
//RES
//1b:x-dglc-HEX-1:5
//2b:b-dgal-HEX-1:5
//3b:b-dglc-HEX-1:5
//4s:n-acetyl
//5b:b-dgal-HEX-1:5
//6b:b-dglc-HEX-1:5
//7s:n-acetyl
//8b:b-dgal-HEX-1:5
//9b:b-dglc-HEX-1:5
//10s:n-acetyl
//11b:b-dgal-HEX-1:5
//12b:b-dglc-HEX-1:5
//13s:n-acetyl
//14b:b-dgal-HEX-1:5
//LIN
//1:1o(4+1)2d
//2:2o(3+1)3d
//3:3d(2+1)4n
//4:3o(4+1)5d
//5:2o(6+1)6d
//6:6d(2+1)7n
//7:6o(4+1)8d
//8:8o(3+1)9d
//9:9d(2+1)10n
//10:9o(3+1)11d
//11:8o(6+1)12d
//12:12d(2+1)13n
//13:12o(4+1)14d
		String sequence = "RES\\n"
				+ "1b:x-dglc-HEX-1:5\\n"
				+ "2b:b-dgal-HEX-1:5\\n"
				+ "3b:b-dglc-HEX-1:5\\n"
				+ "4s:n-acetyl\\n"
				+ "5b:b-dgal-HEX-1:5\\n"
				+ "6b:b-dglc-HEX-1:5\\n"
				+ "7s:n-acetyl\\n"
				+ "8b:b-dgal-HEX-1:5\\n"
				+ "9b:b-dglc-HEX-1:5\\n"
				+ "10s:n-acetyl\\n"
				+ "11b:b-dgal-HEX-1:5\\n"
				+ "12b:b-dglc-HEX-1:5\\n"
				+ "13s:n-acetyl\\n"
				+ "14b:b-dgal-HEX-1:5\\n"
				+ "LIN\\n"
				+ "1:1o(4+1)2d\\n"
				+ "2:2o(3+1)3d\\n"
				+ "3:3d(2+1)4n\\n"
				+ "4:3o(4+1)5d\\n"
				+ "5:2o(6+1)6d\\n"
				+ "6:6d(2+1)7n\\n"
				+ "7:6o(4+1)8d\\n"
				+ "8:8o(3+1)9d\\n"
				+ "9:9d(2+1)10n\\n"
				+ "10:9o(3+1)11d\\n"
				+ "11:8o(6+1)12d\\n"
				+ "12:12d(2+1)13n\\n"
				+ "13:12o(4+1)14d\\n";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setId("G65696SL");
//		glycanProcedure.setContributorId("5854");
		String result = glycanProcedure.register(sequence, "5854");

		Assert.assertNotNull(result);
		
	}
	
	@Test
	@Transactional
	public void testRegisterG46627YI() throws SparqlException, ConvertException
	{
//		GlycoCT:
//		RES
//		1b:x-dglc-HEX-1:5
//		2b:b-dgal-HEX-1:5
//		3b:b-dglc-HEX-1:5
//		4s:n-acetyl
//		5b:b-dgal-HEX-1:5
//		6b:b-dglc-HEX-1:5
//		7s:n-acetyl
//		8b:a-lgal-HEX-1:5|6:d
//		9b:b-dgal-HEX-1:5
//		10b:b-dglc-HEX-1:5
//		11s:n-acetyl
//		12b:b-dgal-HEX-1:5
//		13b:a-lgal-HEX-1:5|6:d
//		LIN
//		1:1o(4+1)2d
//		2:2o(3+1)3d
//		3:3d(2+1)4n
//		4:3o(3+1)5d
//		5:2o(6+1)6d
//		6:6d(2+1)7n
//		7:6o(3+1)8d
//		8:6o(4+1)9d
//		9:9o(3+1)10d
//		10:10d(2+1)11n
//		11:10o(3+1)12d
//		12:12o(2+1)13d
		String sequence = "RES\\n" +
				"1b:x-dglc-HEX-1:5\\n" +
				"2b:b-dgal-HEX-1:5\\n" +
				"3b:b-dglc-HEX-1:5\\n" +
				"4s:n-acetyl\\n" +
				"5b:b-dgal-HEX-1:5\\n" +
				"6b:b-dglc-HEX-1:5\\n" +
				"7s:n-acetyl\\n" +
				"8b:a-lgal-HEX-1:5|6:d\\n" +
				"9b:b-dgal-HEX-1:5\\n" +
				"10b:b-dglc-HEX-1:5\\n" +
				"11s:n-acetyl\\n" +
				"12b:b-dgal-HEX-1:5\\n" +
				"13b:a-lgal-HEX-1:5|6:d\\n" +
				"LIN\\n" +
				"1:1o(4+1)2d\\n" +
				"2:2o(3+1)3d\\n" +
				"3:3d(2+1)4n\\n" +
				"4:3o(3+1)5d\\n" +
				"5:2o(6+1)6d\\n" +
				"6:6d(2+1)7n\\n" +
				"7:6o(3+1)8d\\n" +
				"8:6o(4+1)9d\\n" +
				"9:9o(3+1)10d\\n" +
				"10:10d(2+1)11n\\n" +
				"11:10o(3+1)12d\\n" +
				"12:12o(2+1)13d\\n";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setId("G46627YI");
//		glycanProcedure.setContributorId("5854");
		String result = glycanProcedure.register(sequence, "5854");

		Assert.assertNotNull(result);
//		Assert.assertEquals("G46627YI", result);
	}
	
	@Test
	@Transactional
	public void testRegisterG92195EH() throws SparqlException, ConvertException
	{

		String sequence = "RES\\n" +
				"1b:x-dglc-HEX-1:5\\n" +
				"2b:b-dgal-HEX-1:5\\n" +
				"3b:b-dglc-HEX-1:5\\n" +
				"4s:n-acetyl\\n" +
				"5b:b-dgal-HEX-1:5\\n" +
				"6b:b-dglc-HEX-1:5\\n" +
				"7s:n-acetyl\\n" +
				"8b:a-lgal-HEX-1:5|6:d\\n" +
				"9b:b-dgal-HEX-1:5\\n" +
				"10b:b-dglc-HEX-1:5\\n" +
				"11s:n-acetyl\\n" +
				"12b:b-dgal-HEX-1:5\\n" +
				"13b:a-lgal-HEX-1:5|6:d\\n" +
				"LIN\\n" +
				"1:1o(4+1)2d\\n" +
				"2:2o(3+1)3d\\n" +
				"3:3d(2+1)4n\\n" +
				"4:3o(3+1)5d\\n" +
				"5:2o(6+1)6d\\n" +
				"6:6d(2+1)7n\\n" +
				"7:6o(3+1)8d\\n" +
				"8:6o(4+1)9d\\n" +
				"9:9o(3+1)10d\\n" +
				"10:10d(2+1)11n\\n" +
				"11:10o(3+1)12d\\n" +
				"12:12o(2+1)13d\\n";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setId("G92195EH");
//		glycanProcedure.setContributorId("5854");
		String result = glycanProcedure.register(sequence, "5854");

		Assert.assertNotNull(result);
		
	}
	
	
	@Test
	public void testListAll() throws SparqlException, NoSuchAlgorithmException {
		List<SparqlEntity> se = glycanProcedure.getGlycans("100", "100");
		
		logger.debug(se.toString());
//		Assert.assertNotNull(se);
		
	}
	
	@Test
	public void testFindingMotifs() throws SparqlException {
		
		// structure should have motifs : G99992LL
		String acc = "WURCS=2.0/6,10,9/[a2122h-1x_1-5_2*NCC/3=O][a2122h-1b_1-5_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5][a2112h-1b_1-5_2*NCC/3=O][Aad21122h-2a_2-6_5*NCC/3=O]/1-2-3-4-2-5-6-4-2-5/a4-b1_b4-c1_d2-e1_e4-f1_f6-g2_h2-i1_i4-j1_c?-d1_c?-h1";
		acc = "G95954RU";

		// find motifs for it
		ArrayList<SparqlEntity> list = glycanProcedure.findMotifs(acc);

		ArrayList<String> compare = new ArrayList<String>();
		for (SparqlEntity sparqlEntity : list) {
			String id = sparqlEntity.getValue(Saccharide.PrimaryId);
			compare.add(id);
		}
		
		// check results
		
		ArrayList<String> correct = new ArrayList<String>();
		correct.add("G00034MO");
		correct.add("G00042MO");
		correct.add("G00032MO");
		correct.add("G00055MO");
		correct.add("G00068MO");
		
		logger.debug(compare.toString());
		Assert.assertTrue(compare.containsAll(correct));
	}
	
	@Test
	public void testDatabaseInfo() throws SparqlException {
//		
//		ArrayList<SparqlEntity> list = glycanProcedure.getDatabaseInfo(acc);
//
//		ArrayList<String> compare = new ArrayList<String>();
//		for (SparqlEntity sparqlEntity : list) {
//			String id = sparqlEntity.getValue(Saccharide.PrimaryId);
//			compare.add(id);
//		}
//		
//		// check results
//		
//		ArrayList<String> correct = new ArrayList<String>();
//		correct.add("G00034MO");
//		correct.add("G00042MO");
//		correct.add("G00032MO");
//		correct.add("G00055MO");
//		correct.add("G00068MO");
//		
//		logger.debug(compare.toString());
//		Assert.assertTrue(compare.containsAll(correct));
	}
	
	@Bean
	SparqlEntityFactory sparqlEntityFactory() {
		return new SparqlEntityFactory();
	}
	
//	@Test
	@Transactional
	public void testRegisterUnicarb2237() throws SparqlException, ConvertException
	{

		String sequence = "RES\\n"
				+ "1b:o-dglc-HEX-0:0|1:aldi\\n"
				+ "2s:n-acetyl\\n"
				+ "3b:b-dglc-HEX-1:5\\n"
				+ "4s:n-acetyl\\n"
				+ "5b:a-lgal-HEX-1:5|6:d\\n"
				+ "LIN\\n"
				+ "1:1d(2+1)2n\\n"
				+ "2:1o(4+1)3d\\n"
				+ "3:3d(2+1)4n\\n"
				+ "4:1o(6+1)5d\\n";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setId("G92195EH");
//		glycanProcedure.setContributorId("5854");
		String result = glycanProcedure.register(sequence, "2237");

		Assert.assertNotNull(result);
		
	}
	
	@Test
	@Transactional
	public void testRegisterWithCRLF() throws SparqlException, ConvertException
	{

		String sequence = "RES\r\n"
				+ "1b:x-dglc-HEX-1:5\r\n"
				+ "2b:x-dman-HEX-1:5\r\n"
				+ "3b:x-dglc-HEX-1:5\r\n"
				+ "4s:n-acetyl\r\n"
				+ "LIN\r\n"
				+ "1:1o(-1+1)2d\r\n"
				+ "2:2o(-1+1)3d\r\n"
				+ "3:3d(2+1)4n";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setId("G92195EH");
//		glycanProcedure.setContributorId("5854");
		String result = glycanProcedure.register(sequence, "999");

		Assert.assertNotNull(result);
		
	}
		@Test
	@Transactional
	public void testRegisterUnicarbDB() throws SparqlException, ConvertException
	{

		String sequence = "RES\n"
				+ "1b:o-dglc-HEX-0:0|1:aldi\n"
				+ "2s:n-acetyl\n"
				+ "3b:b-dglc-HEX-1:5\n"
				+ "4s:n-acetyl\n"
				+ "5b:b-dman-HEX-1:5\n"
				+ "6b:a-dman-HEX-1:5\n"
				+ "7b:b-dglc-HEX-1:5\n"
				+ "8s:n-acetyl\n"
				+ "9b:b-dgal-HEX-1:5\n"
				+ "10b:a-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n"
				+ "11s:n-acetyl\n"
				+ "12b:a-dman-HEX-1:5\n"
				+ "13b:a-dman-HEX-1:5\n"
				+ "14b:a-dman-HEX-1:5\n"
				+ "LIN\n"
				+ "1:1d(2+1)2n\n"
				+ "2:1o(4+1)3d\n"
				+ "3:3d(2+1)4n\n"
				+ "4:3o(4+1)5d\n"
				+ "5:5o(3+1)6d\n"
				+ "6:6o(2+1)7d\n"
				+ "7:7d(2+1)8n\n"
				+ "8:7o(4+1)9d\n"
				+ "9:9o(6+2)10d\n"
				+ "10:10d(5+1)11n\n"
				+ "11:5o(6+1)12d\n"
				+ "12:12o(3+1)13d\n"
				+ "13:12o(6+1)14d\n";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setId("G92195EH");
//		glycanProcedure.setContributorId("5854");
		String result = glycanProcedure.register(sequence, "999");

		Assert.assertNotNull(result);
		
	}
	




	
}