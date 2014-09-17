package edu.berkeley.path.next.ResultsProcessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.*;
import java.util.concurrent.Future;

/*
 * This is the manager app that would recieve a notice event that a model run
 * had initiated and would then launch a set of workers to process the results.
 * This example launches three workers and they each establish a connection to
 * the raw output queue, retrieve one message and return a result.
 */


public class ResultsProcessorApp {


    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext();
        ctx.register(ResultsProcessorConfiguration.class);
        ctx.refresh();

        final Logger logger = LogManager.getLogger(ResultsProcessorApp.class.getName());

        LinkCalculator execMgr = ctx.getBean(LinkCalculator.class);

        logger.info("ResultsProcessorApp initialized ");

        //launch three model runs asynchronously
        Future<LinkCalculatorResult> result1 = execMgr.runComputation("Calculator1");
        Thread.sleep(1000);
        Future<LinkCalculatorResult> result2 = execMgr.runComputation("Calculator2");
        Thread.sleep(1000);
        Future<LinkCalculatorResult> result3 = execMgr.runComputation("Calculator3");

        // Wait until they are all done
        while (!(result1.isDone() && result2.isDone() && result3.isDone())) {
            logger.info("ResultsProcessorApp waiting ");
            Thread.sleep(100); //millisecond pause between each check
        }

        // demonstrate that all three completed and the location of the model results was provided
        logger.info("ResultsProcessorApp result1: " + result1.get().getReference());
        logger.info("ResultsProcessorApp result2: " + result2.get().getReference());
        logger.info("ResultsProcessorApp result3: " + result3.get().getReference());

    }

}
