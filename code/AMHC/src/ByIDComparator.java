import java.util.Comparator;
import java.util.Comparator;



	public class ByIDComparator implements Comparator {
		
		public final int compare(Object pFirst, Object pSecond) {
	    int aFirstWeight = ((CluNode) pFirst).it;
		int aSecondWeight = ((CluNode) pSecond).it;
		int diff = aFirstWeight - aSecondWeight;
		if (diff > 0)
		return 1;
		if (diff < 0)
		return -1;
		else
		return 0;
		}
		
   }

