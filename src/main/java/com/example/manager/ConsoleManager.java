package com.example.manager;

import java.util.Scanner;

public class ConsoleManager {
    private static final String CREDENTIALS_FILE = "credentials.xml";
    private static final String CATEGORIES_FILE = "categories.xml";
    private static final String GEOGRAPHIC_FILE = "geographic.xml";
    private static final String CONVERSIONS_FILE = "conversions.xml";

    private UserManager userManager;
    private CategoryManager categoryManager;
    private GeographicManager geographicManager;
    private ConversionManager conversionManager;

    public ConsoleManager() {
        userManager = new UserManager(CREDENTIALS_FILE);
        categoryManager = new CategoryManager(CATEGORIES_FILE);
        geographicManager = new GeographicManager(GEOGRAPHIC_FILE);
        conversionManager = new ConversionManager(CONVERSIONS_FILE);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = false;

        while (!loggedIn) {
            System.out.println("Choose an option: ");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    userManager.registerUser(scanner);
                    break;
                case 2:
                    if (userManager.login(scanner)) {
                        loggedIn = true;
                    } else {
                        System.out.println("Authentication failed.");
                    }
                    break;
                case 3:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }

        // Once logged in, allow access to management options
        boolean running = true;
        while (running) {
            System.out.println("Choose an option: ");
            System.out.println("1. Manage Categories");
            System.out.println("2. Manage Geographic Areas");
            System.out.println("3. Manage Conversion Factors");
//            System.out.println("4. Logout");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    categoryManager.manage(scanner);
                    break;
                case 2:
                    geographicManager.manage(scanner);
                    break;
                case 3:
                    conversionManager.manage(scanner);
                    break;
                case 4:
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        ConsoleManager consoleManager = new ConsoleManager();
        consoleManager.start();
    }
}
