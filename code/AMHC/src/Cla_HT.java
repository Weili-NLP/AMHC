import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

public class Cla_HT {
	  public ClaNode[] nodes;
	 //public Vector<ClaNode> nodes;
	 public ClaNode root;
	 public int Num;
	 
	 public Cla_HT(){
			 nodes=new ClaNode[100];
			 Num=0;
		 }
	 public Cla_HT(int num){
		// nodes=new ClaNode[100];
		// Num=0;
		     nodes=new ClaNode[2*num-1];
			 int i;
			 for(i=0;i<2*num-1;i++){
		        	nodes[i]=new ClaNode();
		     }
	 }
	 public Cla_HT(String filename){
		 //构造分类树ClaHT；
		 CreateHT(filename);
		 root=nodes[Num-1];
		 root.parent=-1;
		 SetParent(root);
		 SetLabels(root);
	 }
	 
	 public boolean CreateHT(String filename){
		 try {
				BufferedReader reader = new BufferedReader(new FileReader(filename));
				String line;
				line = reader.readLine();
				Num=Integer.parseInt(line);
				nodes=new ClaNode[Num*2];
				int id=0;
				while((line = reader.readLine()) != null){
					StringTokenizer tknr = new StringTokenizer(line,"= \t\r\n");
					
					String token = tknr.nextToken();
					int it=Integer.parseInt(token);
					token = tknr.nextToken();
					int cn=Integer.parseInt(token);
					nodes[id]=new ClaNode(it,cn);
					for(int i=0;i<cn;i++){
						token = tknr.nextToken();
						nodes[id].AddChild(Integer.parseInt(token));
					}
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
	 }*/
	 
	 public void SetParent(ClaNode root){
		 int index=-1;
		 if(root.vchild!=null){
			 for(int i=0;i<root.vchild.size();i++){
				 index=ID2index(root.vchild.elementAt(i));
				 nodes[index].parent=root.it;
				 SetParent(nodes[index]);
			 }
			
		 }else return;
	 }
	 public Vector<Integer> SetLabels(ClaNode root){
		 int index=-1;
		 if(root.vchild!=null){
			 for(int i=0;i<root.vchild.size();i++){
				 index=ID2index(root.vchild.elementAt(i));
				 root.AddLables(SetLabels(nodes[index]));
			 }
			return root.labels;
		 }else {
			 return root.labels;
		 }
	 }
	 
	 public void showTree(){
		 for(int i=0;i<nodes.length;i++){
			 System.out.println(nodes[i].it+" "+nodes[i].parent+" "+nodes[i].labels);
		 }
	 }
	 
	 public void AddNode(Vector<Integer> vchild, int cluid,int paid,Vector<Integer> labs){
		 Num++;
		 nodes[Num]=new ClaNode(Num,vchild.size());
		 for(int i=0;i<vchild.size();i++){
			 nodes[Num].AddChild(vchild.elementAt(i));
			 nodes[Num].flag=true;
			 nodes[Num].clu_Impl=cluid;
			 nodes[Num].parent=paid;
			 nodes[Num].labels=(Vector<Integer>)labs.clone();
		 }
	 }
	 
}
