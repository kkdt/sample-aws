/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.support;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Main driver for any tool-like application packaged with this module.
 * 
 * <p>
 * At the minimum, this class expects 1 argument to be the Spring Boot configuration
 * class to load. Any remaining argument(s) will be passed into the configuration
 * class to process.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class SampleAWS {
    private static final Logger logger = Logger.getLogger(SampleAWS.class);
    
    /**
     * Application entry.
     * 
     * @param args must contain the Spring Boot configuration to start up.
     */
    public static void main(String[] args) {
        if(args.length == 0) {
            throw new IllegalArgumentException("No boot configuration found");
        }
        
        String boot = args[0];
        String[] _args = args.length > 1 ?
            Arrays.copyOfRange(args, 1, args.length) : new String[0];
        
        try {
            Class<?> bootClass = Class.forName(boot);
            SpringApplicationBuilder app = new SpringApplicationBuilder(bootClass)
                .bannerMode(Mode.OFF).logStartupInfo(false);
            app.headless(false)
                .web(WebApplicationType.NONE)
                .run(_args);
        } catch (Exception e) {
            System.err.println(String.format("Cannot start up %s: %s", boot, e.getMessage()));
            logger.error(e);
            System.exit(-1);
        }
    }
}
