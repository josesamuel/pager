package pager;

import remoter.annotations.ParamIn;
import remoter.annotations.Remoter;

/**
 * Use this to notify the {@link Pager} clients that a data is replaced or data getDataSize changed.
 *
 * @see Pager#getPagerNotifier()
 */
@Remoter
public interface PagerNotifier<T> {

    /**
     * Notify that a data is replaced with another data
     *
     * @param oldData Old data that is replaced
     * @param newData New data
     */
    void notifyDataReplaced(@ParamIn T oldData, @ParamIn T newData);

    /**
     * Notify that a data is added
     *
     * @param newData New data added
     * @param index   Index where added
     */
    void notifyDataAdded(@ParamIn T newData, int index);

    /**
     * Notify that a data is removed
     *
     * @param index Index where removed
     */
    void notifyDataRemoved(int index);


    /**
     * Notify that the data set has changed and needs to be re-fetched
     */
    void notifyDataSetChanged();

    /**
     * @hide internal method
     */
    void registerListener(@ParamIn PagerListener<T> listener);
}
