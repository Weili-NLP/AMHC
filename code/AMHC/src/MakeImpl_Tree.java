import java.util.Vector;
import java.util.Queue;  
import java.util.LinkedList; 

public class MakeImpl_Tree {
	public Cla_HT claHT;
	public Clu_HT cluHT;
	public Vector<Integer> adjustlist;
	public double theta;
	
	public MakeImpl_Tree(Cla_HT claht,Clu_HT cluht,double thta){
		claHT=claht;
		cluHT=cluht;
		theta=thta;
		adjustlist=new Vector<Integer>();
	}
	public void MakeImpl_Node(ClaNode cla,CluNode clu){
		cla.clu_Impl=clu.it;
		clu.claImpl=cla.it;
		cla.flag=true;
		clu.flag=false;
		for(int i=0;i<cla.labels.size();i++){
			if(!clu.labels.contains(cla.labels.elementAt(i))){
				if(!adjustlist.contains(cla.labels.elementAt(i))){
				adjustlist.addElement(cla.labels.elementAt(i));
				}
			}
		}
	}
	public void MakeImplT(){
		Queue<Integer> queue = new LinkedList<Integer>();
		//queue.offer(claHT.root.it);
		boolean stopped=false;
		CluNode p_Impl=cluHT.root;
		MakeImpl_Node(claHT.root,p_Impl);
		Vector<Integer> labs=new Vector<Integer>();
		int id=-1;
		int index;
		int temp=-1;
		boolean change=true;
		int parImplid;
		while(stopped==false){
			for(int i=claHT.Num-1;i>=25;i--){
				if(claHT.nodes[i].flag==false)
					queue.offer(claHT.nodes[i].it);
			}
			while(!queue.isEmpty()){
				if(change==true){
				id=queue.poll();
				}
				index=claHT.ID2index(id);
				if(index!=(claHT.Num-1)&&claHT.nodes[claHT.ID2index(claHT.nodes[index].parent)].flag==false)
					 continue;
				
				labs=(Vector<Integer>)claHT.nodes[index].labels.clone();
				
				parImplid=GetParentImpl(index);
				
				//CluNode parImpl=cluHT.nodes[cluHT.ID2index(parImplid)];
				p_Impl=cluHT.nodes[cluHT.ID2index(FindImpl(parImplid,labs))];
				while(p_Impl.flag==false){
					if(ChooseCatNodes(id,p_Impl.claImpl,p_Impl)==id){
						SetSubtreeFlag(claHT.nodes[claHT.ID2index(p_Impl.claImpl)]);
						//MakeImpl_Node(claHT.nodes[claHT.ID2index(p_Impl.claImpl)],cluHT.nodes[cluHT.ID2index(FindImpl(p_Impl.it,labs))]);
						
					}else{
						p_Impl=cluHT.nodes[cluHT.ID2index(FindImpl(p_Impl.it,labs))];
					}
					
					//p_Impl= cluHT.nodes[cluHT.ID2index(GetMostCoveredChild(p_Impl,labs))];
				}
				change=true;
			    if((temp=UnCoveredChild(claHT.nodes[index],p_Impl))!=-1){    //判断是否有Uncovered类别全来自一个子类别；
					Pull_UP(temp);
					
					change=false;
				}
			    if(change)
				   MakeImpl_Node(claHT.nodes[index],p_Impl);
				
			}
			int j=0;
			for(j=claHT.Num-1;j>=25;j--){
				if(claHT.nodes[j].flag==false){
					break;
				}
			}
			if(j==24){
				stopped=true;
			}
		}
	}
	public Vector<Integer> UnCoveredLabels(Vector<Integer> labs, Vector<Integer> catlabs,Vector<Integer> vec){
		for(int i=0;i<catlabs.size();i++){
			if(!labs.contains(catlabs.elementAt(i))){
				vec.addElement(catlabs.elementAt(i));
			}
		}
		return vec;
	}
	public double CoveredRatio(Vector<Integer> uncoveredlabs, Vector<Integer> childcatlabs){
		double result=0.0f;
		int a=0;
		for(int i=0;i<uncoveredlabs.size();i++){
			if(childcatlabs.contains(uncoveredlabs.elementAt(i))){
				a++;
			}
		}
		result=a*1.0/childcatlabs.size();
		return result;
	}
	public int UnCoveredChild(ClaNode cla, CluNode pImpl){
		int a=-1;
		Vector<Integer> uncoverlabels=new Vector<Integer>();
		UnCoveredLabels(pImpl.labels,cla.labels,uncoverlabels);
		for(int i=0;i<cla.vchild.size();i++){
			if(CoveredRatio(uncoverlabels,claHT.nodes[claHT.ID2index(cla.vchild.elementAt(i))].labels)>theta){
				return cla.vchild.elementAt(i);
			}
		}
		return a;
	}
	
