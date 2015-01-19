package org.glycoinfo.batch.convert.wurcs;

import org.glycoinfo.batch.TripleStoreItemReader;
import org.glycoinfo.batch.TripleStoreItemWriter;
import org.glycoinfo.batch.convert.ConvertTriple;
import org.glycoinfo.batch.convert.kcf.ConvertTripleProcessor;
import org.glycoinfo.batch.convert.kcf.ConvertTripleStoreConverter;
import org.glycoinfo.batch.convert.kcf.KcfConvertTriple;
import org.glycoinfo.ts.utils.TripleStoreConverter;
import org.glycoinfo.ts.utils.TripleStoreProperties;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.hp.hpl.jena.graph.impl.TripleStore;

@ComponentScan(basePackages = ("org.glycoinfo"))
@EnableAutoConfiguration
@Configuration
// @EnableConfigurationProperties(TripleStoreProperties.class)
// @SpringApplicationConfiguration(classes = TripleStoreProperties.class)
public class WurcsConvertTripleBatch {

	private int pageSize = 10;

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(
				WurcsConvertTripleBatch.class, args);
	}
	
	@Bean
	WurcsConvertTriple getConvertTriple() {
		return new WurcsConvertTriple();
	}
	
	@Bean
	public ItemReader<WurcsConvertTriple> reader() {
		TripleStoreItemReader<WurcsConvertTriple> reader = new TripleStoreItemReader<WurcsConvertTriple>();
		reader.setTripleBean(getConvertTriple());
		reader.setConverter(getConvertTriple());
		reader.setPageSize(pageSize);

		/*
		 * 
		 * SELECT DISTINCT ?s ?AccessionNumber ?Seq ?type WHERE { ?s a
		 * glycan:saccharide . ?s glytoucan:has_primary_id ?AccessionNumber . ?s
		 * glycan:has_glycosequence ?gseq . ?gseq glycan:has_sequence ?Seq .
		 * ?gseq glycan:in_carbohydrate_format glycan:carbohydrate_format_kcf }
		 */
		return reader;
	}

	@Bean
	public ItemWriter<WurcsConvertTriple> writer() {
		return new TripleStoreItemWriter<WurcsConvertTriple>();
	}

	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job importUserJob(JobBuilderFactory jobs, Step s1) {
		return jobs.get("ConvertTriple").incrementer(new RunIdIncrementer())
				.flow(s1).end().build();
	}

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory,
			ItemReader<WurcsConvertTriple> reader,
			ItemWriter<WurcsConvertTriple> writer,
			ItemProcessor<WurcsConvertTriple, WurcsConvertTriple> processor) {
		return stepBuilderFactory.get("step1")
				.<WurcsConvertTriple, WurcsConvertTriple> chunk(10).reader(reader)
				.processor(processor).writer(writer).build();
	}
	
    @Bean
    public ItemProcessor<WurcsConvertTriple, WurcsConvertTriple> processor() {
        return new WurcsConvertTripleProcessor();
    }

}