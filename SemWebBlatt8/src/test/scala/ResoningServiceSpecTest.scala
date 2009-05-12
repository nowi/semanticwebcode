import com.jteigen.scalatest.JUnit4Runner
import de.unima.semweb.blatt8.{WordNetServiceImpl, SingleOWLOntologyManager, ReasoningServiceImpl}
import java.net.URI
import java.util.Properties
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.semanticweb.owl.model.{OWLClass, OWLOntologyManager}
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.beans.factory.BeanFactory

@RunWith(classOf[JUnit4Runner])
class ResoningServiceSpecTest extends Spec with ShouldMatchers {
  describe("A ResoningService Implementation") {

    it("List all concepts with ID containing a given string") {
      val context = new ClassPathXmlApplicationContext("file:applicationContext.xml")
      // an ApplicationContext is also a BeanFactory (via inheritance)
      val reasoningServiceImpl : ReasoningServiceImpl = context.getBean("reasoningServiceImpl").asInstanceOf[ReasoningServiceImpl]
      val properties : Properties = context.getBean("appPropertiesPizzaOntology").asInstanceOf[Properties]
      reasoningServiceImpl.properties = properties
      reasoningServiceImpl.init

      val owlConceptCountry = reasoningServiceImpl.getOWLClass("Country")
      val foundConcepts = reasoningServiceImpl.findConcepts("Country")
      println(foundConcepts)
      foundConcepts should contain (owlConceptCountry)
    }

    it("List all concepts that are stated or infered subconcepts of a given concept") {
      val context = new ClassPathXmlApplicationContext("file:applicationContext.xml")
      // an ApplicationContext is also a BeanFactory (via inheritance)
      val reasoningServiceImpl : ReasoningServiceImpl = context.getBean("reasoningServiceImpl").asInstanceOf[ReasoningServiceImpl]
      val properties : Properties = context.getBean("appPropertiesPizzaOntology").asInstanceOf[Properties]
      reasoningServiceImpl.properties = properties
      reasoningServiceImpl.init


      // the pizza base concept
      val pizzaBase = reasoningServiceImpl.getOWLClass("PizzaBase");
      // should contain those concepts
      val subConceptsOfPizzaBase : Iterable[OWLClass] = for(owlConceptName <- Array("DeepPanBase","ThinAndCrispyBase"))
        yield reasoningServiceImpl.getOWLClass(owlConceptName)
      val foundConcepts  = reasoningServiceImpl.findSubConcepts(pizzaBase)
      foundConcepts.foreach(subConceptsOfPizzaBase should contain (_))

    }


    it("Test for two given concepts of a given ontology, if they are subconcepts of each other or related by a property") {
      val context = new ClassPathXmlApplicationContext("file:applicationContext.xml")
      // an ApplicationContext is also a BeanFactory (via inheritance)
      val reasoningServiceImpl : ReasoningServiceImpl = context.getBean("reasoningServiceImpl").asInstanceOf[ReasoningServiceImpl]
      val properties : Properties = context.getBean("appPropertiesPizzaOntology").asInstanceOf[Properties]
      reasoningServiceImpl.properties = properties
      reasoningServiceImpl.init

      // the pizza base concept
      val pizzaBase = reasoningServiceImpl.getOWLClass("PizzaBase")
      val pizza = reasoningServiceImpl.getOWLClass("Pizza")
      val spicyPizza = reasoningServiceImpl.getOWLClass("SpicyPizza")
      val margehrita = reasoningServiceImpl.getOWLClass("Margherita")
      val deepPanBase = reasoningServiceImpl.getOWLClass("DeepPanBase")
      val mozarellaTopping = reasoningServiceImpl.getOWLClass("MozzarellaTopping")
      val hamTopping = reasoningServiceImpl.getOWLClass("HamTopping")

      reasoningServiceImpl.havePropertyRelation(pizza,pizzaBase) should be (true)
      reasoningServiceImpl.havePropertyRelation(margehrita,pizzaBase) should be (true)
      reasoningServiceImpl.havePropertyRelation(margehrita,mozarellaTopping) should be (true)
      reasoningServiceImpl.havePropertyRelation(margehrita,hamTopping) should be (false)


      // test subsumptions

      reasoningServiceImpl.haveSubsumptionRelation(pizza,pizzaBase) should be (false);
      reasoningServiceImpl.haveSubsumptionRelation(deepPanBase,pizzaBase) should be (true);
      reasoningServiceImpl.haveSubsumptionRelation(pizzaBase,deepPanBase) should be (true);


      // test both
      reasoningServiceImpl.haveRelation(pizza,pizzaBase) should be (true);

    }

    it("Compute the hierarchy of a given ontology, i.e. create a new ontology that contains all inferable direct concept subsumptions") {
      // load person onotlogy
      val context = new ClassPathXmlApplicationContext("file:applicationContext.xml")
      val reasoningServiceImpl : ReasoningServiceImpl = context.getBean("reasoningServiceImpl").asInstanceOf[ReasoningServiceImpl]
      val properties : Properties = context.getBean("appPropertiesPizzaOntology").asInstanceOf[Properties]
      reasoningServiceImpl.properties = properties
      reasoningServiceImpl.init
      reasoningServiceImpl.dumpInferredOntology


    }



    it("Add all subsumption axioms to a given ontology that are stated hypernym relations in wordnet") {
      // hypernyms: Y is a hypernym of X if every X is a (kind of) Y (canine is a hypernym of dog)     
      val context = new ClassPathXmlApplicationContext("file:applicationContext.xml")
      // an ApplicationContext is also a BeanFactory (via inheritance)
      val reasoningServiceImpl : ReasoningServiceImpl = context.getBean("reasoningServiceImpl").asInstanceOf[ReasoningServiceImpl]

      val properties : Properties = context.getBean("appPropertiesPersonOntology").asInstanceOf[Properties]
      reasoningServiceImpl.properties = properties
      reasoningServiceImpl.init

      val wordnetService : WordNetServiceImpl = context.getBean("wordNetService").asInstanceOf[WordNetServiceImpl]


      val canineHypernymeOfDog : Boolean = wordnetService.isHypernymRelation("canine","dog").asInstanceOf[Boolean]
      val dogHypernymeOfCanine : Boolean = wordnetService.isHypernymRelation("dog","canine").asInstanceOf[Boolean]
      canineHypernymeOfDog should be (true);
      dogHypernymeOfCanine should be (false);


      // get the addaxioms
      val addAxioms =  reasoningServiceImpl.hypernymAdditionAxioms
      
      addAxioms.foreach(println _)
      true


    }

    it("Find all concepts of a given ontology that are subconcepts wordnet and add the restrictions that all instances of these concepts have a part that is an engine") {
      // hypernyms: Y is a hypernym of X if every X is a (kind of) Y (canine is a hypernym of dog)
      val context = new ClassPathXmlApplicationContext("file:applicationContext.xml")
      // an ApplicationContext is also a BeanFactory (via inheritance)
      val reasoningServiceImpl : ReasoningServiceImpl = context.getBean("reasoningServiceImpl").asInstanceOf[ReasoningServiceImpl]

      val properties : Properties = context.getBean("appPropertiesPersonOntology").asInstanceOf[Properties]
      reasoningServiceImpl.properties = properties
      reasoningServiceImpl.init
      
      val addAxioms =  reasoningServiceImpl.addHasPartEnginePropertyAssertionAxioms
      addAxioms.foreach(println _)
      true


    }





  }
}