	public void Pull_UP(int claid){
		int par=claHT.nodes[claHT.ID2index(claid)].parent;
		int parpar=claHT.nodes[claHT.ID2index(par)].parent;
		claHT.nodes[claHT.ID2index(par)].vchild.removeElement(claid);
		claHT.nodes[claHT.ID2index(parpar)].vchild.addElement(claid);
		claHT.nodes[claHT.ID2index(claid)].parent=parpar;
		claHT.SetLabels(claHT.nodes[claHT.ID2index(par)]);
	}
	
	public int GetParentImpl(int claindex){
		return  claHT.nodes[claHT.ID2index(claHT.nodes[claindex].parent)].clu_Impl;
		
	}
	public double UnMatchedRatio(Vector<Integer> labels){
		double result=0.0f;
		int a=0;
		for(int i=0;i<labels.size();i++){
			if(adjustlist.contains(labels.elementAt(i))){
				a++;
			}
		}
		result=a*1.0/labels.size();
		return result;
	}
	public int FindImpl(int parid,Vector<Integer> labs){
		
		CluNode parImpl=cluHT.nodes[cluHT.ID2index(parid)];
		int pImpl=GetMostCoveredChild(parImpl,labs);
		Vector<Integer> l=cluHT.nodes[cluHT.ID2index(pImpl)].labels;
		if(IsCompleteMatch(l,labs)){
			pImpl=FindCompleteImpl(cluHT.nodes[cluHT.ID2index(pImpl)],labs);
		}
		else{
			labs=CoveredLabels(l,labs);
			pImpl=FindCompleteImpl(cluHT.nodes[cluHT.ID2index(pImpl)],labs);
		}
		return pImpl;
		
	}
	public Vector<Integer> CoveredLabels(Vector<Integer> labs, Vector<Integer> catlabs){
		Vector<Integer> vec=new Vector<Integer>();
		for(int i=0;i<catlabs.size();i++){
			if(!labs.contains(catlabs.elementAt(i))){
				vec.addElement(catlabs.elementAt(i));
			}
		}
		for(int i=0;i<vec.size();i++){
		      catlabs.removeElement(vec.elementAt(i));
		}
		return catlabs;
	}
	
