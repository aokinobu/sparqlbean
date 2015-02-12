package org.glycoinfo.batch.glyconvert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glycoinfo.conversion.GlyConvert;
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
	public SparqlEntity process(final SparqlEntity sparqlEntity) throws Exception {
		
		// get the sequence
		String sequence = sparqlEntity.getValue(ConvertSelectSparql.Sequence);
		
		// convert the sequence
		GlyConvert converter = getGlyConvert();
		converter.setFromSequence(sequence);
		String convertedSeq = converter.convert();

		// return
		logger.debug("Converting (" + sequence + ") into (" + convertedSeq + ")");
		sparqlEntity.setValue(ConvertInsertSparql.ConvertedSequence, convertedSeq);

		return sparqlEntity;
	}
}