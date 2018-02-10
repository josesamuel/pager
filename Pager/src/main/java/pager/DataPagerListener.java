package pager;


import remoter.annotations.ParamIn;
import remoter.annotations.Remoter;

/**
 * Listener to register to get notified about changes to the data in a {@link DataPager}
 */
@Remoter
public interface DataPagerListener<T> {

    /**
     * Called when a data is replaced with another data
     *
     * @param oldData Old data that is replaced
     * @param newData New data
     */
    void onDataReplaced(@ParamIn T oldData, @ParamIn T newData);

    /**
     * Called when the data set changed, like size changes
     */
    void onDataSetChanged();
}
