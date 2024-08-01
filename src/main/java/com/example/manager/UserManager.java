package com.example.manager;

import com.example.model.User;
import com.example.util.XMLManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Scanner;

public class UserManager {
    private static final String ADMIN_FILE = "admins.xml";
    private static final String CREDENTIALS_FILE = "credentials.xml";
    private User currentUser;

    public void manageLogin(Scanner scanner) {
        boolean authenticated = false;
        while (!authenticated) {
            System.out.println("What type of user are you?");
            System.out.println("1. Admin");
            System.out.println("2. User");
            int userTypeChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (userTypeChoice) {
                case 1:
                    authenticated = handleAdminLogin(scanner);
                    break;
                case 2:
                    authenticated = handleUserLogin(scanner);
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private boolean handleAdminLogin(Scanner scanner) {
        System.out.println("Are you a new configurator or a registered configurator?");
        System.out.println("1. New Admin");
        System.out.println("2. Registered Admin");
        System.out.println("3. Back");
        int adminChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (adminChoice) {
            case 1:
                return registerAdmin(scanner);
            case 2:
                return authenticateAdmin(scanner);
            case 3:
                return false;
            default:
                System.out.println("Invalid choice. Try again.");
                return false;
        }
    }

    private boolean handleUserLogin(Scanner scanner) {
        System.out.println("Are you a new user or a registered user?");
        System.out.println("1. New User");
        System.out.println("2. Registered User");
        System.out.println("3. Back");
        int userChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (userChoice) {
            case 1:
                registerUser(scanner);
                return true;
            case 2:
                return login(scanner);
            case 3:
                return false;
            default:
                System.out.println("Invalid choice. Try again.");
                return false;
        }
    }

    private boolean registerAdmin(Scanner scanner) {
        System.out.print("Choose your username: ");
        String username = scanner.nextLine();
        System.out.print("Choose your password: ");
        String password = scanner.nextLine();

        saveAdminCredentials(username, password);
        System.out.println("Configurator registered successfully.");
        return true;
    }

    private boolean authenticateAdmin(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (validateAdminCredentials(username, password)) {
            System.out.println("Configurator authenticated successfully.");
            return true;
        } else {
            System.out.println("Credentials are incorrect. Try again.");
            return false;
        }
    }

    private boolean login(Scanner scanner) {
        try {
            Document doc = XMLManager.loadXML(CREDENTIALS_FILE);
            Element root = XMLManager.getElementByTagName(doc, "users");

            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            NodeList userList = root.getElementsByTagName("user");
            for (int i = 0; i < userList.getLength(); i++) {
                Element userElement = (Element) userList.item(i);
                if (userElement.getElementsByTagName("username").item(0).getTextContent().equals(username) &&
                        userElement.getElementsByTagName("password").item(0).getTextContent().equals(password)) {
                    currentUser = new User();
                    currentUser.setUsername(username);
                    currentUser.setPassword(password);
                    currentUser.setRole(userElement.getElementsByTagName("role").item(0).getTextContent());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Invalid username or password, please try again");
        return false;
    }

    public void registerUser(Scanner scanner) {
        try {
            Document doc;
            Element root;

            if (XMLManager.fileExists(CREDENTIALS_FILE)) {
                doc = XMLManager.loadXML(CREDENTIALS_FILE);
                root = XMLManager.getElementByTagName(doc, "users");
            } else {
                doc = XMLManager.createDocument();
                root = doc.createElement("users");
                doc.appendChild(root);
            }

            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

//            String role = "";
//            while (!role.equals("admin") && !role.equals("user")) {
//                System.out.print("Enter role (admin/user): ");
//                role = scanner.nextLine();
//            }

            // Check if the user already exists
            NodeList userList = root.getElementsByTagName("user");
            for (int i = 0; i < userList.getLength(); i++) {
                Element userElement = (Element) userList.item(i);
                if (userElement.getElementsByTagName("username").item(0).getTextContent().equals(username)) {
                    System.out.println("User already exists.");
                    return;
                }
            }

            Element userElement = doc.createElement("user");
            Element usernameElement = doc.createElement("username");
            usernameElement.setTextContent(username);
            Element passwordElement = doc.createElement("password");
            passwordElement.setTextContent(password);
//            Element roleElement = doc.createElement("role");
//            roleElement.setTextContent(role);

            userElement.appendChild(usernameElement);
            userElement.appendChild(passwordElement);
//            userElement.appendChild(roleElement);
            root.appendChild(userElement);

            XMLManager.saveXML(doc, CREDENTIALS_FILE);
            System.out.println("User registered successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveAdminCredentials(String username, String password) {
        try {
            Document doc;
            Element root;

            if (XMLManager.fileExists(ADMIN_FILE)) {
                doc = XMLManager.loadXML(ADMIN_FILE);
                root = XMLManager.getElementByTagName(doc, "admins");
            } else {
                doc = XMLManager.createDocument();
                root = doc.createElement("admins");
                doc.appendChild(root);
            }

            Element adminElement = doc.createElement("admin");
            Element usernameElement = doc.createElement("username");
            usernameElement.setTextContent(username);
            Element passwordElement = doc.createElement("password");
            passwordElement.setTextContent(password);

            adminElement.appendChild(usernameElement);
            adminElement.appendChild(passwordElement);
            root.appendChild(adminElement);

            XMLManager.saveXML(doc, ADMIN_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateAdminCredentials(String username, String password) {
        try {
            if (!XMLManager.fileExists(ADMIN_FILE)) {
                return false;
            }

            Document doc = XMLManager.loadXML(ADMIN_FILE);
            NodeList adminList = doc.getElementsByTagName("admin");

            for (int i = 0; i < adminList.getLength(); i++) {
                Element adminElement = (Element) adminList.item(i);
                String storedUsername = adminElement.getElementsByTagName("username").item(0).getTextContent();
                String storedPassword = adminElement.getElementsByTagName("password").item(0).getTextContent();

                if (storedUsername.equals(username) && storedPassword.equals(password)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
