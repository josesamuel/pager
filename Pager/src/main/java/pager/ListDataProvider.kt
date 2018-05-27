package pager

/**
 * A [List] that implements [DataProvider]
 */
class ListDataProvider<T> : ArrayList<T>, DataProvider<T> {

    /**
     * Creates an empty [ListDataProvider]
     */
    constructor() : super()

    /**
     * Creates a [ListDataProvider] with the data from given [collection]
     */
    constructor(collection: Collection<T>) : super(collection)


    /**
     * The [PagerNotifier]. Set it from [Pager.getPagerNotifier]
     */
    protected var pagerNotifier: PagerNotifier<T>? = null

    override fun getDataSize() = size

    override fun add(element: T) = super.add(element).apply {
        if (this) pagerNotifier?.notifyDataAdded(element, size - 1)
    }

    override fun add(index: Int, element: T) = super.add(index, element).apply {
        pagerNotifier?.notifyDataAdded(element, index)
    }

    override fun addAll(elements: Collection<T>) = super.addAll(elements).apply {
        if (this) pagerNotifier?.notifyDataSetChanged()
    }

    override fun addAll(index: Int, elements: Collection<T>) = super.addAll(index, elements).apply {
        if (this) pagerNotifier?.notifyDataSetChanged()
    }

    override fun remove(element: T) = super.remove(element).apply {
        if (this) pagerNotifier?.notifyDataSetChanged()
    }

    override fun removeAll(elements: Collection<T>) = super.removeAll(elements).apply {
        if (this) pagerNotifier?.notifyDataSetChanged()
    }


    override fun removeAt(index: Int) = super.removeAt(index).apply {
        pagerNotifier?.notifyDataRemoved(index)
    }


    override fun removeRange(fromIndex: Int, toIndex: Int) = super.removeRange(fromIndex, toIndex).apply {
        pagerNotifier?.notifyDataSetChanged()
    }

    override fun clear() = super.clear().apply {
        pagerNotifier?.notifyDataSetChanged()
    }

    override fun set(index: Int, element: T) = super.set(index, element).apply {
        pagerNotifier?.notifyDataReplaced(this, element)
    }

    override fun close() {
    }

}