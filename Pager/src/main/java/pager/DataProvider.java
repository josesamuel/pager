package pager;


import remoter.annotations.Remoter;

/**
 * Provides the actual data to {@link DataPager}
 *
 * @param <T> Any primitive type, or Parcelable, or a class annotated as @Parcel
 * @see DataPager
 * @see DataPagerNotifier
 */
@Remoter
public interface DataProvider<T> {

    /**
     * Returns the data at the given position, or throw {@link IndexOutOfBoundsException} if position is invalid
     */
    T get(int position) throws IndexOutOfBoundsException;

    /**
     * Returns the size of the data set
     */
    int size();

}
