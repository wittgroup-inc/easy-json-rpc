@file:Suppress("NOTHING_TO_INLINE")

package com.wittgroupinc.easyjsonrpc

import android.text.TextUtils
import android.util.Log
import io.reactivex.*

/**
 * Example usage of [log]:
Observable.just(10, 20, 30, 40)
.log()
.subscribe({ }, { })

or

Observable.just(10, 20, 30, 40)
.log("CustomTagValue")
.subscribe({ }, { })
 */

inline fun <reified T> printEvent(tag: String, success: T?, error: Throwable?) =
        when {
            success == null && error == null -> Log.d(tag, "Complete") /* Only with Maybe */
            success != null -> Log.d(tag, "Success $success")
            error != null -> Log.d(tag, "Error $error")
            else -> -1 /* Cannot happen*/
        }

inline fun printEvent(tag: String, error: Throwable?) =
        when {
            error != null -> Log.d(tag, "Error $error")
            else -> Log.d(tag, "Complete")
        }


inline fun getTagWithPath(suffix: String?) =
        Thread.currentThread().stackTrace
                .first { it.fileName.endsWith(".kt") }
                .let { stack ->
                    "${stack.fileName.removeSuffix(".kt")}::${stack.methodName}:${stack.lineNumber}${if (TextUtils.isEmpty(suffix)) "" else ":<$suffix>"}"
                }

inline fun <reified T> Single<T>.log(tag: String? = null): Single<T> {
    val tag = getTagWithPath(tag)
    return doOnEvent { success, error -> printEvent(tag, success, error) }
            .doOnSubscribe { Log.d(tag, "Subscribe") }
            .doOnDispose { Log.d(tag, "Dispose") }
}

inline fun <reified T> Maybe<T>.log(tag: String? = null): Maybe<T> {
    val tag = getTagWithPath(tag)
    return doOnEvent { success, error -> printEvent(tag, success, error) }
            .doOnSubscribe { Log.d(tag, "Subscribe") }
            .doOnDispose { Log.d(tag, "Dispose") }
}

inline fun Completable.log(tag: String? = null): Completable {
    val tag = getTagWithPath(tag)
    return doOnEvent { printEvent(tag, it) }
            .doOnSubscribe { Log.d(tag, "Subscribe") }
            .doOnDispose { Log.d(tag, "Dispose") }
}

inline fun <reified T> Observable<T>.log(tag: String? = null): Observable<T> {
    val tag = getTagWithPath(tag)
    return doOnEach { Log.d(tag, "Each $it") }
            .doOnSubscribe { Log.d(tag, "Subscribe") }
            .doOnDispose { Log.d(tag, "Dispose") }
}

inline fun <reified T> Flowable<T>.log(tag: String? = null): Flowable<T> {
    val tag = getTagWithPath(tag)
    return doOnEach { Log.d(tag, "Each $it") }
            .doOnSubscribe { Log.d(tag, "Subscribe") }
            .doOnCancel { Log.d(tag, "Cancel") }
}
