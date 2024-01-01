package com.jhonkk.pokemonapp

import android.app.Application
import android.app.Instrumentation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.util.Preconditions
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.EmptyFragmentActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.runner.AndroidJUnitRunner
import com.jhonkk.common.view.ProfilePicStatus
import com.jhonkk.common.view.ProfilePicView
import com.jhonkk.profilepic.ProfilePicFragment
import com.jhonkk.profilepic.R
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@CustomTestApplication(BaseApplication::class)
interface HiltTestApplication

class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return Instrumentation.newApplication(HiltTestApplication_Application::class.java, context)
    }
}

@ExperimentalCoroutinesApi
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    themeResId: Int = androidx.fragment.testing.manifest.R.style.FragmentScenarioEmptyFragmentActivityTheme,
    fragmentFactory: FragmentFactory? = null,
    crossinline action: T.() -> Unit = {}
) {
    val mainActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    ).putExtra(EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY, themeResId)

    ActivityScenario.launch<HiltTestActivity>(mainActivityIntent).onActivity { activity ->
        fragmentFactory?.let {
            activity.supportFragmentManager.fragmentFactory = it
        }
        val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        fragment.arguments = fragmentArgs

        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, fragment, "")
            .commitNow()

        (fragment as T).action()
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class ProfilePicFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun checkProfilePicViewLikeText() {
        launchFragmentInHiltContainer<ProfilePicFragment> {

        }
        onView(ViewMatchers.withId(R.id.et_name)).perform(ViewActions.typeText("MyName"))
        onView(ViewMatchers.withId(R.id.et_url)).perform(ViewActions.typeText(""))
        closeSoftKeyboard()
        onView(ViewMatchers.withId(R.id.profile_pic_view)).check(ViewAssertions.matches(object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
            }
            override fun matchesSafely(item: View?): Boolean {
                val picView = item as ProfilePicView
                return picView.status == ProfilePicStatus.TEXT
            }
        }))
    }

    @Test
    fun checkProfilePicViewLikeImage() {
        launchFragmentInHiltContainer<ProfilePicFragment> {

        }
        onView(ViewMatchers.withId(R.id.et_name)).perform(ViewActions.typeText(""))
        onView(ViewMatchers.withId(R.id.et_url)).perform(ViewActions.typeText("https://img.favpng.com/14/0/3/standard-test-image-acceptance-testing-png-favpng-Yy97FH3TSHfV906y3fKtRFFzL.jpg"))
        closeSoftKeyboard()
        onView(ViewMatchers.withId(R.id.profile_pic_view)).check(ViewAssertions.matches(object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
            }
            override fun matchesSafely(item: View?): Boolean {
                val picView = item as ProfilePicView
                return picView.status == ProfilePicStatus.IMAGE
            }
        }))
    }

    @Test
    fun checkProfilePicViewLikePlaceholder() {
        launchFragmentInHiltContainer<ProfilePicFragment> {

        }
        onView(ViewMatchers.withId(R.id.et_name)).perform(ViewActions.typeText(""))
        onView(ViewMatchers.withId(R.id.et_url)).perform(ViewActions.typeText(""))
        closeSoftKeyboard()
        onView(ViewMatchers.withId(R.id.profile_pic_view)).check(ViewAssertions.matches(object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
            }
            override fun matchesSafely(item: View?): Boolean {
                val picView = item as ProfilePicView
                return picView.status == ProfilePicStatus.PLACEHOLDER
            }
        }))
    }
}