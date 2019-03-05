import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Date;

public class BFD {
	public Cla_HT claHT;
	public Vector<String> clalist;
	public String dirn;
	
	public BFD(Cla_HT claht,int level,int k,String filename,String dirname){
		clalist=new Vector<String>();
		claHT=claht;
		// 获得当前时间
		//DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		// 转换为字符串
		//String formatDate = format.format(new Date());
		// 随机生成文件编号
		
		String na=level+"_"+k;
		dirn=dirname+na;
		String dir=dirname+na;
		
		File d=new File(dirn);
		d.mkdir();
		
		if(CreateList(filename)&&false)   BuildDirSystem(dir);
		
	}
	public boolean CreateList(String filename){
		 try {
				BufferedReader reader = new BufferedReader(new FileReader(filename));
				String line;
				
				int id=0;
				while((line = reader.readLine()) != null){
					clalist.add(id, line);
				    id++;
				}
				reader.close();
			}
			catch (Exception e){
				System.out.println("Error while reading list:" + e.getMessage());
				e.printStackTrace();
				return false;
			}
			return true;		
	}
	public void BuildDirSystem(String dir){
		CreateDir(claHT.root,dir);
	}
	public void CreateDir(ClaNode root,String dir){
		 if(root.vchild!=null){
			    dir=dir+"\\"+root.it;
			    File d=new File(dir);
			    d.mkdir();
			    GenerateTrainFile(root,dir);
			    GenerateTestFile(root,dir);
			    for(int i=0;i<root.vchild.size();i++){
			    	CreateDir(claHT.nodes[claHT.ID2index(root.vchild.elementAt(i))],dir);
			    }
		 }else {
			 return;
		 }
	}
	
	/*
	public void CreateDir(ClaNode root,String dir){
		 if(root.vchild!=null){
			    dir=dir+"\\"+root.it;
			    File d=new File(dir);
			    d.mkdir();
			    for(int i=0;i<root.vchild.size();i++){
			    	CreateDir(claHT.nodes[claHT.ID2index(root.vchild.elementAt(i))],dir);
			    }
		 }else {
			    dir=dir+"\\"+root.it;
			    File f=new File(dir);
			    try {
					f.createNewFile();
					
				} catch (IOException e) {
					System.out.println("Error create file "+dir); 
					e.printStackTrace();
				}
		 }
	}
	*/
	public String Attr_Class(ClaNode root){
		String res="\n@attribute class {";
		for(int j=0;j<root.vchild.size();j++){
			if(root.vchild.elementAt(j)<clalist.size())
				res+=clalist.elementAt(root.vchild.elementAt(j));
			else
				res+=root.vchild.elementAt(j);
			if(j!=root.vchild.size()-1)
				res+=",";
		}
		res+="}";
		return res;
	}
	public void GenerateTrainFile(ClaNode root,String dir){
		String head="@relation RetuersData_Train"+"\n\n"+"@attribute text string";
		String attr_class=Attr_Class(root);
		
		String data="\n\n@data\n";
		
		String filename=dir+"\\traindata.arff";
		
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter(filename));
			writer.write(head);
			writer.write(attr_class);
			writer.write(data);
			for(int i=0;i<root.labels.size();i++){
				int claid=root.labels.elementAt(i);
				String claname=clalist.elementAt(claid);
				String fname="train"+"\\"+claname+"_train";
				BufferedReader reader = new BufferedReader(new FileReader(fname));
				String line;
				String regex="\\n',"+claname;
				String replacement="\\n',";
				int j=0;
				for(j=0;j<root.vchild.size();j++){
					if(claHT.nodes[claHT.ID2index(root.vchild.elementAt(j))].labels.contains(claid)){
						if(root.vchild.elementAt(j)<clalist.size())
							replacement+=clalist.elementAt(root.vchild.elementAt(j));
						else
							replacement+=root.vchild.elementAt(j);
						break;
					}
				}
				
				while((line = reader.readLine()) != null){
					
					    line=line.replace(regex,replacement);
					
						writer.write(line);
						writer.write("\n");
				}
				reader.close();
			}
			writer.close();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error while generate train file:" + e.getMessage());
				e.printStackTrace();
		}
		
	}
	public void GenerateTestFile(ClaNode root,String dir){
		String head="@relation RetuersData_Test"+"\n\n"+"@attribute text string";
		String attr_class=Attr_Class(root);
		
		String data="\n\n@data";
		
		String filename=dir+"\\testdata.arff";
		
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter(filename));
			writer.write(head);
			writer.write(attr_class);
			writer.write(data);
			for(int i=0;i<root.labels.size();i++){
				int claid=root.labels.elementAt(i);
				String claname=clalist.elementAt(claid);
				String fname="test"+"\\"+claname+"_test";
				BufferedReader reader = new BufferedReader(new FileReader(fname));
				String line;
				String regex="\\n',"+claname;
				String replacement="\\n',";
				int j=0;
				for(j=0;j<root.vchild.size();j++){
					if(claHT.nodes[claHT.ID2index(root.vchild.elementAt(j))].labels.contains(claid)){
						if(root.vchild.elementAt(j)<clalist.size())
							replacement+=clalist.elementAt(root.vchild.elementAt(j));
						else
							replacement+=root.vchild.elementAt(j);
						break;
					}
				}
				while((line = reader.readLine()) != null){
					    line=line.replace(regex,replacement);
						writer.write(line);
						writer.write("\n");
				}
				reader.close();
			}
			writer.close();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error while generate train file:" + e.getMessage());
				e.printStackTrace();
		}
	}
	
}