	public int GetMostCoveredChild(CluNode parImpl,Vector<Integer> labs){
	
		int a=GetCoveredNum(cluHT.nodes[cluHT.ID2index(parImpl.left)].labels,labs);
		int b=GetCoveredNum(cluHT.nodes[cluHT.ID2index(parImpl.right)].labels,labs);
		if(a>b){
			return parImpl.left;
		}else{
			return parImpl.right;
		}
	}
	public boolean IsCompleteMatch(Vector<Integer> l,Vector<Integer> labs){
		if(l.containsAll(labs))
			return true;
		return false;
	}
	public int GetCoveredNum(Vector<Integer> l,Vector<Integer> labs){
		int a=0;
		for(int i=0;i<labs.size();i++){
		   if(l.contains(labs.elementAt(i))){
			   a++;
		   }
		}
		return a;
	}
	public int FindCompleteImpl(CluNode p, Vector<Integer> lab){
		int id=-1;
		id=GetCompleteCoveredChild(p,lab);
		if(id==-1){
			return p.it;
		}else{
			id=FindCompleteImpl(cluHT.nodes[cluHT.ID2index(id)],lab);
			return id;
		}
	}
	public int GetCompleteCoveredChild(CluNode p, Vector<Integer> lab){
		//int id=-1;
		if(cluHT.nodes[cluHT.ID2index(p.left)].labels.containsAll(lab)){
			return p.left;
		}else if(cluHT.nodes[cluHT.ID2index(p.right)].labels.containsAll(lab)){
			return p.right;
		}else return -1;
			
	}
	public int ChooseCatNodes(int clan1,int clan2,CluNode pImpl){
		int a,b,e1;
		a=GetCoveredNum(claHT.nodes[claHT.ID2index(clan1)].labels,cluHT.nodes[cluHT.ID2index(pImpl.left)].labels);
		b=GetCoveredNum(claHT.nodes[claHT.ID2index(clan1)].labels,cluHT.nodes[cluHT.ID2index(pImpl.right)].labels);
		int c,d,e2;
		c=GetCoveredNum(claHT.nodes[claHT.ID2index(clan2)].labels,cluHT.nodes[cluHT.ID2index(pImpl.left)].labels);
		d=GetCoveredNum(claHT.nodes[claHT.ID2index(clan2)].labels,cluHT.nodes[cluHT.ID2index(pImpl.right)].labels);
		e1=(a<=b)?a:b;
		e2=(c<=d)?c:d;
		return (e1>=e2)?clan1:clan2;
	
	}
	public void SetSubtreeFlag(ClaNode cla){
		 if(cla.flag==false)  return;
		 cla.flag=false;
		 cluHT.nodes[cluHT.ID2index(cla.clu_Impl)].flag=true;
		 cluHT.nodes[cluHT.ID2index(cla.clu_Impl)].claImpl=-1;
		 cla.clu_Impl=-1;
		 if(cla.vchild!=null){
			 for(int i=0;i<cla.vchild.size();i++){
				 SetSubtreeFlag(claHT.nodes[claHT.ID2index(cla.vchild.elementAt(i))]);
			 }
		 }
	}
	
	public void AdjustNodes(){
		int id=-1;
		int pid=-1;
		int clapid=-1;
		for(int i=0;i<adjustlist.size();i++){
			//Find the nearest Impl parent node in cluHT;
			id=adjustlist.elementAt(i); //id 属于ClaHT，也属于CluHT；
			pid=cluHT.nodes[cluHT.ID2index(id)].parent;
			//id=claHT.nodes[claHT.ID2index(id)]
		    while(cluHT.nodes[cluHT.ID2index(pid)].flag){
		    	pid=cluHT.nodes[cluHT.ID2index(pid)].parent;
		    }
		    //Add the adjust node as the child of the impl node in claHT;
		    clapid=cluHT.nodes[cluHT.ID2index(pid)].claImpl;
		    pid=claHT.nodes[claHT.ID2index(id)].parent;
		    claHT.nodes[claHT.ID2index(pid)].vchild.removeElement(id);
		    claHT.nodes[claHT.ID2index(clapid)].vchild.addElement(id);
		    claHT.nodes[claHT.ID2index(id)].parent=clapid;
		    
		}
		claHT.SetLabels(claHT.root);
		
	}
	public void MakeLeafImpl(){
		int n=(cluHT.Num+1)/2;
		for(int i=0;i<n;i++){
			claHT.nodes[claHT.ID2index(i)].clu_Impl=i;
			claHT.nodes[claHT.ID2index(i)].flag=true;
			cluHT.nodes[cluHT.ID2index(i)].claImpl=i;
			cluHT.nodes[cluHT.ID2index(i)].flag=false;
		}
	}
	public void showImpl(){
		for(int i=0;i<claHT.Num;i++){
			System.out.println(claHT.nodes[i].clu_Impl);
		}
	}
	public void showAdjustlist(){
		System.out.println(adjustlist);
	}
	public void showClaTree(ClaNode root){
		//只打印中间节点；
		if(root.vchild!=null){
			System.out.print(root.it+" "+root.vchild.size()+" ");
			for(int i=0;i<root.vchild.size();i++){
				System.out.print(root.vchild.elementAt(i)+" ");
			}
			System.out.println();
			for(int i=0;i<root.vchild.size();i++){
				showClaTree(claHT.nodes[claHT.ID2index(root.vchild.elementAt(i))]);
			}
		}
	}
}
