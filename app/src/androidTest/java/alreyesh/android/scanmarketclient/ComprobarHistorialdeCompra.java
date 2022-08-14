package alreyesh.android.scanmarketclient;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import static alreyesh.android.scanmarketclient.TestUtils.withRecyclerView;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import alreyesh.android.scanmarketclient.activities.MainActivity;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ComprobarHistorialdeCompra {
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void comprobarHistorialdeCompra(){
        try {
            onView(withId(android.R.id.home)).perform(click());
        } catch (NoMatchingViewException e) {

            onView(withContentDescription(R.string.burger_descripcion)).perform(click());
        }
        onView(withId( R.id.menu_shop)).perform(click());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withRecyclerView(R.id.recyclerhistory)
                .atPositionOnView(0, R.id.textCodOrder))
                .check(matches(withText("cod: 3u276n51")));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withRecyclerView(R.id.recyclerhistory)
                .atPositionOnView(0, R.id.textTotalOrder))
                .check(matches(withText("total: S/.386.4")));



    }




}
