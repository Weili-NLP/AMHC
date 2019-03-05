  import weka.core.*;
  import weka.core.Capabilities.*;
  import weka.filters.*; 
  import weka.filters.unsupervised.attribute.Remove;
  import java.util.regex.Pattern;


  public class RemoveNumAttr
    extends Filter {
	  protected Remove m_attributeFilter = new Remove();
 /** Whether to invert selection */
    protected boolean m_invert = false;
    public static String pattern="\\D*\\d+.*";
    public int[] attsToDelete ;
    public String globalInfo() {
      return   "A batch filter that adds an additional attribute 'bla' at the end "
             + "containing the index of the processed instance. The output format "
             + "can be collected immediately.";
    }
 
    public Capabilities getCapabilities() {
      Capabilities result = super.getCapabilities();
      result.enableAllAttributes();
      result.enableAllClasses();
      result.enable(Capability.NO_CLASS);  // filter doesn't need class to be set
      return result;
    }
 
    public boolean setInputFormat(Instances instanceInfo) throws Exception {
      super.setInputFormat(instanceInfo);
 
      Instances outFormat = new Instances(instanceInfo, 0);
      attsToDelete = new int[instanceInfo.numAttributes()];
      int numToDelete = 0;
      Pattern.compile(pattern);
      for (int i=0; i<outFormat.numAttributes(); i++) {
      if ((i == outFormat.classIndex() && !m_invert)) {
       	continue; // skip class
      }
      if (Pattern.matches(pattern, outFormat.attribute(i).name())){
    	 // System.out.print(i+" ");
    	  attsToDelete[numToDelete++] = i;
          //outFormat.deleteAttributeAt(i);
      }
      }
      int[] finalAttsToDelete = new int[numToDelete];
      System.arraycopy(attsToDelete, 0, finalAttsToDelete, 0, numToDelete);
      
      m_attributeFilter.setAttributeIndicesArray(finalAttsToDelete);
      m_attributeFilter.setInvertSelection(m_invert);
      
      boolean result = m_attributeFilter.setInputFormat(instanceInfo);
      Instances afOutputFormat = m_attributeFilter.getOutputFormat();
      
      // restore old relation name to hide attribute filter stamp
      afOutputFormat.setRelationName(instanceInfo.relationName());

      setOutputFormat(afOutputFormat);
      return result;
    }
    public boolean input(Instance instance) {
        
        return m_attributeFilter.input(instance);
     }
    public boolean batchFinished() throws Exception {

        return m_attributeFilter.batchFinished();
      }
    public Instance output() {

        return m_attributeFilter.output();
      }
    public Instance outputPeek() {

        return m_attributeFilter.outputPeek();
      }
    public int numPendingOutput() {
    	  
        return m_attributeFilter.numPendingOutput();
      }
    public boolean isOutputFormatDefined() {

        return m_attributeFilter.isOutputFormatDefined();
      }
 /*
    public boolean batchFinished() throws Exception {
      if (getInputFormat() == null)
        throw new NullPointerException("No input instance format defined");
 
      Instances inst = getInputFormat();
      Instances outFormat = getOutputFormat();
      for (int i = 0; i < inst.numInstances(); i++) {
        double[] newValues = new double[outFormat.numAttributes()];
        double[] oldValues = inst.instance(i).toDoubleArray();
        int t=0;
        int s=0;
       // System.arraycopy(oldValues, 0, newValues, 0, newValues.length);
       for(int j=0;j<oldValues.length;j++){
    	   if(j!=attsToDelete[s]){
    		   newValues[t++]=oldValues[j];
    	   }else{s++;}
       }
       if(t==newValues.length){System.out.println("Success");}
        push(new Instance(1.0, newValues));
      }
 
      flushInput();
      m_NewBatch = true;
      m_FirstBatchDone = true;
      return (numPendingOutput() != 0);
    }
 */
    public static void main(String[] args) {
      runFilter(new RemoveNumAttr(), args);
    }
  }