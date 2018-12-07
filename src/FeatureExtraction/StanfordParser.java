package FeatureExtraction;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.*;
import java.util.*;
import java.util.regex.Pattern;

public class StanfordParser 
{
    StanfordCoreNLP pipeline;
    private static final Pattern PUNCT_PATTERN = Pattern.compile("[\\p{Punct}]");
    int commaCount;
    int noNPs, noAllNPs, lengthNPs, lengthAllNPs; // *allNPs refer to all NPs including the embedded ones
    int noVPs, noPPs;

    public StanfordParser()
    {
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, parse");  // added ,parse on 30/01/2014
        pipeline = new StanfordCoreNLP(props);
    }
    
    public HashMap<Integer, Word> parse(String text)
    {
        HashMap<Integer, Word> output = new HashMap<>();
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);  
        // run all Annotators on this text
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        int sent = 0;
        for(CoreMap sentence: sentences) 
        {
            ArrayList<Word> crtSent = new ArrayList<>();
            crtSent.add(new Word());
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) 
            {
                Word w = new Word();
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                w.setWord(word);
                w.setLemma(token.lemma());
                w.setPOS(pos);
                w.setStart(token.beginPosition());
                w.setEnd(token.endPosition()-1);
                w.setLength(token.endPosition()-token.beginPosition());
                w.setSentence(sent);
                crtSent.add(w);
            }
            sent++;
            // this is the Stanford dependency graph of the current sentence
            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class); 
            Collection<TypedDependency> tdl = dependencies.typedDependencies();
            int rootIndex = -1;
            int depth = -1;
            if (!dependencies.getRoots().isEmpty())
            {
                rootIndex = dependencies.getFirstRoot().index();
                Word rad = crtSent.get(rootIndex);
                rad.addDependencyRelation(0, "ROOT");
                crtSent.set(rootIndex, rad);
            }
            for (TypedDependency td: tdl)
            {
                Word crtW = crtSent.get(td.dep().index());
                crtW.addDependencyRelation(td.gov().index(), td.reln().getShortName());
                crtSent.set(td.dep().index(), crtW);
                if (depth == -1 && td.gov().index() == rootIndex)
                {
                    depth = td.gov().depth();
                }
            }
            if (rootIndex != -1)
            {
                crtSent.get(rootIndex).setDepth(depth);
            }
            crtSent.get(crtSent.size()-1).addDependencyRelation(0, "EOS"); // EOS will indicate the end of a sentence
            crtSent.remove(0);
            for (Word crtW: crtSent)
            {
                output.put(crtW.getStart(), crtW);
            }
        }
        return output;
    }
    
    public ArrayList<HashMap<Integer, Word>> parseWithoutPunctuation(String text)
    {
        ArrayList<HashMap<Integer, Word>> output = new ArrayList<>();
        commaCount = 0;  
        noNPs = 0;
        noVPs = 0;
        noPPs = 0;
        noAllNPs = 0;
        lengthNPs = 0;
        lengthAllNPs = 0;
        
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
   
        // run all Annotators on this text
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        int sent = 0;
        for(CoreMap sentence: sentences) 
        {
            HashMap<Integer, Word> out = new HashMap<>();
            ArrayList<Word> crtSent = new ArrayList<>();
            crtSent.add(new Word());
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) 
            {
                Word w = new Word();
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                w.setWord(word);
                w.setLemma(token.lemma());
                w.setPOS(pos);
                w.setStart(token.beginPosition());
                w.setEnd(token.endPosition()-1);
                w.setLength(token.endPosition()-token.beginPosition());
                w.setSentence(sent);
                w.setWordIndex(crtSent.size());
                crtSent.add(w);
                if (word.equals(","))
                {
                    commaCount++;
                }
            }
            sent++;
            HashMap<Integer, Integer> nps = new HashMap<Integer, Integer>();            
            Tree tree = sentence.get(TreeAnnotation.class);
            tree.indentedXMLPrint();
            Iterator it = tree.iterator();
            while (it.hasNext())
            {
                Tree element = (Tree)it.next();
                if (element.isPhrasal() && element.label().value().equals("NP"))
                {
                    int startNP = tree.leftCharEdge(element);
                    int endNP = tree.rightCharEdge(element) + element.getLeaves().size() - 1;
                    int length = endNP - startNP;
                    noAllNPs++;
                    lengthAllNPs = lengthAllNPs + length;                    
                    if (!nps.containsKey(startNP))
                    {
                        nps.put(startNP, length);
                    }
                    else if (nps.get(startNP).intValue() < length)
                    {
                        nps.put(startNP, length);
                    }
                }
                else if (element.isPhrasal() && element.label().value().equals("VP"))
                {
                    noVPs++;
                }       
                else if (element.isPhrasal() && element.label().value().equals("PP"))
                {
                    noPPs++;
                }                
            }
            noNPs = noNPs + nps.keySet().size();
            for (int startNP: nps.keySet())
            {
                lengthNPs = lengthNPs + nps.get(startNP);
            }                       
            // this is the Stanford dependency graph of the current sentence
            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class); 
            Collection<TypedDependency> tdl = dependencies.typedDependencies();
            int rootIndex = -1;
            if (!dependencies.getRoots().isEmpty())
            {
                rootIndex = dependencies.getFirstRoot().index();
                Word rad = crtSent.get(rootIndex);
                rad.addDependencyRelation(0, "ROOT");
                rad.setDepth(tree.depth());
                crtSent.set(rootIndex, rad);
            }
            for (TypedDependency td: tdl)
            {
                Word crtW = crtSent.get(td.dep().index());
                crtW.addDependencyRelation(td.gov().index(), td.reln().getShortName());
                crtSent.set(td.dep().index(), crtW);  
            }
            //crtSent.get(rootIndex).setDepth(depth);
            crtSent.get(crtSent.size()-1).addDependencyRelation(0, "EOS"); // EOS will indicate the end of a sentence
            crtSent.remove(0);
            int ii =0;
            while (ii<crtSent.size())
            {
                Word crtW = crtSent.get(ii);
                if ((ii+2) < crtSent.size())
                {
                    crtW.setNextTwoWords(crtSent.get(ii+1).getLemma() + " " + crtSent.get(ii+2).getLemma());
                }
                else if ((ii+1) < crtSent.size())
                {
                    crtW.setNextTwoWords(crtSent.get(ii+1).getLemma());
                }
                if ((ii+2)<crtSent.size() && crtW.getWord().toLowerCase().matches("^(a|b|c|d|e|f|g|h)$") && crtSent.get(ii+1).getWord().toLowerCase().equals("-rrb-"))
                {
                    ii += 2;
                }
                else if (!PUNCT_PATTERN.matcher(crtW.getWord()).matches())
                {
                    out.put(crtW.getStart(), crtW);
                }
                ii++;
            }
            output.add(out);
        }
        return output;
    }   
    
    int getCommaCount()
    {
        return commaCount;
    }
    
    double getAverageNPLength()
    {
        return (1.0 * lengthNPs) / noNPs;
    
    }
    
    int getNumberNPs()
    {
        return noNPs;
    }
    
    double getAverageAllNPLength()
    {
        return (1.0 * lengthAllNPs) / noAllNPs;
    }
    
    int getNumberAllNPs()
    {
        return noAllNPs;
    }

    int getNumberVPs()
    {
        return noVPs;
    }
    
    int getNumberPPs()
    {
        return noPPs;
    }
    
    String printParsedSentence(HashMap<Integer, Word> parsedSent)
    {
        String pps = "";
        Integer[] keys = new Integer[parsedSent.keySet().size()];
        parsedSent.keySet().toArray(keys);
        Arrays.sort(keys);
        int wordcount = 1;
        for (int poz: keys)
        {
            Word w = parsedSent.get(poz);
            pps = pps + wordcount + "\t" + w.getWord() + "\t" + w.getLemma() + "\t" + w.getPOS()+ "\t" + w.getDependencyRelations()+"\n";
            wordcount++;
        }
        pps = pps + "<s>\t<s>";
        return pps;
    }
    
    String printParsedText(ArrayList<HashMap<Integer, Word>> parsedText)
    {
        String ppt = "";
        for (HashMap<Integer, Word> sent : parsedText)
        {
            ppt = ppt + printParsedSentence(sent) + "\n";
        }
        return ppt;
    }
    
}
