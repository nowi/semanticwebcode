package de.unima.semweb.blatt8;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.mindswap.pellet.owlapi.Reasoner;

/**
 * User: nowi
 * Date: 08.05.2009
 * Time: 21:57:27
 */
@Component
public class SpringStarter {

    public de.unima.semweb.blatt8.ReasoningServiceImpl getFallWinterSpringSummer() {
        return reasoningServiceImpl;
    }


    de.unima.semweb.blatt8.ReasoningServiceImpl reasoningServiceImpl;


    public Reasoner getReasoner() {
        return reasoner;
    }

    public void setReasoner(Reasoner reasoner) {
        this.reasoner = reasoner;
    }

    Reasoner reasoner;

    @Autowired
    public void setReasoningServiceImpl(de.unima.semweb.blatt8.ReasoningServiceImpl reasoningServiceImpl) {
        this.reasoningServiceImpl = reasoningServiceImpl;
    }


    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                new String[]{"file:applicationContext.xml"});
        // an ApplicationContext is also a BeanFactory (via inheritance)
        BeanFactory factory = context;
//        Reasoner reasoner = (Reasoner) factory.getBean("reasoner");
        ReasoningServiceImpl reasoningServiceImpl = (ReasoningServiceImpl) factory.getBean("reasoningServiceImpl");
        reasoningServiceImpl.findConcepts("Count");
        reasoningServiceImpl.findConcepts("CountrySCHASH");
    }


}
