@file:JvmName("RxFirebaseAuth")

package durdinapps.rxfirebase2

import android.support.annotation.NonNull
import com.google.firebase.auth.*
import io.reactivex.*


/**
 * Asynchronously signs in as an anonymous user.
 * If there is already an anonymous user signed in, that user will be returned; otherwise, a new anonymous user identity will be created and returned.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @return a [Maybe] which emits an [AuthResult] if success.
 * @see [Firebase Auth API](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth)
 */
@NonNull
fun signInAnonymously(@NonNull firebaseAuth: FirebaseAuth):
        Maybe<AuthResult> {
    return Maybe.create { emitter ->
        RxHandler.assignOnTask(emitter, firebaseAuth.signInAnonymously())
    }
}

/**
 * Asynchronously signs in using an email and password.
 * Fails with an error if the email address and password do not match.
 *
 *
 * Note: The user's password is NOT the password used to access the user's email account.
 * The email address serves as a unique identifier for the user, and the password is used to access the user's account in your Firebase project.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @param email        email to login.
 * @param password     password to login.
 * @return a [Maybe] which emits an [AuthResult] if success.
 * @see [Firebase Auth API](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth)
 */
@NonNull
fun signInWithEmailAndPassword(@NonNull firebaseAuth: FirebaseAuth,
                               @NonNull email: String,
                               @NonNull password: String):
        Maybe<AuthResult> {
    return Maybe.create { emitter ->
        RxHandler.assignOnTask(emitter, firebaseAuth.signInWithEmailAndPassword(email, password))
    }
}

/**
 * Asynchronously signs in with the given credentials.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @param credential   The auth credential. Value must not be null.
 * @return a [Maybe] which emits an [AuthResult] if success.
 * @see [Firebase Auth API](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth)
 */
@NonNull
fun signInWithCredential(@NonNull firebaseAuth: FirebaseAuth,
                         @NonNull credential: AuthCredential):
        Maybe<AuthResult> {
    return Maybe.create { emitter ->
        RxHandler.assignOnTask(emitter, firebaseAuth.signInWithCredential(credential))
    }
}

/**
 * Asynchronously signs in using a custom token.
 *
 *
 * Custom tokens are used to integrate Firebase Auth with existing auth systems, and must be generated by the auth backend.
 *
 *
 * Fails with an error if the token is invalid, expired, or not accepted by the Firebase Auth service.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @param token        The custom token to sign in with.
 * @return a [Maybe] which emits an [AuthResult] if success.
 * @see [Firebase Auth API](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth)
 */

@NonNull
fun signInWithCustomToken(@NonNull firebaseAuth: FirebaseAuth,
                          @NonNull token: String):
        Maybe<AuthResult> {
    return Maybe.create { emitter ->
        RxHandler.assignOnTask(emitter, firebaseAuth.signInWithCustomToken(token))
    }
}

/**
 * Creates a new user account associated with the specified email address and password.
 *
 *
 * On successful creation of the user account, this user will also be signed in to your application.
 *
 *
 * User account creation can fail if the account already exists or the password is invalid.
 *
 *
 * Note: The email address acts as a unique identifier for the user and enables an email-based password reset.
 * This function will create a new user account and set the initial user password.
 *
 *
 * Custom tokens are used to integrate Firebase Auth with existing auth systems, and must be generated by the auth backend.
 *
 *
 * Fails with an error if the token is invalid, expired, or not accepted by the Firebase Auth service.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @param email        The user's email address.
 * @param password     The user's chosen password.
 * @return a [Maybe] which emits an [AuthResult] if success.
 * @see [Firebase Auth API](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth)
 */

@NonNull
fun createUserWithEmailAndPassword(@NonNull firebaseAuth: FirebaseAuth,
                                   @NonNull email: String,
                                   @NonNull password: String):
        Maybe<AuthResult> {
    return Maybe.create { emitter ->
        RxHandler.assignOnTask(emitter, firebaseAuth.createUserWithEmailAndPassword(email, password))
    }
}

/**
 * Gets the list of provider IDs that can be used to sign in for the given email address. Useful for an "identifier-first" sign-in flow.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @param email        An email address.
 * @return a [Maybe] which emits an [ProviderQueryResult] if success.
 * @see [Firebase Auth API](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth)
 */
@NonNull
fun fetchProvidersForEmail(@NonNull firebaseAuth: FirebaseAuth,
                           @NonNull email: String):
        Maybe<ProviderQueryResult> {
    return Maybe.create { emitter ->
        RxHandler.assignOnTask(emitter, firebaseAuth.fetchProvidersForEmail(email)) }
}

/**
 * Sends a password reset email to the given email address.
 *
 *
 * To complete the password reset, call [FirebaseAuth.confirmPasswordReset] with the code supplied in
 * the email sent to the user, along with the new password specified by the user.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @param email        The email address with the password to be reset.
 * @return a [Completable] which emits when the action is completed.
 * @see [Firebase Auth API](https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth)
 */
