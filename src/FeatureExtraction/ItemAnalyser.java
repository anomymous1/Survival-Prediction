package FeatureExtraction;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Georgiana
 */
public class ItemAnalyser 
{
    static String genericFeatures[] = new String[] {"ItemID"};
    // Lexical complexity & diversity features
    static String lexicalFeatures[] = new String[] {"WordCount", "ContentWordCount", "ContentWordIncidence", "ContentWordCountNoStopwords", "NounCount", "NounIncidence", "VerbCount", "VerbIncidence", "AdjectiveCount", "AdjectiveIncidence", "AdverbCount", "AdverbIncidence", "NumberCount", "NumberIncidence", "TypeCount", "TypeTokenRatio", "CommaCount", "CommaIncidence", "AverageWordLengthInSyllables", "ComplexWordCount", "ComplexWordIncidence"};
    // Syntactic features
    static String syntacticalFeatures[] = new String[] {"AverageSentenceLength", "AverageDepthOfTree", "NegationCount", "NegationIncidence", "NegationInStem", "NegationInLead", "NPCount", "NPIncidence", "AverageNPLength", "NPCountWithEmbedding", "NPIncidenceWithEmbedding", "AverageAllNPLength", "PPCount", "PPIncidence", "PPsPerSentenceRatio", "VPCount", "VPIncidence", "PassiveActiveRatio", "ProportionActiveVPs", "ProportionPassiveVPs", "AverageNoWordsBeforeMainVb", "AgentlessPassiveCount", "RelativeClausesCount", "RelativeClausesIncidence", "ProportionRelativeClauses", "ConditionalClausesCount", "ConditionalClausesIncidence"};
    // Features corresponding to ambiguity in meaning
    static String meaningAmbiguityFeatures[] = new String[] {"PolysemicWordCount", "PolysemicWordIncidence", "AverageSenseNoContentWords", "AverageSenseNoNouns", "AverageSenseNoVerbs", "AverageSenseNoNonAuxiliaryVerbs", "AverageSenseNoAdjectives", "AverageSenseNoAdverbs", "AverageNounDistanceToWNRoot", "AverageVerbDistanceToWNRoot", "AverageNounAndVerbDistanceToWNRoot", "AnswerWordsInWordNetRatio"};    
    // Semantic similarity features
    static String semSimFeatures[] = new String[] {
        "ResnikSimilarityStemKey", "ResnikSimilarityStemDistractors", "ResnikSimilarityKeyDistractors", "ResnikSelfSimilarityStem", 
        "PathSimilarityStemKey", "PathSimilarityStemDistractors", "PathSimilarityKeyDistractors", "PathSelfSimilarityStem", 
        "LinSimilarityStemKey", "LinSimilarityStemDistractors", "LinSimilarityKeyDistractors", "LinSelfSimilarityStem", 
        "WuPalmerSimilarityStemKey", "WuPalmerSimilarityStemDistractors", "WuPalmerSimilarityKeyDistractors", "WuPalmerSelfSimilarityStem", 
        "LeskSimilarityStemKey", "LeskSimilarityStemDistractors", "LeskSimilarityKeyDistractors", "LeskSelfSimilarityStem"
    };
    static String semSimFeatures2[] = new String[] {
        "JiangConrathSimilarityStemKey", "JiangConrathSimilarityStemDistractors", "JiangConrathSimilarityKeyDistractors", "JiangConrathSelfSimilarityStem",
        "LeacockChodorowSimilarityStemKey", "LeacockChodorowSimilarityStemDistractors", "LeacockChodorowSimilarityKeyDistractors", "LeacockChodorowSelfSimilarityStem"
    };    
    //"HirstStOngeSimilarityStemKey", "HirstStOngeSimilarityStemDistractors", "HirstStOngeSimilarityKeyDistractors", "HirstStOngeSelfSimilarityStemLead", "HirstStOngeSelfSimilarityFullItem"    
    // Readability scores
    static String readabilityFeatures[] = new String[] {"FleschReadingEase", "FleschKincaidGradeLevel", "AutomatedReadabilityIndex", "GunningFog", "ColemanLiau", "SMOG", "SMOGIndex"};
    // Word frequency scores
    static String wordFrequencyFeatures[] = new String[] {"AverageWordFrequencyAbs", "AverageWordFrequencyRel", "AverageWordFrequencyRank", "AverageContentFrequencyAbs", "AverageContentFrequencyRel", "AverageContentFrequencyRank", "NotInFirst2000Count", "NotInFirst2000Incidence", "NotInFirst3000Count", "NotInFirst3000Incidence", "NotInFirst4000Count", "NotInFirst4000Incidence", "NotInFirst5000Count", "NotInFirst5000Incidence"};
    // Cognitively motivated features
    static String cognitiveFeatures[] = new String[] {"Imagability", "ImagabilityFoundOnly", "ImagabilityRatio", "Familiarity", "FamiliarityFoundOnly", "FamiliarityRatio", "Concreteness", "ConcretenessFoundOnly", "ConcretenessRatio", "AgeOfAcquisition", "AgeOfAcquisitionFoundOnly", "AgeOfAcquisitionRatio", "MeaningfulnessColoradoFoundOnly", "MeaningfulnessPavioFoundOnly", "NoImagabilityRating", "NoFamiliarityRating", "NoConcretenessRating", "NoAoARating"};
    // Connectives
    static String cohesionFeatures[] = new String[] {"ConnectivesCount", "ConnectivesIncidence",  "AdditiveConnectivesCount", "AdditiveConnectivesIncidence", "TemporalConnectivesCount", "TemporalConnectivesIncidence","CausalConnectivesCount", "CausalConnectivesIncidence", "ReferentialPronounCount", "ReferentialPronounIncidence"};    
    // Item statistics
    static String itemStatistics[] = new String[] {"Meantime"};

