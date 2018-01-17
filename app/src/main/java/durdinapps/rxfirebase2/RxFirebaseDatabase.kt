@file:JvmName("RxFirebaseDatabase")

package durdinapps.rxfirebase2

import android.support.annotation.NonNull
import com.google.firebase.database.*
import durdinapps.rxfirebase2.exceptions.RxFirebaseDataException
import durdinapps.rxfirebase2.DataSnapshotMapper.Companion.DATA_SNAPSHOT_EXISTENCE_PREDICATE
import io.reactivex.*
import io.reactivex.functions.Function

/**
 * Listener for changes in te data at the given query location.
 *
 * @param query    reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
 * @return a {@link Flowable} which emits when a value of the database change in the given query.
 * onComplete called if the dataSnapShot doesn't exist
 */

@NonNull
fun observeValueEvent(@NonNull query: Query,
                      @NonNull strategy: BackpressureStrategy):
        Flowable<DataSnapshot> {

    return Flowable.create({ emitter ->
        val valueEventListener = object : ValueEventListener {

            //@Throws(Exception::class)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    emitter.onNext(dataSnapshot)
                } else {
                    emitter.onComplete()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                emitter.onError(RxFirebaseDataException(error))
            }
        }
        emitter.setCancellable {
            query.removeEventListener(valueEventListener)
        }
        query.addValueEventListener(valueEventListener)

    }, strategy)
}

/**
 * Listener for a single change in te data at the given query location.
 *
 * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @return a {@link Maybe} which emits the actual state of the database for the given query. onSuccess will be only call when
 * the given {@link DataSnapshot} exists. onComplete called if the dataSnapShot doesn't exist
 */
@NonNull
fun observeSingleValueEvent(@NonNull query: Query):
        Maybe<DataSnapshot> {
    return Maybe.create { emitter ->
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override
            fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    emitter.onSuccess(dataSnapshot)
                } else {
                    emitter.onComplete()
                }
            }

            override
            fun onCancelled(error: DatabaseError) {
                emitter.onError(RxFirebaseDataException(error))
            }
        })
    }
}

/**
 * Run a transaction on the data at this location. For more information on running transactions, see
 *
 * @param ref              reference represents a particular location in your database.
 * @param fireLocalEvents  boolean which allow to receive calls of your transaction in your local device.
 * @param transactionValue value of the transaction.
 * @return a {@link Single} which emits the final {@link DataSnapshot} value if the transaction success.
 */

@NonNull
fun runTransaction(@NonNull ref: DatabaseReference,
                   @NonNull fireLocalEvents: Boolean,
                   @NonNull transactionValue: Long):
        Single<DataSnapshot> {

    return Single.create { emitter ->
        ref.runTransaction(object : Transaction.Handler {

            override fun doTransaction(mutableData: MutableData):
                    Transaction.Result {
                val currentValue = mutableData.getValue(Int::class.java)
                if (currentValue == null) {
                    mutableData.value = transactionValue
                } else {
                    mutableData.value = currentValue + transactionValue
                }
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?,
                                    b: Boolean,
                                    dataSnapshot: DataSnapshot) {
                if (databaseError != null) {
                    emitter.onError(RxFirebaseDataException(databaseError))
                } else {
                    emitter.onSuccess(dataSnapshot)
                }
            }
        }, fireLocalEvents)
    }
}

/**
 * Set the given value on the specified {@link DatabaseReference}.
 *
 * @param ref   reference represents a particular location in your database.
 * @param value value to update.
 * @return a {@link Completable} which is complete when the set value call finish successfully.
 */
@NonNull
fun setValue(@NonNull ref: DatabaseReference,
             @NonNull value: Any):
        Completable {

    return Completable.create { e ->
        ref.setValue(value).addOnSuccessListener {
            e.onComplete()
        }.addOnFailureListener {
            exception -> e.onError(exception)
        }
    }
}

/**
 * Update the specific child keys to the specified values.
 *
 * @param ref        reference represents a particular location in your database.
 * @param updateData The paths to update and their new values
 * @return a {@link Completable} which is complete when the update children call finish successfully.
 */
@NonNull
fun updateChildren(@NonNull ref: DatabaseReference,
                   @NonNull updateData: Map<String, Any>):
        Completable {

    return Completable.create { emitter ->

        ref.updateChildren(updateData) {
            error, databaseReference ->

            if (error != null) {
                emitter.onError(RxFirebaseDataException(error))
            } else {
                emitter.onComplete()
            }
        }
    }
}

/**
 * Listener for for child events occurring at the given query location.
 *
 * @param query    reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
 * @return a {@link Flowable} which emits when a value of a child int the database change on the given query.
 */

