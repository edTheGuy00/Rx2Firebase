package durdinapps.rxfirebase2.exceptions


import android.support.annotation.NonNull

class RxFirebaseNullDataException : NullPointerException {

   constructor() : super()

   constructor(@NonNull detailMessage: String) : super(detailMessage)

}
