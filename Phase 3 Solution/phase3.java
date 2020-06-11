//Antone J. Evans Jr.
//Phase 3
//Interal Validation
//Calinski–Harabasz (CH), Silhouette Width (SW) and  Davies–Bouldin (DB) internal validity indices

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

public class phase3 {

	static String fn = "";
	static double numofpoints = 0.0;
	static double dim = 0.0;
	static ArrayList<ArrayList<Double>> Points;
	static ArrayList Divisor;
	static ArrayList<ArrayList<Double>> Cent;
	static ArrayList<ArrayList> ClosestPoints;
	static ArrayList<ArrayList<ArrayList>> allClosestPoints;
	static ArrayList<ArrayList<ArrayList>> allCents;
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
	static int bestCen = 0;
	static ArrayList<Double> numbers;
	static double maxCen = 0;
	static double CH = 0.0;
	static double SW = 0.0;
	static double DB = 0.0;
	static double bestCH = Double.MIN_VALUE;
	static double bestSW = Double.MIN_VALUE;
	static double bestDB = Double.MAX_VALUE;

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

		// Initialize total run arraylists.
		allCents = new ArrayList<ArrayList<ArrayList>>();
		allClosestPoints = new ArrayList<ArrayList<ArrayList>>();

		// Creating a File object that represents the disk file.
		PrintStream o = null;
		try {
			o = new PrintStream(new File("./phase1/phase3_1_" + fn));
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

		while (numCen <= maxCen) {
			// Outputs the Runs
			for (int i = 0; i < numRuns; i++) {
				if (i == 0) {
					System.out.println("\nCent " + numCen);
				}
				System.out.println("\nRun " + (i + 1));
				System.out.println("-------");
				SSEloops = 0;

				// Functions
				// Places the random points
				// ranCentroid();

				// Initialization methods
				// RandPartitions();
				// SSFP();
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
//					bestCH = CH;
//					bestSW = SW;
//					bestDB = DB;
				} else if (SSE < bestSSE) {
					bestSSE = SSE;
					bestRun = i + 1;
//					if (CH > bestCH)
//						bestCH = CH;
//					if(SW > bestSW)
//						bestSW = SW;
//					if(DB < bestDB)
//						bestDB = DB;
				}
				

				// Add all the information for this run.
				allClosestPoints.add(ClosestPoints);
				allCents.add((ArrayList) Cent);
			}
			CHindex(Cent);
			SWindex();
			DBIndex();
			allClosestPoints.clear();
			allCents.clear();
			numCen++;
		}

		// Outputs the best sse and best run
		System.out.println("\nBest SSE: " + bestSSE + "      Best Run: " + bestRun);

