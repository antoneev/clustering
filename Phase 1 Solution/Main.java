// Antone J. Evans Jr.
// Phase1
//Basic K Means

package phase1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

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
		String filename = "";
		Scanner getFile = new Scanner(System.in); // Create a Scanner object
		filename = getFile.nextLine();
		fn = filename;

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

		// Creating a File object that represents the disk file. 
        PrintStream o = null;
		try {
			o = new PrintStream(new File("./phase1/output_" + fn));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
  
        // Store current System.out before assigning a new value 
        PrintStream console = System.out; 
  
        // Assign o to output stream 
        System.setOut(o); 	
		
		System.out.println(fn + " " +numCen + " " + numInt + " " + numThreshold + " " + numRuns);

        
		// Outputs the Runs
		for (int i = 0; i < numRuns; i++) {
			System.out.println("\nRun " + (i + 1));
			System.out.println("-------");
			SSEloops = 0;

			// Functions
			// Opens the file
			DataFile();
			// Places the random points
			ranCentroid();

			// Functions
			do {
				// Finds the distance between each point
				pointsDistance();
				// Divides each point by the centroid to find the closest point
				divideVectors();
				// Calculates the SSE
				SSE();
			}
			// Finds the best run
			while (T > numThreshold && SSEloops <= numRuns);
			if (i == 0) {
				bestSSE = SSE;
				bestRun = i + 1;
			} else if (SSE < bestSSE) {
				bestSSE = SSE;
				bestRun = i + 1;
			}

		}
		// Outputs the best sse and best run
		System.out.println("\nBest SSE: " + bestSSE + "      Best Run: " + bestRun);

		// Modifies the file
//		copyFile();
	}

	public static void DataFile() {
		// Opening file and spliting numbers into a single array
		try (BufferedReader br = new BufferedReader(new FileReader("./phase1/" + fn))) {
			ArrayList<Double> numbers = new ArrayList<>();
			String line = br.readLine();
			while (line != null) {
				 String[] parts = line.trim().split("\\s+");
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

			// Making an arraylist of arraylist
			Points = new ArrayList<>();

			// Adds each points into the arraylist
			for (int i = 0; i < numofpoints; i++) {
				Points.add(new ArrayList<Double>(i));
			}

			// Saves each element into an array based on the number of elements on each line
			int temp = 0;
			int pointindex = -1;
			for (int i = 0; i < (int) numofpoints * dim; i++) {
				temp = (int) (i % dim);
				if (temp == 0) {
					pointindex++;
				}

				Points.get(pointindex).add(numbers.get(i));
			}
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

				// Finds the largest element for the in the centorid list
				if (j == 0) {
					largest = distance;
					index_largest = i;
				} else if (distance > largest) {
					largest = distance;
					index_largest = i;
				}

			}

			// Saves temp SSE
			tempSSE += smallest * smallest;

			// Saves the largest point to the overall point
			if (i == 0) {
				overall = largest;
				index_overall = index_largest;
			} else if (largest > overall) {
				overall = largest;
				index_overall = index_largest;
			}

			// Adds closest points to the arraylist it is closest to
			ClosestPoints.get(index).add(Points.get(i));

		}

		// Divides each array list by the centorid array list
		for (int k = 0; k < numCen; k++) {
			Divisor.add(k, (double) ClosestPoints.get(k).size());
		}

		// Saves the largest SSE into an empty arraylist
		for (int k = 0; k < numCen; k++) {
			if ((double) Divisor.get(k) == 0.0) {
				Cent.set(k, Points.get(index_overall));
				Divisor.set(k, 1.0);
				pointsDistance();
			}
		}
	}

	public static double calculateDistance(ArrayList<Double> arrayList, ArrayList<Double> arrayList2) {
		// Calculates the distance between each element to the centorid
		double Sum = 0.0;
		for (int i = 0; i < arrayList.size(); i++) {
			Sum = Sum + Math.pow((arrayList.get(i) - arrayList2.get(i)), 2.0);
		}
		return Math.sqrt(Sum);
	}

	public static void divideVectors() {
		// Divides each point arraylist by the the centorid arraylist
		ArrayList temp = new ArrayList<>();

		// Adds 0.0 to each array list 
		for (int i = 0; i < dim; i++) {
			temp.add(0.0);
		}
		for (int i = 0; i < ClosestPoints.size(); i++) {
			for (int m = 0; m < dim; m++) {
				temp.set(m, 0.0);
			}
			for (int j = 0; j < ClosestPoints.get(i).size(); j++) {
				temp = addVectors(temp, (ArrayList<Double>) ClosestPoints.get(i).get(j));

			}

			for (int l = 0; l < dim; l++) {
				double t1 = (double) temp.get(l);
				double t2 = (double) Divisor.get(i);
				temp.set(l, t1 / t2);
			}
			ArrayList temparray = (ArrayList) temp.clone();
			Cent.set(i, temparray);
		}
	}

	public static ArrayList<Double> addVectors(ArrayList<Double> first, ArrayList<Double> second) {
		// Adds each vector 
		int length = first.size() < second.size() ? first.size() : second.size();
		ArrayList<Double> result = new ArrayList<>();

		for (int i = 0; i < length; i++) {
			result.add(first.get(i) + second.get(i));
		}
		return result;
	}

	public static void SSE() {
		// Calculates the SSE
		T = 0.0;

		T = Math.abs(SSE - tempSSE) / SSE;
		SSE = tempSSE;
		tempSSE = 0.0;
		int printSSE = SSEloops + 1;

		System.out.print("Iteration " + printSSE + ": SSE = " + SSE + "\n");

		// Used to keep track of loops
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

			// Making an arraylist of arraylist
			Points = new ArrayList<>();

			for (int i = 0; i < numofpoints; i++) {
				Points.add(new ArrayList<Double>(i));
			}

			// Saves each element into an array based on the number of elements on each line
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

			// Gets every 5th element in the array and saves to its own array
			for (int i = 4; i < Points.size(); i += 5) {
				System.out.println(i + " " + Points.get(i));
				tempArrClone.add(Points.get(i));
			}

			// Outputs the 5th elements 9 times 
			for (int i = 0; i < tempArrClone.size(); i++) {
				for (int j = 0; j < 9; j++) {
					Points.add(tempArrClone.get(i));
					// System.out.println(Points);
				}
			}

			// Writes to this file
			FileWriter fw = new FileWriter("./phase1/iris_bezdek_mod.txt");
			BufferedWriter buffer = new BufferedWriter(fw);

			// Adds the points and dimenstion 
			buffer.write("420 4");
			buffer.newLine();
			// Adds each array list to the line and removes the , [ and ]
			for (int i = 0; i < Points.size(); i++) {
				buffer.write(
						String.valueOf(Points.get(i)).toString().replace("[", "").replace("]", "").replace(",", ""));
				buffer.newLine();
			}
			System.out.println("Writing successful");
			// close the file
			buffer.close();

		} catch (IOException xIo) {
			xIo.printStackTrace();
		}

	}

}
