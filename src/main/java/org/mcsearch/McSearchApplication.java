package org.mcsearch;

import org.mcsearch.mapper.WordToFileMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.logging.Logger;

@SpringBootApplication
public class McSearchApplication {
	private static final Logger logger = Logger.getLogger(McSearchApplication.class.getName());

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(McSearchApplication.class, args);
		boolean success =  WordToFileMap.buildMap();

		if(success) {
			logger.info("Mapping successfully loaded");
		} else {
			logger.severe("Unable to load cache mapping, please verify the folder exists. Exiting the application");
			SpringApplication.exit(context);
		}
	}
}
