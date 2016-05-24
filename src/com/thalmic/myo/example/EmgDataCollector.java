package com.thalmic.myo.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.FirmwareVersion;
import com.thalmic.myo.Myo;

public class EmgDataCollector extends AbstractDeviceListener {
	private byte[] emgSamples;

	public EmgDataCollector() {
	}

	@Override
	public void onPair(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
		if (emgSamples != null) {
			for (int i = 0; i < emgSamples.length; i++) {
				emgSamples[i] = 0;
			}
		}
	}

	@Override
	public void onEmgData(Myo myo, long timestamp, byte[] emg) {
		this.emgSamples = emg;
	}

	@Override
	public String toString() {
		return Arrays.toString(emgSamples);
	}
	
	@Override
	// Prepares a file for writing the data by writing the first line of headers which consist of sensor number followed by the time
	public void prepareFile(File file, int time) {
		FileWriter writer = null;
		
		try {
			writer = new FileWriter(file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String text = "";
		for (int i = 1; i<=time; i++) {
			if (i == 1)
				text = text+"S1_"+i+",S2_"+i+",S3_"+i+",S4_"+i+",S5_"+i+",S6_"+i+",S7_"+i+",S8_"+i;
			else
				text = text+",S1_"+i+",S2_"+i+",S3_"+i+",S4_"+i+",S5_"+i+",S6_"+i+",S7_"+i+",S8_"+i;
		}
		
		try {
			writer.write(text+"\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	// Carriage returns a file...
	public void carriageReturn(File file) {
		try {
			FileWriter writer = new FileWriter(file, true);
			writer.write("\n");
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Write each line of EMG data (motion) to a csv and return number of lines written
	@Override
	public int writeToFileMotion(File file) {
		if (emgSamples == null) {
			return -1;
		}
//		int lines = numberOfLines(file);
//		if (lines >= EmgDataSample.MAX_LINES) {
//			System.out.println(EmgDataSample.MAX_LINES+" lines written..");
//			System.exit(1);
//		}
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String text = "";
		for (int i = 0; i<emgSamples.length; i++) {
			if (i != 0)
				text = text+","+emgSamples[i];
			else
				text = ""+emgSamples[0];
		}
		try {
			writer.write(text+",");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return numberOfColumns(file);
	}
	
	// Write each line of EMG data (letter) to a csv and return number of lines written
	@Override
	public int writeToFileLetter(File file, int letter) {
		if (emgSamples == null) {
			return -1;
		}
//		int lines = numberOfLines(file);
//		if (lines >= EmgDataSample.MAX_LINES) {
//			System.out.println(EmgDataSample.MAX_LINES+" lines written..");
//			System.exit(1);
//		}
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String text = "";
		for (int i = 0; i<emgSamples.length; i++) {
			if (i != 0)
				text = text+","+emgSamples[i];
			else
				text = ""+emgSamples[0];
		}
		text = text + "," + (char)letter;
		try {
			writer.write(text+"\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		};
		
		return numberOfLines(file);
	}
	
	// Returns number of lines in a file
	private int numberOfLines(File file) {
		BufferedReader reader = null;
		int lines = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			while (reader.readLine() != null) lines++;
			reader.close();
		} catch (Exception e) {}
		return lines;
	}
	
	// Returns number of columns in a file
	private int numberOfColumns(File file) {
		BufferedReader reader = null;
		int cols = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null) {
//				System.out.println(line);
				cols = line.length() - line.replace(",", "").length();
//				System.out.println("Length of cols = " + cols);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cols;
	}
	
}
