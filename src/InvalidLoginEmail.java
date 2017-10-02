
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;

public class InvalidLoginEmail {
	private static AndroidDriver<WebElement> driver;

	private static Properties testData;
	private static Properties capabilitiesValues;
	/*
	 * Initial set-up to set the emulator
	 * Mapping the required desired capabilities
	 * Please check if the URL port matches the APPIUM port default port is :4723
	 */
	@Before
    public void setUp() throws FileNotFoundException, IOException{
	
	capabilitiesValues  = new Properties();
	capabilitiesValues.load(new FileInputStream("testdata/capabilities.properties"));
	DesiredCapabilities capabilities=DesiredCapabilities.android();
	
	capabilities.setCapability(CapabilityType.BROWSER_NAME,"");
	capabilities.setCapability("deviceName", capabilitiesValues.getProperty("deviceName"));
	capabilities.setCapability("platformVersion", capabilitiesValues.getProperty("platformVersion"));
	capabilities.setCapability("platformName","Android");

	testData = new Properties();
    testData.load(new FileInputStream("testdata/credentials.properties"));
    capabilities.setCapability("appPackage","com.kayak.android");
    capabilities.setCapability("appActivity","com.kayak.android.Splash");
    driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
	driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	/*
	 * Written By:Madhumita
	 * Date:20th Nov 2016
	 * Test For Invalid Login using email-log in  option
	 */
	
	@Test
    public void testInvalidLogin() {
		driver.findElementByName("Sign in with your email").click();
		//read number of test scripts to be executed
		int noOfTests = Integer.parseInt(testData.getProperty("noofscripts"));
		System.out.println("Total Test Cases::"+noOfTests);
		while(noOfTests > 0){
			System.out.println("Test CASE:"+noOfTests);
			//Set the input field for email from the properties file
			driver.findElementByName("Save up to 35% with hotel deals").sendKeys(testData.getProperty("name"+noOfTests));
			//Set the input field for Password from the properties file
			driver.findElementByName("Get flight status alerts").sendKeys(testData.getProperty("pass"+noOfTests));
			//Set the preference for email option(yes /no)
			String email=testData.getProperty("email"+noOfTests);
		
			String ischecked=driver.findElementById("com.kayak.android:id/preferences_signup_deal").getAttribute("checked");
			//Uncheck the default selected "Email me deals" if the value is set as no in the properties file
			if(email.equalsIgnoreCase("no") && ischecked.equals("true"))
				{
			
				driver.findElementById("com.kayak.android:id/preferences_signup_deal").click();
				
				}
			//Check the default selected "Email me deals" if the value is set as yes in the properties file
			if(email.equalsIgnoreCase("yes") && ischecked.equals("false"))
				{
			
				driver.findElementById("com.kayak.android:id/preferences_signup_deal").click();
				
				}
			//hide Keyboard
			driver.hideKeyboard();
			driver.findElementByName("Sign Up").click();
			String msg=driver.findElement(By.id("android:id/message")).getText().toString();
			System.out.println("Invalid Error messsage:"+msg);
			
			driver.findElementByName("OK").click();
			System.out.println("In-Valid Login Test Pass for credentials:"+noOfTests);
			noOfTests--;
		}
	}
	
	@After
    public void closureActivities(){
        driver.quit();
    }
	}
	

