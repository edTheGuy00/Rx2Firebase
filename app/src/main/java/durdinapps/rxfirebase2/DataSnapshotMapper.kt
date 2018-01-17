package durdinapps.rxfirebase2

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.GenericTypeIndicator
import durdinapps.rxfirebase2.exceptions.RxFirebaseDataCastException
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import java.util.ArrayList
import java.util.LinkedHashMap

/**
 *Created by ed on 1/17/18.
 */
abstract class DataSnapshotMapper<T, U> : Function<T, U> {

    companion object {

        @JvmStatic
        fun <U> of(clazz: Class<U>): DataSnapshotMapper<DataSnapshot, U> {
            return TypedDataSnapshotMapper<U>(clazz)
        }

        @JvmStatic
        fun <U> listOf(clazz: Class<U>): DataSnapshotMapper<DataSnapshot, List<U>> {
            return TypedListDataSnapshotMapper(clazz)
        }

        @JvmStatic
        fun <U> listOf(clazz: Class<U>, mapper: Function<DataSnapshot, U>): DataSnapshotMapper<DataSnapshot, List<U>> {
            return TypedListDataSnapshotMapper(clazz, mapper)
        }

        @JvmStatic
        fun <U> mapOf(clazz: Class<U>): DataSnapshotMapper<DataSnapshot, LinkedHashMap<String, U>> {
            return TypedMapDataSnapshotMapper(clazz)
        }

        @JvmStatic
        fun <U> of(genericTypeIndicator: GenericTypeIndicator<U>): DataSnapshotMapper<DataSnapshot, U> {
            return GenericTypedDataSnapshotMapper<U>(genericTypeIndicator)
        }

        @JvmStatic
        fun <U> ofChildEvent(clazz: Class<U>): DataSnapshotMapper<RxFirebaseChildEvent<DataSnapshot>, RxFirebaseChildEvent<U>> {
            return ChildEventDataSnapshotMapper<U>(clazz)
        }

        @JvmStatic
        val DATA_SNAPSHOT_EXISTENCE_PREDICATE:
                Predicate<DataSnapshot> = Predicate { dataSnapshot ->
            dataSnapshot.exists()
        }

    }


}

private class TypedDataSnapshotMapper<U>(private val clazz: Class<U>) : DataSnapshotMapper<DataSnapshot, U>() {

    override fun apply(dataSnapshot: DataSnapshot): U {
        return getDataSnapshotTypedValue<U>(dataSnapshot, clazz)
    }
}

private class TypedListDataSnapshotMapper<U> @JvmOverloads internal constructor(private val clazz: Class<U>, private val mapper: Function<DataSnapshot, U>? = null) : DataSnapshotMapper<DataSnapshot, List<U>>() {

    @Throws(Exception::class)
    override fun apply(dataSnapshot: DataSnapshot): List<U> {
        val items = ArrayList<U>()
        for (childSnapshot in dataSnapshot.children) {
            items.add(if (mapper != null)
                mapper.apply(childSnapshot)
            else
                getDataSnapshotTypedValue<U>(childSnapshot, clazz))
        }
        return items
    }
}

private class GenericTypedDataSnapshotMapper<U>(private val genericTypeIndicator: GenericTypeIndicator<U>) : DataSnapshotMapper<DataSnapshot, U>() {

    override fun apply(dataSnapshot: DataSnapshot): U {
        return dataSnapshot.getValue(genericTypeIndicator) ?: throw Exceptions.propagate(RxFirebaseDataCastException(
                "unable to cast firebase data response to generic type"))
    }
}

private class TypedMapDataSnapshotMapper<U> internal constructor(private val clazz: Class<U>) : DataSnapshotMapper<DataSnapshot, LinkedHashMap<String, U>>() {

    override fun apply(dataSnapshot: DataSnapshot): LinkedHashMap<String, U> {
        val items = LinkedHashMap<String, U>()
        for (childSnapshot in dataSnapshot.children) {
            items.put(childSnapshot.key, getDataSnapshotTypedValue<U>(childSnapshot, clazz))
        }
        return items
    }
}

private fun <U> getDataSnapshotTypedValue(dataSnapshot: DataSnapshot, clazz: Class<U>): U {
    return dataSnapshot.getValue(clazz) ?: throw Exceptions.propagate(RxFirebaseDataCastException(
            "unable to cast firebase data response to " + clazz.simpleName))
}

private class ChildEventDataSnapshotMapper<U>(private val clazz: Class<U>) : DataSnapshotMapper<RxFirebaseChildEvent<DataSnapshot>, RxFirebaseChildEvent<U>>() {

    override fun apply(rxFirebaseChildEvent: RxFirebaseChildEvent<DataSnapshot>): RxFirebaseChildEvent<U> {
        val dataSnapshot = rxFirebaseChildEvent.getValue()
        return if (dataSnapshot.exists()) {
            RxFirebaseChildEvent(
                    dataSnapshot.key,
                    getDataSnapshotTypedValue(dataSnapshot, clazz),
                    rxFirebaseChildEvent.getPreviousChildName()!!,
                    rxFirebaseChildEvent.getEventType())
        } else {
            throw Exceptions.propagate(RuntimeException("child dataSnapshot doesn't exist"))
        }
    }
}