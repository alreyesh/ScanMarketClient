package alreyesh.android.scanmarketclient;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import alreyesh.android.scanmarketclient.activities.MainActivity;
import alreyesh.android.scanmarketclient.utils.Util;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LimitAlertTest {
    private SharedPreferences prefs;

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void limitAlertTest(){
        Context mContext = InstrumentationRegistry.getContext();
        prefs = mContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
            onView(withId(android.R.id.home)).perform(click());
        } catch (NoMatchingViewException e) {
            //openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
            onView(withContentDescription(R.string.burger_descripcion)).perform(click());
        }

        onView(withId( R.id.menu_products)).perform(click());
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //producto1
        onData(anything()).inAdapterView(withId(R.id.am_gv_gridview)).atPosition(0).
                onChildView(withId(R.id.imgProductView)).perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.editCantidad)).perform(ViewActions.typeText("1"), closeSoftKeyboard());
        onView(withId( R.id.btnRegistrarEdit)).perform(click());
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //producto2
        onData(anything()).inAdapterView(withId(R.id.am_gv_gridview)).atPosition(1).
                onChildView(withId(R.id.imgProductView)).perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.editCantidad)).perform(ViewActions.typeText("2"), closeSoftKeyboard());
        onView(withId( R.id.btnRegistrarEdit)).perform(click());
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //producto3
        onData(anything()).inAdapterView(withId(R.id.am_gv_gridview)).atPosition(2).
                onChildView(withId(R.id.imgProductView)).perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.editCantidad)).perform(ViewActions.typeText("2"), closeSoftKeyboard());
        onView(withId( R.id.btnRegistrarEdit)).perform(click());
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String u = Util.getTotalCart(prefs);
        onView(withId(R.id.shopping_total))
                .check(matches(withText("S/. 1011.0")));
    }



}
