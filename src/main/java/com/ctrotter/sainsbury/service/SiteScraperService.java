package com.ctrotter.sainsbury.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.naming.MalformedLinkException;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ctrotter.sainsbury.model.vo.Result;
import com.ctrotter.sainsbury.model.vo.ScrapedData;
import com.ctrotter.sainsbury.model.vo.Total;
import com.ctrotter.sainsbury.service.client.JSoupClient;

/**
 * @author ctrotter
 *
 */
@Service
/**
 * Service to extract data from supplied test url
 * 
 * @author Ctrotter
 *
 */
public class SiteScraperService {

	private static final String TD_CONTAINS_KCAL = "TD:contains(kcal)";

	private static final int TWO_DECIMAL_PLACES = 2;

	private static final double TWENTY_PERCENT = 0.2;

	private static final String HREF_TAG = "href";

	private static final String EMPTY_STRING = "";

	private static final String PER_UNIT = "/unit";

	private static final String PARAGRAPH = "p";

	private static final String POUND_SYMBOL = "\\u00A3";

	private static final String PRICE_PER_UNIT_CLASS_ID = "pricePerUnit";

	private static final String PRODUCT_TEXT_CLASS_ID = "productText";

	private static final String KCAL = "kcal";

	private static final String NUTRITION_LEVEL_CLASSS_ID = "nutritionLevel1";

	private static final String MAIN_PRODUCT_INFO_CLASS_ID = "mainProductInfo";

	private static final String VALID_PATH_URL = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/shop/gb/groceries/";

	private static final String INVALID_PATH_URL = "../../../../../../shop/gb/groceries/berries-cherries-currants/";

	private static final String A_TAG = "a";

	private static final String PRODUCT_NAME_AND_PROMOTIONS_CLASS_ID = "productNameAndPromotions";

	private static final String PRODUCT_CLASS_ID = "product";

	@Autowired
	private JSoupClient jSoupClient;

	/**
	 * Scrape the supplied url looking for specified elements.
	 * 
	 * @param siteToScrape
	 * @return {@link ScrapedData} object populated with supplied data.
	 * @throws IOException
	 * @throws MalformedLinkException
	 */
	public ScrapedData scrapeScrape(String siteToScrape) throws IOException, MalformedLinkException {
		Elements elements = jSoupClient.scrapeSiteForSpecifiedElementsClass(siteToScrape, PRODUCT_CLASS_ID);
		ScrapedData scrapedData = new ScrapedData();
		scrapedData.setResults(transformElement(elements));
		scrapedData.setTotal(calculateTotalsAndVAT(scrapedData.getResults()));
		return scrapedData;
	}

	/**
	 * Calculate Total and Vat for supplied set of results.
	 * 
	 * @param results
	 *            - List of scraped results.
	 * @return Total - representing totalled values
	 */
	private Total calculateTotalsAndVAT(List<Result> results) {
		Total total = new Total();
		if (!CollectionUtils.isEmpty(results)) {
			BigDecimal totalCost = new BigDecimal(0);
			for (Result result : results) {
				totalCost = totalCost.add(result.getUnitPrice());
			}
			total.setGross(totalCost);
			BigDecimal vat = totalCost.multiply(new BigDecimal(TWENTY_PERCENT)).setScale(TWO_DECIMAL_PLACES,
					RoundingMode.DOWN);
			total.setVat(vat);
		}
		return total;
	}

	/**
	 * Transform the supplied list of elements into list of List of REsult Elemets
	 * 
	 * @param elements
	 * @return List<Result> of transformed elements
	 * @throws IOException
	 *             thrown supplied url is invalid, or cannot be accessed.
	 * @throws MalformedLinkException
	 *             is supplied
	 */
	private List<Result> transformElement(Elements elements) throws IOException, MalformedLinkException {
		List<Result> resultList = new ArrayList<>();
		StringBuilder title;
		for (Element element : elements) {
			Result result = new Result();
			title = new StringBuilder();
			Element productAndPromationElement = extractFirstElement(element, PRODUCT_NAME_AND_PROMOTIONS_CLASS_ID);
			Elements hrefElement = productAndPromationElement.getElementsByTag(A_TAG);
			title.append(hrefElement.first().text());
			result.setTitle(title.toString());
			Element productNameAndPromotionTag = hrefElement.first();
			result.setTitle(extractElementText(productNameAndPromotionTag));
			Element unitPriceClassElement = extractFirstElement(element, PRICE_PER_UNIT_CLASS_ID);
			result.setUnitPrice(new BigDecimal(unitPriceClassElement.text().replace(PER_UNIT, EMPTY_STRING)
					.replaceAll(POUND_SYMBOL, EMPTY_STRING)));

			processSecendPage(result, productNameAndPromotionTag);
			resultList.add(result);
		}

		return resultList;
	}

	/**
	 * Extract href link and extract the required data.
	 * 
	 * @param result
	 *            - result object to be populated with additional data.
	 * @param productNameAndPromotionTag
	 *            - Tag containing the href link.
	 * @throws IOException
	 *             if url cannot be read.
	 */
	private void processSecendPage(Result result, Element productNameAndPromotionTag) throws IOException {
		Elements nutitionTable = jSoupClient.scrapeSiteForSpecifiedElementsClass(
				productNameAndPromotionTag.attr(HREF_TAG).replace(INVALID_PATH_URL, VALID_PATH_URL),
				MAIN_PRODUCT_INFO_CLASS_ID);
		Element nutritionRow = nutitionTable.first();

		if (null != nutritionRow.select(TD_CONTAINS_KCAL).first()
				&& nutritionRow.select(TD_CONTAINS_KCAL).first().hasText()) {
			result.setKcalPer100g(
					Integer.parseInt(nutritionRow.select(TD_CONTAINS_KCAL).first().text().replace(KCAL, EMPTY_STRING)));
		} 


		Elements productTexts = nutritionRow.getElementsByClass(PRODUCT_TEXT_CLASS_ID);
		result.setDescription(extractElementsText(productTexts, PARAGRAPH));
	}

	/**
	 * Find elements by supplied classid and extract the first element.
	 * 
	 * @param element
	 *            to be searched upon.
	 * @param classId
	 *            - to be searched for
	 * @return First element of found elements.
	 * @throws MalformedLinkException
	 *             - throw if elements cannot be found.
	 */
	private Element extractFirstElement(Element element, String classId) throws MalformedLinkException {
		Elements elements = element.getElementsByClass(classId);
		if (CollectionUtils.isEmpty(elements)) {
			throw new MalformedLinkException("Required HTML Elements not found :- " + classId);
		}
		return elements.first();
	}

	/**
	 * From the supplied list elements extract text.
	 * 
	 * @param elements
	 * @param elementToSelect
	 * @return
	 */
	private String extractElementsText(Elements elements, String elementToSelect) {
		StringBuilder result = new StringBuilder();
		if (!CollectionUtils.isEmpty(elements)) {
			Element element = elements.first();
			Elements selectedElements = element.select(elementToSelect);
			result.append(selectedElements.first().text());
		}
		return result.toString();
	}

	/**
	 * Extract text section from the supplied element.
	 * 
	 * @param element
	 *            - with text to be extracted.
	 * @return String representing text if supplied.
	 */
	private String extractElementText(Element element) {
		if (element.hasText()) {
			return element.text();
		}
		return EMPTY_STRING;
	}

	public JSoupClient getjSoupClient() {
		return jSoupClient;
	}

	public void setjSoupClient(JSoupClient jSoupClient) {
		this.jSoupClient = jSoupClient;
	}

}
