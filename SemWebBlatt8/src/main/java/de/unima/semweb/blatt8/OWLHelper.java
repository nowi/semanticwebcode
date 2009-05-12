package de.unima.semweb.blatt8;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.InferredAxiomGenerator;
import org.semanticweb.owl.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owl.util.InferredOntologyGenerator;
import org.semanticweb.owl.util.InferredAxiomGeneratorException;
import org.mindswap.pellet.owlapi.Reasoner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ArrayList;
import java.net.URI;
import java.io.File;
import java.io.IOException;

/**
 * User: nowi
 * Date: 12.05.2009
 * Time: 17:11:39
 */
public abstract class OWLHelper {

    public static OWLReasoner createOWLReasoner(OWLOntologyManager man) {
        try {
            // The following code is a little overly complicated.  The reason for using
            // reflection to create an instance of pellet is so that there is no compile time
            // dependency (since the pellet libraries aren't contained in the OWL API repository).
            // Normally, one would simply create an instance using the following incantation:
            //
            //     OWLReasoner reasoner = new Reasoner()
            //
            // Where the full class name for Reasoner is org.mindswap.pellet.owlapi.Reasoner
            //
            // Pellet requires the Pellet libraries  (pellet.jar, aterm-java-x.x.jar) and the
            // XSD libraries that are bundled with pellet: xsdlib.jar and relaxngDatatype.jar
            String reasonerClassName = "org.mindswap.pellet.owlapi.Reasoner";
            Class reasonerClass = Class.forName(reasonerClassName);
            Constructor<OWLReasoner> con = reasonerClass.getConstructor(OWLOntologyManager.class);
            return con.newInstance(man);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }


    public static void dumpInferredOntology(OWLOntologyManager owlOntologyManager, Reasoner reasoner,OWLOntology ontology) {
        try {

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
            OWLOntology infOnt = owlOntologyManager.createOntology(URI.create(ontology.getURI() + "_inferred"));

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
