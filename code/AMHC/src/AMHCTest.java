import java.util.Vector;
import java.util.Queue;  
import java.util.LinkedList; 


public class AMHCTest {
	
	public  static void main(String[] args){
		
		String filename1,filename2;
		filename1="ClaHT";
		filename2="CluHT";
		double theta=0.8;
		Cla_HT claHT=new Cla_HT(filename1);
		Clu_HT cluHT=new Clu_HT(filename2);
		//claHT.showTree();
		//cluHT.showTree();
		MakeImpl_Tree test=new MakeImpl_Tree(claHT,cluHT,theta);
		test.MakeImplT();
		test.MakeLeafImpl();
		//test.showImpl();
		//test.showAdjustlist();
		test.AdjustNodes();
		//test.showClaTree(test.claHT.root);
		AMHC at=new AMHC(25);
		at.ReadDS("HACP\\Res");
		at.Centroid();
		//hacp.computerSim();
		//hacp.SaveSim("HACP\\SimMatrix");
		//at.ReadSim("HACP\\SimMatrix");
		at.MinMaxPartition(1, cluHT, claHT);
		at.ShowFinalCT();
		System.out.println("Finish!");
		
	}
	
}
