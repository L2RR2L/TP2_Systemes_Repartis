package HO;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Receiver {

    private static final String EXCHANGE_NAME = "direct_product_sales";
    private static final String QUEUE_NAME = "product_sales";

    private static final String url = "jdbc:postgresql://localhost:5434/ho-database";
    private static final String user = "ho-database";
    private static final String password = "ho-database-password";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,QUEUE_NAME);

        DeliverCallback deliverCallback =(consumerTag, delivery) -> {
            String query = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received Command: " + query);
            try(java.sql.Connection dbConnection = DriverManager.getConnection(url,user,password)){
                PreparedStatement pst = dbConnection.prepareStatement(query);
                pst.execute();
            }catch(SQLException e){
                System.out.println("problem with query");
                e.printStackTrace();
            }
        };
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,consumerTag -> {});
        System.out.println("[*] Waiting for commands. To exit press Ctrl+C");
    }
}
