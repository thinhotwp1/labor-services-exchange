package com.example.manager;

import com.example.model.GeographicArea;
import com.example.util.XMLManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GeographicManager {
    private String geographicFile;

    public GeographicManager(String geographicFile) {
        this.geographicFile = geographicFile;
    }

    public void manage(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Geographic Area Management:");
            System.out.println("1. Add Geographic Area");
            System.out.println("2. Edit Geographic Area");
            System.out.println("3. Delete Geographic Area");
            System.out.println("4. List Geographic Areas");
            System.out.println("5. Back");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addGeographicArea(scanner);
                    break;
                case 2:
                    editGeographicArea(scanner);
                    break;
                case 3:
                    deleteGeographicArea(scanner);
                    break;
                case 4:
                    listGeographicAreas();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addGeographicArea(Scanner scanner) {
        try {
            Document doc = XMLManager.loadXML(geographicFile);
            Element root = XMLManager.getElementByTagName(doc, "geographicAreas");

            System.out.print("Enter district name: ");
            String district = scanner.nextLine();

            System.out.print("Enter neighborhoods (comma separated): ");
            String[] neighborhoodsArray = scanner.nextLine().split(",");
            List<String> neighborhoods = new ArrayList<>();
            for (String neighborhood : neighborhoodsArray) {
                neighborhoods.add(neighborhood.trim());
            }

            Element geographicElement = doc.createElement("geographicArea");
            Element districtElement = doc.createElement("district");
            districtElement.setTextContent(district);
            Element neighborhoodsElement = doc.createElement("neighborhoods");

            for (String neighborhood : neighborhoods) {
                Element neighborhoodElement = doc.createElement("neighborhood");
                neighborhoodElement.setTextContent(neighborhood);
                neighborhoodsElement.appendChild(neighborhoodElement);
            }

            geographicElement.appendChild(districtElement);
            geographicElement.appendChild(neighborhoodsElement);
            root.appendChild(geographicElement);

            XMLManager.saveXML(doc, geographicFile);
            System.out.println("Geographic area added successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editGeographicArea(Scanner scanner) {
        // Similar to addGeographicArea, but allows editing an existing geographic area
    }

    private void deleteGeographicArea(Scanner scanner) {
        // Similar to addGeographicArea, but allows deleting an existing geographic area
    }

    private void listGeographicAreas() {
        try {
            Document doc = XMLManager.loadXML(geographicFile);
            Element root = XMLManager.getElementByTagName(doc, "geographicAreas");

            NodeList geographicList = root.getElementsByTagName("geographicArea");
            for (int i = 0; i < geographicList.getLength(); i++) {
                Element geographicElement = (Element) geographicList.item(i);
                String district = geographicElement.getElementsByTagName("district").item(0).getTextContent();
                NodeList neighborhoodList = geographicElement.getElementsByTagName("neighborhood");
                List<String> neighborhoods = new ArrayList<>();
                for (int j = 0; j < neighborhoodList.getLength(); j++) {
                    neighborhoods.add(neighborhoodList.item(j).getTextContent());
                }
                System.out.println("District: " + district + ", Neighborhoods: " + String.join(", ", neighborhoods));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
