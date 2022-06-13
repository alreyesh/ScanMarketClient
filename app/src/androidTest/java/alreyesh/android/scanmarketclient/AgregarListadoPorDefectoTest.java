package alreyesh.android.scanmarketclient;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import alreyesh.android.scanmarketclient.Activities.MainActivity;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Splash.SplashActivity;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AgregarListadoPorDefectoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void agregarListadoPorDefectoTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
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

        //onView(withId(android.R.id.home)).perform(click());
        onView(withId(R.id.menu_list_purchase)).perform(click());
        onView(withId( R.id.add_list_purchase)).perform(click());
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editNameList),
                        childAtPosition(
                                allOf(withId(R.id.ListRegistrarPurchase),
                                        childAtPosition(
                                                withId(android.R.id.custom),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editNameList),
                        childAtPosition(
                                allOf(withId(R.id.ListRegistrarPurchase),
                                        childAtPosition(
                                                withId(android.R.id.custom),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("pppp"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.editLimitList),
                        childAtPosition(
                                allOf(withId(R.id.ListRegistrarPurchase),
                                        childAtPosition(
                                                withId(android.R.id.custom),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("588.4"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.editLimitList), withText("588.4"),
                        childAtPosition(
                                allOf(withId(R.id.ListRegistrarPurchase),
                                        childAtPosition(
                                                withId(android.R.id.custom),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText4.perform(pressImeActionButton());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btnRegistrarEditPurchase), withText("Registrar"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ListRegistrarPurchase),
                                        5),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
