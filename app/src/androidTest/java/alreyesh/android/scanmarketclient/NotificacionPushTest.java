package alreyesh.android.scanmarketclient;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import alreyesh.android.scanmarketclient.activities.MainActivity;
@LargeTest
@RunWith(AndroidJUnit4.class)
public class NotificacionPushTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);
    @Test
    public void  notificacionpushtest(){
        onView(withId(R.id.txtResultado))
                .check(matches(withText("resultado")));
    }

}
