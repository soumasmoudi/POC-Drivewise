package DriveWiseProto;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLEntityRemover;

/**
 *
 * @author SMasmoudi
 */

public class newInformationArrival implements Runnable {

    OWLOntology ontology;
    OWLReasoner reasoner;
    OWLDataFactory factory;
    OWLOntologyManager manager;
    OWLEntityRemover remover;
    String base="/http://spider.sigappfr.org/uCSNdoc/ucsn.ttl";
    PrefixManager pm = new DefaultPrefixManager("http://spider.sigappfr.org/uCSNdoc/ucsn.ttl#");
    PrefixManager sosaPM = new DefaultPrefixManager("http://www.w3.org/ns/sosa/");
    File file = new File("./DriveWiseOntologies/drivewise.owl");
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
    Date infoAddedDate;

    newInformationArrival(OWLOntology onto, OWLOntologyManager man, OWLReasoner res, OWLDataFactory fact){
        this.ontology=onto;
        this.reasoner=res;
        this.factory=fact;
        this.manager=man;
    }

    public void run(){

        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(newInformationArrival.class.getName()).log(Level.SEVERE, null, ex);
        }

        //-Information related to Context 1
        //----------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println("Context 1: Bob is in the vehicle which shares the speed in real time \n");
        infoAddedDate=new Date(System.currentTimeMillis());
        System.out.println(sdf.format(infoAddedDate));

