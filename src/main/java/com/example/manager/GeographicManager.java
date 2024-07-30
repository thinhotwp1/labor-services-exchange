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
        try {
            Document doc = XMLManager.loadXML(geographicFile);
            Element root = XMLManager.getElementByTagName(doc, "geographicAreas");

            System.out.print("Enter the name of the district to edit: ");
            String districtToEdit = scanner.nextLine();

            // Find the geographic area to edit
            Element geographicElement = findGeographicElement(root, districtToEdit);
            if (geographicElement == null) {
                System.out.println("Geographic area not found.");
                return;
            }

            System.out.print("Enter new district name: ");
            String newDistrict = scanner.nextLine();

            System.out.print("Enter new neighborhoods (comma separated): ");
            String[] neighborhoodsArray = scanner.nextLine().split(",");
            List<String> neighborhoods = new ArrayList<>();
            for (String neighborhood : neighborhoodsArray) {
                neighborhoods.add(neighborhood.trim());
            }

            // Update the geographic area details
            Element districtElement = (Element) geographicElement.getElementsByTagName("district").item(0);
            districtElement.getElementsByTagName("name").item(0).setTextContent(newDistrict);

            Element neighborhoodsElement = (Element) geographicElement.getElementsByTagName("neighborhoods").item(0);
            neighborhoodsElement.setTextContent(""); // Clear existing neighborhoods
            for (String neighborhood : neighborhoods) {
                Element neighborhoodElement = doc.createElement("neighborhood");
                neighborhoodElement.setTextContent(neighborhood);
                neighborhoodsElement.appendChild(neighborhoodElement);
            }

            XMLManager.saveXML(doc, geographicFile);
            System.out.println("Geographic area edited successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteGeographicArea(Scanner scanner) {
        try {
            Document doc = XMLManager.loadXML(geographicFile);
            Element root = XMLManager.getElementByTagName(doc, "geographicAreas");

            System.out.print("Enter the name of the district to delete: ");
            String districtToDelete = scanner.nextLine();

            // Find and remove the geographic area element
            Element geographicElement = findGeographicElement(root, districtToDelete);
            if (geographicElement == null) {
                System.out.println("Geographic area not found.");
                return;
            }

            root.removeChild(geographicElement);

            XMLManager.saveXML(doc, geographicFile);
            System.out.println("Geographic area deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element findGeographicElement(Element root, String districtName) {
        NodeList districtList = root.getElementsByTagName("district");
        for (int i = 0; i < districtList.getLength(); i++) {
            Element districtElement = (Element) districtList.item(i);
            String name = districtElement.getElementsByTagName("name").item(0).getTextContent();
            if (name.equals(districtName)) {
                return (Element) districtElement.getParentNode(); // Return parent node which is <geographicArea>
            }
        }
        return null;
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
