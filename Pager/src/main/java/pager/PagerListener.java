package pager;


import remoter.annotations.ParamIn;
import remoter.annotations.Remoter;

/**
 * Listener to register to get notified about changes to the data in a {@link Pager}
 */
@Remoter
public interface PagerListener<T> {

    /**
     * Called when a data is replaced with another data
     *
     * @param oldData Old data that is replaced
     * @param newData New data
     */
    void onDataReplaced(@ParamIn T oldData, @ParamIn T newData);

    /**
     * Notify that a data is added
     *
     * @param newData New data added
     * @param index   Index where added
     */
    void onDataAdded(@ParamIn T newData, int index);

    /**
     * Notify that a data is removed
     *
     * @param index Index where removed
     */
    void onDataRemoved(int index);


    /**
     * Called when the data set changed, like getDataSize changes
     */
    void onDataSetChanged();
}
