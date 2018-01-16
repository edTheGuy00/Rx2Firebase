package durdinapps.rxfirebase2

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import durdinapps.rxfirebase2.exceptions.RxFirebaseNullDataException
import io.reactivex.MaybeEmitter
import java.lang.Exception

/**
 *Created by ed on 1/16/18.
 */
class RxHandler<T>(private val emitter: MaybeEmitter<in T>) :
        OnSuccessListener<T>,
        OnFailureListener,
        OnCompleteListener<T> {


    override fun onFailure(e: Exception) {
        emitter.onError(e)
    }

    override fun onComplete(task: Task<T>) {
        emitter.onComplete()
    }

    override fun onSuccess(result: T) {
        if (result != null){
            emitter.onSuccess(result)
        } else {
            emitter.onError(RxFirebaseNullDataException("Observables can't emit null values"))
        }
    }

    companion object {
        @JvmStatic fun <T> assignOnTask(emitter: MaybeEmitter<in T>, task: Task<T>) {
            val handler = RxHandler(emitter)
            task.addOnSuccessListener(handler)
            task.addOnFailureListener(handler)
            try {
                task.addOnCompleteListener(handler)
            } catch (t: Throwable) {
                // ignore
            }
        }
    }
}