package de.unima.semweb.blatt8



import collection.Set
import java.lang.reflect.Constructor
import java.net.URI
import java.util.{Properties, ArrayList, Set, Collections}
import javax.annotation.{Resource, PostConstruct}
import org.mindswap.pellet.owlapi.Reasoner
import org.semanticweb.owl.apibinding.OWLManager
import org.semanticweb.owl.inference.{OWLReasoner, OWLReasonerAdapter}
import org.semanticweb.owl.model._
import org.springframework.beans.factory.annotation.{Autowired, Required, Autowire}
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.stereotype.{Service, Component}
import reflect.BeanProperty

import org.scala_tools.javautils.Imports._


@Service
class ReasoningServiceImpl {

  @BeanProperty
  var reasoner: Reasoner = null


  @BeanProperty
  var properties: Properties = null


  @BeanProperty
  var owlOntologyManager : OWLOntologyManager = null


  @BeanProperty
  var wordnetService: WordNetServiceImpl = null


  @BeanProperty
  var ontology : OWLOntology = null





  def init {
    // load ontology path from properties
    println("Loading owl ontology into manager");
    val physicalURI = URI.create(properties.getProperty("ontology.path"));

    // create ontology manager
    owlOntologyManager = OWLManager.createOWLOntologyManager();

    owlOntologyManager.loadOntologyFromPhysicalURI(physicalURI);
    ontology = owlOntologyManager.getOntologies().iterator().next();

    
    reasoner =  OWLHelper.createOWLReasoner(owlOntologyManager).asInstanceOf[Reasoner]


    println("Initializing Reasoner : %s ".format(reasoner))
    reasoner.loadOntology(ontology)
    reasoner.refresh()

  }



  def findConcepts(searchString : String) : Iterable[OWLClass] =

    {
      for (foundConcept <- ontology.getReferencedClasses().asScala.filter((owlClass: OWLClass) => owlClass.getURI().getFragment().contains(searchString)))
      yield (foundConcept)

    }



  def getOWLClass(fragment : String) : OWLClass  = {
    val uriString : String = ontology.getURI() +"#" + fragment
    println(uriString)
    owlOntologyManager.getOWLDataFactory().getOWLClass(URI.create(uriString));
  }

  def haveHypernymRelation(hypernym : OWLClass,hyponym : OWLClass) : Boolean = {
    (wordnetService.isHypernymRelation(hypernym.getURI().getFragment(),hyponym.getURI().getFragment())).asInstanceOf[Boolean]
  }


  // get all concepts in the ontoli
  def hypernymAdditionAxioms : Iterable[AnyRef] = {
    // for all concepts in the ontology
    val addAxioms  = new ArrayList[AnyRef]
    for(concept1 : OWLClass <- ontology.getReferencedClasses();concept2 : OWLClass <- ontology.getReferencedClasses())
          if(haveHypernymRelation(concept1,concept2)) {
            addAxioms.add(new AddAxiom(ontology, owlOntologyManager.getOWLDataFactory().getOWLSubClassAxiom(concept2,concept1)))
            println("Adding new Axiom : %s subclassOf %s".format(concept2,concept1))
          }


    return addAxioms.asScala    


  }


  def dumpInferredOntology {
    println("Dumping infered ontology : %s using reasoner %s".format(ontology,reasoner))
    OWLHelper.dumpInferredOntology(owlOntologyManager,reasoner,ontology)
  }




  def createHasPartEngineAxiom(individual : OWLIndividual) : AddAxiom = {
    val engineIndividual : OWLIndividual = owlOntologyManager.getOWLDataFactory().getOWLIndividual(URI.create(ontology.getURI() +"#engine"))
    val partProperty : OWLObjectProperty = owlOntologyManager.getOWLDataFactory().getOWLObjectProperty(URI.create(ontology.getURI() +"#has_part"))
    new AddAxiom(ontology, owlOntologyManager.getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(individual,partProperty,engineIndividual))
  }


  def addHasPartEnginePropertyAssertionAxioms : Iterable[AnyRef] = {
    // get all concepts
    val addAxioms  = new ArrayList[AnyRef]
    for(concept : OWLClass <- ontology.getReferencedClasses())
      // if hyponym of automobile
      if(wordnetService.isHypernymRelation("automobile",concept.getURI().getFragment()).asInstanceOf[Boolean] ) {
        // get direct Individuals of concept
        val individuals  = reasoner.getIndividuals(concept,true).asScala
        // the addaxioms
        for(individual <- individuals)addAxioms.add(createHasPartEngineAxiom(individual))

      }

    return addAxioms.asScala


  }

  def findSubConcepts(concept : OWLClass) : Iterable[OWLClass] =
    {
      // init reasoner
      for (foundConcept <- OWLReasonerAdapter.flattenSetOfSets(reasoner.getSubClasses(concept)).asScala)
      yield (foundConcept)

    }



  def haveRelation(concept1 : OWLClass, concept2 : OWLClass) : Boolean =
    haveSubsumptionRelation(concept1,concept2) || havePropertyRelation (concept1,concept2)

  def haveSubsumptionRelation(concept1 : OWLClass, concept2 : OWLClass) : Boolean =
      OWLReasonerAdapter.flattenSetOfSets(reasoner.getSubClasses(concept1)).contains(concept2) || OWLReasonerAdapter.flattenSetOfSets(reasoner.getSuperClasses(concept1)).contains(concept2)

  def havePropertyRelationDirected(concept1 : OWLClass, concept2 : OWLClass) : Boolean = {
     val ont : OWLOntology = ontology
     val restrictionVisitor : OWLPropertyAxiomVisitor = new OWLPropertyAxiomVisitor(Collections.singleton(ont));
        // In this case, restrictions are used as (anonymous) superclasses, so to get the restrictions on
        // margherita pizza we need to obtain the subclass axioms for margherita pizza.
        for( (ax :OWLSubClassAxiom) <- ont.getSubClassAxiomsForLHS(concept1).asScala) {
            val superCls : OWLDescription = ax.getSuperClass();
            // Ask our superclass to accept a visit from the RestrictionVisitor - if it is an
            // existential restiction then our restriction visitor will process it - if not our
            // visitor will ignore it
            superCls.accept(restrictionVisitor);
        }

      val restrictedConcepts  = restrictionVisitor.getRestrictedConcepts().asScala
      restrictedConcepts.contains(concept2)
  }

  def havePropertyRelation(concept1 : OWLClass, concept2 : OWLClass) : Boolean = {
     havePropertyRelationDirected(concept1,concept2) || havePropertyRelationDirected(concept2,concept1)
  }







}
