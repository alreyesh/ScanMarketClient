package alreyesh.android.scanmarketclient;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
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
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import alreyesh.android.scanmarketclient.activities.MainActivity;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class AgregarListadoPorDefectoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);
/*
    @Before
    public void beforetest(){
       ActivityScenario<MainActivity> escenario = mActivityScenarioRule.getScenario();
       escenario.moveToState(Lifecycle.State.RESUMED);
    }
*/

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
        appCompatEditText2.perform(replaceText("compra del dia"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.editLimitList),
                        childAtPosition(
                                allOf(withId(R.id.ListRegistrarPurchase),
                                        childAtPosition(
                                                withId(android.R.id.custom),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("67.8"), closeSoftKeyboard());


        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btnRegistrarEditPurchase), withText("Registrar"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ListRegistrarPurchase),
                                        5),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction textView = onView(
                allOf(withId(R.id.textViewName), withText("compra del dia"),
                        withParent(withParent(withId(R.id.listViewPurchase))),
                        isDisplayed()));
        textView.check(matches(withText("compra del dia")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textViewLimit), withText("S/. 67.8"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("S/. 67.8")));



    }

    public static Matcher<View> childAtPosition(
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
