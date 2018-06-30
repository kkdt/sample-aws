/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Spring Boot Application - Sample Cognito.
 * 
 * @author thinh ho
 *
 */
@SpringBootApplication
@ComponentScan({"kkdt.sample.aws.cognito"})
@PropertySource("classpath:/tools/sampleaws-cognito.properties")
public class SampleCognito implements ApplicationRunner {
    private static final Logger logger = Logger.getLogger(SampleCognito.class);
    
    @Autowired(required=true)
    private Environment environment;
    
    @Autowired(required=true)
    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info(String.format("Cognito PoolID: %s", environment.getProperty("cognito.poolid")));
        logger.info(String.format("Cognito ClientID: %s", environment.getProperty("cognito.clientid")));
        logger.info(String.format("Region: %s", environment.getProperty("cognito.region")));
        
        SampleConsole console = new SampleConsole(environment.getProperty("cognito.title"), applicationContext)
            .layoutComponents();
        
        SwingUtilities.invokeLater(() -> {
            console.pack();
            console.setSize(500, 300);
            console.setVisible(true);
            console.setResizable(false);
        });
    }
    
}
