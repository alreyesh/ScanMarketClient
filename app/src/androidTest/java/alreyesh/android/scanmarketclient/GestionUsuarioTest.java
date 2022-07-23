package alreyesh.android.scanmarketclient;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import alreyesh.android.scanmarketclient.activities.MainActivity;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class GestionUsuarioTest {
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void gestionUsuarioTest(){
        try {
            onView(withId(android.R.id.home)).perform(click());
        } catch (NoMatchingViewException e) {

            onView(withContentDescription(R.string.burger_descripcion)).perform(click());
        }

        onView(withId( R.id.my_info)).perform(click());
        onView(withId(R.id.editTextNombre)).perform(ViewActions.typeText("Alfonso"), closeSoftKeyboard());
        onView(withId(R.id.editTextApellidos)).perform(ViewActions.typeText("Reyes"), closeSoftKeyboard());
        onView(withId(R.id.editTextCelular)).perform(ViewActions.typeText("992563710"), closeSoftKeyboard());
        onView(withId(R.id.editNumDocumento)).perform(ViewActions.typeText("72437169"), closeSoftKeyboard());
        onView(withId(R.id.spinnerTipoDocumento)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("DNI"))).perform(click());
        onView(withId(R.id.spinnerTipoDocumento)).check(matches(withSpinnerText(containsString("DNI"))));

        onView(withId(R.id.btnGrabar)).perform(click());



    }


}
