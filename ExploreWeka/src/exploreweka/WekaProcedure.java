/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploreweka;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.Id3;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.unsupervised.attribute.Remove;

/**
 *
 * @author tama
 */

    public class WekaProcedure {       
       
        public Instances loadData (String filePath) {
            BufferedReader reader;
            Instances data = null ;
            try {
                reader = new BufferedReader(new FileReader(filePath)); 
                data = new Instances(reader);
                reader.close();
                data.setClassIndex(data.numAttributes() - 1);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MachineLearning.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MachineLearning.class.getName()).log(Level.SEVERE, null, ex);
            }
            return data ;
        }

        public Instances removeAttribute(Instances data) {
            Instances newData = null;
            System.out.println("Choose attribute to be deleted");
            for (int i=0; i< data.numAttributes()-1;i++) {;
                System.out.println("Attribute "+i+" : "+data.attribute(i).toString());
            }
            Remove remove = new Remove() ;
            Scanner scanner = new Scanner(System.in) ;
            int pil = scanner.nextInt();
            System.out.print("Invert option ? (Y/N) : ");
            String pil2 = scanner.nextLine();
            boolean invert = true ;
            if (pil2.equals("N")) invert = false ; 
            remove.setAttributeIndices(""+pil);
            remove.setInvertSelection(invert);
            System.out.println("--\n"+data);
            try {
               remove.setInputFormat(data);
               newData = Filter.useFilter(data, remove);
            } catch (Exception ex) {
                Logger.getLogger(MachineLearning.class.getName()).log(Level.SEVERE, null, ex);
            }
            return newData;
        }
        
        public Instances doResample(Instances data) {
            Instances newData = null ;
            System.out.println("Option (leave empty to default) : ");
            Scanner scanner = new Scanner(System.in) ;
            String options = scanner.nextLine();
            Resample resample = new Resample() ;
            try {
                resample.setOptions(weka.core.Utils.splitOptions(options));
                resample.setInputFormat(data);
                newData = Resample.useFilter(data, resample);
            } catch (Exception ex) {
                Logger.getLogger(WekaProcedure.class.getName()).log(Level.SEVERE, null, ex);
            }
            return newData ;            
        }
        
        public J48 buildJ48DT(Instances data,boolean unpruned) {
            String[] options = new String[1];
            options[0] = "-U";           
           
            J48 jj = new J48() ;
            
            try {
                if (unpruned) jj.setOptions(options);    
                jj.buildClassifier(data);  
            } catch (Exception ex) {
                Logger.getLogger(WekaProcedure.class.getName()).log(Level.SEVERE, null, ex);
            }
            return jj ;
        }
        
        public Id3 buildID3DT(Instances data) {
            Id3 DT = new Id3();                
            try {
                 DT.buildClassifier(data);  
            } catch (Exception ex) {
                Logger.getLogger(WekaProcedure.class.getName()).log(Level.SEVERE, null, ex);
            }
            return DT ;
        }
        
        public void testingModelGivenTestSet(Classifier classifier, Instances testData) {
            Evaluation eval;
           try {
                eval = new Evaluation(testData); 
                eval.evaluateModel(classifier, testData);
                System.out.println(eval.toSummaryString("Testing Result : \n======\n", false));
            } catch (Exception ex) {
                Logger.getLogger(WekaProcedure.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        }
        
        public void tenCrossFoldValidation(Classifier classifier, Instances data) {
            Evaluation eval;
            try {
                eval = new Evaluation(data);
                eval.crossValidateModel(classifier, data, 10, new Random(1));
                System.out.println(eval.toSummaryString());
            } catch (Exception ex) {
                Logger.getLogger(WekaProcedure.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        
        public void PercentageSplit(Classifier classifier, double splitPercentage, Instances data) {
            int trainSize = (int) Math.round(data.numInstances() * splitPercentage  / 100);
            int testSize = data.numInstances() - trainSize;
            Instances trainData = new Instances(data, 0, trainSize);
            Instances testData = new Instances(data, trainSize, testSize);
            trainData.setClassIndex(data.numAttributes() - 1);
            testData.setClassIndex(data.numAttributes() - 1);
            try {
                classifier.buildClassifier(trainData);
                Evaluation eval = new Evaluation(testData); 
                eval.evaluateModel(classifier, testData);
                System.out.println(eval.toSummaryString("Testing Result : \n======\n", false));
            } catch (Exception ex) {
                Logger.getLogger(WekaProcedure.class.getName()).log(Level.SEVERE, null, ex);
            }  
        }
        
        public Classifier loadModel(String fileName) {
            ObjectInputStream ois;
             Classifier cls = null;
            try {
                ois = new ObjectInputStream(new FileInputStream("/some/where/j48.model"));
                cls= (Classifier) ois.readObject();
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(WekaProcedure.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(WekaProcedure.class.getName()).log(Level.SEVERE, null, ex);
            }
            return cls ;
        }
        
        public void saveModel(Classifier classifier, String fileName) {
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(new FileOutputStream("/src/Model/fileName.model"));
                oos.writeObject(classifier);
                oos.flush();
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(WekaProcedure.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        
        public void classifySingleData(Classifier classifier, Instances data, int numberData) {
            try {
                double result = classifier.classifyInstance(data.instance(numberData));
                System.out.print("ID: " + data.instance(numberData).value(0));
                System.out.print(", actual: " + data.classAttribute().value((int)data.instance(numberData).classValue()));
                System.out.println(", predicted: " + data.classAttribute().value((int) result));
            } catch (Exception ex) {
                Logger.getLogger(WekaProcedure.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void Menu() {
            System.out.println("1. Load Data");
            System.out.println("2. Remove Attribute");
            System.out.println("3. Do Resample");
            System.out.println("4. Build Classifier");
            System.out.println("5. 10 Fold Cross Validation Evalutation");
            System.out.println("6. Percentage Split Evalutation");
            System.out.println("7. Save Model");
            System.out.println("8. Load Model");
            System.out.println("9. Classify One Instance");
            System.out.println("0. Exit");
            System.out.print("Choose the procedure : ");
        }
    }
    