		// Modifies the file
		// copyFile();
	}

	public static void DataFile() {
		// Opening file and spliting numbers into a single array
		try (BufferedReader br = new BufferedReader(new FileReader("./phase1/" + fn))) {
			numbers = new ArrayList<>();
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
			maxCen = Math.ceil(Math.sqrt(numofpoints / 2));
			dim = numbers.remove(0);

			// Making an arraylist of arraylist
			Points = new ArrayList<>();

			// Adds each points into the arraylist
			for (int i = 0; i < numofpoints; i++) {
				Points.add(new ArrayList<Double>(i));
			}

			// Saves each element into an array based on the number of elements on each line

			// normalization methods
			// DataStore(numofpoints, numbers);
			// Max min normalization method
			norm(numofpoints, numbers);
			// Z score method
			// zScore(numofpoints, numbers);

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
		for (int i = 0; i < numCen - 1; i++) {
			if (i == 0) {
				int randomP = randomPoint.nextInt(Points.size());
				Cent.add(Points.get(randomP));
			} else
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

	// Maximin
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
			else if (i == 1) {
				// cycles through the points of in each in centroid and finds the distance
				for (int j = 0; j < Points.size(); j++) {
					for (int k = 0; k < Cent.size(); k++) {
						// Calls the distance function
						distance += calculateDistance(Points.get(j), Cent.get(k));
					}
					// checks if j equals 0 and checks who has the largest distance and save its
					// index
					if (j == 0) {
						largest = distance;
						index = j;
					} else if (distance > largest) {
						largest = distance;
						index = j;
					}

				}
				Cent.add(Points.get(index));
			} else {
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
				// pointsDistance();
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

		// Delta Obj 
		T = Math.abs(SSE - tempSSE) / SSE;
		SSE = tempSSE;
		tempSSE = 0.0;
		int printSSE = SSEloops + 1;

		System.out.print("Iteration " + printSSE + ": obj = " + SSE + " delta obj = " + T + "\n");
		
		// Used to keep track of loops
		SSEloops += 1;
	}

	public static void CHindex(ArrayList<ArrayList<Double>> Cent) {
		// Calculates Calinski–Harabasz (CH) Index
		// Variables
		Double test;
		double distance = 0.0;
		double SSB = 0.0;
		ArrayList<Double> add = new ArrayList<Double>();

		// Initizes the array for points to be added
		for (int i = 0; i < dim; i++) {
			add.add(0.0);
		}

		// Adds the points in each Centroid
		for (int i = 0; i < numCen; i++) {
			for (int j = 0; j < dim; j++) {
				test = (Double) allCents.get((int) bestRun).get(i).get(j);
				add.set(j, add.get(j) + test);
			}
		}

		// Gets the mean of those points
		for (int i = 0; i < dim; i++) {
			add.set(i, add.get(i) / numCen);
		}

		for (int j = 0; j < Points.size(); j++) {
			// Calls the distance function
			distance += calculateDistance(Points.get(j), add);
		}

		// Takes the SSE away
		SSB = distance - SSE;

		// CH Formula
		CH = (distance / SSE) * ((numofpoints - numCen) / (numCen - 1));

		System.out.println("CH = " + CH);
	}

	public static void SWindex() {
		// Silhouette Width (SW) Index

		ArrayList<ArrayList<Double>> ai = new ArrayList<ArrayList<Double>>();
		// Creates arrays based on number of centroids
		for (int k = 0; k < numCen; k++) {
			ai.add(new ArrayList(k));
		}

		double temp = 0.0;
		double inDist = 0.0;
		double outDist = 0.0;
		double siVal = 0.0;
		SW = 0.0;

		// Gets all of the centroids
		for (int k = 0; k < numCen; k++) {
			// Gets the centroid itself
			// For each point inside this cluster
			for (int m = 0; m < allClosestPoints.get((int) bestRun).get(k).size(); m++) {
				// Gets all the elements in array
				ArrayList<Double> point = (ArrayList<Double>) allClosestPoints.get((int) bestRun).get(k).get(m);
				// Calculate the distance between this point and the other points in this
				// cluster.
				for (int n = 0; n < allClosestPoints.get((int) bestRun).get(k).size(); n++) {
					if (allClosestPoints.get((int) bestRun).get(k).get(n) != point) {
						inDist += calculateDistance(
								(ArrayList<Double>) allClosestPoints.get((int) bestRun).get(k).get(n), point);
					}

				}

				inDist = inDist / ((allClosestPoints.get((int) bestRun).get(k).size() - 1) + 0.00000001);

				// Calculate the distance between this point and and the other points in the
				// closest cluster.
				// Finding the distance between this centroid and all the other centroids.
				int centDist = 0;
				double temp1 = 0.0;
				double temp2 = Double.MAX_VALUE;
				int c = 0;

				for (c = 0; c < numCen; c++) {
					if (c != k) {
						temp1 = calculateDistance(allCents.get((int) bestRun).get(k),
								allCents.get((int) bestRun).get(c));

						if (temp1 < temp2) {
							centDist = c;
							temp2 = temp1;
						}
					}
				}

				// Calculate the distance between this point and all the points in the closest
				// center.
				for (int d = 0; d < allClosestPoints.get((int) bestRun).get(centDist).size(); d++) {
					outDist += calculateDistance(
							(ArrayList<Double>) allClosestPoints.get((int) bestRun).get(centDist).get(d), point);
				}

				// Get the SI value for this point.
				siVal = outDist - inDist;

				if (inDist > outDist)
					siVal /= inDist + 0.00000001;
				else
					siVal /= outDist + 0.00000001;

				// Add to the total SI val
				SW += siVal;
			}

			SW /= Points.size() + 0.00000001;
		}

		System.out.println("SW" + "(" + (int) numCen + ") =" + " " + SW);
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

		// Sets the mean of each point
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
		for (int i = 0; i < numCen; i++) {
			RandPart.add(new ArrayList<Double>());
		}
		// Sets the centroid at random points during the first run
		Random randomPoint = new Random();
		// Saves the random points into seperate arraylist
		for (int i = 0; i < numofpoints; i++) {
			randomP = randomPoint.nextInt((int) numCen);
			RandPart.get(randomP).add(Points.get(i));
			// System.out.println((ArrayList<Double>)Points.get(i));
		}
		// Adds points to centroid arraylist
		for (int i = 0; i < RandPart.size(); i++) {
			Cent.add(centroid(RandPart.get(i)));
		}

	}

	public static ArrayList centroid(ArrayList<ArrayList<Double>> associations) {
		ArrayList<Double> temp = new ArrayList<>();
		// Puts 0.0 into empty arrays
		for (int i = 0; i < dim; i++) {
			temp.add(0.0);
		}
		// Sets the points into the index looped into
		for (int i = 0; i < associations.size(); i++) {
			for (int j = 0; j < associations.get(i).size(); j++) {
				// temp.add(new ArrayList<Integer>());
				temp.set(j, associations.get(i).get(j) + temp.get(j));
			}
		}
		// final calculation
		for (int i = 0; i < temp.size(); i++) {
			temp.set(i, temp.get(i) / associations.size());
		}
		return temp;
	}

	public static void DBIndex() {
		ArrayList<Double> sd = new ArrayList<Double>();
		ArrayList<Double> mean = new ArrayList<Double>();
		int temp;
		double sdval = 0.0;

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

		// Sets the mean of each cluster
		for (int i = 0; i < dim; i++) {
			mean.set(i, mean.get(i) / numofpoints);
		}

		// Setting the std for each cluster of the best run. (hehe)
		for (int k = 0; k < numCen; k++) {
			for (int j = 0; j < allClosestPoints.get((int) bestRun).get(k).size(); j++) {
				sdval += Math
						.pow(calculateDistance((ArrayList<Double>) allClosestPoints.get((int) bestRun).get(k).get(j),
								allCents.get((int) bestRun).get(k)) + 0.000001, 2);
			}

			sd.add(sdval / (allClosestPoints.get((int) bestRun).get(k).size() + 0.000000000001));
		}

		double[] finaldbvals = new double[(int) numCen];

		// Find all the DB vals for each cluster.
		for (int l = 0; l < numCen; l++) {
			double dbval = 0.0;
			double dbtemp = Double.MIN_VALUE;

			for (int m = 0; m < numCen; m++) {
				if (m != l) {
					dbval = (sd.get(l) + sd.get(m))
							/ ((calculateDistance((ArrayList<Double>) allCents.get((int) bestRun).get(l),
									allCents.get((int) bestRun).get(m))) + 0.000000000001);
				}

				if (dbval > dbtemp) {
					dbtemp = dbval;
					finaldbvals[l] = dbval;
				}
			}
		}

		DB = 0.0;

		// Calculutes the DBji
		for (int i = 0; i < finaldbvals.length; i++) {
			DB += finaldbvals[i];
		}

		DB /= finaldbvals.length + 0.000000000001;

		System.out.println("DB = " + DB);
	}

}