package durdinapps.rxfirebase2

import android.support.annotation.NonNull
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.Function

/**
 * Basic builder to create Firebase queries based on filters from different {@link DatabaseReference references}.
 */
class RxFirebaseQuery {

    private var whereMaybe: Maybe<Array<DatabaseReference>>? = null

    /**
     * Retrieve a new instance for {@link RxFirebaseQuery}.
     */
    companion object {
        @JvmStatic
        fun getInstance() : RxFirebaseQuery{
            return RxFirebaseQuery()
        }
    }

    /**
     * Generate a filter based on the method {@link RxFirebaseDatabase#requestFilteredReferenceKeys(DatabaseReference, Query)}.
     *
     * @param from     base reference where you want to retrieve the original references.
     * @param whereRef reference that you use as a filter to create your from references.
     * @return the current instance of {@link RxFirebaseQuery}.
     */
    @NonNull
    fun filterByRefs(@NonNull from: DatabaseReference,
                     @NonNull whereRef: Query):
            RxFirebaseQuery {

        whereMaybe = requestFilteredReferenceKeys(from, whereRef)

        return this
    }

    /**
     * Generate a filter based on a given function.
     *
     * @param whereRef reference that you use as a filter to create your from references.
     * @param mapper   Custom mapper to map the retrieved where references to new [DatabaseReference].
     * @return the current instance of [RxFirebaseQuery].
     */
    @NonNull
    fun filter(@NonNull whereRef: Query,
               @NonNull mapper: Function<in DataSnapshot, out Array<DatabaseReference>>):
            RxFirebaseQuery {

        whereMaybe = observeSingleValueEvent(whereRef, mapper)

        return this
    }

    /**
     * Retrieve the final result as a [Single] which emmit the final result of the event as a [List] of [DataSnapshot].
     */
    fun asList(): Single<List<DataSnapshot>>? {
        return create()?.toList()
    }

    /**
     * Retrieve the final result as a [Single] which emmit the final result of the event as a [List] of a given type.
     *
     * @param mapper specific function to map the dispatched events.
     */
    fun <T> asList(mapper: Function<in List<DataSnapshot>,
            out List<T>>): Single<List<T>>? {

        return create()?.toList()?.map(mapper)
    }

    /**
     * Retrieve the final result of the query as a [Flowable] which emmit mapped values.
     *
     * @param mapper specific function to map the dispatched events.
     */
    fun <T> create(mapper: Function<in DataSnapshot, out T>): Flowable<T>? {
        return create()?.map(mapper)
    }

    /**
     * Retrieve the final result of the query as a [Flowable] which emmit [DataSnapshot].
     */
    @NonNull
    fun create(): Flowable<DataSnapshot>? {
        if (whereMaybe == null)
            throw IllegalArgumentException("It's necessary define a where function to retrieve data")

        return whereMaybe?.toFlowable()?.flatMap { keys ->

            observeMultipleSingleValueEvent(*keys)
        }
    }

}