        //addNewIndividual("Mall", "Environment", manager, ontology, factory,pm);
        createTwoIndividualsRelation("isLocatedInEnv", "Bob", "Vehicle1", manager, ontology, factory, pm);


        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(newInformationArrival.class.getName()).log(Level.SEVERE, null, ex);
        }


        //Information related to Context 2
        //----------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println("Context 2: Bob is driving in a highway and shares his location \n");
        infoAddedDate=new Date(System.currentTimeMillis());
        System.out.println(sdf.format(infoAddedDate));


        createTwoIndividualsRelation("isLocatedInEnv", "Vehicle1", "Highway", manager, ontology, factory, pm);


        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(newInformationArrival.class.getName()).log(Level.SEVERE, null, ex);
        }



        //Information related to Context 1&2bis --> remove its corresponding individuals
        //----------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println("Context 1&2end: Bob leaves the vehicle \n");
        infoAddedDate=new Date(System.currentTimeMillis());
        System.out.println(sdf.format(infoAddedDate));


        OWLIndividual Bob = factory.getOWLNamedIndividual(":Bob", pm);
        OWLIndividual vehicle = factory.getOWLNamedIndividual(":Vehicle1", pm);
        OWLObjectProperty isLocatedInEnv = factory.getOWLObjectProperty(":isLocatedInEnv", pm);

        OWLIndividual Highway = factory.getOWLNamedIndividual(":Highway", pm);

        // Create the axiom to delete
        OWLObjectPropertyAssertionAxiom BobinVehicleToRemove = factory.getOWLObjectPropertyAssertionAxiom(isLocatedInEnv, Bob, vehicle);
        OWLObjectPropertyAssertionAxiom VehicleinHighwayToRemove = factory.getOWLObjectPropertyAssertionAxiom(isLocatedInEnv, vehicle, Highway);

        // Delete the axiome from the ontology
        manager.removeAxiom(ontology, BobinVehicleToRemove);
        manager.removeAxiom(ontology, VehicleinHighwayToRemove);

        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(newInformationArrival.class.getName()).log(Level.SEVERE, null, ex);
        }


        //Information related to Context 3
        //----------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println("Context 3: Bob visits a fitness center where the parking hosts an ANPR system \n");
        infoAddedDate=new Date(System.currentTimeMillis());
        System.out.println(sdf.format(infoAddedDate));


        createTwoIndividualsRelation("isLocatedInEnv", "Bob", "FitnessCenter", manager, ontology, factory, pm);


        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(newInformationArrival.class.getName()).log(Level.SEVERE, null, ex);
        }


        //Information related to Context 4
        //----------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println("Context 4: Bob is in the fitness center and uses his smartwatch  \n");
        infoAddedDate=new Date(System.currentTimeMillis());
        System.out.println(sdf.format(infoAddedDate));

        addNewIndividual("HeartRate", "SensedInformation", manager, ontology, factory,pm);

        createTwoIndividualsRelation("hasSensingStatus", "HeartRate", "SensingStatHEART", manager, ontology, factory, pm);

        //isHostedBy has a Sosa Prefix, so it has different Prefix Manager. Therefore, the corresponding relation is created directly here:
        //----------------------------------------------------------
        OWLIndividual individual1 = factory.getOWLNamedIndividual(":HeartRate",pm);
        OWLIndividual individual2 = factory.getOWLNamedIndividual(":PulseSensor",pm);
        OWLObjectProperty relation = factory.getOWLObjectProperty(":isObservedBy",sosaPM);
        OWLObjectPropertyAssertionAxiom assertion =factory.getOWLObjectPropertyAssertionAxiom(relation, individual1, individual2);
        manager.addAxiom(ontology, assertion);
        //----------------------------------------------------------
        //----------------------------------------------------------------------------------------------------------------------------------------------

        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(newInformationArrival.class.getName()).log(Level.SEVERE, null, ex);
        }




        //Information related to Context 3&4End --> remove its corresponding individuals
        //----------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println("Context 3&4End: Bob leaves the fitness center \n");
        infoAddedDate=new Date(System.currentTimeMillis());
        System.out.println(sdf.format(infoAddedDate));



        OWLIndividual fitnessCenter = factory.getOWLNamedIndividual(":FitnessCenter", pm);

        IndividualToDelete("HeartRate", manager, ontology, factory, pm);


        // Create the axiom to delete
        OWLObjectPropertyAssertionAxiom BobInFitnessToRemove = factory.getOWLObjectPropertyAssertionAxiom(isLocatedInEnv, Bob, fitnessCenter);

        // Delete the axiome from the ontology
        manager.removeAxiom(ontology, BobInFitnessToRemove);




        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(newInformationArrival.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Information related to Context 5
        //----------------------------------------------------------------------------------------------------------------------------------------------
        System.out.println("Context 5: Bob is in the vehicle and shares his attentive probability \n");
        infoAddedDate=new Date(System.currentTimeMillis());
        System.out.println(sdf.format(infoAddedDate));


        createTwoIndividualsRelation("describesEntity", "AttentiveProb", "Bob", manager, ontology, factory, pm);




        try {
            Thread.sleep(7000);
        } catch (InterruptedException ex) {
            Logger.getLogger(newInformationArrival.class.getName()).log(Level.SEVERE, null, ex);
        }



        //Information related to Context 5End --> remove its corresponding individuals
        //------------------------------------------------------------------------------------------------------------
        System.out.println("Context 5End: Bob leaves the vehicle and shares no information  \n");
        infoAddedDate=new Date(System.currentTimeMillis());
        System.out.println(sdf.format(infoAddedDate));


        OWLIndividual AttentiveProb = factory.getOWLNamedIndividual(":AttentiveProb", pm);
        OWLObjectProperty describesEntity = factory.getOWLObjectProperty(":describesEntity", pm);



        // Create the axiom to delete
        OWLObjectPropertyAssertionAxiom BobSahresAPToRemove = factory.getOWLObjectPropertyAssertionAxiom(describesEntity, AttentiveProb, Bob);

        // Delete the axiome from the ontology
        manager.removeAxiom(ontology, BobSahresAPToRemove);



        //----------------------------------------------------------
        //----------------------------------------------------------------------------------------------------------------------------------------------

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(newInformationArrival.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    //add individual to the related class
    public void addNewIndividual(String ind, String cls, OWLOntologyManager man, OWLOntology ont, OWLDataFactory fact, PrefixManager prefixMan){
        //create environment individual and get the existing classes and individuals
        OWLIndividual individual = fact.getOWLNamedIndividual(IRI.create(base + "#"+ind));
        //get existing class
        OWLClass correspClass = fact.getOWLClass(":"+cls,prefixMan);
        //Add indivual as individual of the class envClass
        OWLClassAssertionAxiom classAssertion =fact.getOWLClassAssertionAxiom(correspClass, individual);
        man.addAxiom(ont, classAssertion);
    }

    //add individual to the related class
    public void createTwoIndividualsRelation(String relat, String indiv1,String indiv2, OWLOntologyManager man, OWLOntology ont, OWLDataFactory fact, PrefixManager prefixMan){
        //get existing individuals
        OWLIndividual individual1 = fact.getOWLNamedIndividual(":"+indiv1,prefixMan);
        OWLIndividual individual2 = fact.getOWLNamedIndividual(":"+indiv2,prefixMan);

        //Get existing object properties from the ontology
        OWLObjectProperty relation = fact.getOWLObjectProperty(":"+relat,prefixMan);

        //create the relation between the two individuals (individual1 has relation with individual2)
        OWLObjectPropertyAssertionAxiom assertion =fact.getOWLObjectPropertyAssertionAxiom(relation, individual1, individual2);
        //Add the relations to the local ontology file
        man.addAxiom(ont, assertion);
    }

    public void IndividualToDelete(String indiv, OWLOntologyManager man, OWLOntology ont, OWLDataFactory fact, PrefixManager prefixMan){
        remover = new OWLEntityRemover(Collections.singleton(ont));
        OWLNamedIndividual indivToDelete = fact.getOWLNamedIndividual(":"+indiv,prefixMan);
        indivToDelete.accept(remover);
        man.applyChanges(remover.getChanges());
        remover.reset();
    }
}