    // All features
    static ArrayList<String> features =  new ArrayList<>();
    ItemParser itemParser;
    HashMap<String, Item> allItems;
    HashMap<String, Double> statistics;
    HashMap<String, Double> wordFreqAbsolute;
    HashMap<String, Double> wordFreqRelative;
    HashMap<String, Double> wordFreqRank;
    HashMap<String, Double> wordIMG;
    HashMap<String, Double> wordAoA;
    HashMap<String, Double> wordCNC;
    HashMap<String, Double> wordFAM;    
    HashMap<String, Double> wordMEANC;
    HashMap<String, Double> wordMEANP;
    HashMap<String, Double> connectives;
    HashMap<String, Double> causalConn;
    HashMap<String, Double> temporalConn;
    HashMap<String, Double> additiveConn;
    Pattern conn, causConn, tempConn, addConn;
    WordNetDatabase wnetDatabase;    
    BufferedWriter outFile;
    SemanticSimilarityCalculator semSimCalc;
    final List<String> stopWords = Arrays.asList( 
        "a", "an", "and", "are", "as", "at", "be", "but", "by", 
        "for", "if", "in", "into", "is", "it", 
        "no", "not", "of", "on", "or", "such", 
        "that", "the", "their", "then", "there", "these", 
        "they", "this", "to", "was", "will", "with" 
    ); 