fun observeChildEvent(@NonNull query: Query, strategy: BackpressureStrategy):
        Flowable<RxFirebaseChildEvent<DataSnapshot>> {
    return Flowable.create({ emitter ->

        val childEventListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot,
                                      previousChildName: String) {
                emitter.onNext(
                        RxFirebaseChildEvent(
                                dataSnapshot.key,
                                dataSnapshot,
                                previousChildName,

                                RxFirebaseChildEvent.EventType.ADDED))
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot,
                                        previousChildName: String) {
                emitter.onNext(
                        RxFirebaseChildEvent(
                                dataSnapshot.key,
                                dataSnapshot,
                                previousChildName,

                                RxFirebaseChildEvent.EventType.CHANGED))
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                emitter.onNext(RxFirebaseChildEvent(
                        dataSnapshot.key,
                        dataSnapshot,

                        RxFirebaseChildEvent.EventType.REMOVED))
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot,
                                      previousChildName: String) {
                emitter.onNext(
                        RxFirebaseChildEvent(
                                dataSnapshot.key,
                                dataSnapshot,
                                previousChildName,

                                RxFirebaseChildEvent.EventType.MOVED))
            }

            override fun onCancelled(error: DatabaseError) {
                emitter.onError(RxFirebaseDataException(error))
            }
        }
        emitter.setCancellable { query.removeEventListener(childEventListener) }
        query.addChildEventListener(childEventListener)
    }, strategy)
}

/**
 * Method which retrieve a list of DataSnapshot from multiple {@link DatabaseReference}.
 *
 * @param whereRefs array of {@link DatabaseReference references.}
 * @return a {@link Flowable} which emmit {@link DataSnapshot} from the given queries.
 */
@NonNull
fun observeMultipleSingleValueEvent(@NonNull vararg whereRefs: DatabaseReference):
        Flowable<DataSnapshot> {
    return Maybe.merge(Flowable.fromArray(*whereRefs)
            .map { databaseReference ->
                observeSingleValueEvent(databaseReference)
            }
    )
}

/**
 * Retrieve the child {@link DatabaseReference references} from an specific parent which equals to the
 * references retrieved from another query. Which allow to make a "where" clause on a no relational table.
 * <p>
 * Example:
 * DatabaseReference from = reference.child("Tweets");
 * Query where = reference.child("favorited").child(userA);
 * requestFilteredReferenceKeys(from, where).subscribe...
 * <p>
 * This last method will return the key references(/tweets/tweetId) which the userA mark as favorited.
 * With the given list we can work together with {@link RxFirebaseDatabase#observeMultipleSingleValueEvent(DatabaseReference...)}
 * to retrieve the Datasnapshots from the desired {@link DatabaseReference} based on other {@link DatabaseReference} values.
 *
 * @param from     base reference where you want to retrieve the original references.
 * @param whereRef reference that you use as a filter to create your from references.
 * @return a {@link Maybe} which contain the list of the given DatabaseReferences.
 */

@NonNull
fun requestFilteredReferenceKeys(@NonNull from: DatabaseReference,
                                 @NonNull whereRef: Query):
        Maybe<Array<DatabaseReference>> {

    return observeSingleValueEvent(whereRef, Function { dataSnapshot ->

        val childrenCount = dataSnapshot.childrenCount.toInt()

        //TODO - not too sure whats going on here
        //val filterRefs = arrayOf<DatabaseReference>()
        val filterRefs = Array<DatabaseReference>(childrenCount){ dataSnapshot.ref }

        val iterator = dataSnapshot.children.iterator()
        for (i in 0 until childrenCount) {
            filterRefs[i] = from.child(iterator.next().key)
        }
        filterRefs
    })
}

/**
 * Listener for changes in te data at the given query location.
 *
 * @param query    reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @param clazz    class type for the {@link DataSnapshot} items.
 * @param strategy {@link BackpressureStrategy} associated to this {@link Flowable}
 * @return a {@link Flowable} which emits when a value of the database change in the given query.
 */
@NonNull
fun <T> observeValueEvent(@NonNull query: Query,
                          @NonNull clazz: Class<T>,
                          @NonNull strategy: BackpressureStrategy):
        Flowable<T> {
    return observeValueEvent(query, DataSnapshotMapper.of(clazz), strategy)
}

/**
 * Listener for a single change in te data at the given query location.
 *
 * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @param clazz class type for the {@link DataSnapshot} items.
 * @return a {@link Maybe} which emits the actual state of the database for the given query.
 */
@NonNull
fun <T> observeSingleValueEvent(@NonNull query: Query,
                                @NonNull clazz: Class<T>):
        Maybe<T> {

    return observeSingleValueEvent(query, DataSnapshotMapper.of(clazz))
}

/**
 * Listener for changes in te data at the given query location.
 *
 * @param query    reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @param mapper   specific function to map the dispatched events.
 * @param strategy [BackpressureStrategy] associated to this [Flowable]
 * @return a [Flowable] which emits when a value of the database change in the given query.
 */
