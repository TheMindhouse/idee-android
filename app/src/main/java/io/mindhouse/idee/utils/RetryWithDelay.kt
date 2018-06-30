package io.mindhouse.idee.utils

import io.reactivex.Flowable
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit


/**
 * Created by kmisztal on 28/06/2018.
 *
 * @author Krzysztof Misztal
 */
class RetryWithDelay(
        private val maxRetries: Int,
        private val retryDelayMillis: Long
) : Function<Flowable<out Throwable>, Flowable<*>> {

    private var retryCount: Int = 0

    override fun apply(attempts: Flowable<out Throwable>): Flowable<*> {
        return attempts
                .flatMap { throwable ->
                    if (++retryCount < maxRetries || maxRetries <= 0) {
                        // When this Observable calls onNext, the original
                        // Observable will be retried (i.e. re-subscribed).
                        Flowable.timer(retryDelayMillis,
                                TimeUnit.MILLISECONDS)
                    } else {
                        Flowable.error(throwable)
                    }
                }
    }
}