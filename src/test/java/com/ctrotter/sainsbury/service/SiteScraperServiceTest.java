package com.ctrotter.sainsbury.service;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ctrotter.sainsbury.model.vo.ScrapedData;
import com.ctrotter.sainsbury.service.client.JSoupClient;

@RunWith(MockitoJUnitRunner.class)
public class SiteScraperServiceTest {

	private static final String TEST_CALORIES = "100kcal";

	private static final String PRICE_PER_UNIT = "1.00/unit";

	private static final String ELEMENT_TEXT = "TEXT";

	private static final String PRICE_PER_UNIT_CLASS_ID = "pricePerUnit";

	private static final String PRODUCT_TEXT_CLASS_ID = "productText";

	private static final String NUTRITION_LEVEL_CLASSS_ID = "nutritionLevel1";

	private static final String INVALID_PATH_URL = "../../../../../../shop/gb/groceries/berries-cherries-currants/";

	private static final String A_TAG = "a";

	private static final String PRODUCT_NAME_AND_PROMOTIONS_CLASS_ID = "productNameAndPromotions";

	private static final String PRODUCT_CLASS_ID = "product";


	@Mock
	private JSoupClient jSoupClient;

	@Mock
	private Element priceElement,element, productAndPromationElement, nutritionElement, priceElementParagraph;

	@InjectMocks
	private SiteScraperService siteScraperService = new SiteScraperService();

	@Test
	public void testScrapeScrapeWithNoElementsReturn() throws Exception {
		// Configure
		Elements elements = new Elements();
		when(jSoupClient.scrapeSiteForSpecifiedElementsClass(anyString(), anyString())).thenReturn(elements);
		ScrapedData scrapedData = siteScraperService.scrapeScrape("INVALID_URL");
		// Assert
		Assert.assertTrue(scrapedData.getResults().isEmpty());
		Assert.assertTrue(scrapedData.getTotal().getVat().equals(new BigDecimal(0)));
		Assert.assertTrue(scrapedData.getTotal().getGross().equals(new BigDecimal(0)));
	}

	@Test
	public void testScrapeScrapeWithValidElements() throws Exception {
		// Configure
		Elements elements = new Elements();
		elements.add(element);
		Elements priceElements = new Elements();
		Elements nutritionElements = new Elements();
		Elements priceElementParagraphElements = new Elements();
		nutritionElements.add(nutritionElement);
		priceElements.add(priceElement);
		priceElementParagraphElements.add(priceElementParagraph);
		when(element.getElementsByClass(PRODUCT_NAME_AND_PROMOTIONS_CLASS_ID)).thenReturn(elements);
		when(element.getElementsByClass(PRODUCT_NAME_AND_PROMOTIONS_CLASS_ID)).thenReturn(elements);
		when(element.getElementsByClass(PRICE_PER_UNIT_CLASS_ID)).thenReturn(elements);
		when(element.getElementsByTag(A_TAG)).thenReturn(elements);
		when(element.text()).thenReturn(ELEMENT_TEXT);
		when(element.hasText()).thenReturn(Boolean.TRUE);
		when(element.getElementsByClass(PRICE_PER_UNIT_CLASS_ID)).thenReturn(priceElements);
		when(priceElement.text()).thenReturn(PRICE_PER_UNIT);
		when(element.attr("href")).thenReturn(INVALID_PATH_URL + "/url");
		when(element.getElementsByClass(NUTRITION_LEVEL_CLASSS_ID)).thenReturn(nutritionElements);
		when(nutritionElement.getElementsByClass(NUTRITION_LEVEL_CLASSS_ID)).thenReturn(elements);
		when(nutritionElement.hasText()).thenReturn(Boolean.TRUE);
		when(nutritionElement.text()).thenReturn(TEST_CALORIES);
		when(nutritionElement.getElementsByClass(PRODUCT_TEXT_CLASS_ID)).thenReturn(priceElements);
		when(element.select("TD:contains(kcal)")).thenReturn(nutritionElements);
		when(jSoupClient.scrapeSiteForSpecifiedElementsClass(anyString(), anyString())).thenReturn(elements);
		// Excecute
		ScrapedData scrapedData = siteScraperService.scrapeScrape("VALID_URL");
		// Assert
		Assert.assertTrue(!scrapedData.getResults().isEmpty());
		Assert.assertTrue(scrapedData.getResults().size() == 1);
		Assert.assertTrue(scrapedData.getResults().get(0).getKcalPer100g() == 100);
		Assert.assertTrue(scrapedData.getResults().get(0).getUnitPrice().compareTo(new BigDecimal(1.00)) == 0);
		Assert.assertTrue(scrapedData.getTotal().getGross().compareTo(new BigDecimal(1.00)) == 0);
		Assert.assertTrue(
				scrapedData.getTotal().getVat().compareTo(new BigDecimal(0.20).setScale(2, RoundingMode.DOWN)) == 0);
	}