@NonNull
fun <T> observeValueEvent(@NonNull query: Query,
                          @NonNull mapper: Function<in DataSnapshot, out T>,
                          @NonNull strategy: BackpressureStrategy):
        Flowable<T> {

    return observeValueEvent(query, strategy).map(mapper)
}

/**
 * Listener for a single change in te data at the given query location.
 *
 * @param query  reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @param mapper specific function to map the dispatched events.
 * @return a [Maybe] which emits the actual state of the database for the given query.
 */
@NonNull
fun <T> observeSingleValueEvent(@NonNull query: Query,
                                @NonNull mapper: Function<in DataSnapshot, out T>):
        Maybe<T> {

    return observeSingleValueEvent(query)
            .filter(DATA_SNAPSHOT_EXISTENCE_PREDICATE)
            .map(mapper)
}

/**
 * Listener for for child events occurring at the given query location.
 *
 * @param query    reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @param mapper   specific function to map the dispatched events.
 * @param strategy [BackpressureStrategy] associated to this [Flowable]
 * @return a [Flowable] which emits when a value of a child int the database change on the given query.
 */
@NonNull
fun <T> observeChildEvent(@NonNull query: Query,
                          @NonNull mapper: Function<in RxFirebaseChildEvent<DataSnapshot>,
                                  out RxFirebaseChildEvent<T>>, strategy: BackpressureStrategy):

        Flowable<RxFirebaseChildEvent<T>> {

    return observeChildEvent(query, strategy).map(mapper)
}

/**
 * Listener for changes in the data at the given query location.
 *
 * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @return a [Flowable] which emits when a value of the database change in the given query.
 */
@NonNull
fun observeValueEvent(@NonNull query: Query):
        Flowable<DataSnapshot> {

    return observeValueEvent(query, BackpressureStrategy.DROP)
}


/**
 * Listener for for child events occurring at the given query location.
 *
 * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @return a [Flowable] which emits when a value of a child int the database change on the given query.
 */
@NonNull
fun observeChildEvent(@NonNull query: Query):
        Flowable<RxFirebaseChildEvent<DataSnapshot>> {

    return observeChildEvent(query, BackpressureStrategy.DROP)
}

/**
 * Run a transaction on the data at this location. For more information on running transactions, see
 *
 * @param ref              reference represents a particular location in your database.
 * @param transactionValue value of the transaction.
 * @return a [Single] which emits the final [DataSnapshot] value if the transaction success.
 */

@NonNull
fun runTransaction(@NonNull ref: DatabaseReference,
                   transactionValue: Long):
        Single<DataSnapshot> {

    return runTransaction(ref, true, transactionValue)
}

/**
 * Listener for changes in te data at the given query location.
 *
 * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @param clazz class type for the [DataSnapshot] items.
 * @return a [Flowable] which emits when a value of the database change in the given query.
 */
@NonNull
fun <T> observeValueEvent(@NonNull query: Query,
                          @NonNull clazz: Class<T>):
        Flowable<T> {

    return observeValueEvent(query, DataSnapshotMapper.of(clazz), BackpressureStrategy.DROP)
}

/**
 * Listener for for child events occurring at the given query location.
 *
 * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @param clazz class type for the [DataSnapshot] items.
 * @return a [Flowable] which emits when a value of a child int the database change on the given query.
 */
@NonNull
fun <T> observeChildEvent(@NonNull query: Query,
                          @NonNull clazz: Class<T>):
        Flowable<RxFirebaseChildEvent<T>> {

    return observeChildEvent(query, DataSnapshotMapper.ofChildEvent(clazz), BackpressureStrategy.DROP)
}

/**
 * Listener for changes in te data at the given query location.
 *
 * @param query reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @return a [Flowable] which emits when a value of the database change in the given query.
 */
@NonNull
fun <T> observeValueEvent(@NonNull query: Query,
                          @NonNull mapper: Function<in DataSnapshot, out T>):
        Flowable<T> {

    return observeValueEvent(query, BackpressureStrategy.DROP).map(mapper)
}

/**
 * Listener for for child events occurring at the given query location.
 *
 * @param query  reference represents a particular location in your Database and can be used for reading or writing data to that Database location.
 * @param mapper specific function to map the dispatched events.
 * @return a [Flowable] which emits when a value of a child int the database change on the given query.
 */
@NonNull
fun <T> observeChildEvent(@NonNull query: Query,
                          @NonNull mapper: Function<in RxFirebaseChildEvent<DataSnapshot>,
                                  out RxFirebaseChildEvent<T>>):
        Flowable<RxFirebaseChildEvent<T>> {

    return observeChildEvent(query, BackpressureStrategy.DROP).map(mapper)
}

