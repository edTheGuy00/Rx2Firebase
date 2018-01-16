@file:JvmName("RxFirebaseUser")

package durdinapps.rxfirebase2

import android.support.annotation.NonNull
import com.google.firebase.auth.*
import io.reactivex.*

/**
 * Fetches a Firebase Auth ID Token for the user; useful when authenticating against your own backend.
 *
 * @param firebaseUser current firebaseUser instance.
 * @param forceRefresh force to refresh the token ID.
 * @return a {@link Maybe} which emits an {@link GetTokenResult} if success.
 */
@NonNull
fun getIdToken(@NonNull firebaseUser: FirebaseUser,
               forceRefresh: Boolean) :
        Maybe<GetTokenResult>{
    return Maybe.create { emitter ->

        RxHandler.assignOnTask(emitter, firebaseUser.getIdToken(forceRefresh))
    }
}

/**
 * Updates the email address of the user.
 *
 * @param firebaseUser current firebaseUser instance.
 * @param email        new email.
 * @return a {@link Completable} if the task is complete successfully.
 */

@NonNull
fun updateEmail(@NonNull firebaseUser: FirebaseUser,
                @NonNull email: String) :
        Completable {
    return Completable.create { emitter ->

        RxCompletableHandler.assignOnTask(emitter, firebaseUser.updateEmail(email))
    }
}

/**
 * Updates the password of the user.
 *
 * @param firebaseUser current firebaseUser instance.
 * @param password     new password.
 * @return a {@link Completable} if the task is complete successfully.
 */

@NonNull
fun updatePassword(@NonNull firebaseUser: FirebaseUser,
                @NonNull password: String) :
        Completable {
    return Completable.create { emitter ->

        RxCompletableHandler.assignOnTask(emitter, firebaseUser.updatePassword(password))
    }
}

/**
 * Updates the user profile information.
 *
 * @param firebaseUser current firebaseUser instance.
 * @param request      {@link UserProfileChangeRequest} request for this user.
 * @return a {@link Completable} if the task is complete successfully.
 */
@NonNull
fun updateProfile(@NonNull firebaseUser: FirebaseUser,
                  @NonNull request: UserProfileChangeRequest):
        Completable {
    return Completable.create { emitter ->

        RxCompletableHandler.assignOnTask(emitter, firebaseUser.updateProfile(request))
    }
}

/**
 * Deletes the user record from your Firebase project's database.
 *
 * @param firebaseUser current firebaseUser instance.
 * @return a {@link Completable} if the task is complete successfully.
 */
@NonNull
fun deleteUser(@NonNull firebaseUser: FirebaseUser):
        Completable {
    return Completable.create { emitter ->

        RxCompletableHandler.assignOnTask(emitter, firebaseUser.delete())
    }
}

/**
 * Reauthenticates the user with the given credential.
 *
 * @param firebaseUser current firebaseUser instance.
 * @param credential   [AuthCredential] to re-authenticate.
 * @return a [Completable] if the task is complete successfully.
 */
@NonNull
fun reAuthenticate(@NonNull firebaseUser: FirebaseUser,
                   @NonNull credential: AuthCredential):
        Completable {
    return Completable.create { emitter ->

        RxCompletableHandler.assignOnTask(emitter, firebaseUser.reauthenticate(credential))
    }
}

/**
 * Manually refreshes the data of the current user (for example, attached providers, display name, and so on).
 *
 * @param firebaseUser current firebaseUser instance.
 * @return a [Completable] if the task is complete successfully.
 */
@NonNull
fun reload(@NonNull firebaseUser: FirebaseUser) :
        Completable {
    return Completable.create { emitter ->

        RxCompletableHandler.assignOnTask(emitter, firebaseUser.reload())
    }
}

/**
 * Initiates email verification for the user.
 *
 * @param firebaseUser current firebaseUser instance.
 * @return a [Completable] if the task is complete successfully.
 */

@NonNull
fun sendEmailVerification(@NonNull firebaseUser: FirebaseUser):
        Completable {
    return Completable.create { emitter ->

        RxCompletableHandler.assignOnTask(emitter, firebaseUser.sendEmailVerification())
    }
}

/**
 * Attaches the given [AuthCredential] to the user.
 *
 * @param firebaseUser current firebaseUser instance.
 * @param credential   new [AuthCredential] to link.
 * @return a [Maybe] which emits an [AuthResult] if success.
 */
@NonNull
fun linkWithCredential(@NonNull firebaseUser: FirebaseUser,
                       @NonNull credential: AuthCredential):
        Maybe<AuthResult> {
    return Maybe.create { emitter ->

        RxHandler.assignOnTask(emitter, firebaseUser.linkWithCredential(credential))
    }
}

/**
 * Detaches credentials from a given provider type from this user.
 *
 * @param firebaseUser current firebaseUser instance.
 * @param provider     a unique identifier of the type of provider to be unlinked, for example, [com.google.firebase.auth.FacebookAuthProvider.PROVIDER_ID].
 * @return a [Maybe] which emits an [AuthResult] if success.
 */
@NonNull
fun unlink(@NonNull firebaseUser: FirebaseUser,
           @NonNull provider: String):
        Maybe<AuthResult> {
    return Maybe.create { emitter ->

        RxHandler.assignOnTask(emitter, firebaseUser.unlink(provider))

    }
}

/**
 * updates the current phone number for the given user.
 *
 * @param firebaseUser        current firebaseUser instance.
 * @param phoneAuthCredential new phone credential.
 * @return a [Completable] if the task is complete successfully.
 */
@NonNull
fun updatePhoneNumber(@NonNull firebaseUser: FirebaseUser,
                      @NonNull phoneAuthCredential: PhoneAuthCredential):
        Completable {
    return Completable.create { emitter ->

        RxCompletableHandler.assignOnTask(emitter, firebaseUser.updatePhoneNumber(phoneAuthCredential))
    }
}

/**
 * Reauthenticates the user with the given credential, and returns the profile data for that account.
 * This is useful for operations that require a recent sign-in, to prevent or resolve a [com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException]
 *
 * @param firebaseUser current firebaseUser instance.
 * @param credential   Authcredential used for reauthenticate.
 * @return a [Maybe] which emits an [AuthResult] if success.
 */

@NonNull
fun reauthenticateAndRetrieveData(@NonNull firebaseUser: FirebaseUser,
                                  @NonNull credential: AuthCredential):
        Maybe<AuthResult> {
    return Maybe.create { emitter ->

        RxHandler.assignOnTask(emitter, firebaseUser.reauthenticateAndRetrieveData(credential))
    }
}
