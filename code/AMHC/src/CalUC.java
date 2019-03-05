import java.io.File;
import java.io.IOException;

import weka.classifiers.Evaluation;
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


public class CalUC {
	
	
	public  double Weight[];
	public  double score[];
	public double beta;
	public double punish;
	
	public CalUC(String dir){
		//score=0.0f;
		score=new double[4];
		beta=0.5;
		punish=2.0f;
		Weight=new double[10];
		for(int i=0;i<10;i++){
	        Weight[i]=1.0;
		}
		
		Weight[0]=0.7;
		Weight[1]=0.3;
		//Weight[3]=beta*Weight[2];
		
		//Weight[4]=beta*Weight[3];
		//Weight[5]=beta*Weight[4];
		
		score=Cal_UCScore(dir,0);
		//System.out.println(score);
	}
	
	public double[] Cal_UCScore(String strPath,int level) { 
		int sum=0;
		double st[]=new double[4];
		double temp[]=new double[4];
		//GenerateArffFile(strPath);  //generate train and test file;
		double[] scprf=new double[4];
		scprf=cal_H(strPath);
		for(int i=0;i<4;i++){
			scprf[i]=Weight[level]*scprf[i];
		}
		//double score=Weight[level]*cal_H(strPath);
		
        File dir = new File(strPath); 
        File[] files = dir.listFiles(); 
        
        if (files != null) {
            
        for (int i = 0; i < files.length; i++) { 
            if (files[i].isDirectory()) { 
            	sum++;
               // score+=Cal_UCScore(files[i].getAbsolutePath(),level+1); 
            	temp=Cal_UCScore(files[i].getAbsolutePath(),level+1);
            	st[0]+=temp[0];
            	st[1]+=temp[1];
            	st[2]+=temp[2];
            	st[3]+=temp[3];
            } 
        } 
        if(sum!=0){
        	for(int i=0;i<4;i++){
        		st[i]/=sum;
        		scprf[i]+=st[i];
        	}
        }
        }
        
        return scprf;
    }
	
	public double[] cal_H(String dir){ //compute each level entropy H;
		
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
		double scprf[]=new double[4];
		
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
		smo.buildClassifier(insTrain);//训练出分类器
	
	    ht=-ClaNum*(1.0/ClaNum)*Math.log(1.0/ClaNum);
	   // System.out.println(ht+"\t");
	    
	    Evaluation eTest = new Evaluation(insTrain);
		eTest.evaluateModel(smo, insTest);
	  
		scprf[1]=eTest.weightedFMeasure();
		scprf[2]=eTest.weightedPrecision();
		scprf[3]=eTest.weightedRecall();
	    //ClaNum=insTrain.classAttribute().numValues();
	    
	    double en=0.0f;
	    
		for(int i=0;i<sum;i++){
			a=insTest.instance(i).classValue();//测试值的类标
		    b=smo.classifyInstance(insTest.instance(i));//用分类器进行的分类
		    if(a!=b){
		    	temp++;
		    	entropy+=1.0;
		    	continue;
		    }
		    distribute=smo.distributionForInstance(insTest.instance(i));
			if(distribute!=null){
				temp++;
				en=0.0f;
				for(int j=0;j<ClaNum;j++){
					if(distribute[j]!=0){
				      en+=-distribute[j]*Math.log(distribute[j]);
					}
				}
				en=en/ht;
			}
			/*
			if(a!=b){
				en=en*punish;  //错分的惩罚因子；
			}
			*/
			entropy+=en;
			
		}
		
		
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
		scprf[0]=entropy/H;
		//System.out.println(temp+" Finished;");
		//System.out.println("Classify entropy: "+entropy);
		//System.out.println("Taxonomy entropy: "+H);
		//System.out.println("UCScore: "+a);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return scprf;
	}
	
	public void GenerateArffFile(String dir){
		Instances insTrain,insTest=null;
		String trainfn=dir+"\\traindata.arff";
		String testfn=dir+"\\testdata.arff";
		File trainfile=new File(trainfn);
		File testfile=new File(testfn);
		ArffLoader loader=new ArffLoader();
		try{
		loader.setFile(trainfile);
		insTrain=loader.getDataSet();
		insTrain.setClassIndex(insTrain.numAttributes()-1);
		loader.setFile(testfile);
		insTest=loader.getDataSet();
		insTest.setClassIndex(insTest.numAttributes()-1);
		
		//Create filter;
		StringToWordVector filter=new StringToWordVector();
		filter.setUseStoplist(true);
		filter.setTFTransform(true);
		filter.setIDFTransform(true);
		filter.setLowerCaseTokens(true);
		filter.setOutputWordCounts(true);
		LovinsStemmer stemmer=new LovinsStemmer();
		filter.setStemmer(stemmer);
		//filter.setMinTermFreq(3);
		//filter.setWordsToKeep(1000);
		//filter.setInputFormat(ins);
		filter.setInputFormat(insTrain); // 用instrain的数据初始化过滤器，再应用于train和test的数据集；
		//System.out.println("String to Word");
		Instances newTrain=Filter.useFilter(insTrain, filter);	
    	Instances newTest=Filter.useFilter(insTest, filter);
		//System.out.println("String to Word finish");
		/*
    	 String[] options = new String[4];
    	 options[0] = "-T";                                    // "range"
    	 options[1] = "numeric"; 
    	 options[2] = "-V";                                    // "range"
    	 options[3] = "false";// first attribute
    	 RemoveType remove=new RemoveType();          // new instance of filter
    	 remove.setOptions(options);                           // set options
    	 remove.setInputFormat(newTrain);                          // inform filter about dataset **AFTER** setting options
    	 Instances newTrain2 = Filter.useFilter(newTrain, remove);   // apply filter
    	 Instances newTest2 = Filter.useFilter(newTest, remove);
    	*/
    	
    	RemoveNumAttr remove=new RemoveNumAttr();
    	remove.setInputFormat(newTrain);
    	Instances newTrain2 = Filter.useFilter(newTrain, remove);   // apply filter
   	    Instances newTest2 = Filter.useFilter(newTest, remove);
    	
    	
    	//filter with IG 1000;
    	AttributeSelection filter1=new AttributeSelection();
    	InfoGainAttributeEval eval=new InfoGainAttributeEval();
    	Ranker search=new Ranker();
    	search.setNumToSelect(1000);
    	filter1.setEvaluator(eval);
    	filter1.setSearch(search);
    	filter1.setInputFormat(newTrain2);
    	
    	//System.out.println("IG1000");
    	Instances newTrain1=Filter.useFilter(newTrain2, filter1);	
    	Instances newTest1=Filter.useFilter(newTest2, filter1);
    	//System.out.println("IG1000 Finished");
		
    	
		//save filtered arff file； 
		ArffSaver saver=new ArffSaver();
		saver.setInstances(newTrain1);
		saver.setFile(new File(dir+"\\train.arff"));
		saver.writeBatch();
		saver.setInstances(newTest1);
		saver.setFile(new File(dir+"\\test.arff"));
		saver.writeBatch();
		}catch(Exception e){
			System.out.println("Error when generateing arff train test file!");
		}
		
	}
}
