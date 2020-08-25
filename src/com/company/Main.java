package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // write your code here

        Scanner input = new Scanner(System.in);
        String startDate; //variable to hold start date
        String endDate; //variable to hold end date
        String fileName; //variable to hold filename

        System.out.println("Enter the start date: "); // Requesting Start Date
        startDate = input.nextLine(); // Reading input from user and storing in variable

        System.out.println("Enter the end date"); // Requesting end date
        endDate = input.nextLine(); // Reading input from user and storing in variable

        System.out.println("Enter the filename"); // Requesting filename
        fileName = input.nextLine(); // Reading input from user and storing in variable

        DBapp yo = new DBapp(startDate, endDate, fileName); // Making object from dbClass
        yo.printXML(); // using object to print xml
    }
}
