import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;

/*
 * Script to test if flight tracker module works as expected
 * */
public class FlightTracker {

	//Global Variables
	private static AndroidDriver<WebElement> driver;
	private static Properties testData;
	private static DesiredCapabilities capabilities;

	//Function to setup the project and environment
	private static void setUp() {
		try {
			capabilities = DesiredCapabilities.android();
			capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
			capabilities.setCapability("deviceName", "MyAndroid");
			capabilities.setCapability("platformVersion", "5.0.2");
			capabilities.setCapability("platformName", "Android");

			testData = new Properties();
			testData.load(new FileInputStream("testdata/flightData.properties"));
			capabilities.setCapability("appPackage", "com.kayak.android");
			capabilities.setCapability("appActivity", "com.kayak.android.Splash");

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	 //Get all keys from the properties file
	  public static HashMap<String,String> getAllKeys() {
	    Set<Object> keys = testData.keySet();
	    HashMap<String,String> airLineVsFlightNo = new HashMap<>();
	    
	    for(Object key: keys) {
	    	String keyStr = (String)key;
	    	String value = testData.getProperty(keyStr);
	    	airLineVsFlightNo.put(keyStr, value);
	    }
	    return airLineVsFlightNo;
	  }

	public static void main(String[] args) {
		
		setUp();
		
		try {
			
			HashMap<String,String> airLineFlight = getAllKeys();
			for (String airLine : airLineFlight.keySet()) {
				driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
				driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				String flightNo = airLineFlight.get(airLine);
				System.out.println("Airline: " + airLine + " FlightNo: " + flightNo);
				WebElement e = driver.findElementByAndroidUIAutomator("new UiSelector().description(\"Open navigation drawer\")");
				e.click();

				// Flight Tracker
				WebElement flightTracker = driver.findElementByAndroidUIAutomator("new UiSelector().text(\"Flight Tracker\")");
				flightTracker.click();

				WebElement addFlight = driver.findElementByAndroidUIAutomator("new UiSelector().resourceId(\"com.kayak.android:id/add\")");
				addFlight.click();

				WebElement airline = driver.findElementByAndroidUIAutomator("new UiSelector().resourceId(\"com.kayak.android:id/text\")");
				airline.click();

				//Enter Airline Details
				WebElement airlineSelection = driver.findElementByAndroidUIAutomator("new UiSelector().resourceId(\"com.kayak.android:id/airlinesFilterText\")");
				airlineSelection.sendKeys(airLine);

				//Look for airline and select it
				List<WebElement> listOfAirlines = driver.findElementsByClassName("android.widget.LinearLayout");
				for (WebElement elem : listOfAirlines) {
					String airlineName = elem.findElement(By.className("android.widget.TextView")).getText();
					//System.out.println(airlineName);
					if (airlineName.contains(airLine)) {
						elem.click();
						break;
					}
				}

				//Enter flight no details
				WebElement flightRowNo = driver.findElementByAndroidUIAutomator("new UiSelector().resourceId(\"com.kayak.android:id/flightNumberRow\")");
				flightRowNo.click();

				WebElement flightNoFocussed = driver.findElement(By.className("android.widget.EditText"));
				flightNoFocussed.sendKeys(flightNo);

				//Click on the find flights button
				WebElement findFlightButton = driver.findElementByName("Find Flights");
				findFlightButton.click();

				//If flight results are found, check if it is ariving or departs
				try{
					WebElement status = driver.findElementByAndroidUIAutomator("new UiSelector().resourceId(\"com.kayak.android:id/status\")");
					if (status.getText().contains("Arrive") || status.getText().contains("Departs")) {
						System.out.println("Testcase Passed!");
					}
				} catch(NoSuchElementException ex) {
					//If flight not found, check if the list if empty
					System.out.println("No Flight found!");
					System.out.println("Checking if list is empty..");
					List<WebElement> listOfFlights = driver.findElementsByClassName("android.widget.LinearLayout");
					if(listOfFlights.size()==3) {
						System.out.println("No Search Results for airline..");
						System.out.println("Testcase Passed!");	
					} else {
						System.out.println("Test Case Failed!");
					}
					//android.widget.FrameLayout				
				}
				driver.quit();
			}

		} catch (Exception ex){
			driver.quit();
			System.out.println("Unexpected error occured!");
			//ex.printStackTrace();
		} finally{
			driver.quit();
		}
	}
}