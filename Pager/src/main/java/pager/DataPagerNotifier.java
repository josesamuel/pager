package pager;

import remoter.annotations.ParamIn;
import remoter.annotations.Remoter;

/**
 * Use this to notify the {@link DataPager} clients that a data is replaced or data size changed.
 *
 * @see DataPager#getDataPagerNotifier()
 */
@Remoter
public interface DataPagerNotifier<T> {

    /**
     * Notify that a data is replaced with another data
     *
     * @param oldData Old data that is replaced
     * @param newData New data
     */
    void notifyDataReplaced(@ParamIn T oldData, @ParamIn T newData);

    /**
     * Notify that the data set has changed and needs to be re-fetched
     */
    void notifyDataSetChanged();

    /**
     * @hide internal method
     */
    void registerListener(@ParamIn DataPagerListener<T> listener);
}
