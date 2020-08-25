package com.company;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.sql.*;
import java.util.Properties;

public class DBapp {

    String user; // user variable
    String password; // password variable
    String database; // database variable
    Connection connect = null; // connection variable
    Statement statement = null; // statement variable
    String fileName; // filename variable
    String startDate; // start date variable
    String endDate; // end date variable

    Properties prop = new Properties(); // properties object
    MyIdentity identity  = new MyIdentity(); // identity object to help with identity and accessing the db


    // constructor passes input from user to the db class
    public DBapp(String startDate, String endDate, String fileName){

        this.startDate = startDate;
        this.endDate = endDate;
        this.fileName = fileName;

        startConnection(); // calling start connection in constructor
    }

    // Method to start connection to db
    public void startConnection(){

        identity.setIdentity(prop);
        user = prop.getProperty("user");
        password = prop.getProperty("password");
        database = prop.getProperty("database");

        try{Class.forName("com.mysql.cj.jdbc.Driver");

            connect = DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306", user, password); // connecting to db
            statement = connect.createStatement();

            statement.executeQuery("use " + database + ";");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    //Method to execute customer report query
    public ResultSet customerReport(String startDate, String endDate){

        ResultSet r = null;
            try {
                r = statement.executeQuery("select customers.CompanyName, customers.Address, customers.City, customers.Region, customers.PostalCode, " +
                        "customers.Country, count(orders.CustomerID) As numOfOrders, sum((orderdetails.Quantity * orderdetails.UnitPrice) - orderdetails.Discount) As TotalSales " +
                        "from orders left join customers on orders.CustomerID = customers.CustomerID left join orderdetails on orders.OrderID = orderdetails.OrderID where orders.OrderDate Between '"
                        + startDate + "' And '" + endDate + "' Group by customers.CustomerID;");
            }catch(Exception e){
                System.out.println(e);
            }
            return r;
    }

    //Method to execute product report query
    public ResultSet productReport(String startDate, String endDate){

        ResultSet r = null;
        try{
            r = statement.executeQuery("select categories.CategoryName, products.ProductName, suppliers.CompanyName, sum(orderdetails.Quantity) As UnitsSold, sum((orderdetails.Quantity * orderdetails.UnitPrice) - orderdetails.Discount) as valueOfProductSold from products left join categories on categories.CategoryID = products.CategoryID left join suppliers on suppliers.SupplierID = products.SupplierID left join orderdetails on orderdetails.ProductID = products.ProductID left join orders on orderdetails.OrderID = orders.OrderID where orders.OrderDate between '" + startDate + "' and '" + endDate + "'group by products.ProductID order by categories.CategoryName;");
        }catch(Exception e){
            System.out.println(e);
        }

        return r;
    }

    //Method to execute supplier report query
    public ResultSet supplierReport(String startDate, String endDate){

        ResultSet r = null;
        try{
            r = statement.executeQuery("select suppliers.CompanyName, suppliers.Address, suppliers.City, suppliers.Region, suppliers.PostalCode, suppliers.Country, sum(orderdetails.Quantity) as numberOfProductsSold, sum((orderdetails.Quantity * orderdetails.UnitPrice) - orderdetails.Discount) AS TotalGoodsSold from suppliers right join products on suppliers.SupplierID = products.SupplierID left join orderdetails on products.ProductID = orderdetails.ProductID left join orders on orders.OrderID = orderdetails.OrderID  where orders.OrderDate between '" + startDate + "' and '" + endDate + "' group by suppliers.SupplierID order by suppliers.CompanyName;");
        }catch(Exception e){
            System.out.println(e);
        }

        return  r;
    }

    //Method to print to XML document

    public void printXML(){

        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            //root elements
            Document doc = builder.newDocument();
            Element rootElement = doc.createElement("year_end_summary");
            doc.appendChild(rootElement);

            Element year = doc.createElement("year");
            rootElement.appendChild(year);

            Element start_date = doc.createElement("start_date");
            start_date.appendChild(doc.createTextNode(startDate));
            year.appendChild(start_date);

            Element end_date = doc.createElement("end_date");
            end_date.appendChild(doc.createTextNode(endDate));
            year.appendChild(end_date);

            Element customer_list = doc.createElement("customer_list");
            rootElement.appendChild(customer_list);

            Element product_list = doc.createElement("product_list");
            rootElement.appendChild(product_list);

            Element supplier_list = doc.createElement("supplier_list");
            rootElement.appendChild(supplier_list);

            ResultSet customerReport = this.customerReport(startDate,endDate); // // calling customerreport method and passing result to a resultset variable

            // Reading from db and generating xml tags for the customer resultset
            while(customerReport.next()){
                Element customer = doc.createElement("customer");
                customer_list.appendChild(customer);

                Element customer_name = doc.createElement("customer_name");
                customer_name.appendChild(doc.createTextNode(""+customerReport.getString("CompanyName")));
                customer.appendChild(customer_name);

                Element address = doc.createElement("address");
                customer.appendChild(address);

                Element street_name = doc.createElement("street_name");
                street_name.appendChild(doc.createTextNode(""+customerReport.getString("Address")));
                address.appendChild(street_name);

                Element city = doc.createElement("city");
                city.appendChild(doc.createTextNode(""+customerReport.getString("City")));
                address.appendChild(city);

                Element region = doc.createElement("region");
                region.appendChild(doc.createTextNode(""+customerReport.getString("Region")));
                address.appendChild(region);

                Element postal_code = doc.createElement("postal_code");
                postal_code.appendChild(doc.createTextNode(""+customerReport.getString("PostalCode")));
                address.appendChild(postal_code);

                Element country = doc.createElement("country");
                country.appendChild(doc.createTextNode(customerReport.getString("Country")));
                address.appendChild(country);

                Element num_orders = doc.createElement("num_orders");
                num_orders.appendChild(doc.createTextNode(""+customerReport.getString("numOfOrders")));
                customer.appendChild(num_orders);

                Element order_value = doc.createElement("order_value");
                order_value.appendChild(doc.createTextNode(""+customerReport.getString("TotalSales")));
                customer.appendChild(order_value);

            }

            customerReport.close(); // close customer resultset

            ResultSet productReport = this.productReport(startDate,endDate); // calling productreport method and passing result to a resultset variable

            // Reading from db and generating xml tags for the product resultset
            while(productReport.next()){
                Element category = doc.createElement("category");
                product_list.appendChild(category);

                Element category_name = doc.createElement("category_name");
                category_name.appendChild(doc.createTextNode(""+productReport.getString("CategoryName")));
                category.appendChild(category_name);

                Element product = doc.createElement("product");
                category.appendChild(product);

                Element product_name = doc.createElement("product_name");
                product_name.appendChild(doc.createTextNode(""+productReport.getString("ProductName")));
                product.appendChild(product_name);

                Element supplier_name = doc.createElement("supplier_name");
                supplier_name.appendChild(doc.createTextNode(""+productReport.getString("CompanyName")));
                product.appendChild(supplier_name);

                Element units_sold = doc.createElement("units_sold");
                units_sold.appendChild(doc.createTextNode(""+productReport.getString("UnitsSold")));
                product.appendChild(units_sold);

                Element sale_value = doc.createElement("sale_value");
                sale_value.appendChild(doc.createTextNode(""+productReport.getString("valueOfProductSold")));
                product.appendChild(sale_value);

            }

            productReport.close(); // close product resultset

            ResultSet supplierReport = this.supplierReport(startDate, endDate); // Reading from db and generating xml tags for the supplier resultset

            while(supplierReport.next()){

                Element supplier = doc.createElement("supplier");
                supplier_list.appendChild(supplier);

                Element supplier_name = doc.createElement("supplier_name");
                supplier_name.appendChild(doc.createTextNode(""+supplierReport.getString("CompanyName")));
                supplier.appendChild(supplier_name);

                Element address = doc.createElement("address");
                supplier.appendChild(address);

                Element street_address = doc.createElement("street_address");
                street_address.appendChild(doc.createTextNode(""+supplierReport.getString("Address")));
                address.appendChild(street_address);

                Element city = doc.createElement("city");
                city.appendChild(doc.createTextNode(""+supplierReport.getString("City")));
                address.appendChild(city);

                Element region = doc.createElement("region");
                region.appendChild(doc.createTextNode(""+supplierReport.getString("Region")));
                address.appendChild(region);

                Element postal_Code = doc.createElement("postal_code");
                postal_Code.appendChild(doc.createTextNode(""+supplierReport.getString("PostalCode")));
                address.appendChild(postal_Code);

                Element country = doc.createElement("country");
                country.appendChild(doc.createTextNode(""+supplierReport.getString("country")));
                address.appendChild(country);

                Element num_products = doc.createElement("num_products");
                num_products.appendChild(doc.createTextNode(""+supplierReport.getString("numberOfProductsSold")));
                supplier.appendChild(num_products);

                Element product_value = doc.createElement("product_value");
                product_value.appendChild(doc.createTextNode(""+supplierReport.getString("TotalGoodsSold")));
                supplier.appendChild(product_value);
            }

            supplierReport.close(); // close supplier resultset

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer form = tFactory.newTransformer();
            DOMSource src = new DOMSource(doc); //Creating a new instance of DOMSource to get file src from object od DocumentBuilder class
            StreamResult output = new StreamResult(new File("C:\\Users\\Jesuseyi Fasuyi\\Documents\\"+ fileName + ".xml")); // writing output to file
            form.transform(src, output);

        }catch (Exception e){
            System.out.println(e);
        }

        closeConnection(); // calling close connection in print XML
    }

    // Method to close connection and release resources.
    public void closeConnection(){
        try{
            if (statement != null){
                statement.close();
            }
            if(connect != null){
                connect.close();
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

}
