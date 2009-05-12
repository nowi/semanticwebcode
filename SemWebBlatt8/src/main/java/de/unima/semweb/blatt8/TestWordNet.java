package de.unima.semweb.blatt8;

import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.NounSynset;

/**
 * User: nowi
 * Date: 11.05.2009
 * Time: 20:58:00
 */
public class TestWordNet {

	/**
	 * Main entry point. The command-line arguments are concatenated together
	 * (separated by spaces) and used as the word form to look up.
	 */
	public static void main(String[] args)
	{
		if (args.length > 0)
		{
			//  Set the location of the wordnet database
			System.setProperty( "wordnet.database.dir","./dict" );

			//  Concatenate the command-line arguments
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < args.length; i++)
			{
				buffer.append((i > 0 ? " " : "") + args[i]);
			}
			String wordForm = buffer.toString();
			//  Get the synsets containing the wrod form
			WordNetDatabase database = WordNetDatabase.getFileInstance();

			Synset[] synsets = database.getSynsets(wordForm, SynsetType.NOUN);
			//  Display the word forms and definitions for synsets retrieved
			if (synsets.length > 0)
			{
			if (synsets.length > 1){
				System.out.println("'" +
						wordForm + "' has multiple meanings:");
				for (int i = 0; i < synsets.length; i++)
				{
					System.out.print((i > 0 ? "\n- " : "- ") +
								synsets[i].getDefinition());
				}
				System.out.println("");
			}else{
				System.out.println("The following nouns are hypnyms of '" +
						wordForm + "':");
				NounSynset[] hypernyms = ((NounSynset) synsets[0]).getHyponyms();
				for (int j = 0; j < hypernyms.length; j++)
				{
					for (int k = 0; k < hypernyms[j].getWordForms().length; k++){
						System.out.print((j+k > 0 ? ", " : "") +
							hypernyms[j].getWordForms()[k]);
					}
				}
				System.out.println("");
			}
			}
			else
			{
				System.err.println("No synsets exist that contain " +
						"the word form '" + wordForm + "'");
			}
		}
		else
		{
			System.err.println("You must specify " +
					"a word form for which to retrieve synsets.");
		}
	}

}
    