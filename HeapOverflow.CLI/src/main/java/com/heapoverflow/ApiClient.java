package com.heapoverflow;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ApiClient {

    private static final String API_URL = "http://localhost:8080";

    private static String JwtToken = null;

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
        // todo @michael
    }
}
