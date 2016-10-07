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
import weka.core.Instances;
import weka.core.Attribute;
import java.util.ArrayList;

/**
 *
 * @author ASUS X202E
 */
public class Node {
    private Attribute attribute;
    private String attributeValue;
    private String label;
    private ArrayList<Node> children;

    public Node(Attribute attribute, String attributeValue) {
        this.attribute = attribute;
        this.attributeValue = attributeValue;
        this.label = null ;
        this.children = new ArrayList<Node>() ;
    }
    
    public Node(String label) {
        this.label = label;
        this.children = new ArrayList<Node>() ;
    }
    
    public Node() {        
        this.label = null ;
        this.children = new ArrayList<Node>() ;
    }
    
    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
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

    
    
}
