package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
    public static double[][][] distanceDurationMatrix = new double[30][30][2];
    public static double[][] ilceCoordinates = new double[30][2];

    public static double[][][] readDistanceDurationCSV() {
        try {
            BufferedReader csvReader;
            try {
                csvReader = new BufferedReader(new FileReader("res/csv/Distance-time.csv"));
            } catch (FileNotFoundException e) {
                csvReader = new BufferedReader(new FileReader("src/res/csv/Distance-time.csv"));
            }
            String row;
            int matrixIndex = 0;
            int withinOriginIndex = 0;

            for (int i = 0; i < 901; i++) {
                row = csvReader.readLine();

                if (i == 0) {
                    continue;
                }

                String[] data = row.split(",");

                distanceDurationMatrix[matrixIndex][withinOriginIndex][0] = Double.parseDouble(data[2].replace("\u0000", ""));
                distanceDurationMatrix[matrixIndex][withinOriginIndex][1] = Integer.parseInt(data[3].replace("\u0000", ""));

                withinOriginIndex++;

                if (i % 30 == 0) {
                    matrixIndex++;
                    withinOriginIndex = 0;
                }
            }

            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return distanceDurationMatrix;
    }

    public static double[][] readDistrictCSV() {
        try {
            BufferedReader csvReader;
            try {
                csvReader = new BufferedReader(new FileReader("res/csv/ilceler.csv"));
            } catch (FileNotFoundException e) {
                csvReader = new BufferedReader(new FileReader("src/res/csv/ilceler.csv"));
            }
            String row;

            for (int i = 0; i < 30; i++) {
                row = csvReader.readLine();
                String[] data = row.split(",");

                ilceCoordinates[i][0] = Double.parseDouble(data[2]);
                ilceCoordinates[i][1] = Double.parseDouble(data[3]);
            }
            csvReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ilceCoordinates;
    }
}
