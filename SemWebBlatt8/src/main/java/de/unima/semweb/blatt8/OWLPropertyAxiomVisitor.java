package de.unima.semweb.blatt8;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;

import java.util.Set;
import java.util.HashSet;

/**
 * User: nowi
 * Date: 11.05.2009
 * Time: 18:16:26
 */
public class OWLPropertyAxiomVisitor extends OWLDescriptionVisitorAdapter {
    private boolean processInherited = true;

    private Set<OWLClass> processedClasses;

    private Set<OWLObjectPropertyExpression> restrictedProperties;

    public Set<OWLDescription> getRestrictedConcepts() {
        return restrictedConcepts;
    }

    public void setRestrictedConcepts(Set<OWLDescription> restrictedConcepts) {
        this.restrictedConcepts = restrictedConcepts;
    }

    private Set<OWLDescription> restrictedConcepts;

    private Set<OWLOntology> onts;

    public OWLPropertyAxiomVisitor(Set<OWLOntology> onts) {
        restrictedProperties = new HashSet<OWLObjectPropertyExpression>();
        restrictedConcepts = new HashSet<OWLDescription>();
        processedClasses = new HashSet<OWLClass>();
        this.onts = onts;
    }


    public void setProcessInherited(boolean processInherited) {
        this.processInherited = processInherited;
    }


    public Set<OWLObjectPropertyExpression> getRestrictedProperties() {
        return restrictedProperties;
    }


    public void visit(OWLClass desc) {
        if (processInherited && !processedClasses.contains(desc)) {
            // If we are processing inherited restrictions then
            // we recursively visit named supers.  Note that we
            // need to keep track of the classes that we have processed
            // so that we don't get caught out by cycles in the taxonomy
            processedClasses.add(desc);
            for (OWLOntology ont : onts) {
                for (OWLSubClassAxiom ax : ont.getSubClassAxiomsForLHS(desc)) {
                    ax.getSuperClass().accept(this);
                }
            }
        }
    }


    public void reset() {
        processedClasses.clear();
        restrictedProperties.clear();
        restrictedConcepts.clear();
    }


    public void visit(OWLObjectAllRestriction desc) {
        // This method gets called when a description (OWLDescription) is an
        // existential (someValuesFrom) restriction and it asks us to visit it
        restrictedProperties.add(desc.getProperty());
    }


    @Override
    public void visit(OWLObjectSomeRestriction owlObjectSomeRestriction) {
        restrictedProperties.add(owlObjectSomeRestriction.getProperty());
        restrictedConcepts.add(owlObjectSomeRestriction.getFiller());
        super.visit(owlObjectSomeRestriction);    //To change body of overridden methods use File | Settings | File Templates.
    }



    @Override
    public void visit(OWLObjectMinCardinalityRestriction owlObjectMinCardinalityRestriction) {
        restrictedProperties.add(owlObjectMinCardinalityRestriction.getProperty());
        restrictedConcepts.add(owlObjectMinCardinalityRestriction.getFiller());

        super.visit(owlObjectMinCardinalityRestriction);    //To change body of overridden methods use File | Settings | File Templates.;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void visit(OWLObjectExactCardinalityRestriction owlObjectExactCardinalityRestriction) {
        restrictedProperties.add(owlObjectExactCardinalityRestriction.getProperty());
        restrictedConcepts.add(owlObjectExactCardinalityRestriction.getFiller());
        super.visit(owlObjectExactCardinalityRestriction);    //To change body of overridden methods use File | Settings | File Templates.;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void visit(OWLObjectMaxCardinalityRestriction owlObjectMaxCardinalityRestriction) {
        restrictedProperties.add(owlObjectMaxCardinalityRestriction.getProperty());
        restrictedConcepts.add(owlObjectMaxCardinalityRestriction.getFiller());
        super.visit(owlObjectMaxCardinalityRestriction);    //To change body of overridden methods use File | Settings | File Templates.;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void visit(OWLObjectSelfRestriction owlObjectSelfRestriction) {
        restrictedProperties.add(owlObjectSelfRestriction.getProperty());
        super.visit(owlObjectSelfRestriction);    //To change body of overridden methods use File | Settings | File Templates.;    //To change body of overridden methods use File | Settings | File Templates.
    }

   


}
