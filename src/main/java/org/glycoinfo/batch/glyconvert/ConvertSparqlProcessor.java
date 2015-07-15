package org.glycoinfo.batch.glyconvert;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glycoinfo.batch.glyconvert.wurcs.WurcsConvertSelectSparql;
import org.glycoinfo.conversion.GlyConvert;
import org.glycoinfo.conversion.error.ConvertException;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

public class ConvertSparqlProcessor implements
		ItemProcessor<SparqlEntity, SparqlEntity> {
	protected Log logger = LogFactory.getLog(getClass());
	
	@Autowired(required=true)
	GlyConvert glyConvert;

	public GlyConvert getGlyConvert() {
		return glyConvert;
	}

	public void setGlyConvert(GlyConvert glyConvert) {
		this.glyConvert = glyConvert;
	}

	@Override
	public SparqlEntity process(final SparqlEntity sparqlEntity) throws SparqlException, ConvertException {
		
		// get the sequence
		String sequence = sparqlEntity.getValue(WurcsConvertSelectSparql.Sequence);
		
		// convert the sequence
		GlyConvert converter = getGlyConvert();
		converter.setFromSequence(sequence);
		String convertedSeq = null;
		try {
			convertedSeq = converter.convert();
		} catch (ConvertException e) {
			e.printStackTrace();
			logger.error("error processing:>" + sequence + "<");
			if (e.getMessage() != null && e.getMessage().length() > 0)
				convertedSeq=e.getMessage();
			else
				throw e;
		}

		// return
		logger.debug("Converting (" + sequence + ") into (" + convertedSeq + ")");
		
		String encoded;
		try {
			encoded = URLEncoder.encode(convertedSeq, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new ConvertException(e);
		}
		
		logger.debug("Encoded (" + convertedSeq + ") into (" + encoded + ")");
		
		sparqlEntity.setValue(ConvertInsertSparql.ConvertedSequence, encoded);

		return sparqlEntity;
	}
}