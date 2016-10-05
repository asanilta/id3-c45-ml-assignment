/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id3;
import weka.core.Instances;
import weka.core.Attribute;
import java.util.ArrayList;

/**
 *
 * @author ASUS X202E
 */
public class Node {
    private String attributeName;
    private String attributeValue;
    private double label;
    private ArrayList<Node> children;
//    private Node parent;

    public Node(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }
    
    public Node(double label) {
        this.label = label;
    }
    
    public Node() {
        
    }
    
    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public double getLabel() {
        return label;
    }

    public void setLabel(double label) {
        this.label = label;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }
    
    public void addChild(Node child) {
        children.add(child);
    }

//    public Node getParent() {
//        return parent;
//    }
//
//    public void setParent(Node parent) {
//        this.parent = parent;
//    }
    
}
