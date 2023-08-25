package com.example.bookshelfapp.data.utils

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {task ->
            task.exception?.let {
                cont.resumeWithException(it)
            } ?: cont.resume(task.result, null)
        }
    }
}