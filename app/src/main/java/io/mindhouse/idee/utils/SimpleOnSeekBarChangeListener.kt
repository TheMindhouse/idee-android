package io.mindhouse.idee.utils

import android.widget.SeekBar

/**
 * Created by kmisztal on 30/06/2018.
 *
 * @author Krzysztof Misztal
 */
abstract class SimpleOnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
}