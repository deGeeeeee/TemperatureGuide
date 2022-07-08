package ch.zli.TempGuide;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.tempView), withText("15.0C"),
                        withParent(allOf(withId(R.id.background),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction button = onView(
                allOf(withId(R.id.SettingsButton), withText("SETTINGS"),
                        withParent(allOf(withId(R.id.background),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction viewGroup = onView(
                allOf(withId(R.id.background),
                        withParent(allOf(withId(android.R.id.content),
                                withParent(withId(androidx.appcompat.R.id.decor_content_parent)))),
                        isDisplayed()));
        viewGroup.check(matches(isDisplayed()));
    }

    @Test
    public void changeText_newActivity() {

        onView(withId(R.id.SettingsButton)).perform(click());

    }
}
