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
	return 0;
    }
    
    private double computeIG(Instances data, Attribute attribute) {
        // Pertama hitung Entropy(S)
	double entropyS = computeEntropy(data);
	// Lalu hitung entropi per atribut Entropy(Sv)
	int attrPositive = 0 //change this later;
	int attrNegative = 0 //change this later;
	double entropySv = 0; // value nya di assign dari entropy(pos,neg)
	
	
	return entropyS - entropySv;
	
    }
    
    /**
     * Count the entropy of a training data
     * @param pos   positive label of the training data or specific attribute
     * @param neg   negative label of the training data or specific attribute
     * @return
     */    
    public double entropy(int pos, int neg) {
	double pn = (double) pos/(pos+neg);
	double np = (double) neg/(pos+neg);
	double result = 0;
	if (pos>0) {
	    result += -1*pn*log2(pn);
	}
	if (neg>0) {
	    result += -1*np*log2(np);
	}
	return result;
    }
    
    public double log2(int input) {
	return Math.log(input)/Math.log(2);
    }
    
    public double log2(double input) {
	return Math.log(input)/Math.log(2);
    }
}
