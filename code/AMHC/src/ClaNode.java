import java.util.Vector;


public class ClaNode {
	public int it;
    public Vector<Integer> vchild;
    public int parent;
    public Vector<Integer> labels;
    public boolean flag;
    public int clu_Impl;
    
    public ClaNode(int c,int n){
    	it=c;
    	if(n==0){
           	vchild=null;
           	labels=new Vector<Integer>();
           	labels.addElement(it);
    	}
    	else{
    		vchild=new Vector<Integer>(n);
    		labels=new Vector<Integer>();
    	}
    	flag=false;
    	parent=-1;
    	clu_Impl=-1;
    	
    }
    public ClaNode(){
    	it=-1;
    	vchild=null;
    	labels=null;
    }
   
    public void AddChild(int k){
    	vchild.addElement(k);
    }
    public void AddLables(Vector<Integer> l){
    	for(int i=0;i<l.size();i++){
    	    labels.addElement(l.elementAt(i));
    	}
    	
    }
    public void setchild(Vector<Integer> vc){
    	vchild=new Vector<Integer>(vc);
    }
}

