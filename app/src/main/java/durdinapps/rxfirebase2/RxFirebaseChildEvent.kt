package durdinapps.rxfirebase2

import android.support.annotation.NonNull

/**
 *Created by ed on 1/17/18.
 */
class RxFirebaseChildEvent<T> {

    private var eventType: EventType
    private var key: String
    private var value: T
    private var previousChildName: String? = null

    constructor(@NonNull key: String,
                @NonNull value: T,
                @NonNull previousChildName: String,
                @NonNull eventType: EventType){

        this.key = key
        this.value = value
        this.eventType = eventType
        this.previousChildName = previousChildName
    }

    constructor(@NonNull key: String,
                @NonNull value: T,
                @NonNull eventType: EventType){

        this.key = key
        this.value = value
        this.eventType = eventType
    }

    /**
     * @return the key associate to this [RxFirebaseChildEvent];
     */
    @NonNull
    fun getKey(): String {
        return key
    }

    /**
     * @return the value associate to this [RxFirebaseChildEvent];
     */
    @NonNull
    fun getValue(): T {
        return value
    }

    /**
     * @return the previous child name at this position;
     */

    @NonNull
    fun getPreviousChildName(): String? {
        return previousChildName
    }

    /**
     * @return the kind of event of this event. This is used to different them when an item is added, changed, moved or removed.
     */
    @NonNull
    fun getEventType(): EventType {
        return eventType
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as RxFirebaseChildEvent<*>?

        if (eventType != that!!.eventType) return false
        if (if (value != null) value != that.value else that.value != null) return false
        return if (previousChildName != null) previousChildName == that.previousChildName else that.previousChildName == null

    }

    override fun hashCode(): Int {
        var result = eventType.hashCode()
        result = 31 * result + if (value != null) (value)!!.hashCode() else 0
        result = 31 * result + if (previousChildName != null) { previousChildName!!.hashCode() } else 0
        return result
    }
    /**
     * Enum used to different when an item is added, changed, moved or removed.
     */
    enum class EventType {
        ADDED,
        CHANGED,
        REMOVED,
        MOVED
    }
}