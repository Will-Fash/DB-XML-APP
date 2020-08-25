## Background:

The program takes in the start dates and end dates and file name from a user in order to print
out report in xml based o queries to a Mysql server.

## Files:

There are 3 files in the program
**MyIdentity**: is a helper class to enable us set the properties needed to access the database
**DBapp**: This handles the queries and printing out of the the XML report
**Main**: This is the class the user interacts with, where users enter the start date, end date and
the filename.

## Design Elements:

The program gets inputs from users such as start date, end date and file name and then creates
an object of the DBapp class where a start connection method is called in it’s constructor. A
object created in Main then call the printxml method which runs the queries and generates an
xml object which it then exports to an xml file

## Assumptions:
• Date ranges are valid and are of type string
• Filename is valid.
• Database access credentials are valis

## Reasons for Deployment:
The code is written in such a way that’s easy to use and only requires 3 inputs from the user.
The code is easy to read and can be easily expanded on to add more functionality to generate
more queries
The code is written in such a away that’s not tightly coupled, each of the 3 files can be changed
without needing to change other files.
The application covers all of it’s functional requirements.

## Test Cases:
• Dates are valid;
• Filename is a string
• Folder for file exists
• Database credentials are valid
• Database credentials aren’t alid
• User enters empty filename and dates