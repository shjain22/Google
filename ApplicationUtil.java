package commons;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;

public class ApplicationUtil {


    /**
     * This class is for Pop ups specific to application and not web elements
     */
    private static final Logger logger = Logger.getLogger(ApplicationUtil.class.getName());

    private static Map<String,Boolean> retryErrors = new HashMap<String,Boolean>();
    private static int retryCount = 2;
    static{
        retryErrors.put(Constants.TXT_ACCESSORY_CACHE_ERROR,true);
    }

    /**
     * To Wait For Loading spinner completion
     *
     * @param webDriver WebDriver
     */
    public static void waitForLoadingSpinnerCompletion(WebDriver webDriver) {
        waitForLoadingSpinnerCompletion(webDriver, 30);
    }

    /**
     * To Wait For Loading spinner completion with given timeout
     *
     * @param webDriver WebDriver
     * @param timeoutInSeconds int timeout in seconds
     */
    public static void waitForLoadingSpinnerCompletion(WebDriver webDriver, int timeoutInSeconds) {
        long start = System.currentTimeMillis();
        WaitTypes.waitUntilJQuery(webDriver, timeoutInSeconds);
        WaitTypes.waitForAngularAjaxProcessingToComplete(webDriver,timeoutInSeconds);
        logExecutionTime("spinner verification", start, System.currentTimeMillis());
    }

