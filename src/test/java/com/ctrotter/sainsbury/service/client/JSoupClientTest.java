package com.ctrotter.sainsbury.service.client;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Jsoup.class })
public class JSoupClientTest {

	@InjectMocks
	private JSoupClient jSoupClientTest = new JSoupClient();
	
	@Mock
	private Document document ;
	
	@Test(expected=IOException.class)
	public void testScrapeSiteForSpecifiedElementsClassIOExceptionThrown() throws Exception {
		String SITE_URL = "some_url_string";
		Connection connection = Mockito.mock(Connection.class);
		Mockito.when(connection.get()).thenThrow(new IOException("test"));
		PowerMockito.mockStatic(Jsoup.class);
		PowerMockito.when(Jsoup.connect(Mockito.anyString())).thenReturn(connection);
		String elementClassID = "dummy_id";
		jSoupClientTest.scrapeSiteForSpecifiedElementsClass(SITE_URL, elementClassID );
	}
	
	
	@Test()
	public void testScrapeSiteForSpecifiedElementsClassValid() throws Exception {
		//Configure
		String SITE_URL = "some_url_string";
		Connection connection = Mockito.mock(Connection.class);
		Elements elements = new Elements();
		when(connection.get()).thenReturn(document );
		when(document.getElementsByClass(anyString())).thenReturn(elements);
		PowerMockito.mockStatic(Jsoup.class);
		PowerMockito.when(Jsoup.connect(Mockito.anyString())).thenReturn(connection);
		String elementClassID = "dummy_id";
		//Execute
		Elements results = jSoupClientTest.scrapeSiteForSpecifiedElementsClass(SITE_URL, elementClassID );
		//Assert
		assertTrue(results.isEmpty());
		
		
	}

}
