package FeatureExtraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

public class CorrelationCalculator 
{
    HashMap<String, ArrayList<Double>> featureValues;
    String features[];
    
    public CorrelationCalculator(String fileName)
    {
        featureValues = new HashMap();
        loadFeatureValueFile(fileName);
    }
    
    public void loadFeatureValueFile(String fileName)
    {
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String line = in.readLine();
            features = line.split("\t");
            for (String feat: features)
            {
                ArrayList<Double> val = new ArrayList();
                featureValues.put(feat, val);
            }
            while ((line = in.readLine()) != null) 
            {
                String datavalue[] = line.split("\t");
                // if either stat value is empty
                if (datavalue.length != features.length)
                {
                    continue;
                }
/*                
                // if either stat value is 0
                if (datavalue[datavalue.length-1].equals("0") || datavalue[datavalue.length-2].equals("0") || datavalue[datavalue.length-3].equals("0"))
                {
                    continue;
                }
*/        
                for (int i=1; i<datavalue.length; i++)
                {
                    if (features[i].equals("USAGETYPE"))
                    {
                        continue;
                    }
                    ArrayList<Double> val = featureValues.get(features[i]);
                    val.add(Double.parseDouble(datavalue[i]));
                    featureValues.remove(features[i]);
                    featureValues.put(features[i], val);
                }
            }
            in.close();
        } catch(Exception e){e.printStackTrace();};
    }
    
    public void calculatePearsonsCorrelations(String outFile)
    {
        try 
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
            PearsonsCorrelation correl = new PearsonsCorrelation();
            
            Double[] meantime = new Double[featureValues.get("Meantime").size()];
            featureValues.get("Meantime").toArray(meantime);

            Double[] meantimepw = new Double[featureValues.get("MeantimePerWord").size()];
            featureValues.get("MeantimePerWord").toArray(meantimepw);         

            out.write("PEARSONS\tMeantime\tMeantimePerWord");
            out.newLine();
            for (String feature: features)
            {
                if (feature.matches("^(MedleyID|Meantime|Mediantime|MeantimePerWord)$"))
                {
                    continue;
                }
                Double[] values = new Double[featureValues.get(feature).size()];
                featureValues.get(feature).toArray(values);
                System.out.println(feature + ":" + values.length);
                out.write(feature + "\t");
                out.write(String.format("%.2f", correl.correlation(ArrayUtils.toPrimitive(values), ArrayUtils.toPrimitive(meantime))) + "\t");
                out.write(String.format("%.2f", correl.correlation(ArrayUtils.toPrimitive(values), ArrayUtils.toPrimitive(meantimepw))) + "\t");
                out.newLine();
            }
            out.close();
        } catch (IOException ex) {ex.printStackTrace();};
    }
    
    public void calculateSpearmansCorrelations(String outFile)
    {
        try 
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
            SpearmansCorrelation correl = new SpearmansCorrelation();
            
            Double[] meantime = new Double[featureValues.get("Meantime").size()];
            featureValues.get("Meantime").toArray(meantime);
            
            Double[] meantimepw = new Double[featureValues.get("MeantimePerWord").size()];
            featureValues.get("MeantimePerWord").toArray(meantimepw);         
            
            out.write("SPEARMANS\tMeantime\tMeantimePerWord");
            out.newLine();
            for (String feature: features)
            {
                if (feature.matches("^(MedleyID|Meantime|Mediantime|MeantimePerWord)$"))
                {
                    continue;
                }
                Double[] values = new Double[featureValues.get(feature).size()];
                featureValues.get(feature).toArray(values);
                System.out.println(feature + ":" + values.length);
                out.write(feature + "\t");
                out.write(String.format("%.2f", correl.correlation(ArrayUtils.toPrimitive(values), ArrayUtils.toPrimitive(meantime))) + "\t");
                out.write(String.format("%.2f", correl.correlation(ArrayUtils.toPrimitive(values), ArrayUtils.toPrimitive(meantimepw))) + "\t");
                out.newLine();
            }
            out.close();
        } catch (IOException ex) {ex.printStackTrace();};
    }    
}
