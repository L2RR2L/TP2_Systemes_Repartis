package BO1;

import javax.swing.plaf.nimbus.State;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class Application {
    private static final String path = "src/main/java/BO1/Commands.txt";
    private static final String url = "jdbc:postgresql://localhost:5432/bo1-database";
    private static final String user = "bo1-database";
    private static final String password = "bo1-database-password";

    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in);
            BufferedWriter writer = new BufferedWriter(new FileWriter(path,true))){
            while(true){
                System.out.println("Enter command");
                String input = scanner.nextLine();
                String[] parts = input.split(" ");
                String command = parts[0];
                switch(command){
                    case "select":
                        try(java.sql.Connection dbConnection = DriverManager.getConnection(url,user,password)){
                            String selectQuery = "SELECT * FROM product_sales";
                            PreparedStatement pst = dbConnection.prepareStatement(selectQuery);
                            ResultSet rs = pst.executeQuery();

                            while(rs.next()){
                                int id = rs.getInt("id");
                                Date date = rs.getDate("date");
                                String region = rs.getString("region");
                                String product = rs.getString("product");
                                int qty = rs.getInt("qty");
                                float cost = rs.getFloat("cost");
                                float amount = rs.getFloat("amount");
                                float tax = rs.getFloat("tax");
                                float total = rs.getFloat("total");

                                System.out.println("Row: " + id + " " + date + " " + region + " " + product + " " + qty + " " + cost + " " + amount + " " + tax + " " +  total);
                            }

                            try{
                                writer.write(selectQuery);
                                writer.newLine();
                                writer.flush();
                            }catch(IOException e){
                                System.out.println("problem with writing query to text file");
                                e.printStackTrace();
                            }

                        } catch(SQLException e){
                            System.out.println("problem with query");
                            e.printStackTrace();
                        }


                        break;
                    case "update":
                        try(java.sql.Connection dbConnection = DriverManager.getConnection(url,user,password)){
                            int updateId = Integer.parseInt(parts[1]);
                            String updateColumn = parts[2];
                            String updateValue = parts[3];

                            String updateQuery = "UPDATE product_sales SET " + updateColumn + " = '" + updateValue + "' WHERE id = " + updateId;
                            Statement statement = dbConnection.createStatement();
                            statement.executeUpdate(updateQuery);

                            try{
                                writer.write(updateQuery);
                                writer.newLine();
                                writer.flush();
                            }catch(IOException e){
                                System.out.println("problem with writing query to text file");
                                e.printStackTrace();
                            }

                        } catch(SQLException e){
                            System.out.println("problem with query");
                            e.printStackTrace();
                        }

                        break;
                    case "delete":
                        try(java.sql.Connection dbConnection = DriverManager.getConnection(url,user,password)){
                            int removeId = Integer.parseInt(parts[1]);

                            String deleteQuery = "DELETE FROM product_sales WHERE id = " + removeId;
                            Statement statement = dbConnection.createStatement();
                            statement.executeUpdate(deleteQuery);


                            try{
                                writer.write(deleteQuery);
                                writer.newLine();
                                writer.flush();
                            }catch(IOException e){
                                System.out.println("problem with writing query to text file");
                                e.printStackTrace();
                            }

                        }catch(SQLException e){
                            System.out.println("problem with query");
                            e.printStackTrace();
                        }

                        break;
                    case "insert":

                        try(java.sql.Connection dbConnection = DriverManager.getConnection(url,user,password)){
                            String insertQuery = "INSERT INTO product_sales (id, date, region, product, qty, cost, amount, tax,total) " +
                                    "VALUES (" + Integer.parseInt(parts[1]) + ", " +
                                    "'" + new java.sql.Date(System.currentTimeMillis()) + "', " +
                                    "'" + parts[2] + "', " +
                                    "'" + parts[3] + "', " +
                                    Integer.parseInt(parts[4]) + ", " +
                                    Float.parseFloat(parts[5]) + ", " +
                                    Float.parseFloat(parts[6]) + ", " +
                                    Float.parseFloat(parts[7]) + ", " +
                                    Float.parseFloat(parts[8]) + ")";

                            Statement statement = dbConnection.createStatement();
                            statement.executeUpdate(insertQuery);

                            try{
                                writer.write(insertQuery);
                                writer.newLine();
                                writer.flush();
                            }catch(IOException e){
                                System.out.println("problem with writing query to text file");
                                e.printStackTrace();
                            }

                        } catch(SQLException e){
                            System.out.println("problem with query");
                            e.printStackTrace();
                        }


                        break;

                    case "exit":
                        System.exit(0);
                    default:
                        System.out.println("Invalid command");
                }
            }
        } catch(IOException e){
            System.out.println("problem in the outer region");
            e.printStackTrace();
        }


    }
}
