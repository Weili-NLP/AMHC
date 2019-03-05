import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;
import java.util.Vector;


public class AMHC {
	public Cla_HT HT_final;
	public int num; //叶子类别数目；
	
    public double[][] Sim; //保存叶子类别的Cos相似度矩阵；
    public int[] claN; //保存每个叶子类别的资源数目；
    public double[][][] DS; //保存每个类别对应的资源特征矩阵；
    public double centroid[][];
    public double center[];
 
	public AMHC(int n){
    	Sim=new double[n][n];
    	claN=new int[n];
    	DS=new double[n][][];
    	centroid=new double[n][1000];
    	center=new double[1000];
		num=n;
		HT_final=new Cla_HT(num);
	}
	//HACP
    public boolean ReadDS(String dir){
    	   
    	String filename;
    	int docnum=0;
    	int docid;
    try{
    	for(int i=0;i<num;i++){
    		filename=dir+"\\"+i;
    		BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			line = reader.readLine();
			if(line!=null){
				docnum=Integer.parseInt(line);
			}
    		DS[i]=new double[docnum][1000];
    		zeroDS(DS[i]);
    		
    		docid=0;
    		int termid;
    	
    		while((line = reader.readLine()) != null){
				StringTokenizer tknr = new StringTokenizer(line,"= ,\t\r\n");
				int length = tknr.countTokens();
				for(int j=0;j<length;j++){
					String token1 = tknr.nextToken();j++;
					termid=Integer.parseInt(token1);
					String token2 = tknr.nextToken();
					if(termid==1000) continue;
					DS[i][docid][termid]=Double.parseDouble(token2);
				}
				docid++;
			}
			reader.close();
    		
    		claN[i]=docid;
    	}
    	
  }
		
		catch (Exception e){
			System.out.println("Error while reading file:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;	
    	
    }
    
    public void zeroDS(double ds[][]){
    	int i,j;
    	for(i=0;i<ds.length;i++){
    		for(j=0;j<ds[i].length;j++){
    			ds[i][j]=0;
    		}
    	}
    }
  
    public void computerSim(){
    	int i,j;
    	for(i=0;i<25;i++){  		
    		for(j=0;j<25;j++){
        		Sim[i][j]=interS(i,j);
        	}
    	}
    }
    public double interS(int index1,int index2){
    	int i,j;
    	double sum=0;
    	for(i=0;i<DS[index1].length;i++){  		
    		for(j=0;j<DS[index2].length;j++){
        		sum+=CalCos(DS[index1][i],DS[index2][j]);
        	}
    	}
    	return sum;
    }
    
    public double CalCos(double[] p, double [] q)  
	{    
	    
	    double result=0.0f;
	    
	    for(int i=0;i<p.length;i++)  
	    {     
	        result+=p[i]*q[i];
	    	    
	    }
	      
	    return result;  
	}  
    
    public boolean SaveSim(String filename){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			
			for (int i = 0; i < 25; i++){
				for (int j = 0; j < 25; j++){
					writer.write((Sim[i][j]) + " ");
				}
				writer.write("\n");
			}
			writer.close();
		}
		catch (Exception e){
			System.out.println("Error while saving Sim Matrix:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
    
    public boolean ReadSim(String filename){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			int id=0;
			while((line = reader.readLine()) != null){
				StringTokenizer tknr = new StringTokenizer(line,"= \t\r\n");
				int length = tknr.countTokens();
				for(int i=0;i<length;i++){
					String token = tknr.nextToken();
					Sim[id][i]=Double.parseDouble(token);
					
				}
				id++;
			}
			reader.close();
		}
		
		catch (Exception e){
			System.out.println("Error while reading similarity file:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
    public double CalSimClu(int c1,int c2,Clu_HT T1){
    	double result=0.0f;
    	Vector<Integer> v1=null;
    	Vector<Integer> v2=null;
    	
    	
    		v1=new Vector<Integer>();
    		GetCatID(c1,T1,v1);
    
    	
    		v2=new Vector<Integer>();
    		GetCatID(c2,T1,v2);
    	
    	int i,j;
    	int a=0,b=0;
    	for(i=0;i<v1.size();i++){
    		//a+=claN[v1.elementAt(i)];
    		for(j=0;j<v2.size();j++){
    			result+= Sim[v1.elementAt(i)][v2.elementAt(j)];
    		}
    	}
    	
    	for(i=0;i<v1.size();i++){
    		a+=claN[v1.elementAt(i)];
    	}
    	for(i=0;i<v2.size();i++){
    		b+=claN[v2.elementAt(i)];
    	}
    	result=result/(a*b);
    	return result;
    }
    
    
    public void GetCatID(int k,Clu_HT T1,Vector<Integer> vec){
    	int k1,k2;
    	int index=-1;
    	if(k>num-1){
    		index=T1.ID2index(k);
    		k1=T1.nodes[index].left;
    		GetCatID(k1,T1,vec);
    		k2=T1.nodes[index].right;
    		GetCatID(k2,T1,vec);
    	}else{
    		vec.addElement(k);
    	}
    }
    
    public double ComputeQ(Vector<Integer> vec, Clu_HT T1){
    	double result=0.0f;
    	double intra_s=0;
    	double inter_s=0;
    	int i=0;
    	for(i=0;i<vec.size();i++){
    		for(int j=0;j<vec.size();j++){
    			if(j!=i) {
    				inter_s+=CalSimClu(vec.elementAt(i),vec.elementAt(j),T1);
    			}else{
    				intra_s=CalSimClu(vec.elementAt(i),vec.elementAt(j),T1);
    			}
    		}
    		double result1;
    		result1=inter_s/intra_s;
    		result+=result1;
    		System.out.println(vec.elementAt(i)+": "+inter_s+", "+intra_s+", "+result1);
    	    intra_s=0;
        	inter_s=0;
    	}
    	result=result/T1.vec_LC.size();
    	return result;
    	
    }
	//HACP
    
    //ComputeSSE
    public double ComputeSSE(Vector<Integer> vec, Clu_HT T1){
    	double result=0.0f;
    	for(int i=0;i<vec.size();i++){
    		result+=CluSSE(vec.elementAt(i),T1);
    	}
    	return result;
    }
    //ComputeSSB
    public double ComputeSSB(Vector<Integer> vec, Clu_HT T1){
    	double result=0.0f;
    	for(int i=0;i<vec.size();i++){
    		result+=CluSSB(vec.elementAt(i),T1);
    	}
    	return result;
    }
    public double CluSSE(int cluid,Clu_HT T1){
    	double result=0.0f;
    	Vector<Integer> vec=T1.nodes[T1.ID2index(cluid)].labels;
    	double[] clucentroid=new double[1000];
    	for(int i=0;i<1000;i++){
    		clucentroid[i]=0;
    	}
    	clucentroid=CluCentroid(vec,clucentroid);
    	for(int i=0;i<vec.size();i++){
    		for(int j=0;j<claN[vec.elementAt(i)];j++){
    				result+=SUMVec(DS[vec.elementAt(i)][j],clucentroid);
    		}
    	}
    	return result;
    }
    public double CluSSB(int cluid,Clu_HT T1){
    	double result=0.0f;
    	Vector<Integer> vec=T1.nodes[T1.ID2index(cluid)].labels;
    	int sum=0;
    	for(int i=0;i<vec.size();i++){
    		sum+=claN[vec.elementAt(i)];
    	}
    	
    	double[] clucentroid=new double[1000];
    	for(int i=0;i<1000;i++){
    		clucentroid[i]=0;
    	}
    	clucentroid=CluCentroid(vec,clucentroid);
    
       result=SUMVec(clucentroid,center);
    	
    	return sum*result;
    }
    // compute centroid
    public void Centroid(){
    	for(int i=0;i<num;i++){
    		for(int z=0;z<1000;z++){
    			for(int j=0;j<claN[i];j++){
    				centroid[i][z]+=DS[i][j][z];
    			}
    		}
    	}
    }
    public double SUMVec(double[]a,double[]b){
    	double res=0.0f;
    	for(int i=0;i<a.length;i++){
    		res+=Math.pow(a[i]-b[i], 2);
    	}
    	return res;
    }
    // compute clucentroid
    public double[] CluCentroid(Vector<Integer> vec,double[] clucentroid){
    	int sum=0;
    	for(int i=0;i<vec.size();i++){
    		sum+=claN[vec.elementAt(i)];
    	}
    	
    	for(int j=0;j<1000;j++){
    		for(int i=0;i<vec.size();i++){
    			clucentroid[j]+=centroid[vec.elementAt(i)][j];
    		}
    		clucentroid[j]/=sum;
    	}
    	return clucentroid;
    }
    
    //Total
    public double Total(Vector<Integer> vec){
    	double result=0.0f;
    	for(int i=0;i<1000;i++){
    		center[i]=0;
    	}
    	center=CluCentroid(vec,center);
    	
    	for(int i=0;i<vec.size();i++){
    		for(int j=0;j<claN[vec.elementAt(i)];j++){
    				result+=SUMVec(DS[vec.elementAt(i)][j],center);
    		}
    	}
    	return result;
    }
    
	public void CollectImpl(Clu_HT T1,int rootid,Vector<Integer> cluvec){
		CluNode root=T1.nodes[T1.ID2index(rootid)];
		if(root.left!=-1){
			if(T1.nodes[T1.ID2index(root.left)].flag==false){
				cluvec.addElement(root.left);
			}else{
				CollectImpl(T1,root.left,cluvec);
			}
		}
		if(root.right!=-1){
			if(T1.nodes[T1.ID2index(root.right)].flag==false){
				cluvec.addElement(root.right);
			}else{
				CollectImpl(T1,root.right,cluvec);
			}
		}
		return;
	}
	public void GetClaChildVec(int id,Cla_HT claHT,Vector<Integer> cluvec){
		int cluid;
		int vcindex;
		int vcid;
		int index=claHT.ID2index(id);
		for(int i=0;i<claHT.nodes[index].vchild.size();i++){
			 vcid=claHT.nodes[index].vchild.elementAt(i);
			 if(vcid<num){
				 continue;   //只保留非叶子节点；
			 }
			 vcindex=claHT.ID2index(vcid);
			 cluid=claHT.nodes[vcindex].clu_Impl;
			 cluvec.addElement(cluid);
		}
	}
	public int InterSecNum(Vector<Integer> vec_LC,Vector<Integer> cluv){
		int sum=0;
		for(int i=0;i<cluv.size();i++){
			if(vec_LC.contains(cluv.elementAt(i))){
				sum++;
			}
		}
		return sum;
	}
	public double F_Sita(int sum){ 
		return 1.0/(1+sum);
	}
	public Cla_HT UT2AT(Vector<Integer> handlelc,Clu_HT UT){
		int index=-1;
		int nlab=0;
		int id=-1;
		Cla_HT claht=new Cla_HT();
		claht.nodes[claht.Num++]=new ClaNode(UT.root.it,handlelc.size());
		for(int i=0;i<handlelc.size();i++){
			index=UT.ID2index(handlelc.elementAt(i));
			nlab=UT.nodes[index].labels.size();
			id=claht.Num;
			if(nlab==1){
				nlab=0;
			}
			claht.nodes[id]=new ClaNode(handlelc.elementAt(i),nlab);
			claht.Num++;
			if(nlab!=0){
			for(int j=0;j<nlab;j++){
				claht.nodes[claht.Num++]=new ClaNode(UT.nodes[index].labels.elementAt(j),0);
				claht.nodes[id].vchild.addElement(UT.nodes[index].labels.elementAt(j));
			}
			}
			claht.nodes[0].vchild.addElement(handlelc.elementAt(i));
		}
		claht.root=claht.nodes[0];
		claht.SetLabels(claht.root);
		claht.SetParent(claht.root);
		return claht;
	}
	public double[] Cal_UCSCORE(int d,int i,Vector<Integer>  handlelc,Clu_HT T1){
		
		String filename2="clalist";
		String dirname="F:\\eclipse-java-juno-SR2-win32\\workspace\\AMHC_Combine\\result\\";
	
		BFD bfd=new BFD(UT2AT(handlelc,T1),d,i,filename2,dirname);
		String s=bfd.dirn+"\\"+T1.root.it;
		CalUC ucs=new CalUC(s);
		return ucs.score;
	}
	
	public void showTree(ClaNode root){
		if(root.vchild!=null){
			System.out.print(root.it+" "+root.vchild.size()+" ");
			for(int i=0;i<root.vchild.size();i++){
				System.out.print(root.vchild.elementAt(i)+" ");
			}
			System.out.println();
			for(int i=0;i<root.vchild.size();i++){
				showTree(HT_final.nodes[HT_final.ID2index(root.vchild.elementAt(i))]);
			}
		}
	}
	public double CalCN(Vector<Integer>  handlelc,Clu_HT T1){ //计算子类别数目熵；
		double result=0.0f;
		double p=0.0f;
		int n;
		int htn=handlelc.size();
		int numt=T1.root.labels.size();
		double ht=-htn*(1.0/htn)*Math.log(1.0/htn);
		for(int i=0;i<handlelc.size();i++){
			n=T1.nodes[T1.ID2index(handlelc.elementAt(i))].labels.size();
			p=(double)((n*1.0)/numt);
			if(p!=0){
				result+=-p*Math.log(p);
			}
		}
		return result/ht;
	}
	
	 public double SetNclus(Clu_HT T1){  //the square root of the category number;
		   double result=0.0f;
		   int a=0;
		 //  for(int i=0;i<T1.catn;i++){
	   	 //	 a+=claN[T1.HTree[i].it];
	     //  }
		   a=T1.root.labels.size();
		   result=Math.sqrt(a);
		  // System.out.println(result);
		   return result;
	   }
	 
	public double PN(int alpha,Vector<Integer> vec,Clu_HT T1){ //计算倾向子类别数目的影响；
		double result=0;                                       //Nclus is the preference number;
		int a=1;                                               //Nclus is the function of d;
		int c=vec.size();
		int t=alpha-1;
		while(t>1){
			a*=t;
			t--;
		}
		double Nclus=SetNclus(T1);
		double beta=Nclus/2;
		double result1=1/(a*Math.pow(beta, alpha));
		result=result1*Math.pow(c, alpha-1);
		double b=0;
		b=-c*1.0/beta;
		result=result*Math.exp(b);
		double max=result1*Math.pow(Nclus, alpha-1)*Math.exp(-Nclus*1.0/beta);
		return result/max;
	}
	public int MinID(Vector<Integer> vec){
		int min=vec.elementAt(0);
		for(int i=1;i<vec.size();i++){
			if(vec.elementAt(i)<min){
				min=vec.elementAt(i);
			}
		}
		return min;
	}
	public boolean IsEnd(int f,Vector<Integer> vec){
		return (f==MinID(vec));
	}
	public boolean IsSubNode(Clu_HT T1,int f,int id){
		while(f!=-1){
			if(f==id) return true;
			f=T1.nodes[T1.ID2index(f)].parent;
		}
		return false;
	}
	public boolean IsSubNodeVec(Clu_HT T1,int f,Vector<Integer> vec){
		for(int i=0;i<vec.size();i++){
			if(IsSubNode(T1,f,vec.elementAt(i))){
				return true;
			}
		}
		return false;
	}
	public Vector<Integer> GetHandleLC(Vector<Integer> resv,Clu_HT T1,Vector<Integer> handvec){
		int id;
		handvec=(Vector<Integer>)T1.vec_LC.clone();
		Vector<Integer> removevec=new Vector<Integer>();
		Vector<Integer> addvec=new Vector<Integer>();
		for(int i=0;i<handvec.size();i++){
			id=handvec.elementAt(i);
			for(int j=0;j<resv.size();j++){
				if(IsSubNode(T1,id,resv.elementAt(j))){
					removevec.addElement(id);
					
					if(!addvec.contains(resv.elementAt(j))){
						addvec.addElement(resv.elementAt(j));
					}
					break;
				}
			}
		}
		for(int i=0;i<removevec.size();i++){
			handvec.removeElement(removevec.elementAt(i));
		}
		for(int i=0;i<addvec.size();i++){
			if(!handvec.contains(addvec.elementAt(i))){
				handvec.addElement(addvec.elementAt(i));
			}
		}
		
		return handvec;
	}
	public Vector<Integer> MinMaxPartition(int d,Clu_HT T1,Cla_HT T2){
		int maxdepth=3;
		int Max_LeafNum=5;
		double delta=0.5; 
		double minq=Double.MAX_VALUE;
		double lastq=0.0f;
		int bestcut=0;
		int index=-1;
		double q;
		double pn,cn,sse,ssb,qt;
		int id=-1;
		double sita=1.0f;
		int father=-1;
		boolean IsBegin=true;
		int c=0;
		double scprf[]=new double[4];
		Vector<Integer> bestlc=new Vector<Integer>();
		Vector<Integer> cluvec=new Vector<Integer>();
		Vector<Integer> handlelc=new Vector<Integer>();
		Vector<Integer> reserved=new Vector<Integer>();
		
		
		if(d>maxdepth||(T1.root.it<num))
		return T1.vec_LC;
		
/*	if(d==1){
		bestlc.addElement(47);
		bestlc.addElement(42);
	}else{
	*/
	////////////////////////////////////////////////////////////////////////	
	    double total=Total(T1.root.labels);
		
		
		if(T1.root.flag==false){  //have Impl cla node;
			id=T1.root.claImpl;
			GetClaChildVec(id,T2,cluvec);
		}else{
			CollectImpl(T1,T1.root.it,cluvec);
		}
		
		if(d==1) System.out.println("cluvecc: "+cluvec);
		
		if(cluvec.size()==0){
			if(T1.root.labels.size()<=Max_LeafNum){
					return T1.root.labels;
			}else{ //逐层计算;
				sita=1;
				int catn=(T1.Num+1)/2;  //叶子节点个数;
				for(int i=1;i<catn;i++){
					father=T1.ExtendLC(i);
					if(IsSubNodeVec(T1,father,reserved)){//略过;
						continue;
					}
					handlelc=GetHandleLC(reserved,T1, handlelc);
					if(d==1) System.out.println("T1.vec_LC: "+T1.vec_LC);
					if(d==1) System.out.println("handlelc: "+handlelc);
					scprf=Cal_UCSCORE(d,i,handlelc,T1);
					//q=sita*Cal_UCSCORE(d,i,handlelc,T1);
					
					pn=PN(3,handlelc,T1);
					cn=CalCN(handlelc,T1);
					sse=ComputeSSE(handlelc,T1);
					//ssb=ComputeSSB(handlelc,T1);
					//q=sse/(sse+ssb);
					qt=sse/total;
			//		System.out.println("Precision of classification: "+scprf[1]);
			//		System.out.println("Recall of classification: "+scprf[2]);
					System.out.println("FMeasure of classification: "+scprf[3]);
			//		System.out.println("SSE: "+sse);
			//		System.out.println("SSB: "+ssb);
			//		double st=sse+ssb;
			//		System.out.println("E+B: "+st);
			//		System.out.println("Total: "+total);
			//		System.out.println("q: "+q);
					System.out.println("qt: "+qt);
					System.out.println("UnCertainty of classification: "+scprf[0]);
					System.out.println("Preference Number influence: "+pn);
					System.out.println("Balance of category: "+cn);
					
			//		System.out.println("Final Score of q pn and cn: "+scprf[0]/(pn*cn));
			//		System.out.println("Final Score of q and cn: "+scprf[0]/cn);
					
					q=sita*(0.5*qt+0.5*scprf[0])/(pn*cn);
					
				    System.out.println("Final Q: "+q);
				    if(q<minq){
			        	minq=q;
			        	bestcut=i;
			        	bestlc=(Vector<Integer>)handlelc.clone();  //是否有问题？？？
			        }
			        else{
			        	if((q-lastq)>=delta*lastq){
			        	//	System.out.println("lastq: "+lastq+" q: "+q);
			        		reserved.addElement(father);
			        	}
			        }	
				    lastq=q;
				}//for
			}//else
		}//if
		else{
			int catn=(T1.Num+1)/2;  //叶子节点个数；
			for(int i=1;i<catn;i++){
				//if(d==1) System.out.println("reserved: "+reserved);
				father=T1.ExtendLC(i);
				if(IsEnd(father,cluvec)){ //结束
					System.out.println("End: "+father);
					break;
				}
				if(IsSubNodeVec(T1,father,reserved)){//略过
					System.out.println("Skimmed: "+father);
					continue;
				}
				handlelc=GetHandleLC(reserved,T1, handlelc);
			
				c=InterSecNum(handlelc,cluvec);
				
				if(IsBegin&&(c==0)){ //初始略过；
					continue;
				}else{
					IsBegin=false;
					sita=F_Sita(c);
					if(d==1) System.out.println("T1.vec_LC: "+T1.vec_LC);
					if(d==1) System.out.println("handlelc: "+handlelc);
					
					scprf=Cal_UCSCORE(d,i,handlelc,T1);
				
					
					pn=PN(3,handlelc,T1);
					cn=CalCN(handlelc,T1);
					sse=ComputeSSE(handlelc,T1);
				//	ssb=ComputeSSB(handlelc,T1);
				//	q=sse/(sse+ssb);
					qt=sse/total;
				//	System.out.println("Precision of classification: "+scprf[1]);
				//	System.out.println("Recall of classification: "+scprf[2]);
					System.out.println("FMeasure of classification: "+scprf[3]);
				//	System.out.println("SSE: "+sse);
				//	System.out.println("SSB: "+ssb);
				//	System.out.println("Total: "+total);
				//	double st=sse+ssb;
				//	System.out.println("E+B: "+st);
				//	System.out.println("q: "+q);
					System.out.println("qt: "+qt);
					System.out.println("UnCertainty of classification: "+scprf[0]);
					System.out.println("Preference Number influence: "+pn);
					System.out.println("Balance of category: "+cn);
			//		System.out.println("Sita: "+sita);
				    sita=1;
			//		System.out.println("Final Score of q pn and cn: "+scprf[0]/(pn*cn));
			//		System.out.println("Final Score of q and cn: "+scprf[0]/cn);
					
			//		
				    q=sita*(0.6*qt+0.4*scprf[0])/(0.2*pn+0.8*cn);
				    
				    System.out.println("Final Q: "+q);
			//		System.out.println("Final Score of q pn and cn with sita: "+q);
			//		System.out.println("Final Score of q and cn with sita: "+sita*scprf[0]/cn);
					
				    
				    if(q<minq){
			        	minq=q;
			        	bestcut=i;
			        	bestlc=(Vector<Integer>)handlelc.clone();  //是否有问题？？？
			        } else{
			        	if((q-lastq)>=delta*lastq){
			        		//System.out.println("lastq: "+lastq+" q: "+q);
			        		reserved.addElement(father);
			        	}
			        }
				}//else
				lastq=q;
			}//for
		}//else
	////////////////////////////////////////////////////	
	//}
		if(d==1){
			HT_final.nodes[2*num-2].setchild(bestlc);
		}
		for(int i=0;i<bestlc.size();i++){
		
			Clu_HT t=new Clu_HT(T1.GetCH(bestlc.elementAt(i),num-1));
	
			HT_final.nodes[bestlc.elementAt(i)].setchild(MinMaxPartition(d+1,t,T2));
		}
		return  bestlc;
    }

	public void ShowFinalCT(){
		int i;
		for(i=0;i<2*num-1;i++){
			if(HT_final.nodes[i].vchild!=null){
				System.out.println(i+": "+HT_final.nodes[i].vchild);
			}
		}
	}
	
}
