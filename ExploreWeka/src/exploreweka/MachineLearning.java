/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploreweka;

import id3.Node;
import id3.myID3;
import java.util.Enumeration;
import java.util.Scanner;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class MachineLearning {
    
    public static boolean isDataExist = false ;
    public static boolean isClassifierExist = false ;
    public static String classifierType = "" ;
    public static Instances dataset = null ;
    public static Classifier classifier = null;
    public static String datasetName = "" ;
    
    public static void main(String[] args) {
     
        WekaProcedure wekaProcedure = new WekaProcedure() ;

        Scanner scanner = new Scanner(System.in);
        int pil = 1 ,pil2;
        do {    
            if (!isDataExist) System.out.println("Current data set : - ");
            else System.out.println("Current data set : "+datasetName);
            if (!isClassifierExist) System.out.println("Current Classifier Model : - ");
            else System.out.println(classifierType);
            wekaProcedure.Menu() ;
            pil = scanner.nextInt() ;
            if (pil==1) {
                String filePath = scanner.nextLine() ;
                dataset = wekaProcedure.loadData(filePath);
                isDataExist = true ;
                datasetName = dataset.relationName();
                System.out.println("Data "+datasetName+" loaded ( "+dataset.numAttributes()+" instances )");
            } else if (pil==2) {
                if (!isDataExist) {System.out.println("Dataset still empty. Please load first.");}
                else {                        
                      wekaProcedure.removeAttribute(dataset);
                }
            } else if (pil==3) {
                if (!isDataExist) {System.out.println("Dataset still empty. Please load first.");}
                else {                        
                      wekaProcedure.doResample(dataset);
                }
            } else if (pil==4) {
                if (!isDataExist) {System.out.println("Dataset still empty. Please load first.");}
                else {                        
                    System.out.println("1. ID3\n2. J48\n3. Choose classifier : ");
                    pil2 = scanner.nextInt();
                    if (pil2==1) {
                        isClassifierExist = true ;
                        classifierType = "ID3";
                        classifier = wekaProcedure.buildID3DT(dataset);
                    } else {
                        isClassifierExist = true ;
                        classifierType = "J48";
                        System.out.println("Prune option (Y/N) : ");
                        String pruneOption_s = scanner.nextLine();
                        boolean pruneOption = true ;
                        if (pruneOption_s.equals("N")) pruneOption = false ;
                        wekaProcedure.buildJ48DT(dataset, pruneOption);
                    }
                }
            } else if (pil==5) {
                 if (!isClassifierExist) {System.out.println("Classifier model doesn't exist. Please make model first.");}
                 else {
                     wekaProcedure.tenCrossFoldValidation(classifier, dataset);
                 }
            } else if (pil==6) {
                if (!isClassifierExist) {System.out.println("Classifier model doesn't exist. Please make model first.");}
                 else {
                     System.out.println("Percentage split : ");
                     double percentageSplit = scanner.nextDouble();
                     wekaProcedure.PercentageSplit(classifier, percentageSplit, dataset);
                 }
            } else if (pil==7) {
                if (!isClassifierExist) {System.out.println("Classifier model doesn't exist. Please make model first.");}
                else {
                     System.out.println("Model File name : ");
                     String fileName = scanner.nextLine();
                     wekaProcedure.saveModel(classifier, fileName);
                 }
            } else if (pil==8) {
                System.out.println("Model File name : ");
                String fileName = scanner.nextLine();
                classifier = wekaProcedure.loadModel(fileName);
                isClassifierExist = true ;
            } else if (pil==9) {
                if (!isDataExist || !isClassifierExist) {System.out.println("Make sure your data and classifier model already exist.") ;}
                else {
                    System.out.println("Put the number of data to be classified : (0 - "+dataset.numInstances()+" )");
                    int numberData = scanner.nextInt();
                    wekaProcedure.classifySingleData(classifier, dataset, numberData);
                }
            } else if (pil==10) {
                if (!isClassifierExist) {System.out.println("Classifier model doesn't exist. Please make model first.");}
                 else {
                     wekaProcedure.testingModelGivenTestSet(classifier, dataset);
                 }
            }
        } while (pil!=0) ;
    }    
}
