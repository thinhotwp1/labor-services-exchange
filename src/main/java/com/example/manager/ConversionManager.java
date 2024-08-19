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
            System.out.println("-----------------------------");
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

            // Kiểm tra giá trị yếu tố chuyển đổi
            if (factor < 0.5 || factor > 2.0) {
                System.out.println("Conversion factor must be between 0.5 and 2.0.");
                return;
            }

            addOrUpdateConversionFactor(root, category1, category2, factor);
            calculateMissingConversionFactors(doc, root);

            XMLManager.saveXML(doc, conversionsFile);
            System.out.println("Conversion factor added successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void editConversionFactor(Scanner scanner) {
        try {
            Document doc = XMLManager.loadXML(conversionsFile);
            Element root = XMLManager.getElementByTagName(doc, "conversionFactors");

            System.out.print("Enter the category1 of the conversion factor to edit: ");
            String category1ToEdit = scanner.nextLine();

            System.out.print("Enter the category2 of the conversion factor to edit: ");
            String category2ToEdit = scanner.nextLine();

            // Find the conversion factor to edit
            Element conversionElement = findConversionElement(root, category1ToEdit, category2ToEdit);
            if (conversionElement == null) {
                System.out.println("Conversion factor not found.");
                return;
            }

            System.out.print("Enter new category1: ");
            String newCategory1 = scanner.nextLine();
            System.out.print("Enter new category2: ");
            String newCategory2 = scanner.nextLine();
            System.out.print("Enter new conversion factor: ");
            double newFactor = scanner.nextDouble();
            scanner.nextLine(); // consume newline

            // Kiểm tra giá trị yếu tố chuyển đổi
            if (newFactor < 0.5 || newFactor > 2.0) {
                System.out.println("Conversion factor must be between 0.5 and 2.0.");
                return;
            }

            // Update the conversion factor details
            conversionElement.getElementsByTagName("category1").item(0).setTextContent(newCategory1);
            conversionElement.getElementsByTagName("category2").item(0).setTextContent(newCategory2);
            conversionElement.getElementsByTagName("factor").item(0).setTextContent(String.valueOf(newFactor));

            // Cập nhật yếu tố chuyển đổi nghịch đảo
            Element inverseConversionElement = findConversionElement(root, category2ToEdit, category1ToEdit);
            if (inverseConversionElement != null) {
                inverseConversionElement.getElementsByTagName("category1").item(0).setTextContent(newCategory2);
                inverseConversionElement.getElementsByTagName("category2").item(0).setTextContent(newCategory1);
                inverseConversionElement.getElementsByTagName("factor").item(0).setTextContent(String.valueOf(1.0 / newFactor));
            } else {
                Element newInverseConversionElement = doc.createElement("conversionFactor");
                Element inverseCategory1Element = doc.createElement("category1");
                inverseCategory1Element.setTextContent(newCategory2);
                Element inverseCategory2Element = doc.createElement("category2");
                inverseCategory2Element.setTextContent(newCategory1);
                Element inverseFactorElement = doc.createElement("factor");
                inverseFactorElement.setTextContent(String.valueOf(1.0 / newFactor));

                newInverseConversionElement.appendChild(inverseCategory1Element);
                newInverseConversionElement.appendChild(inverseCategory2Element);
                newInverseConversionElement.appendChild(inverseFactorElement);
                root.appendChild(newInverseConversionElement);
            }

            XMLManager.saveXML(doc, conversionsFile);
            System.out.println("Conversion factor edited successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void calculateMissingConversionFactors(Document doc, Element root) {
        NodeList conversionList = root.getElementsByTagName("conversionFactor");
        int n = conversionList.getLength();

        for (int i = 0; i < n; i++) {
            Element c1Element = (Element) conversionList.item(i);
            String c1Category1 = c1Element.getElementsByTagName("category1").item(0).getTextContent();
            String c1Category2 = c1Element.getElementsByTagName("category2").item(0).getTextContent();
            double f12 = Double.parseDouble(c1Element.getElementsByTagName("factor").item(0).getTextContent());

            for (int j = 0; j < n; j++) { // Start from 0 to cover all pairs
                if (i == j) continue; // Skip the same element

                Element c2Element = (Element) conversionList.item(j);
                String c2Category1 = c2Element.getElementsByTagName("category1").item(0).getTextContent();
                String c2Category2 = c2Element.getElementsByTagName("category2").item(0).getTextContent();
                double f23 = Double.parseDouble(c2Element.getElementsByTagName("factor").item(0).getTextContent());

                if (c1Category2.equals(c2Category1)) {
                    double f13 = f12 * f23;
                    if (!c1Category1.equals(c2Category2)) { // Avoid (c1, c1) pairs
                        addOrUpdateConversionFactor(root, c1Category1, c2Category2, f13);
                    }
                }
            }
        }
    }

    private void addOrUpdateConversionFactor(Element root, String category1, String category2, double factor) {
        Element existingElement = findConversionElement(root, category1, category2);
        if (existingElement == null) {
            Element conversionElement = root.getOwnerDocument().createElement("conversionFactor");
            Element category1Element = root.getOwnerDocument().createElement("category1");
            category1Element.setTextContent(category1);
            Element category2Element = root.getOwnerDocument().createElement("category2");
            category2Element.setTextContent(category2);
            Element factorElement = root.getOwnerDocument().createElement("factor");
            factorElement.setTextContent(String.valueOf(factor));

            conversionElement.appendChild(category1Element);
            conversionElement.appendChild(category2Element);
            conversionElement.appendChild(factorElement);
            root.appendChild(conversionElement);

            // Thêm yếu tố chuyển đổi nghịch đảo
            if (!category1.equals(category2)) { // Avoid (c1, c1) pairs
                Element inverseConversionElement = root.getOwnerDocument().createElement("conversionFactor");
                Element inverseCategory1Element = root.getOwnerDocument().createElement("category1");
                inverseCategory1Element.setTextContent(category2);
                Element inverseCategory2Element = root.getOwnerDocument().createElement("category2");
                inverseCategory2Element.setTextContent(category1);
                Element inverseFactorElement = root.getOwnerDocument().createElement("factor");
                inverseFactorElement.setTextContent(String.valueOf(1.0 / factor));

                inverseConversionElement.appendChild(inverseCategory1Element);
                inverseConversionElement.appendChild(inverseCategory2Element);
                inverseConversionElement.appendChild(inverseFactorElement);
                root.appendChild(inverseConversionElement);
            }
        } else {
            existingElement.getElementsByTagName("factor").item(0).setTextContent(String.valueOf(factor));

            // Cập nhật yếu tố chuyển đổi nghịch đảo
            Element inverseElement = findConversionElement(root, category2, category1);
            if (inverseElement != null) {
                inverseElement.getElementsByTagName("factor").item(0).setTextContent(String.valueOf(1.0 / factor));
            }
        }
    }


    private void deleteConversionFactor(Scanner scanner) {
        try {
            Document doc = XMLManager.loadXML(conversionsFile);
            Element root = XMLManager.getElementByTagName(doc, "conversionFactors");

            System.out.print("Enter the category1 of the conversion factor to delete: ");
            String category1ToDelete = scanner.nextLine();

            System.out.print("Enter the category2 of the conversion factor to delete: ");
            String category2ToDelete = scanner.nextLine();

            // Find and remove the conversion factor element
            Element conversionElement = findConversionElement(root, category1ToDelete, category2ToDelete);
            if (conversionElement == null) {
                System.out.println("Conversion factor not found.");
                return;
            }

            conversionElement.getParentNode().removeChild(conversionElement);

            XMLManager.saveXML(doc, conversionsFile);
            System.out.println("Conversion factor deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element findConversionElement(Element root, String category1, String category2) {
        NodeList conversionList = root.getElementsByTagName("conversionFactor");
        for (int i = 0; i < conversionList.getLength(); i++) {
            Element conversion = (Element) conversionList.item(i);
            String c1 = conversion.getElementsByTagName("category1").item(0).getTextContent();
            String c2 = conversion.getElementsByTagName("category2").item(0).getTextContent();
            if (c1.equals(category1) && c2.equals(category2)) {
                return conversion;
            }
        }
        return null;
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
