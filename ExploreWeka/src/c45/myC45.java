///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package c45;
//import weka.classifiers.Classifier;
//import weka.core.Instances;
//import weka.core.Instance;
//import weka.core.Attribute;
//import java.util.ArrayList;
//import java.util.Enumeration;
//import weka.core.AttributeStats;
//import weka.filters.Filter;
//import weka.filters.unsupervised.attribute.Discretize;
//import weka.filters.unsupervised.attribute.ReplaceMissingValues;
///**
// *
// * @author ASUS X202E
// */
//public class myC45 extends Classifier {
//    double z = 0.69; //confidence interval 25%
//    
//    public void buildClassifier(Instances data) throws Exception {
//        Instances filteredData = handleMissingValues(discretizeData(data));
//        
//    }
//    
//    private Node c45(Node node, Instances data, ArrayList<Attribute> attributes) {
//        if (data.numClasses()==1) {
//            node.setLabel(data.firstInstance().classValue());
//            return node;
//        }
//        else if (attributes.size()==0) {
//            node.setLabel(mostCommonLabel(data));
//            return node;
//        }
//        else {
//            Attribute bestAttribute = findBestAttribute(data,attributes);
//            Enumeration attValues = bestAttribute.enumerateValues();
//            while (attValues.hasMoreElements()) {
//                double value = (double)attValues.nextElement();
//                Node child = new Node(bestAttribute,value);
//                Instances subset = filterInstancesByAttributeValue(data,bestAttribute,value);
//                if (subset.numInstances()==0) {
//                    child.setLabel(mostCommonLabel(data));
//                } else {
//                    attributes.remove(bestAttribute);
//                    child = c45(child,subset,attributes);
//                }
//                node.addChild(child);
//            }
//            return node;
//        }
//    }
//    
//    private double mostCommonLabel(Instances data) {
//        
//    }
//    
//    private Attribute findBestAttribute(Instances data,ArrayList<Attribute> attributes) {
//        
//    }
//    
//    private Instances discretizeData(Instances data) throws Exception {
//        Discretize filter = new Discretize();
//        filter.setInputFormat(data);
//        Instances output = Filter.useFilter(data,filter);
//        return output;
//    }
//    
//    private Instances handleMissingValues(Instances data) throws Exception {
//        ReplaceMissingValues filter = new ReplaceMissingValues();
//        filter.setInputFormat(data);
//        Instances output = Filter.useFilter(data,filter);
//        return output; 
//    }
//    
//    private Node pruneTree(Node root) {
//        Node currentNode = root;
//        ArrayList<Node> children = root.getChildren();
//        
//    }
//    
//    private double computeError (Instances data) {
//        double correct = 0;
//        for (int i=0;i<data.numInstances();i++) {
//            Instance instance = data.instance(i);
//            double value = classifyInstance(instance);
//            if (value==instance.classValue()) {
//                correct++;
//            }
//        }
//        
//        double N = data.numInstances();
//        double f = correct/N;
//        double e = (f+Math.pow(z,2)/2*N+z*Math.sqrt(f/N-Math.pow(f,2)/N+Math.pow(z,2)/4*Math.pow(N,2)))/(1+Math.pow(z,2)/N);        
//        return e;
//    }
//    
//    private Instances filterInstancesByNode(Instances data, Node node) {
//        Instances subset = new Instances(data);
//        subset.delete();
//        Node currentNode = node;
//        while (!isRoot(currentNode)) {
//            Attribute att = node.getAttribute();
//            double val = node.getAttributeValue();
//            subset = filterInstancesByAttributeValue(subset,att,val);
//            currentNode = currentNode.getParent();
//        }
//        return subset;
//    }    
//    
//    private void pruneSubtree(Instances data, Node node) {
//        ArrayList<Node> children = node.getChildren();
//        Instances subset = filterInstancesByNode(data,node);
//        double nodeError = computeError(subset);
//        double childrenError = 0;
//        for (int i=0;i<children.size();i++) {
//            Node child = children.get(i);
//            Instances childSubset = filterInstancesByAttributeValue(subset,child.getAttribute(),child.getAttributeValue());            
//            childrenError +=  computeError(childSubset);
//        }
//        if (childrenError < nodeError) {
//            for (int i=0;i<children.size();i++) {
//                children.set(i,null);
//            }
//            node.setChildren(null);
//        }
//    }
//    
//    
//    private double classifyInstance(Instance instance) {
//        
//    }
//    
//    private Instances filterInstancesByAttributeValue(Instances data, Attribute att, double value) {
//        Instances subset = new Instances(data);
//        subset.delete();
//        for (int i=0; i<data.numInstances(); i++) {
//            Instance currInstance = data.instance(i);
//            if (currInstance.value(att) == value) subset.add(currInstance);
//        }
//        return subset;
//    }
//    
//    private boolean isRoot(Node node) {
//        if (node.getParent()==null) return true;
//        else return false;
//    }
//}
