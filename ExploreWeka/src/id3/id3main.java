/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id3;

import exploreweka.WekaProcedure;
import weka.core.Instances;

/**
 *
 * @author tama
 */
public class id3main {
    
     public static void main(String[] args) {
        WekaProcedure wekaProcedure = new WekaProcedure() ;
        Instances data = wekaProcedure.loadData("src/Dataset/weather.arff");
        myID3 mm = new myID3(data) ;
        System.out.println("Run : ");
        mm.buildID3(mm.root, data);
        
        System.out.println("-----------");
        mm.printTree(mm.root, 0);
        mm.classifyInstances(mm.root,data);
        mm.printSummary();
     }
    
}
