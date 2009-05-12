package de.unima.semweb.blatt8;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Autowired;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.inference.OWLReasonerFactory;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.util.InferredAxiomGenerator;
import org.semanticweb.owl.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owl.util.InferredOntologyGenerator;
import org.semanticweb.owl.util.InferredAxiomGeneratorException;
import org.semanticweb.reasonerfactory.pellet.PelletReasonerFactory;
import org.mindswap.pellet.owlapi.Reasoner;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.io.File;
import java.io.IOException;

/**
 * User: nowi
 * Date: 11.05.2009
 * Time: 15:00:33
 */
public class SingleOWLOntologyManager {

    public Properties getProperties() {
        return properties;
    }



    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    Properties properties;


    public OWLOntology getOntology() {
        return ontology;
    }

    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }

    private OWLOntology ontology;

    public OWLOntologyManager getOwlOntologyManager() {
        return owlOntologyManager;
    }




    public Reasoner getReasoner() {
        return reasoner;
    }

    @Autowired
    public void setReasoner(Reasoner reasoner) {
        this.reasoner = reasoner;
    }

    Reasoner reasoner;



    @Autowired
    public void setOwlOntologyManager(OWLOntologyManager owlOntologyManager) {
        this.owlOntologyManager = owlOntologyManager;
    }


    OWLOntologyManager owlOntologyManager;

    @PostConstruct
    public void init() throws OWLOntologyCreationException {
        System.out.println("Loading owl ontology into manager");
        URI physicalURI = URI.create(properties.getProperty("ontology.path"));
        // Now getAllMatches the manager to load the ontology
        this.owlOntologyManager.loadOntologyFromPhysicalURI(physicalURI);
//        System.out.println("Loaded Ontologies :");
//        for (OWLOntology ontology : owlOntologyManager.getOntologies()) {
//            System.out.println(ontology.getClassAxioms());
//        }

        this.ontology = owlOntologyManager.getOntologies().iterator().next();


    }


    public void dumpInferredOntology() {
        try {

            System.out.println("Dumping inferred Ontology " + getOntology());
            reasoner.loadOntology(getOntology());
            reasoner.refresh();

            // To generate an inferred ontology we use implementations of inferred axiom generators
            // to generate the parts of the ontology we want (e.g. subclass axioms, equivalent classes
            // axioms, class assertion axiom etc. - see the org.semanticweb.owl.util package for more
            // implementations).
            // Set up our list of inferred axiom generators
            List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
            gens.add(new InferredSubClassAxiomGenerator());

            // Put the inferred axiomns into a fresh empty ontology - note that there
            // is nothing stopping us stuffing them back into the original asserted ontology
            // if we wanted to do this.
            OWLOntology infOnt = owlOntologyManager.createOntology(URI.create(getOntology().getURI() + "_inferred"));

            // Now get the inferred ontology generator to generate some inferred axioms
            // for us (into our fresh ontology).  We specify the reasoner that we want
            // to use and the inferred axiom generators that we want to use.
            InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
            iog.fillOntology(owlOntologyManager, infOnt);

            // Save the inferred ontology. (Replace the URI with one that is appropriate for your setup)
            File inferredOntFile = new File("inferredont.owl");
            inferredOntFile.createNewFile();



            owlOntologyManager.saveOntology(infOnt, inferredOntFile.toURI());
            System.out.println("Saved infered ontology to location "  + inferredOntFile.getAbsolutePath());

            }
        catch (InferredAxiomGeneratorException e) {
            e.printStackTrace();
        }
        catch (OWLOntologyChangeException e) {
            e.printStackTrace();
        }
        catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        catch (OWLReasonerException e) {
            e.printStackTrace();
        }
        catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


}
