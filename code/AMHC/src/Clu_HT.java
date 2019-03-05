import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;


public class Clu_HT {
	public CluNode[] nodes;
	 public CluNode root;
	 public int Num;
	 public Vector<Integer> vec_LC;
	 
	 public Clu_HT(String filename){
		 //构造分类树ClaHT；
		 CreateHT(filename);
		 root=nodes[Num-1];
		 root.parent=-1;
		 SetParent(root);
		 SetLabels(root);
		 vec_LC=new Vector<Integer>();
	     vec_LC.addElement(Num-1);  //类别节点编号从0开始；
		 
	 }
	 public Clu_HT(Vector<CluNode> v){
			
			Num=v.size();
			
	    	nodes=new CluNode[Num];
	    	for(int i=0;i<Num;i++){
	        	nodes[i]=new CluNode();
	        }
	    	for(int i=0;i<Num;i++){
	    		nodes[i]=v.elementAt(i);
	    	}
	    	java.util.Arrays.sort(nodes,new ByIDComparator());
	    	root=nodes[Num-1];
	    	root.parent=-1;
	    	vec_LC=new Vector<Integer>();
	    	vec_LC.addElement(nodes[Num-1].it);  //类别节点编号从0开始；
	}
	 
	 public int ExtendLC(int level){
	    	
	    	int id1=Num-level;
	    	
	    	vec_LC.removeElement(nodes[id1].it);
	    	if(nodes[id1].left!=-1){
	    	vec_LC.addElement(nodes[id1].left);
	    	}
	    	if(nodes[id1].right!=-1){
	    	vec_LC.addElement(nodes[id1].right);
	    	}
	    	return nodes[id1].it;
	 }
	 
	 public boolean CreateHT(String filename){
		 try {
				BufferedReader reader = new BufferedReader(new FileReader(filename));
				String line;
				line = reader.readLine();
				Num=Integer.parseInt(line);
				nodes=new CluNode[Num];
				int id=0;
				while((line = reader.readLine()) != null){
					StringTokenizer tknr = new StringTokenizer(line,"= \t\r\n");
					String token = tknr.nextToken();
					int a=Integer.parseInt(token);
					token = tknr.nextToken();
					int b=Integer.parseInt(token);
					token = tknr.nextToken();
					int c=Integer.parseInt(token);
					nodes[id]=new CluNode(a,b,c);
					
				    id++;
				}
				reader.close();
			}
			
			catch (Exception e){
				System.out.println("Error while reading file:" + e.getMessage());
				e.printStackTrace();
				return false;
			}
			return true;		
	 }
	 public int ID2index(int id){
		 int i=0;
		 for(i=0;i<nodes.length;i++){
			 if(nodes[i].it==id){
				 return i;
			 }
		 }
		 return -1;
	 }
	 
	 /*public int ID2index(int id){
		 return id;
	 }
	 */
	 public void GetLeafCat(int k,int fix_cn, Vector<CluNode> vec){
		 int k1,k2;
		 int index=-1;
	    	if(k>fix_cn){
	    		index=ID2index(k);
	    		k1=nodes[index].left;
	    		k2=nodes[index].right;
	    	
	    		GetLeafCat(k1,fix_cn,vec);
	    		GetLeafCat(k2,fix_cn,vec);
	    		//CluNode n=new CluNode(k,k1,k2);
	    		//vec.addElement(n);
	    		vec.addElement(nodes[ID2index(k)]);
	    	}else{
	    		//CluNode n=new CluNode(k,-1,-1);
	    		//vec.addElement(n);
	    		vec.addElement(nodes[ID2index(k)]);
	    	}
	    }
	 public Vector<CluNode> GetCH(int k, int fix_cn){ //k是类别ID；不是下标；
		 Vector<CluNode> vecnode=new Vector<CluNode>();
		 GetLeafCat(k,fix_cn,vecnode);
		 
		 return vecnode;
	 }
	 public void SetParent(CluNode root){
		 int index=-1;
		 if(root.left!=-1){
			 index=ID2index(root.left);
			 nodes[index].parent=root.it;
			 SetParent(nodes[index]);
		 }
		 if(root.right!=-1){
			 index=ID2index(root.right);
			 nodes[index].parent=root.it;
			 SetParent(nodes[index]);
		 }
		 return;
	 }
	 public Vector<Integer> SetLabels(CluNode root){
		 int index1=-1;
		 int index2=-1;
		 if(root.left!=-1){
			 index1=ID2index(root.left);
			 root.AddLables(SetLabels(nodes[index1]));
		 
			 index2=ID2index(root.right);
			 root.AddLables(SetLabels(nodes[index2]));
			 return root.labels;
		 }
		 else{
			 root.labels.addElement(root.it);
			 return root.labels;
		 }
		 
	 }
	 public void showTree(){
		 for(int i=0;i<nodes.length;i++){
			 System.out.println(nodes[i].it+" "+nodes[i].labels);
		 }
	 }
	 
}
