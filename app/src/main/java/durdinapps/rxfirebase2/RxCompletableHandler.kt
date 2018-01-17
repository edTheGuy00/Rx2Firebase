package durdinapps.rxfirebase2

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.reactivex.CompletableEmitter
import java.lang.Exception

/**
 *Created by ed on 1/17/18.
 */
class RxCompletableHandler<T>(private val completableEmitter: CompletableEmitter) :
        OnFailureListener, OnSuccessListener<T>, OnCompleteListener<T> {

    companion object {

        @JvmStatic
        fun <T> assignOnTask(completableEmitter: CompletableEmitter, task: Task<T>) {
            val handler = RxCompletableHandler<T>(completableEmitter)
            task.addOnFailureListener(handler)
            task.addOnSuccessListener(handler)
            try {
                task.addOnCompleteListener(handler)
            } catch (t: Throwable) {
                // ignore
            }

        }
    }

    override fun onComplete(p0: Task<T>) {
        completableEmitter.onComplete()
    }

    override fun onSuccess(p0: T) {
        completableEmitter.onComplete()
    }

    override fun onFailure(p0: Exception) {
        completableEmitter.onError(p0)
    }
}
