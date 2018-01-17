package durdinapps.rxfirebase2

import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot

/**
 *Created by ed on 1/16/18.
 */
abstract class RxFirebaseRecyclerAdapter<ViewHolder : RecyclerView.ViewHolder, T> :
        RecyclerView.Adapter<ViewHolder> {

    private var itemClass: Class<T>
    private var items: ArrayList<T>
    private var keys: ArrayList<String>

    constructor(itemClass: Class<T>)  {
        this.itemClass = itemClass
        this.items = ArrayList()
        this.keys = ArrayList()
    }

    constructor(itemClass: Class<T>,
                @NonNull items: ArrayList<T>,
                @NonNull keys: ArrayList<String>)  {

        this.itemClass = itemClass
        this.items = items
        this.keys = keys

    }

    override
    abstract fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder

    override
    abstract fun onBindViewHolder(holder: ViewHolder, position: Int)

    override
    fun getItemCount(): Int {
        return items.size
    }

    fun manageChildItem(item: RxFirebaseChildEvent<DataSnapshot>) {
        when (item.getEventType()) {
            RxFirebaseChildEvent.EventType.ADDED -> addItem(item.getValue(), item.getPreviousChildName())
            RxFirebaseChildEvent.EventType.CHANGED -> changeItem(item.getValue(), item.getPreviousChildName()!!)
            RxFirebaseChildEvent.EventType.REMOVED -> removeItem(item.getValue())
            RxFirebaseChildEvent.EventType.MOVED -> onItemMoved(item.getValue(), item.getPreviousChildName())
        }
    }

    private fun addItem(dataSnapshot: DataSnapshot, previousChildName: String?) {
        val key = dataSnapshot.key

        if (!keys.contains(key)) {
            val item = dataSnapshot.getValue<T>(this.itemClass)
            val insertedPosition: Int
            if (previousChildName == null) {
                if (item != null) {
                    items.add(0, item)
                }
                keys.add(0, key)
                insertedPosition = 0
            } else {
                val previousIndex = keys.indexOf(previousChildName)
                val nextIndex = previousIndex + 1
                if (nextIndex == items.size) {
                    if (item != null) {
                        items.add(item)
                    }
                    keys.add(key)
                } else {
                    if (item != null) {
                        items.add(nextIndex, item)
                    }
                    keys.add(nextIndex, key)
                }
                insertedPosition = nextIndex
            }
            notifyItemInserted(insertedPosition)
            if (item != null) {
                itemAdded(item, key, insertedPosition)
            }
        }
    }

    fun changeItem(dataSnapshot: DataSnapshot, previousChildName: String) {
        val key = dataSnapshot.key

        if (keys.contains(key)) {
            val index = keys.indexOf(key)
            val oldItem = items[index]
            val newItem = dataSnapshot.getValue<T>(this.itemClass)

            items[index] = newItem!!

            notifyItemChanged(index)
            itemChanged(oldItem, newItem, key, index)
        }
    }

    fun removeItem(dataSnapshot: DataSnapshot) {
        val key = dataSnapshot.key

        if (keys.contains(key)) {
            val index = keys.indexOf(key)
            val item = items[index]

            keys.removeAt(index)
            items.removeAt(index)

            notifyItemRemoved(index)
            itemRemoved(item, key, index)
        }
    }

    fun onItemMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
        val key = dataSnapshot.key

        val index = keys.indexOf(key)
        val item = dataSnapshot.getValue<T>(this.itemClass)
        items.removeAt(index)
        keys.removeAt(index)
        val newPosition: Int
        if (previousChildName == null) {
            if (item != null) {
                items.add(0, item)
            }
            keys.add(0, key)
            newPosition = 0
        } else {
            val previousIndex = keys.indexOf(previousChildName)
            val nextIndex = previousIndex + 1
            if (nextIndex == items.size) {
                if (item != null) {
                    items.add(item)
                }
                keys.add(key)
            } else {
                if (item != null) {
                    items.add(nextIndex, item)
                }
                keys.add(nextIndex, key)
            }
            newPosition = nextIndex
        }
        notifyItemMoved(index, newPosition)
        itemMoved(item, key, index, newPosition)
    }

    ;


    /**
     * Returns the list of items of the adapter: can be useful when dealing with a configuration
     * change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of items of the adapter
     */
    fun getItems(): ArrayList<T> {
        return items
    }

    /**
     * Returns the list of keys of the items of the adapter: can be useful when dealing with a
     * configuration change (e.g.: a device rotation).
     * Just save this list before destroying the adapter and pass it to the new adapter (in the
     * constructor).
     *
     * @return the list of keys of the items of the adapter
     */
    fun getKeys(): ArrayList<String> {
        return keys
    }

    /**
     * Returns the item in the specified position
     *
     * @param position Position of the item in the adapter
     * @return the item
     */
    fun getItem(position: Int): T {
        return items[position]
    }

    /**
     * Returns the item in the specified position
     *
     * @param key Key of the item in the adapter
     * @return the item
     */
    fun getItemByKey(key: String): T {
        return items[keys.indexOf(key)]
    }


    /**
     * Returns the position of the item in the adapter
     *
     * @param item Item to be searched
     * @return the position in the adapter if found, -1 otherwise
     */
    fun getPositionForItem(item: T): Int {
        return if (items.size > 0) items.indexOf(item) else -1
    }

    /**
     * Returns the position of the item in the adapter
     *
     * @param key Key to be searched
     * @return the position in the adapter if found, -1 otherwise
     */
    fun getPositionForKey(key: String): Int {
        return if (keys.size > 0) keys.indexOf(key) else -1
    }

    /**
     * Check if the searched item is in the adapter
     *
     * @param item Item to be searched
     * @return true if the item is in the adapter, false otherwise
     */
    operator fun contains(item: T): Boolean {
        return items.contains(item)
    }

    /**
     * ABSTRACT METHODS THAT MUST BE IMPLEMENTED BY THE EXTENDING ADAPTER.
     */


    /**
     * Called after an item has been added to the adapter
     *
     * @param item     Added item
     * @param key      Key of the added item
     * @param position Position of the added item in the adapter
     */
    protected abstract fun itemAdded(item: T, key: String, position: Int)

    /**
     * Called after an item changed
     *
     * @param oldItem  Old version of the changed item
     * @param newItem  Current version of the changed item
     * @param key      Key of the changed item
     * @param position Position of the changed item in the adapter
     */
    protected abstract fun itemChanged(oldItem: T, newItem: T, key: String, position: Int)

    /**
     * Called after an item has been removed from the adapter
     *
     * @param item     Removed item
     * @param key      Key of the removed item
     * @param position Position of the removed item in the adapter
     */
    protected abstract fun itemRemoved(item: T, key: String, position: Int)

    /**
     * Called after an item changed position
     *
     * @param item        Moved item
     * @param key         Key of the moved item
     * @param oldPosition Old position of the changed item in the adapter
     * @param newPosition New position of the changed item in the adapter
     */
    protected abstract fun itemMoved(item: T?, key: String, oldPosition: Int, newPosition: Int)

}