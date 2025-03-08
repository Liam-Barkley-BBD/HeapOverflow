package com.heapoverflow;

import java.io.IOException;
import java.util.Scanner;

public class ApiClient {

    private static String jwtToken = null;

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Login with Google");
            System.out.println("2. Exit");

            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> login(scanner);
                case 2 -> {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void login(Scanner scanner) throws IOException, InterruptedException {
        // Todo: @Michael
    }
}
