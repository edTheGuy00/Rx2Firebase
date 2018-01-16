package durdinapps.rxfirebase2.exceptions

import android.support.annotation.NonNull

class RxFirebaseDataCastException : Exception {

    constructor () : super()

    constructor(@NonNull detailMessage: String) : super(detailMessage)

    constructor(@NonNull detailMessage : String, @NonNull throwable : Throwable) : super(detailMessage, throwable)

    constructor(@NonNull throwable: Throwable) : super(throwable)

}