package de.unima.semweb.blatt8;

import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.NounSynset;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Required;

/**
 * User: nowi
 * Date: 11.05.2009
 * Time: 21:06:08
 */
public class WordNetServiceImpl {


    public String getWorkingDirectoryString() {
        return workingDirectoryString;
    }

    @Required
    public void setWorkingDirectoryString(String workingDirectoryString) {
        this.workingDirectoryString = workingDirectoryString;
    }

    String workingDirectoryString;

    public Collection<String> getHyponyms(String searchString) {
        Collection<String> hypernymNames = new HashSet<String>();

        //  Set the location of the wordnet database
        System.setProperty("wordnet.database.dir", workingDirectoryString + "/dict");

        //  Get the synsets containing the wrod form
        WordNetDatabase database = WordNetDatabase.getFileInstance();

        Synset[] synsets = database.getSynsets(searchString, SynsetType.NOUN);
        //  Display the word forms and definitions for synsets retrieved
        if (synsets.length > 0) {

            // for each synset
            for (Synset synset : synsets) {
                NounSynset[] hypernyms = ((NounSynset) synset).getHypernyms();
                for (NounSynset nounSynSet : hypernyms) {
                    for (String wordForm : nounSynSet.getWordForms()) {
                        // add to results
                        hypernymNames.add(wordForm);
                    }

                }

            }
        } else

        {
            System.err.println("No synsets exist that contain " +
                    "the word form '" + searchString + "'");
        }


        return hypernymNames;

    }
    
    public Boolean isHypernymRelation(String hypernym,String hyponym) {
        // get the hypernyms of this hyponym
        return (getHyponyms(hyponym).contains(hypernym));


    }


}
