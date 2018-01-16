package durdinapps.rxfirebase2.exceptions

import android.support.annotation.NonNull
import com.google.firebase.database.DatabaseError

/**
 *Created by ed on 1/16/18.
 */
class RxFirebaseDataException(@NonNull private var error: DatabaseError) : Exception() {

    override fun toString(): String {
        return "RxFirebaseDataException{" +
                "error=" + error +
                '}'
    }
}
