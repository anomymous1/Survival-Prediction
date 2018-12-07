package FeatureExtraction;

import java.util.ArrayList;
import java.util.HashMap;

public class Word 
{
    int sentence;
    int ID;
    String word;
    String lemma;
    String pos;
    String next2words;
    int start;
    int end;
    int length;
    int depth;
    int wordIndex;
    HashMap<Integer, String> dependencyRelations; 
    
    public  Word()
    {
        this.dependencyRelations = new HashMap<>(); 
        this.next2words = "";
        this.depth = -1;
    }

    public void setID(int i)
    {
        this.ID = i;
    }
    
    public int getID()
    {
        return this.ID;
    }
    
    public void setWord(String w)
    {
        this.word = w;
    }
    
    public String getWord()
    {
        return this.word;
    }

    public void setLemma(String w)
    {
        this.lemma = w;
    }
    
    public String getLemma()
    {
        return this.lemma;
    }
    
    public void setPOS(String part_of_speech)
    {
        this.pos = part_of_speech;
    }
    
    public String getPOS()
    {
        return this.pos;
    }
    
    public void setStart(int s)
    {
        this.start = s;
    }
    
    public int getStart()
    {
        return this.start;
    }

    public void setEnd(int e)
    {
        this.end = e;
    }
    
    public int getEnd()
    {
        return this.end;
    }
    
    public void setLength(int l)
    {
        this.length = l;
    }
    
    public int getLength()
    {
        return this.length;
    }
    
    public void setNextTwoWords(String s)
    {
        this.next2words = s;
    }
   
    public String getNextTwoWords()
    {
        return this.next2words;
    }
   
    public void setSentence(int s)
    {
        this.sentence = s;
    }
    
    public int getSentence()
    {
        return this.sentence;
    }
    
    public void addDependencyRelation(int gov, String rel)
    {
        this.dependencyRelations.put(gov, rel);
    }
    
    public boolean isDependentOn (int wordID)
    {
        return this.dependencyRelations.containsKey(wordID);
    }
    
    public boolean isRoot()
    {
        return (this.dependencyRelations.containsKey(0) && this.dependencyRelations.get(0).equals("ROOT"));
    }
    
    public String getDependencyRelations()
    {
        String drs = "";
        for (int gov: this.dependencyRelations.keySet())
        {
            drs += gov + "-" + this.dependencyRelations.get(gov) + ";";
        }
        return drs;
    }
    
    public ArrayList<Integer> getHeads()
    {
        ArrayList<Integer> heads = new ArrayList<>();
        for (int gov: this.dependencyRelations.keySet())
        {
            heads.add(gov);
        }        
        return heads;
    }    
    
    public void setDepth(int d)
    {
        this.depth = d;
    }
    
    public int getDepth()
    {
        return this.depth;
    }
    
    public void setWordIndex(int idx)
    {
        this.wordIndex = idx;
    }
    
    public int getWordIndex()
    {
        return this.wordIndex;
    }        
}
