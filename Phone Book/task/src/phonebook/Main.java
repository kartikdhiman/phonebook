package phonebook;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
	private static final String DIRECTORY_PATH = "C:\\Users\\batman\\Downloads\\directory.txt";
	private static final String NAMES_LIST_PATH = "C:\\Users\\batman\\Downloads\\find.txt";

	public static void main(String[] args) {
		var namesToFind = new ArrayList<String>();
		var namesInDirectory = new ArrayList<String>();
		var namesForQuickSort = new ArrayList<String>();
		var namesForInstantSearch = new ArrayList<String>();
		try (var scanner = new Scanner(new File(DIRECTORY_PATH));
				 var scannerFind = new Scanner(new File(NAMES_LIST_PATH))) {
			while (scanner.hasNextLine()) {
				String[] name = scanner.nextLine().split(" ", 2);
				namesInDirectory.add(name[1]);
				namesForQuickSort.add(name[1]);
				namesForInstantSearch.add(name[1]);
			}
			while (scannerFind.hasNextLine()) {
				namesToFind.add(scannerFind.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("Start searching (linear search)...");
		linearSearch(namesToFind, namesInDirectory);

		System.out.println("Start searching (bubble sort + jump search)...");
		jumpSearch(namesToFind, namesInDirectory);

		System.out.println("Start searching (quick sort + binary search)...");
		binarySearch(namesToFind, namesForQuickSort);

		System.out.println("Start searching (hash table)...");
		instantSearch(namesToFind, namesForInstantSearch);
	}

	private static void linearSearch(List<String> namesToFind, List<String> namesInDirectory) {
		var begin = System.currentTimeMillis();
		var found = 0;
		for (String name : namesToFind) {
			if (namesInDirectory.contains(name)) found++;
		}
		var end = System.currentTimeMillis();
		System.out.printf("Found %d / %d entries. Time taken: %s", found, namesToFind.size(), timeDiff(end-begin));
		System.out.println("\n");
	}

	private static void jumpSearch(List<String> namesToFind, List<String> namesInDirectory) {
		int found = 0;
		var beginSort = System.currentTimeMillis();
		// ArrayOutOfBoundException using BubbleSort on large size list
		Collections.sort(namesInDirectory);
//		bubbleSort(namesInDirectory);
		var endSort = System.currentTimeMillis();

		var beginSearch = System.currentTimeMillis();
		for (String value: namesToFind) {
			double step = Math.floor(Math.sqrt(namesInDirectory.size()));
			int curr = 0;
			int ind;
			while (curr <= namesInDirectory.size()) {
				if (namesInDirectory.get(curr).equals(value)) found++;
				else if (namesInDirectory.get(curr).compareTo(value) > 0) {
					ind = curr - 1;

					while (ind > (curr - step) && ind >= 1) {
						if (namesInDirectory.get(ind).equals(value)) found++;
						ind -= 1;
					}
				}
				curr += step;
			}
			ind = namesInDirectory.size() - 1;
			while (ind > curr - step) {
				if (namesInDirectory.get(ind).equals(value)) found++;
				ind -= 1;
			}
		}
		var endSearch = System.currentTimeMillis();

		System.out.printf("Found %d / %d entries. Time taken: %s\n", found, namesToFind.size(), timeDiff(endSearch-beginSort));
		System.out.printf("Sorting time: %s\n", timeDiff(endSort - beginSort));
		System.out.printf("Searching time: %s", timeDiff(endSearch - beginSearch));
		System.out.println("\n");
	}

	static void bubbleSort(List<String> list) {
		int n = list.size();
		String temp;
		for (int i = 0; i < n; i++) {
			for (int j = 1; j < (n - i); j++) {
				if (list.get(j - 1).compareTo(list.get(j)) > 0) {
					//swap elements
					temp = list.get(j - 1);
					list.set(j - 1, list.get(j));
					list.set(j, temp);
				}
			}
		}
	}

	private static void binarySearch(List<String> namesToFind, List<String> namesInDirectory) {
		int found = 0;
		var beginSort = System.currentTimeMillis();
		String[] names = namesInDirectory.toArray(String[]::new);
		quickSort(names);
		var endSort = System.currentTimeMillis();

		var beginSearch = System.currentTimeMillis();
		for (String value : namesToFind) {
			int left = 0;
			int right = names.length - 1;
			while(left <= right) {
				int middle = (left + right) / 2;
				if (names[middle].compareTo(value) == 0) {
					found++;
					break;
				} else if (names[middle].compareTo(value) > 0) {
					right = middle - 1;
				} else {
					left = middle + 1;
				}
			}
		}
		var endSearch = System.currentTimeMillis();

		System.out.printf("Found %d / %d entries. Time taken: %s\n", found, namesToFind.size(), timeDiff(endSearch-beginSort));
		System.out.printf("Sorting time: %s\n", timeDiff(endSort - beginSort));
		System.out.printf("Searching time: %s", timeDiff(endSearch - beginSearch));
		System.out.println("\n");
	}

	static void quickSort(String[] names) {
		quickSort(names, 0, names.length - 1);
	}

	static void quickSort(String[] names, int lowIndex, int highIndex) {
		if (lowIndex >= highIndex)
			return;

		String pivot = names[highIndex];

		int leftPointer = partition(names, lowIndex, highIndex, pivot);

		quickSort(names, lowIndex, leftPointer - 1);
		quickSort(names, leftPointer + 1, highIndex);
	}

	private static int partition(String[] names, int lowIndex, int highIndex, String pivot) {
		int leftPointer = lowIndex;
		int rightPointer = highIndex;

		while (leftPointer < rightPointer) {
			while (names[leftPointer].compareTo(pivot) <= 0 && leftPointer < rightPointer) {
				leftPointer++;
			}
			while (names[rightPointer].compareTo(pivot) >= 0 && leftPointer < rightPointer) {
				rightPointer--;
			}
			swap(names, leftPointer, rightPointer);
		}
		swap(names, leftPointer, highIndex);
		return leftPointer;
	}

	private static void instantSearch(List<String> namesToFind, List<String> namesForInstantSearch) {
		var beginSet = System.currentTimeMillis();
		HashSet<String> set = new HashSet<>(namesForInstantSearch);
		var endSet = System.currentTimeMillis();

		var beginSearch = System.currentTimeMillis();
		var found = 0;
		for (var name: namesToFind) {
			if (set.contains(name)) found++;
		}
		var endSearch = System.currentTimeMillis();

		System.out.printf("Found %d / %d entries. Time taken: %s\n", found, namesToFind.size(), timeDiff(endSearch-beginSet));
		System.out.printf("Creating time: %s\n", timeDiff(endSet - beginSet));
		System.out.printf("Searching time: %s", timeDiff(endSearch - beginSearch));
	}

	static void swap(String[] names, int index1, int index2) {
		String temp = names[index2];
		names[index2] = names[index1];
		names[index1] = temp;
	}

	static String timeDiff(long millis) {
		var r_ms = millis % 1000;
		var sec = millis / 1000;
		var r_sec = sec % 60;
		var min = sec / 60;
		return String.format("%d min. %d sec. %d ms.", min, r_sec, r_ms);
	}
}
