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
            System.out.println("-----------------------------");
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

            System.out.print("Do you want to add this as a subcategory to an existing category? (yes/no): ");
            String addToExisting = scanner.nextLine();

            if ("yes".equalsIgnoreCase(addToExisting)) {
                System.out.print("Enter the name of the parent category: ");
                String parentCategoryName = scanner.nextLine();
                Element parentCategoryElement = findCategoryElement(root, parentCategoryName);
                if (parentCategoryElement != null) {
                    NodeList subCategoriesList = parentCategoryElement.getElementsByTagName("subCategories");
                    Element subCategoriesElement;
                    if (subCategoriesList.getLength() > 0) {
                        subCategoriesElement = (Element) subCategoriesList.item(0);
                    } else {
                        subCategoriesElement = doc.createElement("subCategories");
                        parentCategoryElement.appendChild(subCategoriesElement);
                    }
                    subCategoriesElement.appendChild(categoryElement);
                } else {
                    System.out.println("Parent category not found.");
                }
            } else {
                root.appendChild(categoryElement);
            }

            XMLManager.saveXML(doc, categoriesFile);
            System.out.println("Category added successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editCategory(Scanner scanner) {
        try {
            Document doc = XMLManager.loadXML(categoriesFile);
            Element root = XMLManager.getElementByTagName(doc, "categories");

            System.out.print("Enter the name of the category to edit: ");
            String nameToEdit = scanner.nextLine();

            // Find the category to edit
            Element categoryElement = findCategoryElement(root, nameToEdit);
            if (categoryElement == null) {
                System.out.println("Category not found.");
                return;
            }

            System.out.print("Enter new category name: ");
            String newName = scanner.nextLine();
            System.out.print("Enter new characteristic field: ");
            String newCharacteristicField = scanner.nextLine();

            // Update the category details
            categoryElement.getElementsByTagName("name").item(0).setTextContent(newName);
            categoryElement.getElementsByTagName("characteristicField").item(0).setTextContent(newCharacteristicField);

            XMLManager.saveXML(doc, categoriesFile);
            System.out.println("Category edited successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteCategory(Scanner scanner) {
        try {
            Document doc = XMLManager.loadXML(categoriesFile);
            Element root = XMLManager.getElementByTagName(doc, "categories");

            System.out.print("Enter the name of the category to delete: ");
            String nameToDelete = scanner.nextLine();

            // Find and remove the category element
            Element categoryElement = findCategoryElement(root, nameToDelete);
            if (categoryElement == null) {
                System.out.println("Category not found.");
                return;
            }

            categoryElement.getParentNode().removeChild(categoryElement);

            XMLManager.saveXML(doc, categoriesFile);
            System.out.println("Category deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element findCategoryElement(Element root, String name) {
        NodeList categories = root.getElementsByTagName("category");
        for (int i = 0; i < categories.getLength(); i++) {
            Element category = (Element) categories.item(i);
            if (category.getElementsByTagName("name").item(0).getTextContent().equals(name)) {
                return category;
            }
        }
        return null;
    }

    private void listCategories() {
        try {
            Document doc = XMLManager.loadXML(categoriesFile);
            Element root = XMLManager.getElementByTagName(doc, "categories");

            NodeList categoryList = root.getChildNodes();
            for (int i = 0; i < categoryList.getLength(); i++) {
                if (categoryList.item(i) instanceof Element) {
                    Element categoryElement = (Element) categoryList.item(i);
                    printCategory(categoryElement, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printCategory(Element categoryElement, int indentLevel) {
        String name = categoryElement.getElementsByTagName("name").item(0).getTextContent();
        String characteristicField = categoryElement.getElementsByTagName("characteristicField").item(0).getTextContent();
        String indent = "    ".repeat(indentLevel);
        System.out.println(indent + "Category Name: " + name + ", Characteristic Field: " + characteristicField);

        NodeList subCategoryNodes = categoryElement.getElementsByTagName("subCategories");
        if (subCategoryNodes.getLength() > 0) {
            NodeList subCategories = subCategoryNodes.item(0).getChildNodes();
            for (int i = 0; i < subCategories.getLength(); i++) {
                if (subCategories.item(i) instanceof Element) {
                    Element subCategoryElement = (Element) subCategories.item(i);
                    printCategory(subCategoryElement, indentLevel + 1);
                }
            }
        }
    }

}
