package com.example.manager;

import com.example.config.UserCurrent;
import com.example.model.TypeUser;
import com.example.util.XMLManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Scanner;

public class ConversionManager {
    private String conversionsFile;
    private String categoriesFile;
    private String proposalsFile;

    public ConversionManager() {
        this.conversionsFile = "conversions.xml";
        this.categoriesFile = "categories.xml";  // File chứa thông tin danh mục
        this.proposalsFile = "proposals.xml";
    }


    public void manage(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("-----------------------------");
            System.out.println("Conversion Factor and Proposal Management:");
            System.out.println("1. Add Conversion Factor");
            System.out.println("2. Edit Conversion Factor");
            System.out.println("3. Delete Conversion Factor");
            System.out.println("4. List Conversion Factors");
            System.out.println("==========Proposal==========");
            System.out.println("5. Create Exchange Proposal");
            if (UserCurrent.getCurrentUser().equals(TypeUser.ADMIN)) {
                System.out.println("6. List Exchange Proposals");
                System.out.println("7. Back");
            } else {
                System.out.println("6. Back");
            }

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
                    createExchangeProposal(scanner);
                    break;
                case 6:
                    if (UserCurrent.getCurrentUser().equals(TypeUser.USER)) {
                        running = false;
                    } else {
                        listExchangeProposals();
                    }
                    break;
                case 7:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }


