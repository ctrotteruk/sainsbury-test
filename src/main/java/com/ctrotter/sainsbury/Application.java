package com.ctrotter.sainsbury;

import java.io.IOException;

import javax.naming.MalformedLinkException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.ctrotter.sainsbury.jackson.JacksonMapper;
import com.ctrotter.sainsbury.service.SiteScraperService;

@SpringBootApplication(scanBasePackages= {"com.ctrotter.sainsbury"})
public class Application implements CommandLineRunner {
	
	private static final String urlToScrape = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
	
	@Autowired
	private SiteScraperService siteScraperService;
	
	@Autowired
	private JacksonMapper mapper;

	/**
	 * Main method for spring boot application allowing for fat jar to run correctly. 
	 * Disables console and web app from starting. 
	 * @param args
	 */
    public static void main(String[] args) {
    	new SpringApplicationBuilder(Application.class)
        .web(WebApplicationType.NONE)    
        .logStartupInfo(false)
        .bannerMode(Mode.OFF)
        .build()
        .run(args);
    }

	@Override
	public void run(String... args) throws Exception {
		try {
		System.out.println(mapper.writeValueAsString(siteScraperService.scrapeScrape(urlToScrape)));
		} catch (IOException | MalformedLinkException e) {
			System.out.println(e.getStackTrace());
		}
		System.exit(0);
	}

}