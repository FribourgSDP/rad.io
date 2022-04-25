package com.github.fribourgsdp.radio.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

class CustomMatchers {
    companion object {
        fun atPosition(position: Int, itemId: Int, itemMatcher: Matcher<View?>): Matcher<View?> {
            return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
                override fun describeTo(description: Description) {
                    // Update the description of the matcher
                    description.appendText("Item at position $position: ")
                    itemMatcher.describeTo(description)
                }

                override fun matchesSafely(view: RecyclerView): Boolean {
                    val viewHolder = view.findViewHolderForAdapterPosition(position)
                    // if null:
                        ?: return false

                    return itemMatcher.matches(
                        viewHolder.itemView.findViewById(itemId)
                    )
                }
            }
        }
    }
}