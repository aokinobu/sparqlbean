package org.glycoinfo.rdf;

import java.util.ArrayList;
import java.util.List;

import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.dao.SparqlEntityFactory;

/**
 * 
 * @author aoki
 *
 */
public class InsertSparqlBean implements InsertSparql {


	protected String insert, prefix, where, graph, using;
	protected boolean constant = true;

	String graphbase;

	StringBuffer sparql;
	SparqlEntity sparqlEntity = new SparqlEntity();
	ArrayList<InsertSparql> related;

	public InsertSparqlBean(String sparql) {
		this.sparql = new StringBuffer(sparql);
	}

	public InsertSparqlBean() {
	}

	public String getInsert() throws SparqlException {
		return insert;
	}

	public void setInsert(String insert) {
		this.insert = insert;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}

	public String getGraphBase() {
		return graphbase;
	}

	/**
	 * graph base to set the graph to insert into. 
	 * example: http://glytoucan.org/rdf/demo/0.7
	 * @param base
	 */
	public void setGraphBase(String base) {
		this.graphbase = base;
	}

	
	public void setSparql(String sparql) {
		this.sparql = new StringBuffer(sparql);
	}

	@Override
	public void setSparqlEntity(SparqlEntity sparqlentity) {
		this.sparql = null;
		this.sparqlEntity = sparqlentity;
		
//		SparqlEntityFactory.set(sparqlentity);
	}

	@Override
	public SparqlEntity getSparqlEntity() {
		return this.sparqlEntity;
	}

	public String getUsing() {
		return using;
	}

	public void setUsing(String using) {
		this.using = using;
	}

	/**
	 * 
	 * prefix + "\n" + "INSERT\n" + "{ GRAPH <"+graph+"> {\n" + insert + "  }\n"
	 * + "}\n" + using + "WHERE {\n" + where + "}"
	 * @throws SparqlException 
	 * 
	 * @see org.glycoinfo.rdf.InsertSparql#getSparql()
	 */
	@Override
	public String getSparql() throws SparqlException {
		if (this.sparql == null) {
			StringBuffer sparqlbuf = new StringBuffer();
			sparqlbuf.append(getPrefix() != null ? getPrefix() : "");
			if (getFormat().equals(InsertSparql.SPARQL)) {
				sparqlbuf.append("INSERT ");
				if (isConstant())
					sparqlbuf.append(" DATA \n");
					
				sparqlbuf.append(getGraph() != null ? "{ GRAPH <" + getGraph() + ">\n" : "");
				// sparqlbuf.append(getUsing());
				sparqlbuf.append("{ " + getInsert() + " }\n");
				sparqlbuf.append(getGraph() != null ? "}\n" : "");
				sparqlbuf.append(getUsing() != null ? getUsing() + "\n" : "");
				sparqlbuf.append(getWhere() != null ? "WHERE {" + getWhere() + "}\n" : "");
			} else {
				sparqlbuf.append(getInsert());
			}
			return sparqlbuf.toString();
		} else {
			return this.sparql.toString();
		}
	}

	@Override
	public List<InsertSparql> getRelated() {
		return related;
	}

	@Override
	public void addRelated(List<InsertSparql> related) {
		related.addAll(related);
	}

	@Override
	public String toString() {
		try {
			return getSparql();
		} catch (SparqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public String getFormat() {
		return InsertSparql.SPARQL;
	}
	
	public boolean isConstant() {
		return constant;
	}

	public void setConstant(boolean constant) {
		this.constant = constant;
	}

}