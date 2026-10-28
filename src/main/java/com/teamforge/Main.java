
package com.teamforge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        ArrayList<String> employees = new ArrayList<>();

        System.out.println("Welcome to TeamForge!");
        System.out.println("1. Enter employee names manually");
        System.out.println("2. Read employee names from Excel");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {

            System.out.print("How many employees? ");
            int count = scanner.nextInt();
            scanner.nextLine();

            for (int i = 1; i <= count; i++) {
                System.out.print("Enter employee name " + i + ": ");
                String name = scanner.nextLine().trim();

                if (name.isEmpty()) {
                    System.out.println("Please enter a valid name.");
                    i--;
                } else {
                    employees.add(name);
                }
            }

        } else if (choice == 2) {

            System.out.print("Enter Excel file path: ");
            String filePath = scanner.nextLine();

            try {
                employees = ExcelReader.readNames(filePath);
                System.out.println("Total employees read: " + employees.size());
            } catch (IOException e) {
                System.out.println("Unable to read Excel file.");
                scanner.close();
                return;
            }

        } else {
            System.out.println("Invalid choice!");
            scanner.close();
            return;
        }

        if (employees.size() < 2) {
            System.out.println("At least 2 employees are required.");
            scanner.close();
            return;
        }

        System.out.print("How many teams do you want? ");
        int teamCount = scanner.nextInt();

        if (teamCount < 2 || teamCount > employees.size()) {
            System.out.println("Invalid number of teams!");
            scanner.close();
            return;
        }

        List<List<String>> teams = new ArrayList<>();

        for (int i = 0; i < teamCount; i++) {
            teams.add(new ArrayList<>());
        }

        Collections.shuffle(employees);

        for (int i = 0; i < employees.size(); i++) {
            teams.get(i % teamCount).add(employees.get(i));
        }

        for (int i = 0; i < teams.size(); i++) {
            System.out.println("\nTeam " + (i + 1) + ":");

            for (String name : teams.get(i)) {
                System.out.println(name);
            }
        }

        saveTeams(teams);

        scanner.close();
    }

    public static void saveTeams(List<List<String>> teams) {

        try (SessionFactory factory =
                     new Configuration().configure().buildSessionFactory();
             Session session = factory.openSession()) {

            Transaction transaction = session.beginTransaction();

            try {
                session.doWork(connection -> {

                    for (int i = 0; i < teams.size(); i++) {

                        String tableName = "team_" + (i + 1);

                        try (Statement statement = connection.createStatement()) {
                            statement.executeUpdate(
                                "CREATE TABLE IF NOT EXISTS " + tableName +
                                " (employee_name VARCHAR(255) NOT NULL)");

                            statement.executeUpdate("DELETE FROM " + tableName);
                        }

                        String sql = "INSERT INTO " + tableName +
                                     " (employee_name) VALUES (?)";

                        try (PreparedStatement statement =
                                     connection.prepareStatement(sql)) {

                            for (String name : teams.get(i)) {
                                statement.setString(1, name);
                                statement.addBatch();
                            }

                            statement.executeBatch();
                        }
                    }
                });

                transaction.commit();
                System.out.println("\nTeams saved to MySQL successfully!");

            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                System.out.println("Unable to save teams: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}
