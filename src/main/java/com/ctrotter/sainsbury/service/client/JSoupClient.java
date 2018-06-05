package com.ctrotter.sainsbury.service.client;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class JSoupClient {

	/**
	 * Utility class for obtaining Elements from a specified site. 
	 * 
	 * @param url - url represneting site to scrape.
	 * @param elementClass - specific css class to look for in html 
	 * @return list of {@link Elements} contained within html class.
	 * @throws IOException - thrown if site cannot be reached / read.
	 */
	public Elements scrapeSiteForSpecifiedElementsClass(String url, String elementClass) throws IOException {
		Document doc = null;
		Elements elements = null;
		Connection connection = Jsoup.connect(url);
		doc = connection.get();
		elements = doc.getElementsByClass(elementClass);
		return elements;
	}

}