@NonNull
fun sendPasswordResetEmail(@NonNull firebaseAuth: FirebaseAuth,
                           @NonNull email: String):
        Completable {
    return Completable.create { emitter ->
        RxCompletableHandler.assignOnTask(emitter, firebaseAuth.sendPasswordResetEmail(email))
    }
}

/**
 * Observable which track the auth changes of [FirebaseAuth] to listen when an user is logged or not.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @return an [Observable] which emits every time that the [FirebaseAuth] state change.
 */
@NonNull
fun observeAuthState(@NonNull firebaseAuth: FirebaseAuth):
        Observable<FirebaseAuth> {

    return Observable.create { emitter ->
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            emitter.onNext(firebaseAuth)
        }
        firebaseAuth.addAuthStateListener(authStateListener)

        emitter.setCancellable {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }
}

/**
 * Checks that the code given is valid. This code will have been generated
 * by [FirebaseAuth.sendPasswordResetEmail] or [com.google.firebase.auth.FirebaseUser.sendEmailVerification] valid for a single use.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @param code         generated code by firebase.
 * @return a [Maybe] which emits when the action is completed.
 */
@NonNull
fun checkActionCode(@NonNull firebaseAuth: FirebaseAuth,
                    @NonNull code: String):
        Maybe<ActionCodeResult> {
    return Maybe.create { emitter ->
        RxHandler.assignOnTask(emitter, firebaseAuth.checkActionCode(code))
    }
}

/**
 * Changes the user's password to newPassword for the account for which the code is valid.
 * Code validity can be checked with [FirebaseAuth.verifyPasswordResetCode].
 * This use case is only valid for signed-out users, and behavior is undefined for signed-in users.
 * Password changes for signed-in users should be made using [com.google.firebase.auth.FirebaseUser.updatePassword].
 *
 * @param firebaseAuth firebaseAuth instance.
 * @param code         generated code by firebase.
 * @param newPassword  new password for the user.
 * @return a [Completable] which emits when the action is completed.
 */
@NonNull
fun confirmPasswordReset(@NonNull firebaseAuth: FirebaseAuth,
                         @NonNull code: String,
                         @NonNull newPassword: String):
        Completable {
    return Completable.create { emitter ->
        RxCompletableHandler.assignOnTask(emitter, firebaseAuth.confirmPasswordReset(code, newPassword))
    }
}

/**
 * Applies the given code, which can be any out of band code which is valid according
 * to [FirebaseAuth.checkActionCode] that does not also pass [{verifyPasswordResetCode(String)][FirebaseAuth],
 * which requires an additional parameter.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @param code         generated code by firebase.
 * @return a [Completable] which emits when the action is completed.
 */
@NonNull
fun applyActionCode(@NonNull firebaseAuth: FirebaseAuth,
                    @NonNull code: String):
        Completable {
    return Completable.create { emitter ->
        RxCompletableHandler.assignOnTask(emitter, firebaseAuth.applyActionCode(code))
    }
}

/**
 * Checks that the code is a valid password reset out of band code.
 * This code will have been generated by a call to [FirebaseAuth.sendPasswordResetEmail], and is valid for a single use.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @param code         generated code by firebase.
 * @return a [Maybe] which emits when the action is completed.
 */
@NonNull
fun verifyPasswordResetCode(@NonNull firebaseAuth: FirebaseAuth,
                            @NonNull code: String):
        Maybe<String> {
    return Maybe.create { emitter ->
        RxHandler.assignOnTask(emitter, firebaseAuth.verifyPasswordResetCode(code))
    }
}

/**
 * Registers a listener to changes in the ID token state.
 * There can be more than one listener registered at the same time. The listeners are called asynchronously, possibly on a different thread.
 *
 * Authentication state changes are:
 *
 * -When a user signs in
 * -When the current user signs out
 * -When the current user changes
 * -When there is a change in the current user's token
 *
 * Use RemoveIdTokenListener to unregister a listener.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @param idTokenListener given token listener.
 * @return a [Completable] which emits when the action is completed.
 */
@NonNull
fun addIdTokenListener(@NonNull firebaseAuth: FirebaseAuth,
                       @NonNull idTokenListener: FirebaseAuth.IdTokenListener):
        Completable {
    return Completable.create { emitter ->
        firebaseAuth.addIdTokenListener(idTokenListener)
        emitter.onComplete()
    }
}

/**
 * Unregisters a listener of ID token changes.
 * Listener must previously been added with AddIdTokenListener.
 *
 * @param firebaseAuth firebaseAuth instance.
 * @param idTokenListener given token listener.
 * @return a [Completable] which emits when the action is completed.
 */
@NonNull
fun removeIdTokenListener(@NonNull firebaseAuth: FirebaseAuth,
                          @NonNull idTokenListener: FirebaseAuth.IdTokenListener):
        Completable {
    return Completable.create { emitter ->
        firebaseAuth.removeIdTokenListener(idTokenListener)
        emitter.onComplete()
    }
}