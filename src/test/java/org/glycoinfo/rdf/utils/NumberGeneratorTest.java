package org.glycoinfo.rdf.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.context.annotation.Configuration;

@RunWith(BlockJUnit4ClassRunner.class)
@Configuration
public class NumberGeneratorTest {
	
	private static final Log logger = LogFactory.getLog(NumberGeneratorTest.class);
	
	@Test
	public void testHash() {
		Date dateVal = new Date(0);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		logger.debug(sdf.format(dateVal));
		logger.debug(NumberGenerator.generateHash("5858UnicarbDBGlytoucanPartner", sdf.format(dateVal)));
		logger.debug(NumberGenerator.generateHash("5852UnicarbDBGlytoucanPartner", sdf.format(dateVal)));
		logger.debug(NumberGenerator.generateHash("5853UnicarbDBGlytoucanPartner", sdf.format(dateVal)));
		logger.debug(NumberGenerator.generateHash("5858UnicarbDBGlytoucanPartner", sdf.format(dateVal)));
		logger.debug(NumberGenerator.generateHash("5852UnicarbDBGlytoucanPartner", sdf.format(dateVal)));
		logger.debug(NumberGenerator.generateHash("5853UnicarbDBGlytoucanPartner", sdf.format(dateVal)));
		try {
			logger.debug(URLEncoder.encode(NumberGenerator.generateHash("5858UnicarbDBGlytoucanPartner", sdf.format(dateVal)), "UTF-8"));
			logger.debug(URLEncoder.encode("$5$19700101010000$xeBxaHB8k6f.H6Y4t1Aoa6kSxGU90nr1XpXZ3rgTcr9", "UTF-8"));
			logger.debug(URLEncoder.encode(NumberGenerator.generateHash("5852UnicarbDBGlytoucanPartner", sdf.format(dateVal)), "UTF-8"));
			logger.debug(URLEncoder.encode("$5$19700101010000$KtaqOjyOLmtVNpA6YBeFpw7.u32eR9NT6/5OZIJHoJ5", "UTF-8"));
			// these don't work on different environments like jenkins.  is date(0) different per jvm/env/time settings?
//			Assert.assertEquals("JDUkMTk3MDAxMDEwOTAwMDAkWEM2MXJ1bmRCWTZGYmZ6R0dYbnJWSllndzJienFrZnJiTFJBbm9tYTZVLw==", NumberGenerator.generateHash("5858UnicarbDBGlytoucanPartner", sdf.format(dateVal)));
//			Assert.assertEquals("JDUkMTk3MDAxMDEwOTAwMDAkaHJXRkV0S0s0dmtPMmxzR1dERGR2Q1RFMlRRY3o2MERwTFIzakZKQkpiNw==", NumberGenerator.generateHash("5852UnicarbDBGlytoucanPartner", sdf.format(dateVal)));
			Assert.assertEquals("6337f441f8b4387d865ebc3415e89fb99b29c43a45f4b24ba76cc82e0713c698", NumberGenerator.generateHash("5852UnicarbDBGlytoucanPartner", "testSalt"));
			
			} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void anotherMethod() {
	  String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex("aokinobu@gmail.com");
	  
	  Assert.assertEquals("14e1d868cf50557143032041eef95cc7271b8c3a0bdc5a52fb849cdf29ef4aff", sha256hex);
	  logger.debug(sha256hex);
	}
}
