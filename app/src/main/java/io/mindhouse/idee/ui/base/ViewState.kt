package io.mindhouse.idee.ui.base

/**
 * Base ViewState to be rendered by view.
 *
 * @author Krzysztof Misztal
 */
interface ViewState {

    /**
     * String resId of message to be displayed
     */
    val errorMessage: String?
}