    private void createExchangeProposal(Scanner scanner) {
        try {
            System.out.print("Enter the service you need: ");
            String requestCategory = scanner.nextLine();

            System.out.print("Enter the duration of the service (hours): ");
            int requestDuration = scanner.nextInt();
            scanner.nextLine(); // consume newline

            System.out.print("Enter the service you offer: ");
            String offerCategory = scanner.nextLine();

            double conversionFactor = findConversionFactor(requestCategory, offerCategory);
            if (conversionFactor == -1) {
                System.out.println("No conversion factor found between these categories.");
                return;
            }

            int offerDuration = (int) Math.round(requestDuration * conversionFactor);

            System.out.println("Your offer: " + offerDuration + " hours of " + offerCategory + " in exchange for " + requestDuration + " hours of " + requestCategory);
            System.out.print("Do you confirm this proposal? (yes/no): ");
            String confirmation = scanner.nextLine();

            if (confirmation.equalsIgnoreCase("yes")) {
                saveProposal(UserCurrent.getCurrentUser().name(), requestCategory, requestDuration, offerCategory, offerDuration);
                System.out.println("Proposal confirmed and saved.");
            } else {
                System.out.println("Proposal rejected.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveProposal(String username, String requestCategory, int requestDuration, String offerCategory, int offerDuration) {
        try {
            Document doc = XMLManager.loadXML(proposalsFile);
            Element root = XMLManager.getElementByTagName(doc, "exchangeProposals");

            Element proposalElement = doc.createElement("proposal");

            Element userElement = doc.createElement("user");
            userElement.setTextContent(username);

            Element requestElement = doc.createElement("request");
            Element requestCategoryElement = doc.createElement("category");
            requestCategoryElement.setTextContent(requestCategory);
            Element requestDurationElement = doc.createElement("duration");
            requestDurationElement.setTextContent(String.valueOf(requestDuration));
            requestElement.appendChild(requestCategoryElement);
            requestElement.appendChild(requestDurationElement);

            Element offerElement = doc.createElement("offer");
            Element offerCategoryElement = doc.createElement("category");
            offerCategoryElement.setTextContent(offerCategory);
            Element offerDurationElement = doc.createElement("duration");
            offerDurationElement.setTextContent(String.valueOf(offerDuration));
            offerElement.appendChild(offerCategoryElement);
            offerElement.appendChild(offerDurationElement);

            proposalElement.appendChild(userElement);
            proposalElement.appendChild(requestElement);
            proposalElement.appendChild(offerElement);

            root.appendChild(proposalElement);

            XMLManager.saveXML(doc, proposalsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listExchangeProposals() {
        try {
            Document doc = XMLManager.loadXML(proposalsFile);
            Element root = XMLManager.getElementByTagName(doc, "exchangeProposals");

            NodeList proposalList = root.getElementsByTagName("proposal");
            System.out.println("List Exchange Proposals:");
            for (int i = 0; i < proposalList.getLength(); i++) {
                Element proposalElement = (Element) proposalList.item(i);

                String user = proposalElement.getElementsByTagName("user").item(0).getTextContent();
                String requestCategory = proposalElement.getElementsByTagName("category").item(0).getTextContent();
                String requestDuration = proposalElement.getElementsByTagName("duration").item(0).getTextContent();
                String offerCategory = proposalElement.getElementsByTagName("category").item(1).getTextContent();
                String offerDuration = proposalElement.getElementsByTagName("duration").item(1).getTextContent();

                System.out.println("-----------------------------");
                System.out.print(i + 1); // request: [Piano lessons for beginners, 10 hours] offer: [High school math tutoring 10 hours]".
                System.out.print(". Request: [" + requestCategory + ", " + requestDuration + " hours] ");
                System.out.println("offer: [" + offerCategory + ", " + offerDuration + " hours]");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double findConversionFactor(String category1, String category2) {
        try {
            Document doc = XMLManager.loadXML(conversionsFile);
            Element root = XMLManager.getElementByTagName(doc, "conversionFactors");

            Element conversionElement = findConversionElement(root, category1, category2);
            if (conversionElement != null) {
                return Double.parseDouble(conversionElement.getElementsByTagName("factor").item(0).getTextContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void addConversionFactor(Scanner scanner) {
        try {
            CategoryManager categoryManager = new CategoryManager();
            System.out.println("-----------------------------");
            System.out.println("List categories:");
            categoryManager.listCategories();
            System.out.println("-----------------------------");

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

            // Xóa yếu tố chuyển đổi cũ và yếu tố chuyển đổi nghịch đảo cũ
            conversionElement.getParentNode().removeChild(conversionElement);
            Element inverseConversionElement = findConversionElement(root, category2ToEdit, category1ToEdit);
            if (inverseConversionElement != null) {
                inverseConversionElement.getParentNode().removeChild(inverseConversionElement);
            }

            // Thêm yếu tố chuyển đổi mới và yếu tố chuyển đổi nghịch đảo mới
            addOrUpdateConversionFactor(root, newCategory1, newCategory2, newFactor);
            calculateMissingConversionFactors(doc, root);

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
            if (conversionElement != null) {
                conversionElement.getParentNode().removeChild(conversionElement);
            }

            // Find and remove the inverse conversion factor element
            Element inverseElement = findConversionElement(root, category2ToDelete, category1ToDelete);
            if (inverseElement != null) {
                inverseElement.getParentNode().removeChild(inverseElement);
            }

            XMLManager.saveXML(doc, conversionsFile);
            System.out.println("Conversion factor deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Phương thức để liệt kê các yếu tố chuyển đổi với tên danh mục
    private void listConversionFactors() {
        try {
            Document doc = XMLManager.loadXML(conversionsFile);
            Element root = XMLManager.getElementByTagName(doc, "conversionFactors");

            NodeList conversionList = root.getElementsByTagName("conversionFactor");
            for (int i = 0; i < conversionList.getLength(); i++) {
                Element conversionElement = (Element) conversionList.item(i);
                String category1 = conversionElement.getElementsByTagName("category1").item(0).getTextContent();
                String category2 = conversionElement.getElementsByTagName("category2").item(0).getTextContent();
                String factor = conversionElement.getElementsByTagName("factor").item(0).getTextContent();

                // Lấy tên danh mục từ categories.xml
                String category1Name = getCategoryName(category1);
                String category2Name = getCategoryName(category2);

                // Hiển thị tên danh mục và yếu tố chuyển đổi
                System.out.println(category1Name + " and " + category2Name + ": Conversion Factor " + factor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Phương thức để lấy tên danh mục từ categories.xml
    private String getCategoryName(String categoryName) {
        try {
            Document doc = XMLManager.loadXML(categoriesFile);
            Element root = XMLManager.getElementByTagName(doc, "categories");

            return findCategoryName(root, categoryName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Category"; // Nếu không tìm thấy, trả về "Unknown Category"
    }

    // Phương thức để tìm tên danh mục từ phần tử gốc, hỗ trợ tìm đệ quy
    private String findCategoryName(Element root, String categoryName) {
        NodeList categories = root.getElementsByTagName("category");
        for (int i = 0; i < categories.getLength(); i++) {
            Element category = (Element) categories.item(i);
            String name = category.getElementsByTagName("name").item(0).getTextContent();

            if (name.equals(categoryName)) {
                return name; // Trả về nếu tìm thấy
            }

            // Nếu danh mục có các danh mục con, tìm đệ quy
            NodeList subCategories = category.getElementsByTagName("subCategories");
            if (subCategories.getLength() > 0) {
                String foundName = findCategoryName((Element) subCategories.item(0), categoryName);
                if (foundName != null) {
                    return foundName;
                }
            }
        }
        return null; // Trả về null nếu không tìm thấy
    }

    private Element findConversionElement(Element root, String category1, String category2) {
        NodeList conversionList = root.getElementsByTagName("conversionFactor");
        for (int i = 0; i < conversionList.getLength(); i++) {
            Element conversionElement = (Element) conversionList.item(i);
            String currentCategory1 = conversionElement.getElementsByTagName("category1").item(0).getTextContent();
            String currentCategory2 = conversionElement.getElementsByTagName("category2").item(0).getTextContent();
            if (currentCategory1.equals(category1) && currentCategory2.equals(category2)) {
                return conversionElement;
            }
        }
        return null;
    }
}
