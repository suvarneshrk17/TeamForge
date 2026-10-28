
package com.teamforge;

import java.util.ArrayList;
import java.util.Scanner;

public class ManualEmployeeReader {

    public static ArrayList<String> readEmployees(Scanner scanner) {

        ArrayList<String> employees = new ArrayList<>();

        System.out.print("How many employees? ");
        int count = scanner.nextInt();
        scanner.nextLine();

        for (int i = 1; i <= count; i++) {

            System.out.print("Enter employee name " + i + ": ");
            String name = scanner.nextLine();

            if (name.trim().isEmpty()) {
                System.out.println("Please enter a valid name.");
                i--;
            } else {
                employees.add(name.trim());
            }
        }

        return employees;
    }
}
