package phase1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class main_get {

	static String fn = "";
	static Scanner getCentroids;
	static Scanner getIterations;
	static Scanner getThreshold;
	static Scanner getRuns;
	static double numofpoints = 0.0;
	static double dim = 0.0;
	static ArrayList<ArrayList> Points;
	static ArrayList Divisor;
	static ArrayList<ArrayList<Double>> Cent;
	static ArrayList<ArrayList> ClosestPoints;
	static int numCentroids = 0;
	static double numCen = 0;
	static double numInt = 0;
	static double numThreshold = 0;
	static double numRuns = 0;
	static double SSE = 1.0;
	static double prevSSE = 0.0;
	static double smallest = 0.0;
	static double largest = 0.0;
	static double overall = 0.0;
	static int SSEloops = 0;
	static double tempSSE = 0.0;
	static double maxThreshold = 0.0;
	static double T = 0.0;
	static double bestSSE = 0.0;
	static double bestRun = 0.0;

	public static void main(String[] args) {

		// %F
//		String filename = "";
//		Scanner getFile = new Scanner(System.in); // Create a Scanner object
//		fn = getFile.nextLine();

		// %K
		double numCentroids = 0.0;
		getCentroids = new Scanner(System.in);
		numCentroids = getCentroids.nextDouble();
		numCen = numCentroids;

		// %I
		double maxIterations = 0.0;
		getIterations = new Scanner(System.in);
		maxIterations = getIterations.nextDouble();
		numInt = maxIterations;

		// %T
		double maxThreshold = 0.0;
		getThreshold = new Scanner(System.in);
		maxThreshold = getThreshold.nextDouble();
		numThreshold = maxThreshold;

		// %R
		double maxRuns = 0.0;
		getRuns = new Scanner(System.in);
		maxRuns = getRuns.nextDouble();
		numRuns = maxRuns;
		
		for(int i = 0; i < numRuns; i++) {
		System.out.println("\nRun " + (i+1));
		System.out.println("-------");
		SSEloops = 0;
		
		DataFile();
		ranCentroid();
		
		// Functions
		do{
		pointsDistance();
		divideVectors();
		SSE();
	
		}
		while(T > numThreshold && SSEloops <= numRuns);
		if(i == 0)
		{
			bestSSE = SSE;
			bestRun = i + 1;
		}
		else if (SSE < bestSSE) {
			bestSSE = SSE;
			bestRun = i + 1;
		}
			
		}
		System.out.println("\nBest SSE: " + bestSSE + "      Best Run: " + bestRun);
		
//		copyFile();
	}

	public static void DataFile() {
		// Opening file and spliting numbers into a single array
		try (BufferedReader br = new BufferedReader(new FileReader("./phase1/iris_bezdek_mod.txt"))) {
			ArrayList<Double> numbers = new ArrayList<>();
			String line = br.readLine();
			while (line != null) {
				String[] parts = line.split(" ");
				// Reads each elements in the text file into the program 1 by 1
				for (String part : parts) {
					numbers.add(Double.valueOf(part)); // throws java.lang.NumberFormatException
				}
				line = br.readLine();
			}
			Double[] numArray = new Double[numbers.size()];
			// Pops the first line of the text file into variables
			numbers.toArray(numArray);
			numofpoints = numbers.remove(0);
			dim = numbers.remove(0);

			for (Double number : numbers) {
				//System.out.print(number);
				//System.out.print(" ");
			}

			System.out.print("\n");

			// Making an arraylist of arraylist
			Points = new ArrayList<>();

			for (int i = 0; i < numofpoints; i++) {
				Points.add(new ArrayList<Double>(i));
			}

			//Saves each element into an array based on the number of elements on each line
			int temp = 0;
			int pointindex = -1;
			for (int i = 0; i < (int) numofpoints * dim; i++) {
				temp = (int) (i % dim);
				if (temp == 0) {
					pointindex++;
				}

				Points.get(pointindex).add(numbers.get(i));
			}
			//System.out.println("\n" + Points);

		} catch (IOException xIo) {
			xIo.printStackTrace();
		}
	}

	public static void ranCentroid() {
		// Sets the centroid at random points during the first run
		Random randomPoint = new Random();
		Cent = new ArrayList<>();
		// Saves the random points into seperate arraylist
		for (int i = 0; i < numCen; i++) {
			int randomP = randomPoint.nextInt(Points.size());
			Cent.add(Points.get(randomP));
		}
		//System.out.println("\n" + Cent);
	}

	public static void pointsDistance() {
		ClosestPoints = new ArrayList<>();
		Divisor = new ArrayList<>();
		double distance = 0;
		smallest = 0;
		int index = 0;
		largest = 0;
		int index_overall = 0;
		overall = 0;
		int index_largest = 0;
		
		// Creates arrays based on user input
		for (int k = 0; k < numCen; k++) {
			ClosestPoints.add(new ArrayList(k));
		}
		
		// Adds points to the centroid that it is closest too
		for (int i = 0; i < Points.size(); i++) {
			index = 0;
			for (int j = 0; j < Cent.size(); j++) {
				// Calls the distance function 
				distance = calculateDistance(Points.get(i), Cent.get(j));
				if (j == 0) {
					smallest = distance;
					index = j;
				} else if (distance < smallest) {
					smallest = distance;
					index = j;
				}  
				
				//grad portion
				if (j == 0) {
					largest = distance;
					index_largest = i;
				} else if (distance > largest) {
					largest = distance;
					index_largest = i;
				}  				
				
			}
			
			tempSSE += smallest * smallest;

			if (i == 0) {
				overall = largest;
				index_overall = index_largest;
			} else if (largest > overall) {
				overall = largest;
				index_overall = index_largest;
			}  				
			
			ClosestPoints.get(index).add(Points.get(i));
			
		}
		//System.out.println(ClosestPoints);

		//System.out.println(ClosestPoints.get(0).size());
		//System.out.println(ClosestPoints.get(1).size());

		// Calculates the distance between each point 
		for (int k = 0; k < numCen; k++) {
			Divisor.add(k, (double) ClosestPoints.get(k).size());
		}
		
		for (int k = 0; k < numCen; k++) {
			if((double)Divisor.get(k) == 0.0) {
				Cent.set(k, Points.get(index_overall));
				Divisor.set(k, 1.0);
				pointsDistance();
			}
		}
		
			
		System.out.println(Divisor);
	}

	public static double calculateDistance(ArrayList<Double> arrayList, ArrayList<Double> arrayList2) {
		double Sum = 0.0;
		for (int i = 0; i < arrayList.size(); i++) {
			Sum = Sum + Math.pow((arrayList.get(i) - arrayList2.get(i)), 2.0);
		}
		//System.out.println("SUM = " + Sum);
		return Math.sqrt(Sum);
	}

	public static void divideVectors() {
		ArrayList temp = new ArrayList<>();

		for (int i = 0; i < dim; i++) {
			temp.add(0.0);
		}
		//System.out.println(Cent + "i");
		for (int i = 0; i < ClosestPoints.size(); i++) {
			for (int m = 0; m < dim; m++) {
				temp.set(m, 0.0);
			}
			//System.out.println(temp);
			for (int j = 0; j < ClosestPoints.get(i).size(); j++) {
				temp = addVectors(temp, (ArrayList<Double>) ClosestPoints.get(i).get(j));

			}
			//System.out.println(temp);

			for (int l = 0; l < dim; l++) {
				double t1 = (double) temp.get(l);
				double t2 = (double) Divisor.get(i);
				temp.set(l, t1 / t2);

			}
			ArrayList temparray = (ArrayList) temp.clone();

			Cent.set(i, temparray);

		}

		//System.out.println(Cent + "i");
	}

	public static ArrayList<Double> addVectors(ArrayList<Double> first, ArrayList<Double> second) {
		int length = first.size() < second.size() ? first.size() : second.size();
		ArrayList<Double> result = new ArrayList<>();

		for (int i = 0; i < length; i++) {
			result.add(first.get(i) + second.get(i));
		}
		return result;
	}

	public static void SSE() {
		T = 0.0;
		
		T = Math.abs(SSE - tempSSE )/SSE;
		SSE = tempSSE;
		tempSSE = 0.0;
		int printSSE = SSEloops + 1;
		
		System.out.print("Iteration " + printSSE + ": SSE = " + SSE + "\n");
		
		
		
		SSEloops += 1;
	}
	
	public static void copyFile() {
		ArrayList<ArrayList> tempArrClone = new ArrayList<>();
		
		// Opening file and spliting numbers into a single array
		try (BufferedReader br = new BufferedReader(new FileReader("./phase1/iris_bezdek.txt"))) {
			ArrayList<Double> numbers = new ArrayList<>();
			String line = br.readLine();
			while (line != null) {
				String[] parts = line.split(" ");
				// Reads each elements in the text file into the program 1 by 1
				for (String part : parts) {
					numbers.add(Double.valueOf(part)); // throws java.lang.NumberFormatException
				}
				line = br.readLine();
			}
			Double[] numArray = new Double[numbers.size()];
			// Pops the first line of the text file into variables
			numbers.toArray(numArray);
			numofpoints = numbers.remove(0);
			dim = numbers.remove(0);

			for (Double number : numbers) {
				System.out.print(number);
				System.out.print(" ");
			}

			System.out.print("\n");

			// Making an arraylist of arraylist
			Points = new ArrayList<>();

			for (int i = 0; i < numofpoints; i++) {
				Points.add(new ArrayList<Double>(i));
			}

			//Saves each element into an array based on the number of elements on each line
			int temp = 0;
			int pointindex = -1;
			for (int i = 0; i < (int) numofpoints * dim; i++) {
				temp = (int) (i % dim);
				if (temp == 0) {
					pointindex++;
				}

				Points.get(pointindex).add(numbers.get(i));
			}
			System.out.println("\n" + Points);

			for (int i = 4; i < Points.size(); i+=5) {
				System.out.println(i + " " + Points.get(i));
				//Points.add(Points.get(i));
				//Points.get(pointindex).add(numbers.get(i)); 
				tempArrClone.add(Points.get(i));
			}

			System.out.println("tempArrClone" + tempArrClone);				

			System.out.println(tempArrClone.get(0));
			
			for(int i = 0; i < tempArrClone.size(); i++) {
				for(int j = 0; j < 9; j++) {
				Points.add(tempArrClone.get(i));
				//System.out.println(Points);
				}
			}

			System.out.println("this " + Points);
			
		        // attach a file to FileWriter  
		        FileWriter fw=new FileWriter("./phase1/iris_bezdek_mod.txt"); 
		        BufferedWriter buffer = new BufferedWriter(fw);
		        
		        // read character wise from string and write  
		        // into FileWriter  
		        buffer.write("420 4");
		        buffer.newLine();
		        for (int i = 0; i < Points.size(); i++) {
		        	buffer.write(String.valueOf(Points.get(i)).toString().replace("[","").replace("]","").replace(",",""));
		            buffer.newLine();
		        }
				//Points.get(0).add(2);
		        System.out.println(Points.toString().replace("[","").replace("]","").replace(",",""));
		        System.out.println("Writing successful"); 
		        //close the file  
		        buffer.close(); 
		
		} catch (IOException xIo) {
			xIo.printStackTrace();
		}

	}

}
