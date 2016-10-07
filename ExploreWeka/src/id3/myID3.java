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
    public Node root ;
    Instances trainingData ;
    Instances testData ;
    public static final String ROOT_STRING = "root" ;
    public int[][] confussionMatrix ;
    
    public myID3(Instances data) {
        root = new Node() ;
        root.setAttribute(null);
        root.setLabel(null);
        root.setAttributeValue(ROOT_STRING);
        root.setChildren(new ArrayList<Node>());
        this.trainingData = data ;
    }
    
    public void classifyInstances(Node model , Instances testSet) {
        this.testData = testSet ;
        AttributeStats attrStats = trainingData.attributeStats(trainingData.classIndex());
        int[] nominalCount = attrStats.nominalCounts;
        int distinctClass = getTotalDistinctLabel(trainingData);
        confussionMatrix = new int[distinctClass][distinctClass];
        for (int i=0;i<distinctClass;i++) {
            for (int j=0 ; j<distinctClass;j++) confussionMatrix[i][j]=0;
        }
        Attribute attr = trainingData.classAttribute() ;
        //attr.indexOfValue(ROOT_STRING)
        for (int i=0;i<testSet.numInstances();i++) {
            String labelFromClassf = classifyOneInstance(model,testSet.instance(i));
            String labelFromData = testSet.instance(i).stringValue(attr);
            int idxLabelFromClassf = attr.indexOfValue(labelFromClassf);
            int idxLabelFromData = attr.indexOfValue(labelFromData);
            confussionMatrix[idxLabelFromClassf][idxLabelFromData]++ ;
        }
    }
    
    public double[] countAccuracy() {
        int rightClassif = 0, totalData = 0 ;
        double[] accuracy = new double[2] ;
        for (int i=0;i<getTotalDistinctLabel(trainingData);i++) {
            for (int j=0; j<getTotalDistinctLabel(trainingData);j++) {
                totalData+=confussionMatrix[i][j] ;
                if (i==j) rightClassif+=confussionMatrix[i][j] ;
            }
        }
        accuracy[0] = rightClassif ;
        accuracy[1] = totalData ;
        return accuracy ;
    }
    
    public void printSummary() {
        printConfussionMatrix() ;
        System.out.println() ;
        double[] accuracy = countAccuracy() ;
        System.out.println("\nTotal Instances : "+accuracy[1]);
        System.out.println("Right classification : "+accuracy[0]+" ( "+(double)accuracy[0]/accuracy[1]*100+"% ) ");
    }
    
    public void printConfussionMatrix() {       
        Attribute attr = trainingData.classAttribute() ;
        AttributeStats attrStats = trainingData.attributeStats(trainingData.classIndex());
        System.out.print("\n#");
        for (int i=0 ; i<attrStats.distinctCount;i++) System.out.print("\t"+attr.value(i));
        for (int i=0; i<attrStats.distinctCount;i++) {
            System.out.print("\n"+attr.value(i));
            for (int j=0; j<attrStats.distinctCount;j++) {
                //System.out.print(i+"|"+j+"\n");
                System.out.print("\t"+confussionMatrix[i][j]);
            }
        }
    }
    
    public String classifyOneInstance(Node model ,Instance instance) {
        if (model.getChildren().size()==0) {
            String attrName = model.getAttribute().name() ;
            String attrValue = model.getAttributeValue();
            if (checkAttrValueSame(instance,attrName,attrValue)) {
                    return model.getLabel();
            }
        } else {
            for (int i=0 ; i< model.getChildren().size();i++) {
                String attrName = model.getChildren().get(i).getAttribute().name() ;
                String attrValue = model.getChildren().get(i).getAttributeValue();
                if (checkAttrValueSame(instance,attrName,attrValue)) {
                    return classifyOneInstance(model.getChildren().get(i),instance);
                } 
            }
        }
        return "unclassified";
    }
    
    public boolean checkAttrValueSame(Instance instance, String attrName, String attrValue) {
        String instanceAttrVal= instance.stringValue(trainingData.attribute(attrName));
        return attrValue.equals(instanceAttrVal);
    }
    
    public int findAttributeIndex(String value, Instance instance) {
        for (int i=0;i<instance.numAttributes();i++) {
            if (instance.attribute(i).name().equals(value)) return i ;
        }
        return -1 ;
    }
    
    public void printSpace(int x) {
        for (int i=0 ; i<x ; i++) System.out.print(" ");
    }
    
    public void printTree(Node tree,int totalSpace) {
        if (tree.getChildren().size()==0) {
            printSpace(totalSpace);
            if (tree.getAttribute() == null) System.out.print("Attribut : null");
            else System.out.print("Attribut : "+tree.getAttribute().name());
            System.out.println(" | Value : "+tree.getAttributeValue()+" | Label : "+tree.getLabel());
        } else {
            printSpace(totalSpace);
            if (tree.getAttribute() == null) System.out.print("Attribut : null");
            else System.out.print("Attribut : "+tree.getAttribute().name());
            System.out.println(" | Value : "+tree.getAttributeValue()+" | Label : "+tree.getLabel());
            for (int i=0;i<tree.getChildren().size();i++) {
                printTree(tree.getChildren().get(i),totalSpace+2);
            }
        }
    }
    public void buildID3(Node root, Instances data) {
        Attribute bestAttribute = findBestAttribute(data);
        AttributeStats attributeStats = data.attributeStats(bestAttribute.index());
        Enumeration attValues = bestAttribute.enumerateValues();
        int[] totalCount = attributeStats.nominalCounts ; 
        int idx = 0, it = 0;    
//        System.out.println("Best attr : "+bestAttribute.name());
        data.sort(bestAttribute);
        while (attValues.hasMoreElements()) {
            String value = (String)attValues.nextElement();
  //          System.out.println("  value : "+value);
            Node child = new Node(bestAttribute,value);
            Instances subset = new Instances(data,idx,totalCount[it]);
            idx+=totalCount[it];
            it++;
            int numClasses = getTotalDistinctLabel(subset) ;
            //System.out.println(subset);
    //        System.out.println("        "+subset.numInstances()+"|"+numClasses);
            if (subset.numInstances()==0) {
                child.setLabel(mostCommonLabel(data));
                root.addChild(child);
//                System.out.println("Add new child : "+"|"+child.getAttributeValue()+
//                        "|"+child.getLabel()+" from "+root.getAttributeValue()+"|"+root.getLabel());
            } else if (numClasses==1) {
                String label = subset.firstInstance().stringValue(subset.attribute(subset.classIndex()));
                child.setLabel(label);
                root.addChild(child);
            } else {
                root.addChild(child);
                subset.deleteAttributeAt(bestAttribute.index());
                buildID3(child,subset);                
            }            
        }
    }
    public Attribute findBestAttribute(Instances data) {
        //hitung pake entropy & ig & syalala
	double[] result = findMaxIG(data) ;
        Attribute attr = data.attribute((int)result[0]);
        return attr;
    }
    
    private String mostCommonLabel(Instances data) {
        int distinctLabel = getTotalDistinctLabel(data);
        int classIdx = data.classIndex();
	AttributeStats attributeStats = data.attributeStats(classIdx);
	int[] classCount = attributeStats.nominalCounts;
	int max = 0, idx = 0 ;
        for (int i=0;i<classCount.length;i++) {
            if (classCount[i]>max) {
                max = classCount[i] ;
                idx = i ;
            }
        }
	return data.attribute(classIdx).value(idx);
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

    @Override
    public void buildClassifier(Instances i) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
