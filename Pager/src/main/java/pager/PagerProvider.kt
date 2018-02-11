package pager

/**
 * Provides paging to the data provided by DataProvider
 *
 * @author js
 */
internal class PagerProvider<T>(private val dataProvider: DataProvider<T>) : PagerListener<T> {

    var windowSize = 30
    var dataSize = dataProvider.size()
        private set
    private var pagedData = mutableListOf<T>()
    private var currentStart = -1
    private var currentEnd = -1


    /**
     * Fill the window and returns the data at given position
     */
    @Throws(IndexOutOfBoundsException::class)
    @Synchronized
    operator fun get(position: Int): T? {
        fillIfNeeded(position)
        return pagedData[position - currentStart]
    }

    /**
     * Fill the window as needed
     */
    private fun fillIfNeeded(index: Int) {
        if (index < 0 || index >= dataSize) {
            throw IllegalArgumentException("$index is out of range")
        }

        //Check whether out of current window
        if (index < currentStart || index > currentEnd) {

            //needs to be refilled

            val range = windowSize / 2
            var newStart = index - range
            if (newStart < 0) {
                newStart = 0
            }
            var newEnd = newStart + windowSize - 1
            if (newEnd >= dataSize) {
                newEnd = dataSize - 1
            }

            //check whether new range overlaps
            if ((newEnd > currentStart && newEnd < currentEnd) || (newStart > currentStart && newStart < currentEnd)) {

                //keep the overlaps.. remove the leftovers
                if (newStart < currentStart) {
                    if (currentEnd >= 0) {
                        val removeCount = currentEnd - newEnd
                        for (i in 1..removeCount) {
                            pagedData.removeAt(newEnd + 1 - currentStart)
                        }
                    }
                    for (i in currentStart - 1 downTo newStart) {
                        pagedData.add(0, dataProvider[i])
                    }
                } else {
                    if (currentStart >= 0) {
                        val removeCount = newStart - currentStart
                        for (i in 1..removeCount) {
                            pagedData.removeAt(0)
                        }
                    }

                    for (i in (currentEnd + 1)..newEnd) {
                        pagedData.add(dataProvider[i])
                    }

                }
            } else {
                //no overlaps, start over
                pagedData.clear()
                for (i in newStart..newEnd) {
                    pagedData.add(dataProvider[i])
                }
            }

            currentStart = newStart
            currentEnd = newEnd

        }
    }

    override fun onDataReplaced(oldData: T, newData: T) {
        pagedData.forEachIndexed { index, t -> if (t == oldData) pagedData[index] = newData }
    }

    @Synchronized
    override fun onDataSetChanged() {
        dataSize = dataProvider.size()
        currentStart = -1
        currentEnd = -1
        pagedData.clear()
    }

    @Synchronized
    override fun onDataAdded(newData: T, index: Int) {
        dataSize = dataProvider.size()
        if (index < currentStart) {
            currentStart++
            currentEnd++
        } else if (index in currentStart..currentEnd) {
            pagedData.add(index - currentStart, newData)
            pagedData.removeAt(pagedData.size - 1)
        }
    }

    @Synchronized
    override fun onDataRemoved(index: Int) {
        dataSize = dataProvider.size()
        if (index < currentStart) {
            currentStart--
            currentEnd--
        } else if (index in currentStart..currentEnd) {
            pagedData.removeAt(index - currentStart)
            currentEnd--
            if (currentEnd < currentStart) {
                currentStart = -1
                currentEnd = -1
            }
        }
    }


}
