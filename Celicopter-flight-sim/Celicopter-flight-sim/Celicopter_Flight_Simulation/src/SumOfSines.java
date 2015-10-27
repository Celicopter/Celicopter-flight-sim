
public class SumOfSines {
	private double[] amplitudes;
	private double[] frequencies;
	private double[] phaseOffset;
	
	public SumOfSines(){
		amplitudes=new double[1];
		amplitudes[0]=1;
		frequencies=new double[1];
		frequencies[0]=1;
		phaseOffset=new double[1];
		phaseOffset[0]=0;
	}
	
	public SumOfSines(double[] a,double[]f,double[] p){
		if(a.length==f.length && a.length==p.length){
			amplitudes=a;
			frequencies=f;
			phaseOffset=p;
		}
		else {
			System.err.println("Error-array dimentions do not agree");
			amplitudes=new double[1];
			amplitudes[0]=1;
			frequencies=new double[1];
			frequencies[0]=1;
			phaseOffset=new double[1];
			phaseOffset[0]=0;
		}
	}
	
	public double evaluate(long time){
		double r=0;
		for(int i=0;i<amplitudes.length;i++)
			r+=amplitudes[i]*Math.sin(frequencies[i]*time+phaseOffset[i]);
		return r;
	}
}
