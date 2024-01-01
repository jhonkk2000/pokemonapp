package com.jhonkk.pokemonapp

import android.view.View
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.util.HumanReadables
import com.jhonkk.common.view.ProfilePicStatus
import com.jhonkk.common.view.ProfilePicView
import org.hamcrest.CoreMatchers.any
import org.hamcrest.Matcher
import java.util.concurrent.TimeoutException

class WaitUntilChangeStatePic(private val timeout: Long, private val status: ProfilePicStatus): ViewAction {

    override fun getConstraints(): Matcher<View> {
        return any(View::class.java)
    }

    override fun getDescription(): String {
        return "wait up to $timeout milliseconds for the view to be gone"
    }

    override fun perform(uiController: UiController, view: View) {

        val endTime = System.currentTimeMillis() + timeout
        val picView = view as ProfilePicView
        do {
            if (picView.status == status) return
            uiController.loopMainThreadForAtLeast(50)
        } while (System.currentTimeMillis() < endTime)

        throw PerformException.Builder()
            .withActionDescription(description)
            .withCause(TimeoutException("Waited $timeout milliseconds"))
            .withViewDescription(HumanReadables.describe(view))
            .build()
    }

}