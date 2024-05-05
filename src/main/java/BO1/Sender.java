package BO1;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.*;

public class Sender {

    private static final String EXCHANGE_NAME = "direct_product_sales";
    private static final String QUEUE_NAME = "product_sales";
    private static String path = "src/main/java/BO1/Commands.txt";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try(Connection connection = factory.newConnection(); Channel channel = connection.createChannel()){
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,QUEUE_NAME);

            String line;
            try(BufferedReader reader = new BufferedReader(new FileReader(path))){
                while((line = reader.readLine())!= null){
                    channel.basicPublish(EXCHANGE_NAME,QUEUE_NAME,null,line.getBytes());
                    System.out.println("[x] sent: ' " + line + "'");
                }
            }catch(IOException e){
                System.out.println("problem with opening file");
                e.printStackTrace();
            }

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))){
                writer.write("");
            }catch(IOException e){
                System.out.println("problem with opening file to delete its contents");
                e.printStackTrace();
            }

        }catch(IOException e){
            System.out.println("problem with rabbitmq");
            e.printStackTrace();
        }
    }
}
