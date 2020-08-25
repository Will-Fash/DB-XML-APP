package com.company;
import java.util.Properties;

// A helper class that contains all the properties to identify and access the
// database.

public class MyIdentity {

    public static void setIdentity(Properties prop) {
        prop.setProperty("database", "*******");
        prop.setProperty("user", "**********");
        prop.setProperty("password", "********");
    }

}
