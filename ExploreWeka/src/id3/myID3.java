/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id3;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.Attribute;
import java.util.ArrayList;
import java.util.Enumeration;
import weka.core.AttributeStats;
/**
 *
 * @author ASUS X202E
 */
public class myID3 extends Classifier {
    Node root;

    public myID3(Instances data) {
  
    }
    
    public void buildClassifier(Instances data) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i=0;i<data.numAttributes();i++) {
            attributes.add(data.attribute(i));
        }
        attributes.remove(data.classAttribute());
        root = id3(data,attributes);
    }
    
    public void classifyInstances(Instance instance) {
        
    }
    
    private Node id3(Instances data, ArrayList<Attribute> attributes) {
        if (data.numClasses()==1) return new Node(data.firstInstance().classValue());
        else if (attributes.size()==0) return new Node(mostCommonLabel(data));
        else {
            Node node = new Node();
            Attribute bestAttribute = findBestAttribute(data,attributes);
            Enumeration attValues = bestAttribute.enumerateValues();
            while (attValues.hasMoreElements()) {
                String value = (String)attValues.nextElement();
                Node child = new Node(bestAttribute.name(),value);
                Instances subset = new Instances(data);
                subset.delete();
                for (int i=0; i<data.numInstances(); i++) {
                    Instance currInstance = data.instance(i);
                    if (currInstance.stringValue(bestAttribute).equals(value)) subset.add(currInstance);
                }
                if (subset.numInstances()==0) {
                    child.addChild(new Node(mostCommonLabel(data)));
                } else {
                    attributes.remove(bestAttribute);
                    Node childBranch = id3(subset,attributes);
                    child.addChild(childBranch);
                }
                node.addChild(child);
            }
            return node;
        }
    }
    
    public Attribute findBestAttribute(Instances data, ArrayList<Attribute> attributes) {
        //hitung pake entropy & ig & syalala
	double[] result = findMaxIG(data) ;
        Attribute attr = data.attribute((int)result[0]);
        return attr;
    }
    
    private double mostCommonLabel(Instances data) {
        for (int i=0;i<data.numInstances();i++) {
            //double value = data.instance(i).classValue();
            //hitung value mana yang paling banyak
           
        }
        //return value yg paling banyak        
	return 0;
    }
    
    //Menghitung nilai Entropy S
    public double computeEntropyS(Instances data) {
	//find the positive result
	int classIdx = data.classIndex();
	AttributeStats attributeStats = data.attributeStats(classIdx);
	int[] classCount = attributeStats.nominalCounts;
	int sum = 0;
	for (int i = 0; i<classCount.length; i++) {
	    sum+=classCount[i];
	}
	double result = 0;
	for (int i = 0; i<classCount.length; i++) {
	    double p = (double) classCount[i]/sum;
	    result += -1*p*log2(p);
	}
	return result;
    }
    
    //Menghitung information gain dari data untuk atribut ke-numberIndex
    public double computeIG(Instances data, int numberIndex) {
        // Pertama hitung Entropy(S)
	double entropyS = computeEntropyS(data);
	// Lalu hitung entropi per atribut Entropy(Sv)
	double entropySv = countReminder(data,numberIndex);
	
	return entropyS - entropySv;
	
    }
    
    //Menghitung entropy untuk suatu value atribut, misalnya I(2/5,1/5,2/5)
    public double countEntropy(double[] data) {
        double sum = 0 ;
        double total = 0;
        for (int i=0;i<data.length;i++) total+=data[i];
        //System.out.println("total : "+total);
        for (int i=0;i<data.length;i++) {
            double temp = (double) data[i]/total ;
          //  System.out.println(data[i]+"|"+temp);
            sum+=(-1*temp*log2(temp));
        }
        return sum ;
    }
    
    //Menghitung nilai reminder suatu atribut ( IG(A) = Entropy(S) - Reminder(A) )
    public double countReminder(Instances data, int numberIndex) {
        int TOTAL_DISTINCT_LABEL = getTotalDistinctLabel(data);
        int TOTAL_INSTANCES = data.numInstances();
        AttributeStats attrStats = data.attributeStats(numberIndex);
        int TOTAL_DISTINCT_VALUE = attrStats.distinctCount;
        Attribute attr = data.attribute(numberIndex);
//        System.out.println("Attr : "+data.attribute(numberIndex).name()+"|"+TOTAL_DISTINCT_VALUE);
        int[] attrCount = attrStats.nominalCounts;
        double sum = 0 ;
        int enumResult[][] = enumData(data,attr,TOTAL_DISTINCT_VALUE,TOTAL_DISTINCT_LABEL,attrCount);
        for (int i = 0;i<TOTAL_DISTINCT_VALUE;i++) {
    //        System.out.println("-- "+attr.value(i));
            double[] tempD = new double[TOTAL_DISTINCT_LABEL];
            for (int j=0;j<TOTAL_DISTINCT_LABEL;j++) {
                //System.out.println(i+"|"+j);
                tempD[j] = enumResult[i][j];
    //            System.out.print(" -> "+tempD[j]);
            }
     //       System.out.println("\n"+attrCount[i]+"/"+TOTAL_INSTANCES);
          double entropy = countEntropy(tempD) ;
        //    System.out.println("Entropy "+attr.value(i)+" : "+entropy);
            sum+=(double) attrCount[i]/TOTAL_INSTANCES*entropy;
        }
//        for (int i=0;i<TOTAL_DISTINCT_VALUE;i++) {
//            System.out.print("Enum Tbable : ");
//            for (int j=0;j< TOTAL_DISTINCT_LABEL;j++) {
//               System.out.print(enumResult[i][j]+" ");
//            }
//            System.out.println(" *"+i);
//        }
        return sum ;
    }
    
    /*
        Untuk melakukan enumerasi terhadap data untuk suatu atribut. Misalnya atribut X, yang bernilai A dengan label yes 5,
        label no 3, yang bernilai B label yes 4, label no 2.
        totalAttr => jumlah distinct value di 1 atribut
        totalLabel => jumlah distinct label        
    */
    public int[][] enumData(Instances data,Attribute attr, int totalAttr, int totalLabel, int[] attrCount) {
       // System.out.println("enum data | totalData : "+data.numInstances());
        int[][] attrClassTotal = new int[totalAttr][totalLabel];
        int curIdx = 0;
        int classIdx = data.classIndex();
	data.sort(attr);
        Instances tempData = new Instances(data,0,attrCount[0]);
        curIdx+=attrCount[0];
        AttributeStats attributeStats = tempData.attributeStats(classIdx);
	int[] nominalCount = attributeStats.nominalCounts;
        for (int i=0;i<nominalCount.length;i++) {
            attrClassTotal[0][i] = nominalCount[i];
        }
        for (int i=1;i<totalAttr;i++) {
            //System.out.println("Enum data | i :"+i+"|"+curIdx+"|"+(curIdx+attrCount[i]));
            tempData = new Instances(data,curIdx,attrCount[i]);
            curIdx+=attrCount[i];
            attributeStats = tempData.attributeStats(classIdx);
	    nominalCount = attributeStats.nominalCounts;
            for (int j=0;j<nominalCount.length;j++) {
                attrClassTotal[i][j] = nominalCount[j];
            }      
        }
        return attrClassTotal ;
    }
    
    //Menghitung information gain maksimal untuk suatu data
    public double[] findMaxIG(Instances data) {
        double maxGain = 0 ;
        int attrIdx = 0 ;
        for (int i=0;i<data.numAttributes()-1;i++) {
            double temp_IG = computeIG(data, i);
            if (temp_IG > maxGain) {
                maxGain = temp_IG ;
                attrIdx = i ;
            }
        }
        double[] maxGainContainer = new double[2] ;
        maxGainContainer[0] = attrIdx ;
        maxGainContainer[1] = maxGain ;
        return maxGainContainer;
    }
    
    //Menghitung jumlah label (kelas) pada suatu data
    public int getTotalDistinctLabel(Instances data) {
        int classIdx = data.classIndex();
	AttributeStats attributeStats = data.attributeStats(classIdx);
        return attributeStats.distinctCount;
    }
    
    public double log2(int input) {
        if (input==0) return 0 ;
         return Math.log(input)/Math.log(2);
    }
    
    public double log2(double input) {
       if (input==0) return 0 ;
         return Math.log(input)/Math.log(2);
    }
}
