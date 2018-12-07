package FeatureExtraction;

import java.util.ArrayList;
import java.util.HashMap;

public class Item 
{
    String ItemID, fullText, newStats, mrt;
    HashMap<String, Integer> startOffset, endOffset;
    ArrayList<HashMap<Integer, Word>> parsedItem;
    String ageGroup, age, gender, problem;
    int ageInDays, commaCount;   
    int noRows;
    double avgNPLength, avgAllNPLength;
    int noPPs, noNPs, noAllNPs, noVPs;
    
    boolean answersAsImage; // This applies only to the items from the Item Coding project.
    
    
    public Item()
    {
        this.startOffset = new HashMap<>();
        this.endOffset = new HashMap<>();
        
        this.newStats = null;
    }
    
    public void setID(String id)
    {
        this.ItemID = id;
    }
    
    public String getID()
    {
        return this.ItemID;
    }
    
    public void setFullText(String text)
    {
        this.fullText = text;        
    }
    
    public String getFullText()
    {
        return this.fullText;
    }
    
    public void setParsedItem(ArrayList<HashMap<Integer, Word>> output)
    {
        this.parsedItem = output;
    }
    
    public ArrayList<HashMap<Integer, Word>> getParsedItem()
    {
        return this.parsedItem;
    }
    
    public void setStart(String partType, int offset)
    {
        this.startOffset.put(partType, offset);
    }
    
    public int getStart(String partType)
    {
        if (this.startOffset.containsKey(partType))
        {
            return this.startOffset.get(partType);
        }
        return -1;
    }
    
    public void setEnd(String partType, int offset)
    {
        this.endOffset.put(partType, offset);
    }
    
    public int getEnd(String partType)
    {
        if (this.endOffset.containsKey(partType))
        {
            return this.endOffset.get(partType);
        }
        return -1;
    }  

    public void setAgeInDays(int aid)
    {
        this.ageInDays = aid;
    }
    
    public int getAgeInDays()
    {
        return this.ageInDays;
    }
    
    public void setAgeGroup(String ag)
    {
        this.ageGroup = ag;
    }

    public String getAgeGroup()
    {
        return this.ageGroup;
    }
    
    public String getAge()
    {
        return this.age;
    }
    
    public void setAge(String a)
    {
        this.age = a;
    }

    public String getGender()
    {
        return this.gender;
    }
    
    public void setGender(String g)
    {
        this.gender = g;
    }
    
    public String getProblem()
    {
        return this.problem;
    }
    
    public void setProblem(String p)
    {
        this.problem = p;
    }     
    
    
    
    public void setNewStats(String ns)
    {
        this.newStats = ns;
    }
    
    public String getNewStats()
    {
        return this.newStats;
    }
    
    public void setCommaCount(int cc)
    {
        this.commaCount = cc;
    }
    
    public int getCommaCount()
    {
        return this.commaCount;
    }
    
    public void setAverageNPLength(double cc)
    {
        this.avgNPLength = cc;
    }
    
    public double getAverageNPLength()
    {
        return this.avgNPLength;
    }    

    public void setAverageAllNPLength(double cc)
    {
        this.avgAllNPLength = cc;
    }
    
    public double getAverageAllNPLength()
    {
        return this.avgAllNPLength;
    }     

    public void setNumberNPs(int cc)
    {
        this.noNPs = cc;
    }
    
    public int getNumberNPs()
    {
        return this.noNPs;
    } 
    
    public void setNumberAllNPs(int cc)
    {
        this.noAllNPs = cc;
    }
    
    public int getNumberAllNPs()
    {
        return this.noAllNPs;
    } 
    
    public void setNumberVPs(int cc)
    {
        this.noVPs = cc;
    }
    
    public int getNumberVPs()
    {
        return this.noVPs;
    } 
    
    public void setNumberPPs(int cc)
    {
        this.noPPs = cc;
    }
    
    public int getNumberPPs()
    {
        return this.noPPs;
    }      
    
    public void setNoRows(int nr)
    {
        this.noRows = nr;
    }
    
    public int getNoRows()
    {
        return this.noRows;
    }
    
    public void setAnswersAsImage(boolean ans)
    {
        this.answersAsImage = ans;
    }
    
    public boolean getAnswersAsImage()
    {
        return this.answersAsImage;
    }
    
    public ArrayList<Integer> getAllDependentWords(HashMap<Integer, Word> sentence, int wordIndex)
    {
        ArrayList<Integer> depWords = new ArrayList<>();
        for (Word w : sentence.values())
        {
            if (w.isDependentOn(wordIndex))
            {
                depWords.add(w.getWordIndex());
            }
        }
        return depWords;
    }
    
    public void setReadingTime(String r)
    {
        this.mrt = r;
    }
    
    public String getReadingTime()
    {
        return this.mrt;
    }
}
