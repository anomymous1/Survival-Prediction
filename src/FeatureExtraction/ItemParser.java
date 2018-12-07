package FeatureExtraction;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author Georgiana
 */
public class ItemParser 
{
    StanfordParser stanfordp;    
    HashMap<String, String> rtimes;
    
    public ItemParser()
    {        
        stanfordp = new  StanfordParser();   
        loadStats("../TestItemsVictoria/ReadingTimes/MeanReadingTimes.txt");
    }  

    private void loadStats(String statsFile)
    {
        // Loading file with reading times
        System.out.println("Loading stats file.");
        rtimes = new HashMap<>();
        BufferedReader in;
        try
        {
            Pattern p = Pattern.compile("^(.*)\\t(.*)$");
            in = new BufferedReader(new FileReader(statsFile));
            String line;
            while ((line = in.readLine()) != null)
            {
                Matcher m = p.matcher(line);
                if (m.matches())
                {
                    rtimes.put(m.group(1), m.group(2));
                }
            }
            in.close();
        }
        catch (IOException e){}                
        System.out.println("Finished loading reading times file.");
    }  
    
    public Item parseFile(File inFile)
    {
        Item doc = new Item();
        String newLine = String.format("%n");
        if (!inFile.exists()) 
        {
            System.out.println("Input file not found: " + inFile.getName());
            return null;
        }
        System.out.println("Processing input file: " + inFile.getName());
        
        // Get each relevant String from the item and then parse it
        BufferedReader in;
        try
        {
            in = new BufferedReader(new FileReader(inFile));
            String medleyID = inFile.getName().substring(0, inFile.getName().indexOf("."));
            String line;
            String textType = "";
            String correctAnswer = "";
            String field_FULL = "";
            String field_CORRECT = "";
            String field_WRONG = "";
            String field_STEM = "";
            String field_LEAD = "";
            int startCorrect = 0;
            boolean startedStem = false;
            boolean startedAnswers = false;
            ArrayList<String> stemAndLead = new ArrayList<>();
            int leads = 0;
            while ((line = in.readLine()) != null)
            {
                line = line.trim();
                if (Pattern.matches("^\\s*START.*$", line))
                {
                    String infoStart = "";
                    Pattern p = Pattern.compile("^\\s*START (.*)$");
                    Matcher m = p.matcher(line);
                    if (m.matches())
                    {
                        infoStart = m.group(1);
                    }
                    if (infoStart.startsWith("TEXT")) // stem start
                    {
                        doc.setID(medleyID);
                        startedStem = true;
                        textType = "STEM";
                        doc.setStart("STEM", field_FULL.length());
                    }
                    else if (infoStart.startsWith("ANSWER")) // start options
                    {
                        textType = "WRONG";
                        startedAnswers = true;
                        doc.setStart("ANSWERS", field_FULL.length());
                    }
                }
                else if (Pattern.matches("^\\s*END.*$", line))
                {
                    String infoEnd = "";
                    Pattern p = Pattern.compile("^\\s*END (.*)$");
                    Matcher m = p.matcher(line);
                    if (m.matches())
                    {
                        infoEnd = m.group(1);
                    }
                    if (infoEnd.startsWith("TEXT")) // stem end
                    {
                        startedStem = false;
                        for (int csl=0;csl<stemAndLead.size()-1; csl++)
                        {
                            field_STEM = field_STEM + "\n" + stemAndLead.get(csl).replaceAll("&quot;", "\"");
                            field_FULL = field_FULL + "\n" + stemAndLead.get(csl).replaceAll("&quot;", "\"");
                        }
                        field_FULL = field_FULL.trim();
                        doc.setEnd("STEM", field_FULL.length());
                        doc.setStart("LEAD", field_FULL.length());
                        field_LEAD = field_LEAD + stemAndLead.get(stemAndLead.size()-1).replaceAll("&quot;", "\"");
                        field_FULL = field_FULL + "\n" + stemAndLead.get(stemAndLead.size()-1).replaceAll("&quot;", "\"");
                        field_FULL = field_FULL.trim();
                        doc.setEnd("LEAD", field_FULL.length());
                    }
                    else if (infoEnd.startsWith("ANSWER")) // end options
                    {
                        startedAnswers = false;
                        doc.setEnd("ANSWERS", field_FULL.length());
                    }
                }
                else if (line.startsWith("ANSWER"))//(Pattern.matches("^\\s*ANSWER.*$", line))
                {
                    Pattern p = Pattern.compile("^\\s*ANSWER: (.*)$");
                    Matcher m = p.matcher(line);
                    if (m.matches())
                    {
                        correctAnswer = m.group(1).toLowerCase();
                    }                    
                }
                else if (startedStem && ! Pattern.matches("^\\s*$", line))
                {

                    HashMap<Integer, Word> wordMap = stanfordp.parse(line);
                    Set<Integer> s;
                    Integer[] keys = new Integer[wordMap.keySet().size()];
                    wordMap.keySet().toArray(keys);
                    Arrays.sort(keys);
                    Word lastWord = wordMap.get(keys[keys.length-1]);
                    int lastSentence = lastWord.getSentence();
                    int firstWordIdx = keys.length-1;
                    while (firstWordIdx>0 && wordMap.get(keys[firstWordIdx]).getSentence() == lastSentence)
                    {
                        firstWordIdx--;
                    }
                    String stemText = "";
                    String leadText;
                    if (firstWordIdx == 0)
                    {
                        leadText = line;
                    }
                    else
                    {
                        stemText = line.substring(0, keys[firstWordIdx+1]-1);
                        leadText = line.substring(keys[firstWordIdx+1]);
                    }
                    if (!Pattern.matches("^\\s*$", stemText))
                    {
                        stemAndLead.add(stemText);
                    }
                    stemAndLead.add(leadText);
                    leads++;
                }
                else if (startedAnswers && Pattern.matches("^\\s*.\\).*$", line))
                {
                    Pattern p = Pattern.compile("^\\s*(.)\\).*$");
                    Matcher m = p.matcher(line);
                    String answer = "";
                    if (m.matches())
                    {
                        answer = m.group(1);
                    }
                    if (answer.equals(correctAnswer))
                    {
                        doc.setStart("CORRECT", field_FULL.length());
                        startCorrect = field_FULL.length();
                        textType = "CORRECT";
                        field_CORRECT = field_CORRECT + line.replaceAll("&quot;", "\"");
                    }
                    else
                    {
                        textType = "WRONG";
                        field_WRONG = field_WRONG + newLine + line.replaceAll("&quot;", "\"");
                    }
                    field_FULL = field_FULL + newLine + line.replaceAll("&quot;", "\"");                                            
                    field_FULL = field_FULL.trim();
                }
                else
                {
                    if (textType.equals("STEM")||textType.equals("LEAD"))
                    {
                        stemAndLead.add(line);
                    }
                    else
                    {
                        if (textType.equals("CORRECT"))
                        {
                            field_CORRECT = field_CORRECT + line.replaceAll("&quot;", "\"");
                        }
                        else
                        {
                            field_WRONG = field_WRONG + newLine + line.replaceAll("&quot;", "\"");
                        }
                        field_FULL = field_FULL + newLine + line.replaceAll("&quot;", "\"");
                        field_FULL = field_FULL.trim();
                    }
                }  
            }
            startCorrect += field_CORRECT.length();
            doc.setEnd("CORRECT", startCorrect);
            doc.setFullText(field_FULL);
            doc.setNoRows(0);
            System.out.println("field_FULL:"+field_FULL);
            doc.setParsedItem(stanfordp.parseWithoutPunctuation(field_FULL));
            doc.setCommaCount(stanfordp.getCommaCount());
            doc.setNumberNPs(stanfordp.getNumberNPs());
            doc.setAverageNPLength(stanfordp.getAverageNPLength());
            doc.setNumberAllNPs(stanfordp.getNumberAllNPs());
            doc.setAverageAllNPLength(stanfordp.getAverageAllNPLength());
            doc.setNumberVPs(stanfordp.getNumberVPs());    
            doc.setNumberPPs(stanfordp.getNumberPPs());         
            if (rtimes.containsKey(medleyID))
            {
                doc.setReadingTime(rtimes.get(medleyID));
            }            
            System.out.println("========TEXT======\n"+field_FULL+"\n=======================\n");
            //System.out.println(stanfordp.printParsedText(doc.getParsedItem())+"\n");
            in.close();
        }
        catch (Exception e){e.printStackTrace();}    
        return doc;
    }        
}
