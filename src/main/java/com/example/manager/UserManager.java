package com.example.manager;

import com.example.model.User;
import com.example.util.XMLManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Scanner;

public class UserManager {
    private String credentialsFile;
    private User currentUser;

    public UserManager(String credentialsFile) {
        this.credentialsFile = credentialsFile;
    }

    public boolean login(Scanner scanner) {
        try {
            Document doc = XMLManager.loadXML(credentialsFile);
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
        return false;
    }

    public void registerUser(Scanner scanner) {
        try {
            Document doc = XMLManager.loadXML(credentialsFile);
            Element root = XMLManager.getElementByTagName(doc, "users");

            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            String role = " ";
            while(!role.equals("admin") && !role.equals("user")){
                System.out.print("Enter role (admin/user): ");
                role = scanner.nextLine();
            }

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
            Element roleElement = doc.createElement("role");
            roleElement.setTextContent(role);

            userElement.appendChild(usernameElement);
            userElement.appendChild(passwordElement);
            userElement.appendChild(roleElement);
            root.appendChild(userElement);

            XMLManager.saveXML(doc, credentialsFile);
            System.out.println("User registered successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
