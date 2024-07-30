package com.example.manager;

import com.example.model.ConversionFactor;
import com.example.util.XMLManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Scanner;

public class ConversionManager {
    private String conversionsFile;

    public ConversionManager(String conversionsFile) {
        this.conversionsFile = conversionsFile;
    }

    public void manage(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Conversion Factor Management:");
            System.out.println("1. Add Conversion Factor");
            System.out.println("2. Edit Conversion Factor");
            System.out.println("3. Delete Conversion Factor");
            System.out.println("4. List Conversion Factors");
            System.out.println("5. Back");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addConversionFactor(scanner);
                    break;
                case 2:
                    editConversionFactor(scanner);
                    break;
                case 3:
                    deleteConversionFactor(scanner);
                    break;
                case 4:
                    listConversionFactors();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addConversionFactor(Scanner scanner) {
        try {
            Document doc = XMLManager.loadXML(conversionsFile);
            Element root = XMLManager.getElementByTagName(doc, "conversionFactors");

            System.out.print("Enter category 1: ");
            String category1 = scanner.nextLine();

            System.out.print("Enter category 2: ");
            String category2 = scanner.nextLine();

            System.out.print("Enter conversion factor: ");
            double factor = scanner.nextDouble();
            scanner.nextLine(); // consume newline

            Element conversionElement = doc.createElement("conversionFactor");
            Element category1Element = doc.createElement("category1");
            category1Element.setTextContent(category1);
            Element category2Element = doc.createElement("category2");
            category2Element.setTextContent(category2);
            Element factorElement = doc.createElement("factor");
            factorElement.setTextContent(String.valueOf(factor));

            conversionElement.appendChild(category1Element);
            conversionElement.appendChild(category2Element);
            conversionElement.appendChild(factorElement);
            root.appendChild(conversionElement);

            XMLManager.saveXML(doc, conversionsFile);
            System.out.println("Conversion factor added successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editConversionFactor(Scanner scanner) {
        // Similar to addConversionFactor, but allows editing an existing conversion factor
    }

    private void deleteConversionFactor(Scanner scanner) {
        // Similar to addConversionFactor, but allows deleting an existing conversion factor
    }

    private void listConversionFactors() {
        try {
            Document doc = XMLManager.loadXML(conversionsFile);
            Element root = XMLManager.getElementByTagName(doc, "conversionFactors");

            NodeList conversionList = root.getElementsByTagName("conversionFactor");
            for (int i = 0; i < conversionList.getLength(); i++) {
                Element conversionElement = (Element) conversionList.item(i);
                String category1 = conversionElement.getElementsByTagName("category1").item(0).getTextContent();
                String category2 = conversionElement.getElementsByTagName("category2").item(0).getTextContent();
                double factor = Double.parseDouble(conversionElement.getElementsByTagName("factor").item(0).getTextContent());
                System.out.println("Category 1: " + category1 + ", Category 2: " + category2 + ", Factor: " + factor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
