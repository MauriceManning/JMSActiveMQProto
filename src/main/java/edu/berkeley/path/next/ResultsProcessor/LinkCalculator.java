package edu.berkeley.path.next.ResultsProcessor;

import edu.berkeley.path.next.CTMEngine.LinkDataRaw;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.Future;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.AsyncResult;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.jms.*;

/*
 *  This service is launched as a worker to make a connection to the raw output
 *  queue, retrieve messages which contain raw model output and run a calculation
 *  on the data. In this instance, it just gets one message and then returns a
 *  bogus result.
 */

@Service
public class LinkCalculator {


    @Async
    public Future<LinkCalculatorResult> runComputation(String param)   {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ResultsProcessorConfiguration.class);
        ctx.refresh();

        final Logger logger = LogManager.getLogger(LinkCalculator.class.getName());

        logger.info("LinkCalculator: run the computation " + param);

        LinkCalculatorResult result = ctx.getBean(LinkCalculatorResult.class);


        try {
            // Create a ConnectionFactory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    "admin", "admin", ActiveMQConnection.DEFAULT_BROKER_URL);
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();
            // Create a Session
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            // Create the destination
            Destination destination = session.createQueue("testQ");
            // Create a MessageConsumer from the Session to the Queue
            MessageConsumer consumer = session.createConsumer(destination);
            // Wait for a message
            Message message = consumer.receive(5000);
            //logger.info("LinkCalculator: message: " + message.toString());
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                System.out.println("Text Message is " + text);
            }
            else if (message instanceof ObjectMessage) {
                ObjectMessage objMessage = (ObjectMessage) message;
                LinkDataRaw ldr = (LinkDataRaw) objMessage.getObject();
                System.out.println("LinkCalculator: raw object  is " + ldr.toString());
                double speedlimit = ldr.getSpeedLimit();
                System.out.println("LinkCalculator: getSpeedLimit  is " + speedlimit);
            }
            else {
                System.out.println(message);
            }
            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        result.setName(param);
        result.setReference("results in folder: " + param);

        //return the result in  the Future object
        return new AsyncResult<LinkCalculatorResult>(result);

    }

}
