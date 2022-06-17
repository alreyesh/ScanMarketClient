package alreyesh.android.scanmarketclient.Splash;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
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

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ListaCompraCarritoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void listaCompraCarritoTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support'-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        ViewInteraction appCompatImageButton = onView( allOf(withContentDescription("Burger"),  childAtPosition( allOf(withId(R.id.toolbar), childAtPosition(withClassName(is("android.widget.LinearLayout")), 0)), 1), isDisplayed()));
        appCompatImageButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(160);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.menu_list_purchase)).perform(click());
        onView(withId( R.id.add_list_purchase)).perform(click());

        ViewInteraction appCompatEditText = onView(allOf(withId(R.id.editNameList), childAtPosition(allOf(withId(R.id.ListRegistrarPurchase), childAtPosition(withId(android.R.id.custom), 0)), 1), isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(allOf(withId(R.id.editNameList), childAtPosition(allOf(withId(R.id.ListRegistrarPurchase), childAtPosition(withId(android.R.id.custom), 0)), 1), isDisplayed()));
        appCompatEditText2.perform(replaceText("ff"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.editLimitList), childAtPosition(allOf(withId(R.id.ListRegistrarPurchase), childAtPosition(withId(android.R.id.custom), 0)), 2), isDisplayed()));
        appCompatEditText3.perform(replaceText("58.68"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(allOf(withId(R.id.editLimitList), withText("58.68"), childAtPosition(allOf(withId(R.id.ListRegistrarPurchase), childAtPosition(withId(android.R.id.custom), 0)), 2), isDisplayed()));
        appCompatEditText4.perform(pressImeActionButton());

        ViewInteraction appCompatButton2 = onView(allOf(withId(R.id.btnRegistrarEditPurchase), withText("Registrar"), childAtPosition(childAtPosition(withId(R.id.ListRegistrarPurchase), 5), 0), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction recyclerView = onView(allOf(withId(R.id.recyclerView), childAtPosition(withId(R.id.fragment_list_purchase), 0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        try {
            Thread.sleep(160);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction appCompatImageButton3 = onView(allOf(withContentDescription("Burger"), childAtPosition(allOf(withId(R.id.toolbar), childAtPosition(withClassName(is("android.widget.LinearLayout")), 0)), 1), isDisplayed()));
        appCompatImageButton3.perform(click());

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId( R.id.menu_products)).perform(click());

        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        DataInteraction cardView = onData(anything()).inAdapterView(allOf(withId(R.id.am_gv_gridview), childAtPosition(withClassName(is("android.widget.RelativeLayout")), 0))).atPosition(0);
        cardView.perform(click());

        ViewInteraction appCompatEditText5 = onView(allOf(withId(R.id.editCantidad), childAtPosition(childAtPosition(withClassName(is("android.widget.LinearLayout")), 0), 1), isDisplayed()));
        appCompatEditText5.perform(click());

        ViewInteraction appCompatEditText6 = onView(allOf(withId(R.id.editCantidad), childAtPosition(childAtPosition(withClassName(is("android.widget.LinearLayout")), 0), 1), isDisplayed()));
        appCompatEditText6.perform(replaceText("5"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(allOf(withId(R.id.btnRegistrarEdit), withText("Agregar"), childAtPosition(childAtPosition(withClassName(is("android.widget.LinearLayout")), 1), 1), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction cartCounterActionView = onView(
                allOf(withId(R.id.action_addcart), withContentDescription("Add Cart"), childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 3), isDisplayed()));
        cartCounterActionView.perform(click());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.recyclerViewCart), childAtPosition(withId(R.id.fragment_list_cart), 0)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction appCompatEditText7 = onView(allOf(withId(R.id.editCantidad), childAtPosition(childAtPosition(withClassName(is("android.widget.LinearLayout")), 0), 1), isDisplayed()));
        appCompatEditText7.perform(click());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.editCantidad), childAtPosition(childAtPosition(withClassName(is("android.widget.LinearLayout")), 0), 1), isDisplayed()));
        appCompatEditText8.perform(replaceText("6"), closeSoftKeyboard());

        ViewInteraction appCompatButton4 = onView(allOf(withId(R.id.btnRegistrarEdit), withText("Agregar"), childAtPosition(childAtPosition(withClassName(is("android.widget.LinearLayout")), 1), 1), isDisplayed()));
        appCompatButton4.perform(click());
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
