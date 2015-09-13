package org.glycoinfo.rdf.glycan.wurcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glycoinfo.rdf.glycan.Saccharide;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * SelectSparql for retrieving various details about a Saccharide. 
 * Includes Mass, Date entered, Contributor, and both GlycoCT and WURCS Sequence Formats.
 * 
 * @author aoki
 *
 * This work is licensed under the Creative Commons Attribution 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/.
 *
 */
@Component
public class GlycoSequenceResourceEntryContributorSelectSparql extends GlycoSequenceSelectSparql {
	public static final String DateEntered = Saccharide.URI;
	public static final String Mass = "Mass";
	public static final String Contributor = "Contributor";

	public GlycoSequenceResourceEntryContributorSelectSparql(String sparql) {
		super(sparql);
	}

	public GlycoSequenceResourceEntryContributorSelectSparql() {
		super();
		this.select += "?" + DateEntered + "\n"
						+ "?" + Mass + "\n"
						+ "?" + Contributor + "\n"
						+ "?GlycoCTSequence" + "\n"
						+ "?DateRegistered" + "\n";
		
		this.where += "?" + SaccharideURI + " glycan:has_glycosequence ?GlycanSequenceGlycoCTURI .\n"
				+ "?GlycanSequenceGlycoCTURI glycan:in_carbohydrate_format glycan:carbohydrate_format_glycoct .\n"
				+ "?GlycanSequenceGlycoCTURI glycan:has_sequence ?GlycoCTSequence .\n"
				+ "?" + SaccharideURI + " glycan:has_resource_entry ?resource .\n"
				+ "?resource glycan:in_glycan_database glytoucan:database_glytoucan .\n"
				+ "?resource glytoucan:contributor ?contributorURI .\n"
				+ "?contributorURI foaf:name ?" + Contributor + " .\n"
				+ "?resource glytoucan:date_registered ?DateRegistered .\n"
				+ "OPTIONAL {\n"
				+ "?" + SaccharideURI + " glytoucan:has_derivatized_mass ?derivMass .\n"
				+ "?derivMass glytoucan:has_mass ?" + Mass + " .\n"
				+ "}\n"
				;
	}
	
	protected Log logger = LogFactory.getLog(getClass());

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.state(getPrefix() != null, "A prefix is required");
		Assert.state(getSelect() != null, "A select is required");
	}
}