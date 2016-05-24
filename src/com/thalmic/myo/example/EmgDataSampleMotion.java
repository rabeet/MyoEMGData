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
		int currentLetter = 65;
		File file = null;
		Scanner inp = new Scanner(System.in);
		
		try {
			Hub hub = new Hub("com.example.emg-data-sample");

			System.out.println("Attempting to find a Myo...");
			Myo myo = hub.waitForMyo(10000);

			if (myo == null) {
				throw new RuntimeException("Unable to find a Myo!");
			}

			System.out.println("Connected to a Myo armband!");
			myo.setStreamEmg(StreamEmgType.STREAM_EMG_ENABLED);
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
//				hub.run(READING_FREQUENCY);
				new Scanner(System.in).nextLine();
				
				for (int j = 0; j < HOW_MANY_WORDS; j++) {
					
					// Skip J and Z -- not needed in motion
//					if ((char)currentLetter == 'J' || (char)currentLetter == 'Z') {
//						currentLetter++;
//						continue;
//					}
					System.out.println("Please make hand gesture for word " + (j+1) + " now.");
					System.out.println("Press enter to start recording...");
					inp.nextLine();
					while (maxColumnsWritten(lines)) {
						hub.run(READING_FREQUENCY);
						lines = dataCollector.writeToFileMotion(file);
					}
					dataCollector.carriageReturn(file);
					lines = 0;
				}
				currentLetter = 65;
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
	
	public static boolean maxLinesWritten(int lines) {
		if (lines == 0)
			return true;
		return (lines % MAX_RECORDS) != 0;
	}
	
	public static boolean maxColumnsWritten(int columns) {
		if (columns == 0) return true;
		return columns != 8*MAX_RECORDS;
	}

	public static String checkFileName() {
		int count = 1;
		String name = "data" + count + "-" + "_MOTION_" + READING_FREQUENCY + "ms_" + MAX_RECORDS +"_RECORDS" + ".csv";
		File data = new File(name);
		while (data.exists()) {
			// System.out.println("File " + data.getPath() + " exists.. Creating
			// new file name 'data"+(count+1)+".csv'");
			count++;
			name = "data" + count + "-" + HOW_MANY_WORDS + "_MOTION_" + READING_FREQUENCY + "ms_" + MAX_RECORDS +"_LINES" + ".csv";
			data = new File(name);
		}
		System.out.println("Creating " + data.getAbsolutePath());
		return name;
	}
}