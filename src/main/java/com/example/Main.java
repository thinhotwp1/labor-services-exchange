package com.example;

import com.example.manager.CategoryManager;
import com.example.manager.ConversionManager;
import com.example.manager.GeographicManager;
import com.example.manager.UserManager;
import com.example.model.Login;
import com.example.model.TypeUser;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserManager userManager = new UserManager();

        Login login = userManager.manageLogin(scanner);

        boolean running = true;
        while (running) {
            System.out.println("-----------------------------");
            System.out.println("Main Menu:");
            System.out.println("1. Category Management");
            System.out.println("2. Geographic Area Management");
            System.out.println("3. Conversion Factor Management");
            System.out.println("4. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
//                    if (login.getTypeUser() != TypeUser.ADMIN) {
//                        System.out.println("You are not admin. Invalid choice. Try again.");
//                        break;
//                    }
                    CategoryManager categoryManager = new CategoryManager();
                    categoryManager.manage(scanner);
                    break;
                case 2:
                    GeographicManager geographicManager = new GeographicManager();
                    geographicManager.manage(scanner);
                    break;
                case 3:
                    ConversionManager conversionManager = new ConversionManager();
                    conversionManager.manage(scanner);
                    break;
                case 4:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
    }
}
