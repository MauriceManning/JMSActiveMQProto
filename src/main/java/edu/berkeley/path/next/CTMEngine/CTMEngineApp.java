package edu.berkeley.path.next.CTMEngine;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.jms.*;

/**
 * This app represents a CTM model run, it simply creates a queue and places a
 * handful of raw link outputs on the queue.
 */
public class CTMEngineApp {


    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("ctm-engine.xml");
        context.start();

        final Logger logger = LogManager.getLogger(CTMEngineApp.class.getName());

        logger.info("CTMEngineApp initialized ");


        // linkManager creates the data to publish representing ccFramework output
        LinkManager linkManager = context.getBean(LinkManager.class);


        LinkDataRaw ldr = linkManager.getLink();

        logger.info("trafficMonitorApp links to publish: " + ldr);

        try {
            // Create a ConnectionFactory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", ActiveMQConnection.DEFAULT_BROKER_URL);
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();
            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Create the destination
            Destination destination = session.createQueue("testQ");
            // Create a MessageProducer from the Session to the Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            // Create a messages
            ObjectMessage listMessage = session.createObjectMessage(ldr);
            TextMessage message = session.createTextMessage("Helloworld");

            //counters
            int x = 0;

            while (x < 5) {
                logger.info("CTMEngineApp send: " + x);
                producer.send(listMessage);
                x++;
            }
            session.close();
            connection.close();
            System.out.println("Messages sent");

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

}
