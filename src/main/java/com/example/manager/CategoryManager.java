package com.example.manager;

import com.example.model.Category;
import com.example.util.XMLManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CategoryManager {
    private String categoriesFile;

    public CategoryManager(String categoriesFile) {
        this.categoriesFile = categoriesFile;
    }

    public void manage(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Category Management:");
            System.out.println("1. Add Category");
            System.out.println("2. Edit Category");
            System.out.println("3. Delete Category");
            System.out.println("4. List Categories");
            System.out.println("5. Back");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addCategory(scanner);
                    break;
                case 2:
                    editCategory(scanner);
                    break;
                case 3:
                    deleteCategory(scanner);
                    break;
                case 4:
                    listCategories();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addCategory(Scanner scanner) {
        try {
            Document doc = XMLManager.loadXML(categoriesFile);
            Element root = XMLManager.getElementByTagName(doc, "categories");

            System.out.print("Enter category name: ");
            String name = scanner.nextLine();

            System.out.print("Enter characteristic field: ");
            String characteristicField = scanner.nextLine();

            Element categoryElement = doc.createElement("category");
            Element nameElement = doc.createElement("name");
            nameElement.setTextContent(name);
            Element characteristicFieldElement = doc.createElement("characteristicField");
            characteristicFieldElement.setTextContent(characteristicField);

            categoryElement.appendChild(nameElement);
            categoryElement.appendChild(characteristicFieldElement);
            root.appendChild(categoryElement);

            XMLManager.saveXML(doc, categoriesFile);
            System.out.println("Category added successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editCategory(Scanner scanner) {
        // Similar to addCategory, but allows editing an existing category
    }

    private void deleteCategory(Scanner scanner) {
        // Similar to addCategory, but allows deleting an existing category
    }

    private void listCategories() {
        try {
            Document doc = XMLManager.loadXML(categoriesFile);
            Element root = XMLManager.getElementByTagName(doc, "categories");

            NodeList categoryList = root.getElementsByTagName("category");
            for (int i = 0; i < categoryList.getLength(); i++) {
                Element categoryElement = (Element) categoryList.item(i);
                String name = categoryElement.getElementsByTagName("name").item(0).getTextContent();
                String characteristicField = categoryElement.getElementsByTagName("characteristicField").item(0).getTextContent();
                System.out.println("Category Name: " + name + ", Characteristic Field: " + characteristicField);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