    public ItemAnalyser(String itemFolder, String featureFile)
    {
        features.addAll(Arrays.asList(genericFeatures));
        features.addAll(Arrays.asList(lexicalFeatures));
        features.addAll(Arrays.asList(syntacticalFeatures));
        features.addAll(Arrays.asList(meaningAmbiguityFeatures));
        features.addAll(Arrays.asList(semSimFeatures));
        features.addAll(Arrays.asList(semSimFeatures2));        
        features.addAll(Arrays.asList(readabilityFeatures));
        features.addAll(Arrays.asList(wordFrequencyFeatures));
        features.addAll(Arrays.asList(cognitiveFeatures));
        features.addAll(Arrays.asList(cohesionFeatures));

        features.addAll(Arrays.asList(itemStatistics));
        // Creating and initialising the item parser
        itemParser = new ItemParser();
        // Initialising WordNet
        System.setProperty("wordnet.database.dir", "./data/dict/");            
        this.wnetDatabase = WordNetDatabase.getFileInstance();
        // Initialising MetaMap
        int timeout = -1; // use default timeout
        semSimCalc = new SemanticSimilarityCalculator();
        allItems = new HashMap<>();
        try
        {
            // Load BNC Frequency file
            this.wordFreqRank = loadFile("./data/BNCFrequencyList.txt", 1);
            this.wordFreqAbsolute = loadFile("./data/BNCFrequencyList.txt", 2);
            this.wordFreqRelative = loadFile("./data/BNCFrequencyList.txt", 3);
            // Load word imagability data
            this.wordIMG = loadFile("./data/IMAG_mrc2.txt", 1);
            // Load word familiarity data
            this.wordFAM = loadFile("./data/FAM_mrc2.txt", 1);
            // Load word concreteness data
            this.wordCNC = loadFile("./data/CNC_mrc2.txt", 1);
            // Load word age-of-acquisition data
            this.wordAoA = loadFile("./data/AoA_ratings_Kuperman.txt", 1);
            // Load word meaningfulness data
            this.wordMEANP = loadFile("./data/MEANP_mrc2.txt", 1);
            this.wordMEANC = loadFile("./data/MEANC_mrc2.txt", 1);
            // Load connectives
            this.connectives = loadFile("./data/Connectives.txt", 0);
            this.causalConn = loadFile("./data/CausalConnectives.txt", 0);
            this.temporalConn = loadFile("./data/TemporalConnectives.txt", 0);
            this.additiveConn = loadFile("./data/AdditiveConnectives.txt", 0);
            this.conn = Pattern.compile("^("+String.join("|",this.connectives.keySet())+")( .*)?$");
            this.tempConn = Pattern.compile("^("+String.join("|",this.temporalConn.keySet())+")( .*)?$");
            this.causConn = Pattern.compile("^("+String.join("|",this.causalConn.keySet())+")( .*)?$");
            this.addConn =  Pattern.compile("^("+String.join("|",this.additiveConn.keySet())+")( .*)?$");
            System.out.println("Temporal Connectives:"+String.join(";",this.temporalConn.keySet()));
            
            outFile = new BufferedWriter(new FileWriter(featureFile));
            for (String f: features)
            {
                outFile.write(f+"\t");
            }
            outFile.newLine(); 
            // Parsing each file and converting it to an item            
            File[] files = new File(itemFolder).listFiles();
            for (File f: files)
            {
                if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead() && f.getName().toLowerCase().endsWith(".txt"))
                {
                    getItemFromFile(f);
                }
            }
            outFile.close();
            
        } catch(IOException e){e.printStackTrace();};
    }
    
    public HashMap<String,Double> loadFile(String fileName, int column) throws IOException
    {
        System.out.println("Loading file " + fileName);
        HashMap<String,Double> ht = new HashMap();
        BufferedReader in;
        in = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = in.readLine()) != null)
        {
            String[] comp = line.trim().split("\\s+");
            if (column == 0)
            {
                ht.put(comp[0], 1.0);
            }
            else if (!comp[0].isEmpty() && comp[column].matches("-?\\d+(\\.\\d+)?"))
            {
                ht.put(comp[0], Double.parseDouble(comp[column]));
            }
        }
        in.close();
        return ht;
    }      
    
    private void getItemFromFile(File f) throws IOException
    {
        
        Item item =  itemParser.parseFile(f);
        allItems.put(item.getID(), item);
        analyseItem(item);
    }
    
    private void analyseItem(Item item) throws IOException
    {
        //if (item.getReadingTime().isEmpty())
        //{
        //    return;
        //}        
        // ItemID
        outFile.write(item.getID()+"\t");
        // HasImages
        //outFile.write("0\t");
        // HasTables        
        //outFile.write("0\t");
        // NoOfTableRows
        //outFile.write(item.getNoRows()+"\t");
        // gather word-related statistics about the item
        getStatistics(item);
        // write the remaining statistics to file
        
        for (int i=1; i<features.size()-1; i++)
        {
            outFile.write(String.format("%.2f",statistics.get(features.get(i)))+"\t");
        }
        //outFile.write(item.getReadingTime());
        outFile.newLine();        
    }
               
    private void getStatistics(Item item)
    {       
        statistics = new HashMap<>();
        HashMap<String, Integer> words = new HashMap();
        int wordCount = 0, contentWordCount = 0, contentWordCountNoStopwords = 0;
        int nounNo = 0, verbNo = 0, adjNo = 0, advNo = 0, numberNo = 0, nonAuxVerb = 0;
        int senseNo = 0, senseNoNoun = 0, senseNoVerb = 0, senseNoAdj = 0, senseNoAdv = 0, polysemousWords = 0, senseNoNonAuxVerb = 0;
        int sentNo = 0;
        int wordsBeforeMainVb = 0;
        int complexWordCount = 0;
        int syllables = 0;
        int characters = 0;
        double depthSum = 0.0;
        double nounDistToRoot = 0.0;
        double verbDistToRoot  = 0.0;
        double freqRank = 0.0;
        double freqAbs = 0.0;
        double freqRel = 0.0;
        double contentFreqRank = 0.0;
        double contentFreqAbs = 0.0;
        double contentFreqRel = 0.0;
        double IMG = 0.0;
        double CNC = 0.0;
        double FAM = 0.0;
        double AoA = 0.0;
        double MEANC = 0.0;
        double MEANP = 0.0;
        int wIMG = 0, wCNC = 0, wFAM = 0, wAoA = 0, wMEANP = 0, wMEANC = 0;
        int negationNo = 0;
        int agentlessPassive = 0;
        int connectives = 0, tempConnectives = 0, causalConnectives = 0, additiveConnectives = 0;
        int startAnswers = item.getStart("ANSWERS");
        int endAnswers = item.getEnd("ANSWERS");
        int startCorrect = item.getStart("CORRECT");
        int endCorrect = item.getEnd("CORRECT");
        int startDiagnosis = item.getStart("DIAGNOSIS");
        int startLead = item.getStart("LEAD");
        int endLead = item.getEnd("LEAD");        
        int pronounNo = 0;
        int answerWordsNotInWN = 0;
        int negationInLead = 0;
        int noClauses = 0;
        int noRelativeClauses = 0;
        int noConditionalClauses = 0;
        int noPassives = 0;
        int notInFirst2000 = 0;
        int notInFirst3000 = 0;
        int notInFirst4000 = 0;
        int notInFirst5000 = 0;        
        startDiagnosis = (startDiagnosis==-1 ? Integer.MAX_VALUE : startDiagnosis);
        ArrayList<Word> wordsStemLead = new ArrayList<>();
        ArrayList<Word> wordsDistractors = new ArrayList<>();
        ArrayList<Word> wordsCorrect = new ArrayList<>();
        ArrayList<Word> wordsFullItem = new ArrayList<>();
        int wordsInAnswers;
        for (HashMap<Integer, Word> sent: item.getParsedItem())
        {
            sentNo++;
            for (int offset: sent.keySet())
            {
                Word w = sent.get(offset);
                wordCount++;
                String wordContext = w.getLemma() + " " + w.getNextTwoWords();
                Matcher m1 = conn.matcher(wordContext);
                if (m1.matches())
                {
                    if (!(m1.group(1).endsWith(" as") || m1.group(1).endsWith(" that") || m1.group(1).endsWith(" if")))
                    {
                        connectives++;
                    }                        
                }
                m1 = tempConn.matcher(wordContext);
                if (m1.matches())
                {
                    if (!m1.group(1).endsWith(" as"))
                    {
                        tempConnectives++;
                    }                        
                }     
                m1 = causConn.matcher(wordContext);
                if (m1.matches())
                {
                    if (!m1.group(1).endsWith(" as"))
                    {
                        causalConnectives++;
                    }                        
                }
                m1 = addConn.matcher(wordContext);
                if (m1.matches())
                {
                    additiveConnectives++;                     
                }          
                if (w.getLemma().matches("^(he|his|she|her|they|their|it|its)$"))
                {
                    pronounNo++;
                }
                syllables += SyllableFinder.syllableCount(w.getWord());
                characters += w.getWord().length();
                if (isComplex(w.getWord()))
                {
                    complexWordCount++;
                }
                if (w.getPOS().startsWith("CD") || w.getLemma().matches("^\\d+[\\d\\.\\,]*$"))
                {
                    numberNo++;
                }
                if (this.wordFreqRank.containsKey(w.getLemma()))
                {
                    double rank =  this.wordFreqRank.get(w.getLemma());
                    freqRank += rank;
                    if (rank > 2000)
                    {
                        notInFirst2000++;
                    }
                    if (rank > 3000)
                    {
                        notInFirst3000++;
                    }
                    if (rank > 4000)
                    {
                        notInFirst4000++;
                    }
                    if (rank > 5000)
                    {
                        notInFirst5000++;
                    }
                    freqAbs += this.wordFreqAbsolute.get(w.getLemma());
                    freqRel += this.wordFreqRelative.get(w.getLemma());
                }  
                else
                {
                    notInFirst2000++;
                    notInFirst3000++;
                    notInFirst4000++;
                    notInFirst5000++;
                }                
                if (w.isRoot())
                {
                    depthSum += w.getDepth();
                    if (w.getPOS().startsWith("VB")) // this is the main verb of the sentence
                    {
                        wordsBeforeMainVb = wordsBeforeMainVb + w.getWordIndex() - 1;
                    }
                }                
                if (words.containsKey(w.getWord().toLowerCase()))
                {
                    words.put(w.getWord().toLowerCase(), words.get(w.getWord().toLowerCase())+1);
                }
                else
                {
                    words.put(w.getWord().toLowerCase(), 1);
                }
                if (w.getDependencyRelations().contains("-neg") || (w.getDependencyRelations().contains("-det") && w.getLemma().equals("no")) || (w.getLemma().matches("^(not|never|except|none|neither|nor)$")))
                {
                    negationNo++;
                    if (offset >= startLead && offset <= endLead)
                    {
                        negationInLead = 1;
                    }                    
                }
                if (w.getLemma().equals("if"))
                {
                    noConditionalClauses++;
                }
                if (w.getPOS().startsWith("VB") && !w.getDependencyRelations().contains("aux"))
                {
                    noClauses++;
                    ArrayList<Integer> heads = w.getHeads();
                    for (Integer head:heads)
                    {
                        if (sent.containsKey(head) && sent.get(head).getPOS().startsWith("N"))
                        {
                            noRelativeClauses++;
                        }
                    }
                }
                
                if (w.getPOS().equals("VBN")) // potential passive
                {
                    ArrayList<Integer> deps = item.getAllDependentWords(sent, w.getWordIndex());
                    //System.out.println("Verb "+ w.getWord() + " has id " + w.getWordIndex() + " has deps " + deps);
                    boolean hasPassiveAux = false;
                    boolean hasAgent = false;
                    for (Integer dep:deps)
                    {
                        if (sent.containsKey(dep) && sent.get(dep).getDependencyRelations().contains("-auxpass"))
                        {
                            hasPassiveAux = true;
                        }
                        else
                        {
                            if (sent.containsKey(dep) && sent.get(dep).getDependencyRelations().contains("-agent"))
                            {
                                hasAgent = true;
                            }                            
                        }
                    }
                    if (hasPassiveAux)
                    {
                        noPassives++;
                    }                    
                    if (hasPassiveAux && !hasAgent)
                    {
                        agentlessPassive++;
                    }
                }                
                if (w.getPOS().matches("^(NN|VB|JJ|RB).*$")) // content word
                {
                    contentWordCount++;   
                    if(offset < startAnswers)
                    {
                        wordsStemLead.add(w);
                    }
                    else if (offset >= startCorrect && offset <= endCorrect)
                    {
                        wordsCorrect.add(w);
                    }
                    else if (offset < startDiagnosis)
                    {
                        wordsDistractors.add(w);
                    }                
                    wordsFullItem.add(w);
                    if (this.wordFreqRank.containsKey(w.getLemma()))
                    {
                        contentFreqRank += this.wordFreqRank.get(w.getLemma());
                        contentFreqAbs += this.wordFreqAbsolute.get(w.getLemma());
                        contentFreqRel += this.wordFreqRelative.get(w.getLemma());
                    }                                    
                    if (wordIMG.containsKey(w.getLemma()))
                    {
                        IMG += wordIMG.get(w.getLemma());
                        wIMG++;
                    }
                    if (wordCNC.containsKey(w.getLemma()))
                    {
                        CNC += wordCNC.get(w.getLemma());
                        wCNC++;
                    }
                    if (wordFAM.containsKey(w.getLemma()))
                    {
                        FAM += wordFAM.get(w.getLemma());
                        wFAM++;
                    }
                    if (wordAoA.containsKey(w.getLemma()))
                    {
                        AoA += wordAoA.get(w.getLemma());
                        wAoA++;
                    }
                    if (wordMEANC.containsKey(w.getLemma()))
                    {
                        MEANC += wordMEANC.get(w.getLemma());
                        wMEANC++;
                    }
                    if (wordMEANP.containsKey(w.getLemma()))
                    {
                        MEANP += wordMEANP.get(w.getLemma());
                        wMEANP++;
                    }
                    if (!stopWords.contains(w.getWord().toLowerCase()) && !stopWords.contains(w.getLemma().toLowerCase()))
                    {
                        contentWordCountNoStopwords++;
                    }
                    SynsetType stype = getSynsetType(w.getPOS());
                    Synset[] synsets;
                    synsets = wnetDatabase.getSynsets(w.getLemma(), stype);
                    if (synsets.length >= 2)
                    {
                        polysemousWords++;
                    }
                    else if (synsets.length == 0 && offset >= startAnswers && offset < startDiagnosis)
                    {
                        answerWordsNotInWN++;
                    }
                    senseNo += synsets.length;
                    if (stype.equals(SynsetType.NOUN))
                    {
                        senseNoNoun += synsets.length;
                        nounDistToRoot = nounDistToRoot + getDistanceToRoot(synsets, stype);
                        nounNo++;
                    }
                    else if (stype.equals(SynsetType.VERB))
                    {
                        senseNoVerb += synsets.length;
                        verbDistToRoot = verbDistToRoot + getDistanceToRoot(synsets, stype);
                        verbNo++;
                        if (!w.getDependencyRelations().contains("aux"))
                        {
                            senseNoNonAuxVerb += synsets.length;
                            nonAuxVerb++;
                        }                        
                    }
                    else if (stype.equals(SynsetType.ADJECTIVE))
                    {
                        senseNoAdj += synsets.length;
                        adjNo++;
                    }
                    else
                    {
                        senseNoAdv += synsets.length;
                        advNo++;
                    }
                }
            }

        }
        nounNo = (nounNo == 0 ? 1 : nounNo);
        verbNo = (verbNo == 0 ? 1 : verbNo);
        adjNo = (adjNo == 0 ? 1 : adjNo);
        advNo = (advNo == 0 ? 1 : advNo);
        wordsInAnswers = wordsCorrect.size() + wordsDistractors.size();
        wordsInAnswers = (wordsInAnswers == 0 ? 1 : wordsInAnswers);
      
        statistics.put("AverageDepthOfTree", (depthSum/sentNo));
        statistics.put("WordCount", 1.0 * wordCount);
        statistics.put("AverageWordLengthInSyllables", ((wordCount == 0) ? 0.0 : (1.0 * syllables) / wordCount)); // for Victoria        
        statistics.put("ComplexWordCount", 1.0 * complexWordCount);
        statistics.put("ComplexWordIncidence",((wordCount == 0) ? 0.0 :(1000.0 * complexWordCount) / wordCount));        
            statistics.put("SentenceNumber", 1.0* sentNo); // this is not a reported feature
        statistics.put("AverageSentenceLength", (1.0 * wordCount) / sentNo);
        statistics.put("ContentWordCount", 1.0 * contentWordCount);
        statistics.put("ContentWordCountNoStopwords", 1.0 * contentWordCountNoStopwords);
        statistics.put("ContentWordIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * contentWordCount) / wordCount));
        statistics.put("NounCount", 1.0 * nounNo);
        statistics.put("NounIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * nounNo) / wordCount));
        statistics.put("VerbCount", 1.0 * verbNo);
        statistics.put("VerbIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * verbNo) / wordCount));
        statistics.put("AdjectiveCount", 1.0 * adjNo);
        statistics.put("AdjectiveIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * adjNo) / wordCount));
        statistics.put("AdverbCount", 1.0 * advNo);
        statistics.put("AdverbIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * advNo) / wordCount));
        statistics.put("NumberCount", 1.0 * numberNo);
        statistics.put("NumberIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * numberNo) / wordCount));
        statistics.put("ReferentialPronounCount", 1.0 * pronounNo);
        statistics.put("ReferentialPronounIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * pronounNo) / wordCount));        
        statistics.put("AverageSenseNoContentWords", 1.0 * senseNo / contentWordCount);
        statistics.put("AverageSenseNoNouns", 1.0 * senseNoNoun / nounNo);
        statistics.put("AverageSenseNoVerbs", 1.0 * senseNoVerb / verbNo);
        statistics.put("AverageSenseNoNonAuxiliaryVerbs", 1.0 * senseNoNonAuxVerb / nonAuxVerb);       
        statistics.put("AverageSenseNoAdjectives", 1.0 * senseNoAdj / adjNo);
        statistics.put("AverageSenseNoAdverbs", 1.0 * senseNoAdv / advNo);
        statistics.put("PolysemicWordIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * polysemousWords) / wordCount));
        statistics.put("PolysemicWordCount", (1.0 * polysemousWords));
        statistics.put("TypeCount", (1.0 * words.keySet().size()));
        statistics.put("TypeTokenRatio", (1.0 * words.keySet().size()) / wordCount);
        statistics.put("CommaCount", (1.0 * item.getCommaCount()));        
        statistics.put("CommaIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * item.getCommaCount()) / wordCount));        
        statistics.put("AverageNoWordsBeforeMainVb", (1.0 * wordsBeforeMainVb) / sentNo);
        statistics.put("AverageNounDistanceToWNRoot", (1.0 * nounDistToRoot) / nounNo);
        statistics.put("AverageVerbDistanceToWNRoot", (1.0 * verbDistToRoot) / verbNo);
        statistics.put("AverageNounAndVerbDistanceToWNRoot", (nounDistToRoot + verbDistToRoot) / (nounNo + verbNo));
        statistics.put("SMOG", getSMOG(sentNo, complexWordCount));
        statistics.put("SMOGIndex", getSMOGIndex(sentNo, complexWordCount));
        statistics.put("FleschReadingEase", getFleschReadingEase(sentNo, wordCount, complexWordCount, syllables));
        statistics.put("FleschKincaidGradeLevel", getFleschKincaidGradeLevel(sentNo, wordCount, syllables));
        statistics.put("AutomatedReadabilityIndex", getARI(sentNo, wordCount, characters));
        statistics.put("GunningFog", getGunningFog(sentNo, wordCount, complexWordCount));
        statistics.put("ColemanLiau", getColemanLiau(sentNo, wordCount, characters));
        statistics.put("AverageWordFrequencyAbs", freqAbs / wordCount);
        statistics.put("AverageWordFrequencyRel", freqRel / wordCount);
        statistics.put("AverageWordFrequencyRank", freqRank / wordCount);
        statistics.put("NotInFirst2000Count", 1.0 * notInFirst2000);
        statistics.put("NotInFirst2000Incidence", 1.0 * notInFirst2000 / wordCount);
        statistics.put("NotInFirst3000Count", 1.0 * notInFirst3000);
        statistics.put("NotInFirst3000Incidence", 1.0 * notInFirst3000 / wordCount);
        statistics.put("NotInFirst4000Count", 1.0 * notInFirst4000);
        statistics.put("NotInFirst4000Incidence", 1.0 * notInFirst4000 / wordCount);
        statistics.put("NotInFirst5000Count", 1.0 * notInFirst5000);
        statistics.put("NotInFirst5000Incidence", 1.0 * notInFirst5000 / wordCount);        
        statistics.put("AverageContentFrequencyAbs", contentFreqAbs/contentWordCount);
        statistics.put("AverageContentFrequencyRel", contentFreqRel/contentWordCount);
        statistics.put("AverageContentFrequencyRank", contentFreqRank/contentWordCount);
        statistics.put("Imagability", IMG / contentWordCount);
        statistics.put("ImagabilityFoundOnly", (wIMG == 0.0 ? 0.0 : ((1.0 * IMG) / wIMG)));
        statistics.put("ImagabilityRatio", (IMG * wIMG)/contentWordCount);
        statistics.put("Familiarity", FAM / contentWordCount);
        statistics.put("FamiliarityFoundOnly", (wFAM == 0.0 ? 0.0 : (FAM / wFAM)));
        statistics.put("FamiliarityRatio", (FAM * wFAM)/contentWordCount);
        statistics.put("Concreteness", CNC / contentWordCount);
        statistics.put("ConcretenessFoundOnly", (wCNC == 0.0 ? 0.0 : (CNC / wCNC)));
        statistics.put("ConcretenessRatio", (CNC * wCNC)/contentWordCount);
        statistics.put("AgeOfAcquisition", AoA / contentWordCount);
        statistics.put("AgeOfAcquisitionFoundOnly", (wAoA == 0.0 ? 0.0 : (AoA / wAoA)));
        statistics.put("AgeOfAcquisitionRatio", (AoA * wAoA)/contentWordCount);
        statistics.put("NoImagabilityRating", 1.0 * (contentWordCount-wIMG)/contentWordCount);
        statistics.put("NoFamiliarityRating", 1.0 * (contentWordCount-wFAM)/contentWordCount);
        statistics.put("NoConcretenessRating", 1.0 * (contentWordCount-wCNC)/contentWordCount);
        statistics.put("NoAoARating", 1.0 * (contentWordCount-wAoA)/contentWordCount);        
        //statistics.put("MeaningfulnessRatioColorado", (MEANC * wMEANC)/contentWordCount);
        statistics.put("MeaningfulnessColoradoFoundOnly", (wMEANC == 0.0 ? 0.0 : (MEANC / wMEANC)));
        //statistics.put("MeaningfulnessRatioPavio", (MEANP * wMEANP)/contentWordCount);
        statistics.put("MeaningfulnessPavioFoundOnly", (wMEANP == 0.0 ? 0.0 : (MEANP / wMEANP)));
        statistics.put("NegationCount", 1.0 * negationNo);
        statistics.put("NegationIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * negationNo)/wordCount));        
        statistics.put("NegationInStem", (negationNo != 0 ? 1.0 : 0.0));
        statistics.put("NegationInLead", 1.0 * negationInLead);        
        statistics.put("AgentlessPassiveCount", 1.0 * agentlessPassive);
        statistics.put("NPCount", 1.0 * item.getNumberNPs());
        statistics.put("AverageNPLength", item.getAverageNPLength());
        statistics.put("NPIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * item.getNumberNPs()) / wordCount));
        statistics.put("NPCountWithEmbedding", 1.0 * item.getNumberAllNPs());
        statistics.put("AverageAllNPLength", item.getAverageAllNPLength());
        statistics.put("NPIncidenceWithEmbedding", ((wordCount == 0) ? 0.0 :(1000.0 * item.getNumberAllNPs()) / wordCount));
        statistics.put("PPCount", 1.0 * item.getNumberPPs());
        statistics.put("PPIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * item.getNumberPPs())/wordCount));        
        statistics.put("PPsPerSentenceRatio", (1.0 * item.getNumberPPs()) / sentNo);
        statistics.put("VPCount", 1.0 * noClauses);
        statistics.put("VPIncidence", 1000.0 * noClauses / wordCount);
        statistics.put("VPCountStanford", 1.0 * item.getNumberVPs());
        statistics.put("PassiveActiveRatio", (1.0 * noPassives) / noClauses);
        statistics.put("ProportionActiveVPs", 1.0 * (noClauses-noPassives) / noClauses);
        statistics.put("ProportionPassiveVPs", 1.0 * noPassives / noClauses);
        statistics.put("ConditionalClausesCount", 1.0 * noConditionalClauses);
        statistics.put("ConditionalClausesIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * noConditionalClauses) / wordCount));
        statistics.put("RelativeClausesCount", 1.0 * noRelativeClauses);
        statistics.put("RelativeClausesIncidence", ((wordCount == 0) ? 0.0 :(1000.0 * noRelativeClauses) / wordCount));
        statistics.put("ProportionRelativeClauses", 1.0 * noRelativeClauses/noClauses);
        statistics.put("ConnectivesCount", 1.0 * connectives);
        statistics.put("ConnectivesIncidence", ((wordCount == 0) ? 0.0 :(1000.0 *connectives)/wordCount));
        statistics.put("TemporalConnectivesCount", 1.0 * tempConnectives);
        statistics.put("TemporalConnectivesIncidence", ((wordCount == 0) ? 0.0 :(1000.0 *tempConnectives)/wordCount));
        statistics.put("CausalConnectivesCount", 1.0 * causalConnectives);
        statistics.put("CausalConnectivesIncidence", ((wordCount == 0) ? 0.0 :(1000.0 *causalConnectives)/wordCount));
        statistics.put("AdditiveConnectivesCount", 1.0 * additiveConnectives);
        statistics.put("AdditiveConnectivesIncidence", ((wordCount == 0) ? 0.0 :(1000.0 *additiveConnectives)/wordCount));
        statistics.put("AnswerWordsInWordNetRatio", 1.0 * (wordsCorrect.size() + wordsDistractors.size() -  answerWordsNotInWN) / wordsInAnswers);
        
        statistics.put("ResnikSimilarityStemKey", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsCorrect, "Resnik"));
        statistics.put("ResnikSimilarityStemDistractors", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsDistractors, "Resnik"));
        statistics.put("ResnikSimilarityKeyDistractors", semSimCalc.getSemanticSimilarity(wordsCorrect, wordsDistractors, "Resnik"));
        statistics.put("ResnikSelfSimilarityStem", semSimCalc.getSemanticSelfSimilarity(wordsStemLead, "Resnik"));
//        statistics.put("ResnikSelfSimilarityFullItem", semSimCalc.getSemanticSelfSimilarity(wordsFullItem, "Resnik"));
        statistics.put("PathSimilarityStemKey", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsCorrect, "Path"));
        statistics.put("PathSimilarityStemDistractors", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsDistractors, "Path"));
        statistics.put("PathSimilarityKeyDistractors", semSimCalc.getSemanticSimilarity(wordsCorrect, wordsDistractors, "Path"));
        statistics.put("PathSelfSimilarityStem", semSimCalc.getSemanticSelfSimilarity(wordsStemLead, "Path"));
//        statistics.put("PathSelfSimilarityFullItem", semSimCalc.getSemanticSelfSimilarity(wordsFullItem, "Path"));
        statistics.put("LinSimilarityStemKey", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsCorrect, "Lin"));
        statistics.put("LinSimilarityStemDistractors", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsDistractors, "Lin"));
        statistics.put("LinSimilarityKeyDistractors", semSimCalc.getSemanticSimilarity(wordsCorrect, wordsDistractors, "Lin"));
        statistics.put("LinSelfSimilarityStem", semSimCalc.getSemanticSelfSimilarity(wordsStemLead, "Lin"));
//        statistics.put("LinSelfSimilarityFullItem", semSimCalc.getSemanticSelfSimilarity(wordsFullItem, "Lin"));        
        statistics.put("JiangConrathSimilarityStemKey", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsCorrect, "JiangConrath"));
        statistics.put("JiangConrathSimilarityStemDistractors", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsDistractors, "JiangConrath"));
        statistics.put("JiangConrathSimilarityKeyDistractors", semSimCalc.getSemanticSimilarity(wordsCorrect, wordsDistractors, "JiangConrath"));
        statistics.put("JiangConrathSelfSimilarityStem", semSimCalc.getSemanticSelfSimilarity(wordsStemLead, "JiangConrath"));
        //statistics.put("JiangConrathSelfSimilarityFullItem", semSimCalc.getSemanticSelfSimilarity(wordsFullItem, "JiangConrath"));       
        statistics.put("WuPalmerSimilarityStemKey", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsCorrect, "WuPalmer"));
        statistics.put("WuPalmerSimilarityStemDistractors", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsDistractors, "WuPalmer"));
        statistics.put("WuPalmerSimilarityKeyDistractors", semSimCalc.getSemanticSimilarity(wordsCorrect, wordsDistractors, "WuPalmer"));
        statistics.put("WuPalmerSelfSimilarityStem", semSimCalc.getSemanticSelfSimilarity(wordsStemLead, "WuPalmer"));
//        statistics.put("WuPalmerSelfSimilarityFullItem", semSimCalc.getSemanticSelfSimilarity(wordsFullItem, "WuPalmer"));        
        statistics.put("LeskSimilarityStemKey", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsCorrect, "Lesk"));
        statistics.put("LeskSimilarityStemDistractors", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsDistractors, "Lesk"));
        statistics.put("LeskSimilarityKeyDistractors", semSimCalc.getSemanticSimilarity(wordsCorrect, wordsDistractors, "Lesk"));
        statistics.put("LeskSelfSimilarityStem", semSimCalc.getSemanticSelfSimilarity(wordsStemLead, "Lesk"));
//        statistics.put("LeskSelfSimilarityFullItem", semSimCalc.getSemanticSelfSimilarity(wordsFullItem, "Lesk"));        
        statistics.put("LeacockChodorowSimilarityStemKey", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsCorrect, "LeacockChodorow"));
        statistics.put("LeacockChodorowSimilarityStemDistractors", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsDistractors, "LeacockChodorow"));
        statistics.put("LeacockChodorowSimilarityKeyDistractors", semSimCalc.getSemanticSimilarity(wordsCorrect, wordsDistractors, "LeacockChodorow"));
        statistics.put("LeacockChodorowSelfSimilarityStem", semSimCalc.getSemanticSelfSimilarity(wordsStemLead, "LeacockChodorow"));
        //statistics.put("LeacockChodorowSelfSimilarityFullItem", semSimCalc.getSemanticSelfSimilarity(wordsFullItem, "LeacockChodorow"));        
        //statistics.put("HirstStOngeSimilarityStemKey", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsCorrect, "HirstStOnge"));
        //statistics.put("HirstStOngeSimilarityStemDistractors", semSimCalc.getSemanticSimilarity(wordsStemLead, wordsDistractors, "HirstStOnge"));
        //statistics.put("HirstStOngeSimilarityKeyDistractors", semSimCalc.getSemanticSimilarity(wordsCorrect, wordsDistractors, "HirstStOnge"));
        //statistics.put("HirstStOngeSelfSimilarityStem", semSimCalc.getSemanticSelfSimilarity(wordsStemLead, "HirstStOnge"));
        //statistics.put("HirstStOngeSelfSimilarityFullItem", semSimCalc.getSemanticSelfSimilarity(wordsFullItem, "HirstStOnge"));                
        
    }

    //Returns true if the word contains 3 or more syllables
    private static boolean isComplex(String w) 
    {
	int syllableNo = SyllableFinder.syllableCount(w);
	return (syllableNo > 2);
    }
    
    //Returns the SMOG value for the text
    private double getSMOG(int sentences, int complex) 
    {
	double score = 1.043 * Math.sqrt(complex * (30.0 / sentences)) + 3.1291;
	return round(score, 2);
    }    

    //Returns the SMOG index of the text
    private double getSMOGIndex(int sentences, int complex) 
    {
        double score = Math.sqrt(complex * (30.0 / sentences)) + 3;
        return round(score, 2);
    }
    
    //Returns the Flesch Reading Ease value for the text
    private double getFleschReadingEase(int sentences, int words, int complex, int syllables) 
    {
        double score = 206.835 - 1.015 * words / sentences - 84.6 * syllables / words;
        return round(score, 2);
    }

    //Returns the Flesch-Kincaid Grade Level value for the text
    private double getFleschKincaidGradeLevel(int sentences, int words, int syllables) 
    {
        double score = 0.39 * words / sentences + 11.8 * syllables / words - 15.59;
        return round(score, 2);
    }

    //Returns the Automated Readability Index for text
    private double getARI(int sentences, int words, int characters) 
    {
        double score = 4.71 * characters / words + 0.5 * words / sentences - 21.43;
        return round(score, 2);
    }

    //Returns the Gunning-Fog index for text
    private double getGunningFog(int sentences, int words, int complex) 
    {
        double score = 0.4 * (words / sentences + 100 * complex / words);
	return round(score, 2);
    }

    //Returns the Coleman-Liau value for the text
    private double getColemanLiau(int sentences, int words, int characters) 
    {
        double score = (5.89 * characters / words) - (30 * sentences / words) - 15.8;
        return round(score, 2);
    }

    private static Double round(double d, int decimalPlace) 
    {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }   
        
    private double getDistanceToRoot(Synset[] synsets, SynsetType sType)
    {
        if (synsets.length == 0)
        {
            return 0.0;
        }
        double sumDistances = 0.0;
        for (Synset s : synsets)
        {
             sumDistances = sumDistances + getDistToRoot(s, sType);
        }
        return (sumDistances / synsets.length);
    }
    
    private double getDistToRoot(Synset s, SynsetType st)
    {
        Synset[] sh1;
        if (st.equals(SynsetType.NOUN))
        {
            sh1 = ((NounSynset)s).getHypernyms();
        }
        else 
        {
            sh1 = ((VerbSynset)s).getHypernyms();
        }
        if (sh1 == null || sh1.length == 0)
        {
            return 0.0;
        }
        double sum = 0.0;
        for (Synset syn : sh1)
        {
            sum = sum + getDistToRoot(syn, st) + 1;
        }
        return (sum / sh1.length);
    }
    
    private SynsetType getSynsetType(String pos)
    {
        if (pos.startsWith("NN"))
        {
            return SynsetType.NOUN;
        }
        if (pos.startsWith("VB"))
        {
            return SynsetType.VERB;
        }
        if (pos.startsWith("JJ"))
        {
            return SynsetType.ADJECTIVE;
        }
        if (pos.startsWith("RB"))
        {
            return SynsetType.ADVERB;
        }                    
        return null;
    }
    
}
