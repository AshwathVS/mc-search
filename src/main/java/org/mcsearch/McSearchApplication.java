package org.mcsearch;

import org.mcsearch.mapper.WordToByteMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.logging.Logger;

@SpringBootApplication
public class McSearchApplication {
	private static final Logger logger = Logger.getLogger(McSearchApplication.class.getName());

	public static void main(String[] args) {
		boolean success = WordToByteMap.loadMap();

		if(success) {
			logger.info("Mapping successfully loaded, starting application");
			ApplicationContext context = SpringApplication.run(McSearchApplication.class, args);
		} else {
			logger.severe("Unable to find necessary index files, try again");
		}
	}
}
