package io.mindhouse.idee.ui.widget

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.ImageView
import io.mindhouse.idee.R


/**
 * Custom ic_loader.
 */
class IdeeLoader @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private lateinit var animatable: AnimatedVectorDrawable

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animatable = ContextCompat.getDrawable(context, R.drawable.animated_loader) as AnimatedVectorDrawable
        setImageDrawable(animatable)
        animatable.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animatable.stop()
    }
}