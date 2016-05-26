package com.thalmic.myo.example;

import java.io.File;
import java.util.Scanner;

import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;

// Recording time-series data

public class EmgDataSampleMotion {

	private static final int READING_FREQUENCY = 50;
	private static final int HOW_MANY_WORDS = 2;
	private static final int HOW_MANY_SETS = 2;
	public static final int MAX_RECORDS = 100;

	public static void main(String[] args) {
		int lines = 0;
		File file = null;
		Scanner inp = new Scanner(System.in);
		
		try {
			// Create a new "Hub" which is used by the Myo API to keep track of the Myo devices
			Hub hub = new Hub("com.example.emg-data-sample");

			// hub.waitForMyo(ms) returns a Myo object if found within ms milliseconds, or null otherwise
			System.out.println("Attempting to find a Myo...");
			Myo myo = hub.waitForMyo(10000);

			if (myo == null) {
				throw new RuntimeException("Unable to find a Myo!");
			}

			// setStreamEmg tells the Myo to send EMG data
			System.out.println("Connected to a Myo armband!");
			myo.setStreamEmg(StreamEmgType.STREAM_EMG_ENABLED);
			
			// crete an instance of a DeviceListener to record the data, and tell the hub to use it
			DeviceListener dataCollector = new EmgDataCollector();
			hub.addListener(dataCollector);

			System.out.println(
					"Will write " + MAX_RECORDS + " lines of data for each letter in " + HOW_MANY_SETS + " sets. Reading once every " + READING_FREQUENCY + " ms.");
			System.out.println();
			

			for (int i = 0; i < HOW_MANY_SETS; i++) {
				System.out.println("==============================");
				System.out.println("                       SET "+(i+1));
				System.out.println("==============================");
				file = new File(checkFileName());
				file.createNewFile();
				dataCollector.prepareFile(file, MAX_RECORDS);
				new Scanner(System.in).nextLine();
				
				for (int j = 0; j < HOW_MANY_WORDS; j++) {
					
					System.out.println("Please make hand gesture for word " + (j+1) + " now.");
					System.out.println("Press enter to start recording...");
					inp.nextLine();
					
					lines = 0;
					while (!maxColumnsWritten(lines)) {
						// run the event loop for READING_FREQUENCY milliseconds and record all values within this time
						hub.run(READING_FREQUENCY);
						lines = dataCollector.writeToFileMotion(file);
					}
					dataCollector.carriageReturn(file);
				}
			}
		} catch (Exception e) {
			System.err.println("Error: ");
			e.printStackTrace();
			System.exit(1);
		}

		finally {
			System.out.println("bye!");
		}
	}
	
	// returns true if and only if the maximum number of lines has been written to the file
	public static boolean maxLinesWritten(int lines) {
		if (lines == 0)
			return false;
		return lines >= MAX_RECORDS;
	}
	
	// returns true if and only if the maximum number of columns has been written to the file
	public static boolean maxColumnsWritten(int columns) {
		if (columns == 0) return false;
		return columns == 8 * MAX_RECORDS;
	}

	// Ensure that the file name doesn't exist. If it does, generate a unique one.
	public static String checkFileName() {
		int count = 1;
		String name = "data" + count + "-" + "_MOTION_" + READING_FREQUENCY + "ms_" + MAX_RECORDS +"_RECORDS" + ".csv";
		File data = new File(name);
		while (data.exists()) {
			count++;
			name = "data" + count + "-" + HOW_MANY_WORDS + "_MOTION_" + READING_FREQUENCY + "ms_" + MAX_RECORDS +"_LINES" + ".csv";
			data = new File(name);
		}
		System.out.println("Creating " + data.getAbsolutePath());
		return name;
	}
}