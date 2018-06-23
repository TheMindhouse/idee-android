package io.mindhouse.idee.utils

import android.text.Editable
import android.text.TextWatcher

/**
 * Created by kmisztal on 18/05/2018.
 *
 * @author Krzysztof Misztal
 */
abstract class SimpleTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}