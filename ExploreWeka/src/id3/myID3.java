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
    
    public void buildClassifier(Instances data) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i=0;i<data.numAttributes();i++) {
            attributes.add(data.attribute(i));
        }
        attributes.remove(data.classAttribute());
        root = id3(data,attributes);
    }
    
    public void classifyInstance(Instance instance) {
        
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
    
    private Attribute findBestAttribute(Instances data, ArrayList<Attribute> attributes) {
        //hitung pake entropy & ig & syalala
	return null;
    }
    
    private double mostCommonLabel(Instances data) {
        for (int i=0;i<data.numInstances();i++) {
            //double value = data.instance(i).classValue();
            //hitung value mana yang paling banyak
           
        }
        //return value yg paling banyak        
	return 0;
    }
    
    private double computeEntropy(Instances data) {
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
    
    private double computeIG(Instances data, Attribute attribute) {
        // Pertama hitung Entropy(S)
	double entropyS = computeEntropy(data);
	// Lalu hitung entropi per atribut Entropy(Sv)
	int attrIdx = 0; //index attribute
	AttributeStats attributeStats = data.attributeStats(attrIdx);
	int[] attrCount = attributeStats.nominalCounts;
	int sum = 0;
	for (int i = 0; i<attrCount.length; i++) {
	    sum+=attrCount[i];
	}
	double entropySv = 0;
	for (int i = 0; i<attrCount.length; i++) {
	    double p = (double) attrCount[i]/sum;
	    entropySv += -1*p*log2(p);
	}
	
	return entropyS - entropySv;
	
    }
    
    public double log2(int input) {
	return Math.log(input)/Math.log(2);
    }
    
    public double log2(double input) {
	return Math.log(input)/Math.log(2);
    }
}