	@Test
	public void testScrapeScrapeWithValidElementsNoText() throws Exception {
		// Configure
		Elements elements = new Elements();
		elements.add(element);
		Elements priceElements = new Elements();
		Elements nutritionElements = new Elements();
		Elements priceElementParagraphElements = new Elements();
		nutritionElements.add(nutritionElement);
		priceElements.add(priceElement);
		priceElementParagraphElements.add(priceElementParagraph);
		when(element.getElementsByClass(PRODUCT_NAME_AND_PROMOTIONS_CLASS_ID)).thenReturn(elements);
		when(element.getElementsByClass(PRODUCT_NAME_AND_PROMOTIONS_CLASS_ID)).thenReturn(elements);
		when(element.getElementsByClass(PRICE_PER_UNIT_CLASS_ID)).thenReturn(elements);
		when(element.getElementsByTag(A_TAG)).thenReturn(elements);
		when(element.text()).thenReturn(ELEMENT_TEXT);
		when(element.hasText()).thenReturn(Boolean.FALSE);
		when(element.getElementsByClass(PRICE_PER_UNIT_CLASS_ID)).thenReturn(priceElements);
		when(priceElement.text()).thenReturn(PRICE_PER_UNIT);
		when(element.attr("href")).thenReturn(INVALID_PATH_URL + "/url");
		when(element.getElementsByClass(NUTRITION_LEVEL_CLASSS_ID)).thenReturn(nutritionElements);
		when(nutritionElement.getElementsByClass(NUTRITION_LEVEL_CLASSS_ID)).thenReturn(elements);
		when(nutritionElement.hasText()).thenReturn(Boolean.FALSE);
		when(nutritionElement.text()).thenReturn(TEST_CALORIES);
		when(nutritionElement.getElementsByClass(PRODUCT_TEXT_CLASS_ID)).thenReturn(priceElements);
		when(element.select("TD:contains(kcal)")).thenReturn(nutritionElements);
		when(jSoupClient.scrapeSiteForSpecifiedElementsClass(anyString(), anyString())).thenReturn(elements);
		// Excecute
		ScrapedData scrapedData = siteScraperService.scrapeScrape("VALID_URL");
		// Assert
		Assert.assertTrue(!scrapedData.getResults().isEmpty());
		Assert.assertTrue(scrapedData.getResults().size() == 1);
		Assert.assertTrue(scrapedData.getResults().get(0).getKcalPer100g() == null);
		Assert.assertTrue(StringUtils.isEmpty(scrapedData.getResults().get(0).getDescription()));
		Assert.assertTrue(scrapedData.getResults().get(0).getUnitPrice().compareTo(new BigDecimal(1.00)) == 0);
		Assert.assertTrue(scrapedData.getTotal().getGross().compareTo(new BigDecimal(1.00)) == 0);
		Assert.assertTrue(
				scrapedData.getTotal().getVat().compareTo(new BigDecimal(0.20).setScale(2, RoundingMode.DOWN)) == 0);
	}

	@Test
	public void testScrapeScrapeNoNutritionElementsFound() throws Exception {
		// Configure
		Elements elements = new Elements();
		elements.add(element);
		Elements priceElements = new Elements();
		Elements nutritionElements = new Elements();
		Elements priceElementParagraphElements = new Elements();
		priceElements.add(priceElement);
		priceElementParagraphElements.add(priceElementParagraph);
		when(element.getElementsByClass(PRODUCT_NAME_AND_PROMOTIONS_CLASS_ID)).thenReturn(elements);
		when(element.getElementsByClass(PRODUCT_NAME_AND_PROMOTIONS_CLASS_ID)).thenReturn(elements);
		when(element.getElementsByClass(PRICE_PER_UNIT_CLASS_ID)).thenReturn(elements);
		when(element.getElementsByTag(A_TAG)).thenReturn(elements);
		when(element.text()).thenReturn(ELEMENT_TEXT);
		when(element.hasText()).thenReturn(Boolean.FALSE);
		when(element.getElementsByClass(PRICE_PER_UNIT_CLASS_ID)).thenReturn(priceElements);
		when(priceElement.text()).thenReturn(PRICE_PER_UNIT);
		when(element.attr("href")).thenReturn(INVALID_PATH_URL + "/url");
		when(element.getElementsByClass(NUTRITION_LEVEL_CLASSS_ID)).thenReturn(nutritionElements);
		when(nutritionElement.getElementsByClass(NUTRITION_LEVEL_CLASSS_ID)).thenReturn(elements);
		when(nutritionElement.hasText()).thenReturn(Boolean.FALSE);
		when(nutritionElement.text()).thenReturn(TEST_CALORIES);
		when(nutritionElement.getElementsByClass(PRODUCT_TEXT_CLASS_ID)).thenReturn(priceElements);
		when(element.select("TD:contains(kcal)")).thenReturn(nutritionElements);
		
		when(jSoupClient.scrapeSiteForSpecifiedElementsClass(anyString(), anyString())).thenReturn(elements);
		// Excecute
		ScrapedData scrapedData = siteScraperService.scrapeScrape("VALID_URL");
		// Assert
		Assert.assertTrue(!scrapedData.getResults().isEmpty());
		Assert.assertTrue(scrapedData.getResults().size() == 1);
		Assert.assertTrue(scrapedData.getResults().get(0).getKcalPer100g() == null);
		Assert.assertTrue(scrapedData.getResults().get(0).getUnitPrice().compareTo(new BigDecimal(1.00)) == 0);
		Assert.assertTrue(scrapedData.getTotal().getGross().compareTo(new BigDecimal(1.00)) == 0);
		Assert.assertTrue(
				scrapedData.getTotal().getVat().compareTo(new BigDecimal(0.20).setScale(2, RoundingMode.DOWN)) == 0);
	}
}
