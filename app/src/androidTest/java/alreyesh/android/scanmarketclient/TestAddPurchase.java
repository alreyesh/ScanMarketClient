package alreyesh.android.scanmarketclient;

import android.os.Build;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import alreyesh.android.scanmarketclient.Activities.MainActivity;


@Config(sdk = Build.VERSION_CODES.P)
public class TestAddPurchase {


    @Test
    public void TestAddPurchase()  {
  /*      System.out.println("Creación del driver");
        AndroidDriver<AndroidElement> driver = null;
        DesiredCapabilities dc = new DesiredCapabilities();

        dc.setCapability("platformName","android");
        dc.setCapability("appActivity",".Splash.SplashActivity");
        dc.setCapability("appPackage","alreyesh.android.scanmarketclient");

        //dc.setCapability("appium:uiautomator2ServerInstallTimeout","90000");

       try {
          driver= new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"),dc);

       } catch (MalformedURLException e){
           e.printStackTrace();
       }


        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        System.out.println("Driver creado!");
        System.out.println("Ingresando a la aplicación....");

        MobileElement el1 =  driver.findElementByAccessibilityId("Burger");
        el1.click();
        MobileElement el2 = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/androidx.drawerlayout.widget.DrawerLayout/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/androidx.appcompat.widget.LinearLayoutCompat[3]/android.widget.CheckedTextView");
        el2.click();
        MobileElement el3 = (MobileElement) driver.findElementByAccessibilityId("AddPurchase");
        el3.click();
        MobileElement el4 = (MobileElement) driver.findElementById("alreyesh.android.scanmarketclient:id/editNameList");
        el4.click();
        el4.sendKeys("Compras Mensuales");
        MobileElement el5 = (MobileElement) driver.findElementById("alreyesh.android.scanmarketclient:id/editLimitList");
        el5.click();
        el5.sendKeys("520.50");
        MobileElement el6 = (MobileElement) driver.findElementById("alreyesh.android.scanmarketclient:id/color_select_button");
        el6.click();
        MobileElement el7 = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/androidx.appcompat.widget.LinearLayoutCompat/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.widget.FrameLayout[6]");
        el7.click();
        MobileElement el8 = (MobileElement) driver.findElementById("android:id/button1");
        el8.click();
        MobileElement el9 = (MobileElement) driver.findElementById("alreyesh.android.scanmarketclient:id/icon_select_button");
        el9.click();
        MobileElement el10 = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/androidx.recyclerview.widget.RecyclerView/android.widget.ImageView[28]");
        el10.click();
        MobileElement el11 = (MobileElement) driver.findElementById("alreyesh.android.scanmarketclient:id/icd_btn_select");
        el11.click();
        MobileElement el12 = (MobileElement) driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout[3]/android.widget.Button[1]");
        el12.click();

        try {
         Thread.sleep(50000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }


        Assert.assertEquals(driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/androidx.drawerlayout.widget.DrawerLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.widget.FrameLayout[1]/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.TextView"),"Compras Mensuales");

        driver.quit();

*/
    }





}

