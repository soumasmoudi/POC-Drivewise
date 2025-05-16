package DriveWiseProto;


import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.exceptions.SWRLBuiltInException;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import java.io.FileNotFoundException;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentSource;



public class ReasoningEngine {
    public static void main(String[] args) throws  OWLException, SWRLParseException, SWRLBuiltInException {
        try{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

            File file = new File("./DriveWiseOntologies/drivewise.owl");

            //if (!file.exists()) {
             //   throw new FileNotFoundException("Ontology file not found: " + file.getAbsolutePath());
            //}
            //Map the ontologies' local files
            AutoIRIMapper mapperThs = new AutoIRIMapper(new File(file.getParent()),true);
            manager.addIRIMapper(mapperThs);

            //Load the Drivewise ontology file
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);

            // Check if ontology was loaded correctly
            //if (ontology == null) {
             //   throw new OWLException("Ontology could not be loaded.");
            //}

            // Create a Pellet reasoner
            PelletReasoner reasoner = com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory.getInstance().createReasoner(ontology);
            //if (reasoner == null) {
               // throw new OWLException("Pellet reasoner could not be created.");
            //}
            // Create a SWRL rule engine
            SWRLRuleEngine ruleEngine = SWRLAPIFactory.createSWRLRuleEngine(ontology);


            //Define the list of SWRL Privacy Rules


            // Rule 1
            ruleEngine.createSWRLRule("Driver-attentive-probability-rule",
                        "ucsn:Driver(?dr) ^ ucsn:Vehicle(?v) ^ ucsn:hasVehicle(?dr, ?v)  ^" +
                            "ucsn:AttentiveProbability(?dap) ^" +
                            "ucsn:describesEntity(?dap, ?dr) ^ " +
                            "ucsn:hasSensingStatus(?dap, ?status) ^" +
                            "ucsn:DataConsumer(?d) ^ " +
                            "ucsn:isSharedWith(?status, ?d) ^ " +
                            "swrlx:createOWLThing(?p, 1) ^ swrlx:createOWLThing(?s, 1) " +
                            "-> " +
                            "ucsn:PrivacyRisk(?p) ^ ucsn:PrivacySensitiveInfo(?s) " +
                            "^ ucsn:hasDescription(?s, \"Risk of inferring the driving capabilities of the user\"^^rdf:PlainLiteral) " +
                            "^ ucsn:hasInference(?p, ?s) ^ ucsn:hasSensedInfo(?p, ?dap)"

            );


            // Rule 2.1
            ruleEngine.createSWRLRule("Fitness-Center-with-ANPR-rule",
                    "ucsn:Driver(?dr) ^ ucsn:Environment(?env) ^ ucsn:hasEnvDescription(?env, \"Fitness Center\"^^rdf:PlainLiteral) " +
                            "^ ssn:System(ucsn:ANPR) ^ sosa:isHostedBy(ucsn:ANPR, ?env) ^ ucsn:isLocatedInEnv(?dr, ?env)" +
                            "^ swrlx:createOWLThing(?p, 1) ^ swrlx:createOWLThing(?s, 1) " +
                            "-> ucsn:PrivacyRisk(?p) ^ ucsn:PrivacySensitiveInfo(?s) " +
                            "^ ucsn:hasDescription(?s, \"The risk of inferring the fitness habits of the user\"^^rdf:PlainLiteral) " +
                            "^ ucsn:hasInference(?p, ?s)"

            );



            // Rule 2.2
            ruleEngine.createSWRLRule("Fitness-Center-and-Smartwatch-rule",
                    "ucsn:Driver(?dr) ^ ucsn:Environment(?env) ^ ucsn:hasEnvDescription(?env, \"Fitness Center\"^^rdf:PlainLiteral) " +
                            "^ sosa:Platform(ucsn:SmartWatch) ^ ucsn:isAttachedToUser(ucsn:SmartWatch, ?dr)"+
                            "^ sosa:Sensor(ucsn:PulseSensor) ^ sosa:isHostedBy(?sens, ucsn:SmartWatch) ^  ucsn:SensedInformation(?sensinfo) ^ sosa:isObservedBy(?sensinfo, ucsn:PulseSensor) ^ ucsn:isLocatedInEnv(?dr, ?env)" +
                            "^ ucsn:hasSensingStatus(?sensinfo, ?status) ^" +
                            "ucsn:DataConsumer(?d) ^ ucsn:isSharedWith(?status, ?d) ^ ucsn:isSensedBy(?status, ucsn:PulseSensor)" +
                            "^ swrlx:createOWLThing(?p, 1) ^ swrlx:createOWLThing(?s, 1) " +
                            "-> ucsn:PrivacyRisk(?p) ^ ucsn:PrivacySensitiveInfo(?s) " +
                            "^ ucsn:hasDescription(?s, \"The risk of inferring the health status of the user\"^^rdf:PlainLiteral) " +
                            "^ ucsn:hasInference(?p, ?s)"

            );



            // Rule 3
            ruleEngine.createSWRLRule("Driving-behavior-related-rule",
                    "ucsn:Driver(?dr) ^ ucsn:Vehicle(?v) ^ ucsn:hasVehicle(?dr, ?v) ^ ucsn:isLocatedInEnv(?dr, ?v) ^ " +
                            "ucsn:VehicleSpeed(?sp) ^ ucsn:describesEntity(?sp, ?v) ^ ucsn:hasSensingStatus(?sp, ?status)  ^" +
                            "ucsn:DataConsumer(?d) ^ ucsn:isSharedWith(?status, ?d) ^" +
                            " swrlx:createOWLThing(?p, 1) ^ swrlx:createOWLThing(?s, 1) " +
                            "-> ucsn:PrivacyRisk(?p) ^ ucsn:PrivacySensitiveInfo(?s) " +
                            "^ ucsn:hasDescription(?s, \"The risk of inferring the driving behavior of the user\"^^rdf:PlainLiteral) " +
                            "^ ucsn:hasInference(?p, ?s)"

            );



            // Rule 4
            ruleEngine.createSWRLRule("Speeding-violation-related-rule",
                    "ucsn:Driver(?dr) ^ ucsn:Vehicle(?v) ^ ucsn:hasVehicle(?dr, ?v) ^ ucsn:Environment(?env) ^ ucsn:isLocatedInEnv(?v, ?env) ^  " +
                            "ucsn:hasEnvDescription(?env, \"Highway\"^^rdf:PlainLiteral) ^ ucsn:VehicleSpeed(?sp) ^ ucsn:describesEntity(?sp, ?v) ^ ucsn:hasSensingStatus(?sp, ?status)  ^" +
                            "ucsn:DataConsumer(?d) ^ ucsn:isSharedWith(?status, ?d) ^" +
                            " swrlx:createOWLThing(?p, 1) ^ swrlx:createOWLThing(?s, 1) " +
                            "-> ucsn:PrivacyRisk(?p) ^ ucsn:PrivacySensitiveInfo(?s) " +
                            "^ ucsn:hasDescription(?s, \"The risk of detecting speeding violations\"^^rdf:PlainLiteral) " +
                            "^ ucsn:hasInference(?p, ?s)"

            );


            //Create the remover to delete detected privacy risks before saving the ontology (we do not need them after notifying the user because we are reasoning in REAL-TIME)
            OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
            OWLDataFactory factory=manager.getOWLDataFactory();
            PrefixManager pm = new DefaultPrefixManager("http://spider.sigappfr.org/uCSNdoc/ucsn.ttl#");
            OWLClass PrivacyRisk,PrivacySensitiveInfo ;
            NodeSet<OWLNamedIndividual> individualsToDelete;

            //Launch the newInformationArrival thread for injecting new information to the data model
            newInformationArrival inf = new newInformationArrival(ontology,manager,reasoner,factory);
            Thread thread=new Thread(inf);
            thread.start();

            //Define variables to calculate the execution time
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
            Date executionDate;

            executionDate=new Date(System.currentTimeMillis());
            System.out.println("Date: "+ sdf.format(executionDate) +" ms");


            //create infinite loop to apply continuous reasoning on modeled OWL data
            while(true){
                executionDate=new Date(System.currentTimeMillis());
                System.out.println("Date: "+ sdf.format(executionDate) +" ms");


                // Measure the start time of the reasoning process
                long startTime = System.currentTimeMillis();

                //Run the inference rule engine to detect the involved Privacy Risks
                ruleEngine.infer();


                //Flush the reasoner to consider all ontology updates (inferred privacy risks)
                try{
                    reasoner.flush();

                }catch(Exception e){
                    System.out.println(e.getMessage());
                }


                // Measure the end time of the reasoning process
                long endTime = System.currentTimeMillis();

                // Calculate and display the execution time
                long executionTime = endTime - startTime;
                System.out.println("Reasoning execution time: " + executionTime + " ms");


                //Print the total number of detected risks
                IndividualsCounter(ontology,reasoner);

                //Print the corresponding privacy-sensitive information descriptions
                OWLDataProperty riskDesc = factory.getOWLDataProperty(":hasDescription", pm);
                printDataPropertyValue(ontology,reasoner,riskDesc);

                //Delete the detected risks and their corresponding sensitive information before saving updated in the ontology file
                //-----------------------------------------------------------------------
                PrivacyRisk = factory.getOWLClass("PrivacyRisk",pm);
                PrivacySensitiveInfo=factory.getOWLClass("PrivacySensitiveInfo",pm);
                individualsToDelete = reasoner.getInstances(PrivacyRisk, false);
                for(OWLNamedIndividual i : individualsToDelete.getFlattened())
                {
                    i.accept(remover);
                }
                manager.applyChanges(remover.getChanges());
                remover.reset();
                individualsToDelete = reasoner.getInstances(PrivacySensitiveInfo, false);
                for(OWLNamedIndividual i : individualsToDelete.getFlattened())
                {
                    i.accept(remover);
                }

                manager.applyChanges(remover.getChanges());
                remover.reset();


                //Flush the reasoner to take into consideration the ontology updates (i.e., deleted Risks' individuals)
                //before saving the updates to the ontology document file (i.e., only considers newly-received information)

                reasoner.flush();

                //Save ontology changes to the main ontology document file
                manager.saveOntology(ontology,IRI.create(file.toURI()));

            }
        }
        catch (ExceptionInInitializerError e) {
            // Capturer et afficher la cause de l'erreur
            Throwable cause = e.getCause();
            System.err.println("Initial error cause: " + (cause != null ? cause.getMessage() : "Unknown"));
            e.printStackTrace();
        } catch (Exception e) {
            // Capturer et afficher toute autre exception
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }



    //used for counting the number of detected risks for each iteration
    public static void IndividualsCounter(OWLOntology ontology, OWLReasoner reasoner){

        for (OWLClass c : ontology.getClassesInSignature()) {
            if(c.getIRI().getFragment().equals("PrivacyRisk"))
            {
                NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(c, false);
                int count=0;
                for (OWLNamedIndividual i : instances.getFlattened()) {
                    count++;
                }
                System.out.println("Number of detected privacy risks is: " + count + " Risks");
            }


        }
    }

    //used for printing the description of the inferred privacy risks
    public static void printDataPropertyValue(OWLOntology ontology, OWLReasoner reasoner, OWLDataProperty property)
    {
        for (OWLClass c : ontology.getClassesInSignature()) {
            assert c != null;
            if(c.getIRI().getFragment().equals("PrivacySensitiveInfo"))
            {

                NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(c, false);
                int k=1;
                for (OWLNamedIndividual i : instances.getFlattened()) {
                    assert i != null;
                    Set<OWLLiteral> riskDescription = reasoner.getDataPropertyValues(i, property);

                    for (OWLLiteral j : riskDescription) {
                        System.out.println("Risk "+k+":" + j.getLiteral()+"\n" );
                        k++;
                    }

                }
            }

        }
    }

}