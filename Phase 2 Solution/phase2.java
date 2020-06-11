//Antone J. Evans Jr.
//Phase2
//Normalization and Initialization 
//Mix-max and z-score normaliztion 

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
import java.util.Stack;

public class phase2 {

	static String fn = "";
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

		Scanner input = new Scanner(System.in); // Create a Scanner object

		// %F
		String filename = "";
		filename = input.nextLine();
		fn = filename;

		// %K
		double numCentroids = 0.0;
		numCentroids = input.nextDouble();
		numCen = numCentroids;

		// %I
		double maxIterations = 0.0;
		maxIterations = input.nextDouble();
		numInt = maxIterations;

		// %T
		double maxThreshold = 0.0;
		maxThreshold = input.nextDouble();
		numThreshold = maxThreshold;

		// %R
		double maxRuns = 0.0;
		maxRuns = input.nextDouble();
		numRuns = maxRuns;

		// Creating a File object that represents the disk file.
		PrintStream o = null;
		try {
			o = new PrintStream(new File("./phase1/maximin_maxmin_output_" + fn));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Store current System.out before assigning a new value
		PrintStream console = System.out;

		// Assign o to output stream
		System.setOut(o);

		System.out.println(fn + " " + numCen + " " + numInt + " " + numThreshold + " " + numRuns);

		// Opens the file
		DataFile();

		// Outputs the Runs
		for (int i = 0; i < numRuns; i++) {
			System.out.println("\nRun " + (i + 1));
			System.out.println("-------");
			SSEloops = 0;

			// Functions
			// Places the random points
			//ranCentroid();
			
			// Initialization methods
			//RandPartitions();
			//SSFP();
			maximin();
			
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
		//copyFile();
	}

	public static void DataFile() {
		// Opening file and spliting numbers into a single array
		try (BufferedReader br = new BufferedReader(new FileReader("./phase1/" + fn))) {
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

			// Adds each points into the arraylist
			for (int i = 0; i < numofpoints; i++) {
				Points.add(new ArrayList<Double>(i));
			}

			// Saves each element into an array based on the number of elements on each line
			
			// normalization methods
			//DataStore(numofpoints, numbers);
			// Max min normalization method
			norm(numofpoints, numbers);
			// Z score method 
			//zScore(numofpoints, numbers);
			
		} catch (IOException xIo) {
			xIo.printStackTrace();
		}
	}

	public static void ranCentroid() {
		// Sets the centroid at random points during the first run
		Random randomPoint = new Random();
		Cent = new ArrayList<>();
		System.out.println(Points);
		// Saves the random points into seperate arraylist
		for (int i = 0; i < numCen; i++) {
			int randomP = randomPoint.nextInt(Points.size());
			Cent.add(Points.get(randomP));
		}
	}
	
	// Simple selection farthest points
	public static void SSFP() {
		// Sets the centroid at random points during the first run
		Random randomPoint = new Random();
		Cent = new ArrayList<>();
		double distance = 0;
		int index = 0;
		// Saves the random points into seperate arraylist
		for (int i = 0; i < numCen-1; i++) {
			if (i == 0) {
			int randomP = randomPoint.nextInt(Points.size());
			Cent.add(Points.get(randomP));
			}
			else
				for (int j = 0; j < Points.size(); j++) {
					
		
					for (int k = 0; k < Cent.size(); k++) {
						// Calls the distance function
						distance += calculateDistance(Points.get(j), Cent.get(k));
					}
						if (j == 0) {
							largest = distance;
							index = j;
						} else if (distance > largest) {
							largest = distance;
							index = j;
						}
						
				}
			Cent.add(Points.get(index));
		}	
	}

		//Maximin
		public static void maximin() {
			// Sets the centroid at random points during the first run
			Random randomPoint = new Random();
			Cent = new ArrayList<>();
			double distance = 0;
			int index = 0;
			// Saves the random points into seperate arraylist
			for (int i = 0; i < numCen; i++) {
				if (i == 0) {
				int randomP = randomPoint.nextInt(Points.size());
				Cent.add(Points.get(randomP));
				}
				// Checks if i equals to 1 
				else if(i == 1) {
					// cycles through the points of in each in centroid and finds the distance
					for (int j = 0; j < Points.size(); j++) {
						for (int k = 0; k < Cent.size(); k++) {
							// Calls the distance function
							distance += calculateDistance(Points.get(j), Cent.get(k));
						}
						// checks if j equals 0 and checks who has the largest distance and save its index 
							if (j == 0) {
								largest = distance;
								index = j;
							} else if (distance > largest) {
								largest = distance;
								index = j;
							}
							
					}
				Cent.add(Points.get(index));
			}
				else {
					for (int j = 0; j < Points.size(); j++) {
						for (int k = 0; k < Cent.size(); k++) {
							// Calls the distance function
							distance += calculateDistance(Points.get(j), Cent.get(k));
						}
							if (j == 0) {
								smallest = distance;
								index = j;
							} else if (distance < smallest) {
								smallest = distance;
								index = j;
							}
					}
				Cent.add(Points.get(index));	
				}	
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
				} else if (distance >= largest) {
					largest = distance;
					index_largest = i;
				}

			}

			// Saves temp SSE
			tempSSE += smallest;

			// Saves the largest point to the overall point
			if (i == 0) {
				overall = largest;
				index_overall = index_largest;
			} else if (largest >= overall) {
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
				//pointsDistance();
			}
		}
		
	}

	public static double calculateDistance(ArrayList<Double> arrayList, ArrayList<Double> arrayList2) {
		// Calculates the distance between each element to the centorid
		double Sum = 0.0;
		for (int i = 0; i < arrayList.size(); i++) {
			Sum = Sum + ((arrayList.get(i) - arrayList2.get(i)) * (arrayList.get(i) - arrayList2.get(i)));
		}
		return Sum;
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

	public static void DataStore(double numofpoints, ArrayList<Double> numbers) {
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
	}

	public static void norm(double numofpoints, ArrayList<Double> numbers) {
		int temp = 0;
		ArrayList<Double> mincoords = new ArrayList<Double>();
		ArrayList<Double> maxcoords = new ArrayList<Double>();
		int pointindex = -1;

		// Finds the min and max points
		for (int i = 0; i < (int) numofpoints * dim; i++) {
			temp = (int) (i % dim);

			if (temp < dim) {
				mincoords.add(numbers.get(i));
				maxcoords.add(numbers.get(i));
			}

			if (mincoords.get(temp) > numbers.get(i)) {
				mincoords.set(temp, numbers.get(i));
			}

			if (maxcoords.get(temp) <= numbers.get(i)) {
				maxcoords.set(temp, numbers.get(i));
			}
		}
		for (int i = 0; i < (int) numofpoints * dim; i++) {
			temp = (int) (i % dim);
			numbers.set(i, (numbers.get(i) - mincoords.get(temp))
					/ (maxcoords.get(temp) - mincoords.get(temp) + 0.000000000001));
		}
		// Stores data into arraylist of arrylist
		DataStore(numofpoints, numbers);

	}

	public static void zScore(double numofpoints, ArrayList<Double> numbers) {
		int temp = 0;
		ArrayList<Double> mean = new ArrayList<Double>();
		ArrayList<Double> sd = new ArrayList<Double>();
		int pointindex = 0;

		// Finds the min and max points
		for (int i = 0; i < (int) numofpoints * dim; i++) {
			temp = (int) (i % dim);

			if (i < dim) {
				mean.add(numbers.get(i));
				sd.add(numbers.get(i));
			} else {
				mean.set(temp, mean.get(temp) + numbers.get(i));
			}
		}
		
		//Sets the mean of each point
		for (int i = 0; i < dim; i++) {
			mean.set(i, mean.get(i) / numofpoints);
		}
		
		// Sets the sd of each point 
		for (int i = 0; i < (int) numofpoints * dim; i++) {
			temp = (int) (i % dim);
			sd.set(temp, sd.get(temp) + Math.pow(numbers.get(i) - mean.get(temp), 2) + 0.00000001);
		}
		
		// Calculates the z score 
		for (int i = 0; i < (int) numofpoints * dim; i++) {
			temp = (int) (i % dim);
			numbers.set(i, (numbers.get(i) - mean.get(temp)) / sd.get(temp));
		}

		// Stores data into arraylist of arrylist
		DataStore(numofpoints, numbers);
	}
 
	public static void RandPartitions() {
		int randomP = 0;
		ArrayList<ArrayList> RandPart = new ArrayList<ArrayList>();
		int save = 0;
		Cent = new ArrayList<>();
		for(int i = 0; i < numCen; i++) {
			RandPart.add(new ArrayList<Double>());
		}
			// Sets the centroid at random points during the first run
			Random randomPoint = new Random();
			// Saves the random points into seperate arraylist
			for (int i = 0; i < numofpoints; i++) {
				randomP = randomPoint.nextInt((int)numCen);
				RandPart.get(randomP).add(Points.get(i));
				//System.out.println((ArrayList<Double>)Points.get(i));
			}
			//  Adds points to centroid arraylist
			for(int i = 0; i < RandPart.size(); i++) {
				Cent.add(centroid(RandPart.get(i)));
			}

	}
	
	public static ArrayList centroid(ArrayList<ArrayList<Double>> associations) {
		ArrayList<Double> temp = new ArrayList<>();
		// Puts 0.0 into empty arrays 
		for(int i = 0; i < dim; i++) {
			temp.add(0.0);
		}
		// Sets the points into the index looped into
		for(int i = 0; i < associations.size(); i++) {
			for(int j = 0; j < associations.get(i).size(); j++) {
			//temp.add(new ArrayList<Integer>());
				temp.set(j, associations.get(i).get(j) + temp.get(j));
			}
		}
		// final calculation 
		for(int i = 0; i < temp.size();i++) {
			temp.set(i, temp.get(i)/associations.size());
		}
		return temp;
	}
	
}