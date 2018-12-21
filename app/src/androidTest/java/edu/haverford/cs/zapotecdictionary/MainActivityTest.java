package edu.haverford.cs.zapotecdictionary;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.INTERNET",
                    "android.permission.ACCESS_NETWORK_STATE",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void mainActivityTest() {
        ViewInteraction tabView = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        1),
                        isDisplayed()));
        tabView.perform(click());

        ViewInteraction tabView2 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        0),
                        isDisplayed()));
        tabView2.perform(click());

        ViewInteraction tabView3 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        2),
                        isDisplayed()));
        tabView3.perform(click());

        ViewInteraction tabView4 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        0),
                        isDisplayed()));
        tabView4.perform(click());

        ViewInteraction searchAutoComplete = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete.perform(replaceText("yar"), closeSoftKeyboard());

        DataInteraction constraintLayout = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(0);
        constraintLayout.perform(click());

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.searchWords_voiceE),
                        childAtPosition(
                                allOf(withId(R.id.searchWords_posE_layout),
                                        childAtPosition(
                                                withId(R.id.word_page),
                                                1)),
                                0),
                        isDisplayed()));
        imageButton.perform(click());

        ViewInteraction tabView5 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        1),
                        isDisplayed()));
        tabView5.perform(click());

        ViewInteraction tabView6 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        0),
                        isDisplayed()));
        tabView6.perform(click());

        ViewInteraction searchAutoComplete2 = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")), withText("yar"),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete2.perform(click());

        ViewInteraction searchAutoComplete3 = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")), withText("yar"),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete3.perform(replaceText(" yar"));

        ViewInteraction searchAutoComplete4 = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")), withText(" yar"),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete4.perform(closeSoftKeyboard());

        DataInteraction constraintLayout2 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(0);
        constraintLayout2.perform(click());

        ViewInteraction tabView7 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        1),
                        isDisplayed()));
        tabView7.perform(click());

        DataInteraction constraintLayout3 = onData(anything())
                .inAdapterView(allOf(withId(R.id.history_list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(0);
        constraintLayout3.perform(click());

        ViewInteraction imageButton2 = onView(
                allOf(withId(R.id.searchWords_voiceE),
                        childAtPosition(
                                allOf(withId(R.id.searchWords_posE_layout),
                                        childAtPosition(
                                                withId(R.id.word_page),
                                                1)),
                                0),
                        isDisplayed()));
        imageButton2.perform(click());

        ViewInteraction tabView8 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        2),
                        isDisplayed()));
        tabView8.perform(click());

        ViewInteraction tabView9 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        3),
                        isDisplayed()));
        tabView9.perform(click());

        ViewInteraction tabView10 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        0),
                        isDisplayed()));
        tabView10.perform(click());

        ViewInteraction imageView = onView(
                allOf(withClassName(is("android.widget.ImageView")), withContentDescription("Clear query"),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        imageView.perform(click());

        ViewInteraction imageButton3 = onView(
                allOf(withId(R.id.searchWords_voiceE),
                        childAtPosition(
                                allOf(withId(R.id.searchWords_posE_layout),
                                        childAtPosition(
                                                withId(R.id.word_page),
                                                1)),
                                0),
                        isDisplayed()));
        imageButton3.perform(click());

        ViewInteraction tabView11 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        1),
                        isDisplayed()));
        tabView11.perform(click());

        ViewInteraction linearLayout = onView(
                allOf(withContentDescription("Zunni, Navigate home"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.widget.ActionBarView")),
                                        childAtPosition(
                                                withClassName(is("com.android.internal.widget.ActionBarContainer")),
                                                0)),
                                0),
                        isDisplayed()));
        linearLayout.perform(click());

        ViewInteraction tabView12 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        0),
                        isDisplayed()));
        tabView12.perform(click());

        ViewInteraction searchAutoComplete5 = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete5.perform(click());

        ViewInteraction searchAutoComplete6 = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete6.perform(replaceText("good"), closeSoftKeyboard());

        DataInteraction constraintLayout4 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(0);
        constraintLayout4.perform(click());

        ViewInteraction imageButton4 = onView(
                allOf(withId(R.id.searchWords_voiceE),
                        childAtPosition(
                                allOf(withId(R.id.searchWords_posE_layout),
                                        childAtPosition(
                                                withId(R.id.word_page),
                                                1)),
                                0),
                        isDisplayed()));
        imageButton4.perform(click());

        ViewInteraction tabView13 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        2),
                        isDisplayed()));
        tabView13.perform(click());

        ViewInteraction tabView14 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("com.android.internal.widget.ScrollingTabContainerView")),
                                0),
                        0),
                        isDisplayed()));
        tabView14.perform(click());

        ViewInteraction imageView2 = onView(
                allOf(withClassName(is("android.widget.ImageView")), withContentDescription("Clear query"),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        imageView2.perform(click());
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