    /**
     * close/continue popup window . Keep it generic if the need is only to close such popups.
     *
     * @param driver webDriver
     */
    public static void closeOrContinueMessageWindow(WebDriver driver) {
        if (SeleniumWebUtils.isElementPresent(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindow), driver)) {
            WaitTypes.waitFor(2000);
            String messageWindowText = driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindow)).getText();
            logger.info("messageWindowText: " + messageWindowText);
            if (messageWindowText.contains(Constants.TXT_INELIGIBLE_SKUS_ERROR)) {
                SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindowCloseBtn)), driver);
                logger.warn("NOTE: Device Sku selected is inEligible. Go to DeviceSelection and select different device and continue. If same result, halt the execution");
            } else if (messageWindowText.contains(Constants.TXT_DEVICE_NOT_ELIGIBLE_FOR_EIP)) {
                SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindowCloseBtn)), driver);
                logger.warn("NOTE: As Device is not EIP eligible, will be selecting non-EIP price.");
            } else if (messageWindowText.contains(Constants.TXT_SWITCH_TO_SHIP_TO_FULFILLMENT)) {
                SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindowContinueBtn)), driver);
                logger.info("Continued to Ship-To.");
            } else if (messageWindowText.contains(Constants.TXT_DEVICE_JOD_CONDITIONS)) {
                SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindowContinueBtn)), driver);
                logger.info("Continued to Add to Cart.");
            } else if (messageWindowText.contains(Constants.TXT_DELAYED_SHIPPING_WARNING)) {
                SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindowContinueBtn)), driver);
                logger.warn("NOTE: Continued past Delayed Shipping warning.");
            } else if (messageWindowText.contains(Constants.TXT_MULTI_DEVICE_VALIDATION)) {
                SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_deviceWillNotUpgradedModelCloseBtn)), driver);
                logger.warn("NOTE: Closing pop up of current device for upgrade and including next device in upgrade");
            } else if (messageWindowText.contains(Constants.TXT_EIP_BALANCE_PAYMENT_REQUIRED)) {
                SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindowOkBtn)), driver);
                logger.warn("NOTE: Closing pop up of EIP Balance Payment Required warning");
            } else if(messageWindowText.contains(Constants.TXT_DECLINE_DEVICEPROTECTION)){
                SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindowContinueBtn)), driver);
                logger.warn("NOTE: Continue the Declining Device Protection.");
            } else if(messageWindowText.contains(Constants.TXT_ACCESSORY_CACHE_ERROR)){
                SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindowCloseBtn)), driver);
                logger.warn("NOTE: Closing the popup of accessory cache error and it will work after refresh");
                driver.navigate().refresh();
            } else if(messageWindowText.contains(Constants.TXT_DEVICE_PROTECTION_WARNING)) {
                SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindowContinueBtn)), driver);
                logger.warn("NOTE: Device protection warning");
            }
            else {
                SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindowCloseBtn)), driver);
                logger.info("Popup window closed.");

                Assert.assertThat("TradeIn Check Service Error:", messageWindowText, not(containsString(Constants.TXT_TRADEIN_ERROR)));
                Assert.assertThat("Out of Stock Error:", messageWindowText, not(containsString(Constants.TXT_OUT_OF_STOCK_ERROR)));
                Assert.assertThat("Invalid Credit Card Error:", messageWindowText, not(containsString(Constants.TXT_INVALID_CC_INFO_WARNING)));
                Assert.assertThat("Processing Error:", messageWindowText, not(containsString(Constants.TXT_PROCESSING_ERROR)));
                Assert.assertThat("Service Exception:", messageWindowText, not(containsString(Constants.TXT_SERVICE_EXCEPTION)));
                Assert.assertThat(messageWindowText, messageWindowText, not(containsString(Constants.TXT_ASSOCTIATED_TO_MULTIPLE_IMEI)));

            }


        } else if (SeleniumWebUtils.isElementPresent(By.xpath(ElementIdentifierConstants.MODAL_POPUP_responsiveWindow), driver)) {
            WaitTypes.waitFor(2000);
            String messageWindowText = driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_responsiveWindow)).getText();
            logger.info("messageWindowText: " + messageWindowText);
            if (messageWindowText.contains(Constants.TXT_EIP_THRESHOLD)) {
                SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_eipThresholdCheckoutNowBtn)), driver);
                logger.warn("NOTE: Continued past EIP Threshold warning.");
            }
        }else if(SeleniumWebUtils.isElementPresent(By.xpath(ElementIdentifierConstants.MODAL_POPUP_WARNINGWINDOW),driver)){
            WaitTypes.waitFor(2000);
            String warningMessage = SeleniumWebUtils.read(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_WARNINGWINDOW)));
            logger.info("Warning message: "+warningMessage);
            SeleniumWebUtils.scrollClick(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_WARNINGWindowCloseBtn)),driver);
        }
    }

    /**
     * To close the BanLockPopup when its displayed.
     *
     * @param driver
     */
    public static boolean checkAndCloseBanLockPopup(WebDriver driver) {
        boolean banlock = false;
        if(SeleniumWebUtils.isElementPresent(By.xpath(ElementIdentifierConstants.MODAL_POPUP_BANLOCK),driver)){
            WebElement banlockPopup = driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_BANLOCK));
            if(!StringUtils.equals(banlockPopup.getAttribute("style"),"display: none;")){
                logger.info("Banlock popup is present on the page");
                logger.info("closing the banlock popup");
                driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_banLockOKBtn)).click();
                banlock = true;
            }else{
                logger.debug("Banlock popup is hiding");
            }
        }else{
            logger.info("Banlock popup in not present on the page");
        }
        return banlock;
    }


    public static boolean verifyProcessingError(WebDriver driver) {
        if (SeleniumWebUtils.isElementPresent(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindow), driver)) {
            WaitTypes.waitFor(2000);
            if(driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindow)).getText().contains(Constants.TXT_PROCESSING_ERROR))
                driver.findElement(By.xpath(ElementIdentifierConstants.MODAL_POPUP_messageWindowCloseBtn)).click();
            return true;
        }
        return false;
    }
    /**
     * To log the execution time.
     *
     * @param executionEntity
     * @param startTime
     * @param endTime
     */
    public static void logExecutionTime(String executionEntity, long startTime, long endTime) {
        logger.info("====Execution Time:" + (endTime - startTime) + " millis for executionEntity:" + executionEntity);
    }

    /**
     * To verify page presence
     *
     * @param webPage
     * @param identifier
     * @return boolean true if expected string present in the url, false otherwise
     */
    public static boolean verifyPage(WebPage webPage, String identifier) {
        waitForLoadingSpinnerCompletion(webPage.getDriver(),30);
        try {
            (new WebDriverWait(webPage.getDriver(), 30)).until(ExpectedConditions.urlContains(identifier));//IE fix
        }catch (TimeoutException te){
            logger.error("VerifyPage Failed for the identifier - "+identifier+" with error -"+te.getMessage());
            return false;
        }
        return SeleniumWebUtils.verify(webPage.getDriver().getCurrentUrl(), identifier);
    }

    /**
     * To verify page presence
     *
     * @param webDriver
     * @param identifier
     * @return boolean true if expected string present in the url, false otherwise
     */
    //Calling method should take care of handling this method call .....
    public static boolean verifyPage(WebDriver webDriver, String identifier) {
        waitForLoadingSpinnerCompletion(webDriver,30);//IE fix
        try {
            (new WebDriverWait(webDriver,30)).until(ExpectedConditions.urlContains(identifier));//IE fix
        }catch (TimeoutException te){
            logger.error("VerifyPage Failed for the identifier - "+identifier+" with error -"+te.getMessage());
            return false;
        }
        logger.info("verifying page for url - "+webDriver.getCurrentUrl()+" with -"+identifier);
        return SeleniumWebUtils.verify(webDriver.getCurrentUrl(), identifier);
    }
}
