package io.mindhouse.idee.ui.widget

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.EditText
import timber.log.Timber


/**
 * An EditText that lets you use actions ("Done", "Go", etc.) on multi-line edits.
 */
class ActionEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : EditText(context, attrs, defStyleAttr, defStyleRes) {

    init {
        //For some reason it does't read TextAppearance
        try {
            val ta = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.fontFamily), defStyleAttr, defStyleRes)
            if (ta.hasValue(0)) {
                val id = ta.getResourceId(0, 0)
                val font = ResourcesCompat.getFont(context, id)
                typeface = font
            }

            ta.recycle()

        } catch (e: Exception) {
            Timber.e(e, "Error initializing ActionEditText")
        }

    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val conn = super.onCreateInputConnection(outAttrs)
        outAttrs.imeOptions = outAttrs.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION.inv()
        return conn
    }
}