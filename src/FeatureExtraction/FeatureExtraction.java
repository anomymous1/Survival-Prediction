package FeatureExtraction;

/**
 *
 * @author Georgiana
 */
public class FeatureExtraction 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        System.out.println(args);
        
        if (args.length < 2)
        {
            System.out.println("Please provide the following three arguments:\tinputItemFolder\toutputFile\tcorrelationsFile");
            // inputItemFolder is the folder containing the .txt files corresponding to the test items
            // outputFile is the file containing for each item the set of features and feature values extracted by this program
            // correlationsFile is the file containing the correlations between each feature and the item statistics
            //../TestItems/ features.txt
            System.exit(0);
        }
        
        String itemFolder = args[0].trim();
        String featureFile = args[1].trim();
        
        
        ItemAnalyser ia = new ItemAnalyser(itemFolder, featureFile);
        
        //featureFile = "commonItemsOnlyNumericFeatures.txt";
       // correlationsFile = "correlationCommonItems.txt";
      
/*        
        CorrelationCalculator cc = new CorrelationCalculator();
        cc.calculateCorrelations(featureFile, correlationsFile);
*/
        //CorrelationCalculator cc = new CorrelationCalculator(featureFile);
        //cc.calculatePearsonsCorrelations(correlationsFile.replace(".txt", "_Pearsons.txt"));
        //cc.calculateSpearmansCorrelations(correlationsFile.replace(".txt", "_Spearmans.txt"));
    }
    
}
