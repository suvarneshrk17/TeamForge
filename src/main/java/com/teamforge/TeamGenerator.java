package com.teamforge;
import java.util.ArrayList;
import java.util.Collections;

public class TeamGenerator {

    public static void generateTeams(ArrayList<String> employees, int teamCount) {

        if (teamCount < 2 || teamCount > employees.size()) {
            System.out.println("Invalid number of teams!");
            return;
        }

        Collections.shuffle(employees);

        for (int i = 0; i < teamCount; i++) {

            System.out.println("\nTeam " + (i + 1) + ":");

            for (int j = i; j < employees.size(); j += teamCount) {
                System.out.println(employees.get(j));
            }
        }

        System.out.println("\nTeam generation completed successfully!");
    }
}
