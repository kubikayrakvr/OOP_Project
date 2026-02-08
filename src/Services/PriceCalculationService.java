package Services;

import java.io.*;
import Managers.CalculatePrice;

public class PriceCalculationService {

    private static final String FILE_PATH = "price_parameters.txt";

    static {
        FileHelper.ensureFileExists(FILE_PATH);
    }
    
    public static CalculatePrice loadCalculator() {
        // Default values
        double base = 500.0;
        double tax = 50.0;
        double bagFee = 15.0;
        double bussMulti = 2.0;
        double durCost = 1.5;

        File file = new File(FILE_PATH);
        
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        double value = Double.parseDouble(parts[1].trim());

                        switch (key) {
                            case "basePrice": base = value; break;
                            case "tax": tax = value; break;
                            case "baggageFeePerKg": bagFee = value; break;
                            case "businessMultiplier": bussMulti = value; break;
                            case "durationCostPerMinute": durCost = value; break; 
                        }
                    }
                }
            } 
            catch (IOException | NumberFormatException e) {
                System.err.println("Using default values due to read error.");
            }
        } 
        else {
            createDefaultFile(); 
        }

        return new CalculatePrice(base, tax, bagFee, bussMulti, durCost);
    }
    
    public static boolean saveParameters(double base, double tax, double bagFee, double bussMult, double durCost) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            bw.write("basePrice=" + base + "\n");
            bw.write("tax=" + tax + "\n");
            bw.write("baggageFeePerKg=" + bagFee + "\n");
            bw.write("businessMultiplier=" + bussMult + "\n");
            bw.write("durationCostPerMinute=" + durCost + "\n");
            return true;
        } 
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void createDefaultFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            bw.write("basePrice=500.0\n");
            bw.write("tax=50.0\n");
            bw.write("baggageFeePerKg=15.0\n");
            bw.write("businessMultiplier=2.5\n");
            bw.write("durationCostPerMinute=1.5\n"); 
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}