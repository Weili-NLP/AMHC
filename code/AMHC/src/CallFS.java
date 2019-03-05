import java.io.File;
import java.io.IOException;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.core.stemmers.LovinsStemmer;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.filters.unsupervised.attribute.RemoveType;
import weka.classifiers.Evaluation;


public class CallFS{
	
	
	public  double Weight[];
	public  double score;
	public double beta;
	public double pre;
	public double rec;
	public double fm;
	
	public CallFS(String dir){
		score=0.0f;
		beta=0.5;
		
		Weight=new double[10];
		for(int i=0;i<10;i++){
	        Weight[i]=1.0;
		}
		
		Weight[1]=beta*Weight[0];
		Weight[2]=beta*Weight[1];
		Weight[3]=beta*Weight[2];
		Weight[4]=beta*Weight[3];
		Weight[5]=beta*Weight[4];
		
		score=Cal_UCScore(dir,0);
		System.out.println(score);
	}
	
	public double Cal_UCScore(String strPath,int level) { 
		
		//GenerateArffFile(strPath);  //generate train and test file;
		
		double score=Weight[level]*cal_H(strPath);
		
        File dir = new File(strPath); 
        File[] files = dir.listFiles(); 
        
        if (files != null) {
            
        for (int i = 0; i < files.length; i++) { 
            if (files[i].isDirectory()) { 
                score+=Cal_UCScore(files[i].getAbsolutePath(),level+1); 
            } 
        } 
        }
        
        return score;
    }
	
	public double cal_H(String dir){ //compute each level entropy H;
		
		System.out.println(dir);
		double p=0.0f;;
		Instances insTest,insTrain=null;
		int ClaNum;
		double a,b;
		double ht=0.0f;
		//float right=0.0f;
		int temp=0;
		double entropy=0.0f;
		double[] distribute = null;
		
		int sum;
		String trainfn=dir+"\\train.arff";
		String testfn=dir+"\\test.arff";
		File trainfile=new File(trainfn);
		File testfile=new File(testfn);
		
		a=0.0f;
		try {
		ArffLoader loader=new ArffLoader();
		
		loader.setFile(trainfile);
		
		insTrain=loader.getDataSet();
		insTrain.setClassIndex(insTrain.numAttributes()-1);
		
		loader.setFile(testfile);
		insTest=loader.getDataSet();
 
		insTest.setClassIndex(insTest.numAttributes()-1);
		
		ClaNum=insTrain.numClasses();
		/*
		  int[] sta=new int[ClaNum];
		 
		for(int i=0;i<ClaNum;i++){
			sta[i]=0;
		}
		*/
		sum=insTest.numInstances();
		
		SMO smo=new SMO();
		smo.buildClassifier(insTrain);//ÑµÁ·³ö·ÖÀàÆ÷
	   
		Evaluation eTest = new Evaluation(insTrain);
		eTest.evaluateModel(smo, insTest);
	  
		eTest.weightedFMeasure();
		eTest.weightedPrecision();
		eTest.weightedRecall();
		
		entropy=entropy/(temp*1.0);
        //Compute the level entropy;
		
		/*
		double H=0.0f;
		for(int i=0;i<ClaNum;i++){
			p=(double)((sta[i]*1.0)/sum);
			if(p!=0){
				H+=-p*Math.log(p);
			}
		}
		H=H/ht;
		*/
		
	    double H=1.0f;
		a=entropy/H;
		//System.out.println(temp+" Finished;");
		//System.out.println("Classify entropy: "+entropy);
		//System.out.println("Taxonomy entropy: "+H);
		System.out.println("UCScore: "+a);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return a;
	}
	

}
