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

public class CalUC {
	
	
	public  double Weight[];
	public  double score;
	
	public CalUC(String dir){
		score=0.0f;
		Weight=new double[10];
		for(int i=0;i<10;i++){
	        Weight[i]=1.0;
		}
		score=Cal_UCScore(dir,0);
		System.out.println(score);
	}
	
	public double Cal_UCScore(String strPath,int level) { 
		
		GenerateArffFile(strPath);
		
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
		
		double p=0.0f;;
		Instances insTest,insTrain=null;
		int ClaNum;
		double a,b;
		double ht=0.0f;
		//float right=0.0f;
		int temp=0;
		double entropy=0;
		double[] distribute = null;
		
		int sum;
		String trainfn=dir+"\\train.arff";
		String testfn=dir+"\\test.arff";
		File trainfile=new File(trainfn);
		File testfile=new File(testfn);
		
		try {
		ArffLoader loader=new ArffLoader();
		
		loader.setFile(trainfile);
		
		insTrain=loader.getDataSet();
		insTrain.setClassIndex(insTrain.numAttributes()-1);
		
		loader.setFile(testfile);
		insTest=loader.getDataSet();
 
		insTest.setClassIndex(insTest.numAttributes()-1);
		
		ClaNum=insTrain.numClasses();
		int[] sta=new int[ClaNum];
		for(int i=0;i<ClaNum;i++){
			sta[i]=0;
		}
		sum=insTest.numInstances();
		
		SMO smo=new SMO();
		smo.buildClassifier(insTrain);//训练出分类器
	
	    ht=-ClaNum*(1.0/ClaNum)*Math.log(1.0/ClaNum);
	    System.out.println(ht+"\t");
	    
	   
	    //ClaNum=insTrain.classAttribute().numValues();
	    
	    double en=0.0f;
	    
		for(int i=0;i<sum;i++){
			a=insTest.instance(i).classValue();//测试值的类标
		    b=smo.classifyInstance(insTest.instance(i));//用分类器进行的分类
		    sta[(int)b]++;
		    
		    distribute=smo.distributionForInstance(insTest.instance(i));
		   
		  // System.out.println("所属的类别："+a+"\t"+"所分的类别："+b);	
		
			if(distribute!=null){
				
				temp++;
				en=0.0f;
				//distribute=smo.distributionForInstance((insTest.instance(i)));
				//System.out.println("分类概率分布是：");
				for(int j=0;j<ClaNum;j++){
				     // System.out.print(distribute[j]+"\t");
					if(distribute[j]!=0){
				      en+=-distribute[j]*Math.log(distribute[j]);
					}
				}
				en=en/ht;
				System.out.println(en+"\t");
			}
			
			entropy+=en;
			
		}
		
		
		entropy=entropy/temp;
        //Compute the level entropy;
		double H=0;
		
		for(int i=0;i<ClaNum;i++){
			p=(double)((sta[i]*1.0)/sum);
			H+=-p*Math.log(p);
		}
		H=H/ht;
		p=entropy/H;
		System.out.println(temp+" Finished;");
		System.out.println("Classify entropy: "+entropy);
		System.out.println("Taxonomy entropy: "+H);
		System.out.println("UC Score: "+p);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return p;
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
		System.out.println("String to Word");
		Instances newTrain=Filter.useFilter(insTrain, filter);	
    	Instances newTest=Filter.useFilter(insTest, filter);
		System.out.println("String to Word finish");
		
    	//filter with IG 1000;
    	AttributeSelection filter1=new AttributeSelection();
    	InfoGainAttributeEval eval=new InfoGainAttributeEval();
    	Ranker search=new Ranker();
    	search.setNumToSelect(1000);
    	filter1.setEvaluator(eval);
    	filter1.setSearch(search);
    	filter1.setInputFormat(newTrain);
    	
    	System.out.println("IG1000");
    	Instances newTrain1=Filter.useFilter(newTrain, filter1);	
    	Instances newTest1=Filter.useFilter(newTest, filter1);
    	System.out.println("IG1000 Finished");
		
    	
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
