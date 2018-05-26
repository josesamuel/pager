package pager;


import remoter.annotations.Remoter;

/**
 * Provides the actual data to {@link Pager}
 *
 * @param <T> Any primitive type, or Parcelable, or a class annotated as @Parcel
 * @see Pager
 * @see PagerNotifier
 * @see ListDataProvider
 */
@Remoter
public interface DataProvider<T> {

    /**
     * Returns the data at the given position, or throw {@link IndexOutOfBoundsException} if position is invalid
     */
    T get(int position) throws IndexOutOfBoundsException;

    /**
     * Returns the getDataSize of the data set
     */
    int getDataSize();

    /**
     * Close the provider and do any cleanup
     */
    void close();

}
