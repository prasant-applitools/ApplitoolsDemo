package com.applitools.example;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;

public class ApplitoolsDemo {

	private static VisualGridRunner vgRunner = null;
	private static Eyes eyes = null;
	private static String API_KEY = null;
	private static String BATCH_NAME = null;
	private static String BATCH_ID = null;
	private WebDriver webDriver = null;
	private static BatchInfo BATCH = null;

	@BeforeAll
	public static void beforeAll() {
		
		API_KEY = System.getenv("APPLITOOLS_API_KEY");
		assertNotNull(API_KEY,"API Key is required!!");
		
		BATCH_NAME = System.getenv("APPLITOOLS_BATCH_NAME");
		
		if(BATCH_NAME ==null || BATCH_NAME.isEmpty())
			BATCH_NAME = "Default Batch";
		
		BATCH_ID = System.getenv("APPLITOOLS_BATCH_ID");
		
		if(BATCH_ID ==null || BATCH_ID.isEmpty()) {
			Date now = new Date();
			BATCH_ID = now.toString();
		}
		
		vgRunner = new VisualGridRunner(10);
		eyes = new Eyes(vgRunner);
		BATCH = new BatchInfo(BATCH_NAME);
		BATCH.setId(BATCH_ID);
	}

	@BeforeEach
	public void setup() {

		Configuration config = eyes.getConfiguration();

		config.setApiKey(API_KEY);

		config.setLayoutBreakpoints(true);

		// Read the headless mode setting from an environment variable.
		// Use headless mode for Continuous Integration (CI) execution.
		// Use headed mode for local development.
		boolean headless = Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS", "false"));

		// Create a new batch for tests.
		// A batch is the collection of visual tests.
		// Batches are displayed in the dashboard, so use meaningful names.
		config.setBatch(BATCH);

		// Add 3 desktop browsers with different viewports for cross-browser testing in
		// the Ultrafast Grid.
		// Other browsers are also available, like Edge and IE.
		config.addBrowser(800, 600, BrowserType.CHROME);
		config.addBrowser(1600, 1200, BrowserType.CHROME);
		config.addBrowser(1920, 1080, BrowserType.CHROME);

		eyes.setConfiguration(config);

		ChromeOptions chromeOption = new ChromeOptions();
		chromeOption.addArguments("--remote-allow-origins=*");
		chromeOption.setHeadless(false);

		this.webDriver = new ChromeDriver(chromeOption);

		eyes.open(this.webDriver, // WebDriver object to "watch"
				"Housing.com_2", // The name of the app under test
				"Housing_selenium", // The name of the test case
				new RectangleSize(1360, 800));
	}

	@Test
	public void applitoolsDemo() throws InterruptedException {
		// Load the home page.
		this.webDriver.get("https://housing.com/buy-real-estate-nashik");

		Thread.sleep(2000);

		try {

			WebElement floatingElem = this.webDriver.findElement(By.xpath("//video[@class='css-orcsns']/.."));

			String rmCommand = "arguments[0].remove();";

			((JavascriptExecutor) this.webDriver).executeScript(rmCommand, floatingElem);

		} catch (Exception e) {

		}

		eyes.check(Target.window().lazyLoad().matchLevel(MatchLevel.STRICT)
//        		.ignore(null)
//        		.layout(xyz)

				.fully().withName("Landing Page - Buy"));

		WebDriverWait wait = new WebDriverWait(this.webDriver, Duration.ofSeconds(30));

		WebElement topLocality = wait
				.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("section[data-q='topLocalities']")));

		String scrCommand = "arguments[0].scrollIntoView(true);";

		((JavascriptExecutor) this.webDriver).executeScript(scrCommand, topLocality);

		eyes.check(Target.region(topLocality).withName("Top Locality - Nasik"));
	}

	@AfterEach
	public void tearDown() {
		if (eyes != null) {
			eyes.closeAsync();
		}

		if (this.webDriver != null) {
			this.webDriver.quit();
		}
	}

	@AfterAll
	public static void onComplete() {
		if (vgRunner != null) {
			TestResultsSummary allTestResults = vgRunner.getAllTestResults();
			System.out.println(allTestResults);
		}
	}

}
