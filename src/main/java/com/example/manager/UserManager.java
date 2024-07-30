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
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
