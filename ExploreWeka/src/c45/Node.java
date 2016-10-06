/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c45;
import weka.core.Instances;
import weka.core.Attribute;
import java.util.ArrayList;

/**
 *
 * @author ASUS X202E
 */
public class Node {
    private Attribute attribute;
    private double attributeValue;
    private double label;
    private ArrayList<Node> children;
    private Node parent;

    public Node(Attribute attribute, double attributeValue) {
        this.attribute = attribute;
        this.attributeValue = attributeValue;
    }
    
    public Node(double label) {
        this.label = label;
    }
    
    public Node() {
        
    }
    
    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public double getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(double attributeValue) {
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

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    
}
