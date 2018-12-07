package FeatureExtraction;
 
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.MatrixCalculator;
import edu.cmu.lti.ws4j.util.WS4JConfiguration; 
import java.util.ArrayList;
import java.util.Arrays;

public class SemanticSimilarityCalculator 
{
        public SemanticSimilarityCalculator()
        {
        }
        
        private static ILexicalDatabase db = new NictWordNet();
 
        private static RelatednessCalculator[] rcs = {
            new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db), 
            new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
        }; 

        public double getSemanticSimilarity(Word w1, Word w2, String measure)
        {
            WS4JConfiguration.getInstance().setMFS(true); 
            RelatednessCalculator r = getSimilarityCalculator(measure);
            if (w1.getLemma().equals("") || w2.getLemma().equals(""))
            {
                return 0;
            }
            return r.calcRelatednessOfWords(w1.getLemma(), w2.getLemma());
        }
        
        public double getSemanticSimilarity(ArrayList<Word> wordList1, ArrayList<Word> wordList2, String measure)
        {
            String[] words1 = extractLemmaList(wordList1);
            String[] words2 = extractLemmaList(wordList2);
            System.out.println("Calculating semantic similarity between two word vectors:");
            System.out.println("First vector:"+String.join(";", words1));
            System.out.println("Second vector:"+String.join(";", words2));
            if (words1.length == 0 || words2.length == 0)
            {
                return 0;
            }
            RelatednessCalculator r = getSimilarityCalculator(measure);
            double[][] simMatrix  = MatrixCalculator.getNormalizedSimilarityMatrix(words1, words2, r);
            return getGeometricSimilarity(simMatrix);
        }
        
        public double getSemanticSelfSimilarity(ArrayList<Word> wordList, String measure)
        {
            String[] words = extractLemmaList(wordList);
            if (words.length == 0)
            {
                return 0;
            }
            RelatednessCalculator r = getSimilarityCalculator(measure);
            double[][] simMatrix  = MatrixCalculator.getNormalizedSimilarityMatrix(words, words, r);
            return getGeometricSimilarity(simMatrix);            
        }
        
        private RelatednessCalculator getSimilarityCalculator(String measure)
        {
            for ( RelatednessCalculator rc : rcs ) 
            {
                if (rc.getClass().getName().contains(measure))
                {
                    return rc;
                }
            }
            // The default calculator is the Path one.
            return rcs[7]; 
        }
        
        private String[] extractLemmaList(ArrayList<Word> wordList)
        {
            String[] result = new String[wordList.size()];
            for (int i=0; i<wordList.size(); i++)
            {
                result[i] = wordList.get(i).getLemma();
            }
            return result;
        }
        
        private double getGeometricSimilarity(double[][] similarityMatrix)
        {
            double result = 0;
            double[] meanVector = new double[similarityMatrix[0].length];
            Arrays.fill(meanVector, 0.0);
            // compute the mean vector of the rows of the similarity matrix
            for (int i=0; i<similarityMatrix.length; i++)
            {
                for (int j=0; j<similarityMatrix[0].length; j++)
                {
                    similarityMatrix[i][j] = (Math.abs(similarityMatrix[i][j]) > 1.0 ? 1.0 : similarityMatrix[i][j]);
                    meanVector[j] = meanVector[j] + similarityMatrix[i][j]/similarityMatrix.length;
                }
            }
            // compute the magnitude of the mean vector
            for (int j=0; j<meanVector.length; j++)
            {
                result = result + Math.pow(meanVector[j], 2);
            }
            result = Math.sqrt(result);
            // divide the magnitude by sqrt(n)
            result = result / Math.sqrt(meanVector.length);
            return result;
        }
    
    
}
