import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class testead{
	public static void main(String[] args){
		readInputSpecs();
		System.out.println("ITERATIONS_DELAY="+ITERATIONS_DELAY);
		System.out.println("XGain="+XGain);
		System.out.println("DISTANCE_FROM_SCREEN_IN_INCHES="+DISTANCE_FROM_SCREEN_IN_INCHES);
		System.out.println("DELAY_TIME="+DELAY_TIME);
		for(int i=0;i<SPATIAL_FREQUENCIES.length;i++)
			System.out.println("SPATIAL_FREQUENCIES["+i+"]="+SPATIAL_FREQUENCIES[i]);
		for(int i=0;i<MODULATIONS.length;i++)
			System.out.println("MODULATIONS["+i+"]="+MODULATIONS[i]);
	}
	private static int ITERATIONS_DELAY;
	private static int XGain;
	private static int DISTANCE_FROM_SCREEN_IN_INCHES;
	private static double[] SPATIAL_FREQUENCIES;
	private static double[] MODULATIONS;
	private static int DELAY_TIME;
	public static void readInputSpecs(){
		try {
			//Starts methodology for loading certain input parameters from file
			Scanner fileReader=new Scanner(new File("InputSpecifications.txt"));
			ArrayList<Integer> pixelDiams=new ArrayList<Integer>();
			ArrayList<Double> modulations=new ArrayList<Double>();
			while(fileReader.hasNextLine()){
				String line=fileReader.nextLine();
				String[] wordsNumbers=line.split(":");
				if(wordsNumbers.length<=1)
					continue;
				String[] numbers=wordsNumbers[1].split(" ");
				String[] words=wordsNumbers[0].split(" ");
				if(words[0].equalsIgnoreCase("Spatial")){
					for(int i=0;i<numbers.length;i++)
						try{
							pixelDiams.add(Integer.parseInt(numbers[i]));
						}
					catch(NumberFormatException e){
						continue;
					}
				}
				if(words[0].equalsIgnoreCase("Modulations")){
					for(int i=0;i<numbers.length;i++)
						try{
							modulations.add(Double.parseDouble(numbers[i]));
						}
					catch(NumberFormatException e){
						continue;
					}
				}
				if(words[0].equalsIgnoreCase("Delay") && words[1].equalsIgnoreCase("time")){
					for(int i=0;i<numbers.length;i++)
						try{
							DELAY_TIME=Integer.parseInt(numbers[i]);
							break;
						}
					catch(NumberFormatException e){
						continue;
					}
				}
				if(words[0].equalsIgnoreCase("Iterations") && words[1].equalsIgnoreCase("delay")){
					for(int i=0;i<numbers.length;i++)
						try{
							ITERATIONS_DELAY=Integer.parseInt(numbers[i]);
							break;
						}
					catch(NumberFormatException e){
						continue;
					}
				}
				if(words[0].equalsIgnoreCase("XGain")){
					for(int i=0;i<numbers.length;i++)
						try{
							XGain=Integer.parseInt(numbers[i]);
							break;
						}
					catch(NumberFormatException e){
						continue;
					}
				}
				if(words[0].equalsIgnoreCase("Distance") && words[1].equalsIgnoreCase("from")){
					for(int i=0;i<numbers.length;i++)
						try{
							DISTANCE_FROM_SCREEN_IN_INCHES=Integer.parseInt(numbers[i]);
							break;
						}
					catch(NumberFormatException e){
						continue;
					}
				}
			}
			SPATIAL_FREQUENCIES=new double[pixelDiams.size()];
			for(int i=0;i<SPATIAL_FREQUENCIES.length;i++)
				SPATIAL_FREQUENCIES[i]=pixelDiams.get(i);
			MODULATIONS=new double[modulations.size()];
			for(int i=0;i<MODULATIONS.length;i++)
				MODULATIONS[i]=modulations.get(i);
			fileReader.close();
		}

		//Ends Input Specifications read-in from file
		catch (FileNotFoundException e) {
			System.err.println("Cannot read input file");
			System.exit(0);
		}
	}
}