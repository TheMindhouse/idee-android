package io.mindhouse.idee.ui.dialog

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.mindhouse.idee.R
import kotlinx.android.synthetic.main.fragment_dialog.*


/**
 * Created by kmisztal on 11/07/2018.
 *
 * @author Krzysztof Misztal
 */
class IdeeDialog : DialogFragment() {

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val KEY_POSITIVE_TEXT = "positive_text"
        private const val KEY_NEGATIVE_TEXT = "negative_text"
        private const val KEY_VIEW = "view"

        fun newInstance(titleRes: Int?, messageRes: Int?, positiveButtonText: Int? = R.string.ok,
                        negativeButtonText: Int? = R.string.cancel, @LayoutRes view: Int? = null): IdeeDialog {
            val fragment = IdeeDialog()

            val args = Bundle()
            args.putInt(KEY_TITLE, titleRes ?: 0)
            args.putInt(KEY_MESSAGE, messageRes ?: 0)
            args.putInt(KEY_POSITIVE_TEXT, positiveButtonText ?: 0)
            args.putInt(KEY_NEGATIVE_TEXT, negativeButtonText ?: 0)
            args.putInt(KEY_VIEW, view ?: 0)

            fragment.arguments = args
            return fragment
        }
    }

    private val titleRes: Int by lazy { arguments?.getInt(KEY_TITLE) ?: 0 }
    private val messageRes: Int by lazy { arguments?.getInt(KEY_MESSAGE) ?: 0 }
    private val positiveTextRes: Int by lazy { arguments?.getInt(KEY_POSITIVE_TEXT) ?: 0 }
    private val negativeTextRes: Int by lazy { arguments?.getInt(KEY_NEGATIVE_TEXT) ?: 0 }
    private val viewRes: Int by lazy { arguments?.getInt(KEY_VIEW) ?: 0 }

    var customView: View? = null
        private set

    var onPositiveClickListener: View.OnClickListener? = null
    var onNegativeClickListener: View.OnClickListener? = null

    /**
     * Called when dialog is shown
     */
    var onShownListener: (() -> Unit)? = null

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadConfig(view)
        positiveButton.setOnClickListener {
            dismiss()
            onPositiveClickListener?.onClick(it)
        }

        negativeButton.setOnClickListener {
            dismiss()
            onNegativeClickListener?.onClick(it)
        }

        onShownListener?.invoke()
    }

    //==========================================================================
    // private
    //==========================================================================

    private fun loadConfig(view: View) {
        if (titleRes != 0) {
            title.setText(titleRes)
        } else {
            title.visibility = View.GONE
        }

        if (messageRes != 0) {
            message.setText(messageRes)
        } else {
            message.visibility = View.GONE
        }

        if (positiveTextRes != 0) {
            positiveButton.setText(positiveTextRes)
        } else {
            positiveButton.visibility = View.GONE
        }

        if (negativeTextRes != 0) {
            negativeButton.setText(negativeTextRes)
        } else {
            negativeButton.visibility = View.GONE
        }

        if (viewRes != 0) {
            customView = LayoutInflater.from(view.context).inflate(viewRes, container, true)
        } else {
            container.visibility = View.GONE
        }
    }

}