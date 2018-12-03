import java.io.*;

import org.apache.jena.query.* ;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

public class MultipleQueries {

    static final String inputFilePoll  = "/home/alexis/Master/HALA/world_poll-france.ttl";
    static final String inputFileMeteo = "/home/alexis/Master/HALA/meteo2018.ttl";

    public static void main (String args[]) {
        Model modelPoll = ModelFactory.createDefaultModel();
        Model modelMeteo = ModelFactory.createDefaultModel();
        Model modelOutput = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open( inputFilePoll );
        if (in == null) {
            throw new IllegalArgumentException( "File: " + inputFilePoll + " not found");
        }

        InputStream in2 = FileManager.get().open( inputFileMeteo );
        if (in2 == null) {
            throw new IllegalArgumentException( "File: " + inputFileMeteo + " not found");
        }

        
        // read the RDF/XML file
        modelPoll.read(in, null, "Turtle");
        modelMeteo.read(in2, null, "Turtle");
        
        modelOutput.add(modelMeteo);
        modelOutput.add(modelPoll);



        String prefixes = "PREFIX dbos: <http://dbpedia.org/ontology/>"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + "PREFIX m3lite: <http://purl.org/iot/vocab/m3-lite/>"
                + "PREFIX gn: <http://www.geonames.org/ontology/>"
                + "PREFIX foaf: <http://xmlns.com/foaf/spec/>"
                + "PREFIX dbo: <http://dbpedia.org/ontology#>"
                + "PREFIX geo:  <https://www.w3.org/2003/01/geo/wgs84_pos#>"
                + "PREFIX locah:  <http://data.archiveshub.ac.uk/def/>"
               	+ "PREFIX moac:  <http://observedchange.com/moac/ns#>"
               	+ "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>"
               	+ "PREFIX bio:  <http://purl.org/vocab/bio/0.1/>"
           		+ "PREFIX rsctx:  <http://softeng.polito.it/rsctx#>"
       			+ "PREFIX dbpac:  <http://dbpedia.org/page/Category:>"
                + "PREFIX atd:  <https://data.nasa.gov/ontologies/atmonto/data#>"
                + "PREFIX ex:  <http://ex.org/meteo#>";

        String queryString = prefixes + "Select ?ville ?year ?month ?day ?temperature ?valeur WHERE {"
                + "?x dbo:city ?ville;"
                + "dbo:date ?date;"
                + "dbo:temperature ?temp."
                + "?y dbos:city ?ville_poll;"
                + "rdf:value ?valeur;"
                + "dbos:date ?date_poll."
                + "FILTER(regex(ucase(?ville),ucase(?ville_poll)))"
                + "FILTER(?year = ?year_poll && ?month = ?month_poll && ?day = ?day_poll)"
				+ "BIND((<http://www.w3.org/2001/XMLSchema#decimal>(?temp) - 273.15) AS ?temperature)"
                + "BIND(year(?date_poll) as ?year_poll)"
                + "BIND(month(?date_poll) as ?month_poll)"
                + "BIND(day(?date_poll) as ?day_poll)"
                + "BIND(year(?date) as ?year)"
                + "BIND(month(?date) as ?month)"
                + "BIND(day(?date) as ?day)"
                + "}"
                + "LIMIT 100000";
        
        String queryString2 = prefixes + "";

        Query query = QueryFactory.create(queryString) ;
    	try (QueryExecution qexec = QueryExecutionFactory.create(query, modelOutput)) {
    		ResultSet results = qexec.execSelect() ;
    		ResultSetFormatter.out(System.out, results, query) ;
	    }
    }
}
