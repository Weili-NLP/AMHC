
import java.util.Vector;


public class CluNode {
	public int it;
    public int left;
    public int right;
    public Vector<Integer> labels;
    public int claImpl;
    public boolean flag;
    public int parent;
    
    public CluNode(){
    
    }
    public CluNode(int c,int l,int r){
    	it=c;
    	left=l;
    	right=r;
    	labels=new Vector<Integer>();
    	claImpl=-1;
    	flag=true;
    }
    public void AddLables(Vector<Integer> l){
    	for(int i=0;i<l.size();i++){
    	    labels.addElement(l.elementAt(i));
    	}
    	
    }
   
}

