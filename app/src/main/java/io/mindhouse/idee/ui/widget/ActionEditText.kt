package io.mindhouse.idee.ui.widget

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection


/**
 * An EditText that lets you use actions ("Done", "Go", etc.) on multi-line edits.
 */
class ActionEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : TextInputEditText(context, attrs, defStyleAttr) {

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val conn = super.onCreateInputConnection(outAttrs)
        outAttrs.imeOptions = outAttrs.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION.inv()
        return conn
    }
}