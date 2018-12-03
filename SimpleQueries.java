import java.io.*;

import org.apache.jena.query.* ;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

public class SimpleQueries {

    static final String inputFileName  = "/home/alexis/Master/HALA/meteo.ttl";
	
    public static void main (String args[]) {
    	Model model = ModelFactory.createDefaultModel();
    	
        InputStream in = FileManager.get().open( inputFileName );
        if (in == null) {
            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }
        
        // read the RDF/XML file
        model.read(in, null, "Turtle");
        
    	String queryString1 = "SELECT ?ville ?temperature\n"
	    		+ "WHERE {\n"
    			+ "?releve <http://www.w3.org/2000/01/rdf-schema#type> <http://ex.org/meteo#ReleveMeteo>;\n"
	    		+ "<http://dbpedia.org/ontology#temperature> ?temp;\n"
	    		+ "<http://dbpedia.org/ontology#city> ?ville\n"
	    		+ "BIND((<http://www.w3.org/2001/XMLSchema#decimal>(?temp) - 273.15) AS ?temperature)"
	    		+ "} ORDER BY DESC(?temperature)\n"
	    		+ "LIMIT 10\n";
    	
    	String queryString2 = "SELECT ?date ?vitessevent\n"
	    		+ "WHERE {\n"
    			+ "?releve <http://www.w3.org/2000/01/rdf-schema#type> <http://ex.org/meteo#ReleveMeteo>;\n"
    			+ "<http://purl.org/dc/elements/1.1/#date> ?date;\n"
	    		+ "<https://data.nasa.gov/ontologies/atmonto/data#surfaceWindSpeed> ?vitessevent;\n"
	    		+ "<http://dbpedia.org/ontology#city> ?ville\n"
	    		+ "FILTER(regex(?ville,\"PERPIGNAN\"))"
	    		+ "} LIMIT 10\n";
    	
    	Query query = QueryFactory.create(queryString1) ;
    	try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
    		ResultSet results = qexec.execSelect() ;
    		ResultSetFormatter.out(System.out, results, query) ;
	    }
    }
}
