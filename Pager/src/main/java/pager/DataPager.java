package pager;


import android.support.annotation.NonNull;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;
import org.parceler.ParcelProperty;
import org.parceler.Transient;

import java.util.Iterator;

import remoter.RemoterProxy;

/**
 * Provides access to a collection of data paging them as needed.
 * <p>
 * Only a subset of the data is loaded on to memory. This window can be configured using {@link #setPageSize(int)}
 * <p>
 * This can be passed across process using AIDL or <a href="https://bit.ly/Remoter">Remoter</a>
 *
 * @param <T> Any primitive type, or Parcelable, or a class annotated as @Parcel
 * @author js
 */
@Parcel
public final class DataPager<T> implements Iterable<T> {

    private DataProvider<T> dataProvider;
    private DataPagerNotifier<T> dataPagerNotifier;
    @Transient
    private DataPagerProvider<T> pagedDataProvider;
    @Transient
    private DataPagerListener<T> dataPagerListener;

    /**
     * Creates an instance of {@link DataPager}
     *
     * @param dataProvider The {@link DataProvider} that provides the actual data.
     */
    @ParcelConstructor
    public DataPager(@NonNull @ParcelProperty("dataProvider") DataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
        this.pagedDataProvider = new DataPagerProvider<>(dataProvider);
        setDataPagerNotifier(new DataPagerNotifier<T>() {
            DataPagerListener<T> listener;

            @Override
            public void notifyDataReplaced(T oldData, T newData) {
                if (listener != null) {
                    listener.onDataReplaced(oldData, newData);
                }
            }

            @Override
            public void notifyDataAdded(T newData, int index) {
                if (listener != null) {
                    listener.onDataAdded(newData, index);
                }
            }

            @Override
            public void notifyDataRemoved(int index) {
                if (listener != null) {
                    listener.onDataRemoved(index);
                }
            }

            @Override
            public void notifyDataSetChanged() {
                if (listener != null) {
                    listener.onDataSetChanged();
                }
            }

            @Override
            public void registerListener(DataPagerListener<T> listener) {
                this.listener = listener;
            }
        });
    }

    /**
     * Returns the data at the given position
     *
     * @param position The position
     * @return T the data at the given position
     * @throws IndexOutOfBoundsException If the position is invalid
     */
    public T get(int position) throws IndexOutOfBoundsException {
        return pagedDataProvider.get(position);
    }

    /**
     * Returns the number of elements
     */
    public int size() {
        return pagedDataProvider.getDataSize();
    }

    /**
     * Sets the paging size
     */
    public void setPageSize(int size) {
        pagedDataProvider.setWindowSize(size);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int index;
            int dataSize = size();

            @Override
            public boolean hasNext() {
                return index < dataSize;
            }

            @Override
            public T next() {
                return get(index++);
            }
        };
    }

    /**
     * Sets a {@link DataPagerListener} to listen for data replacements or data updates
     */
    public void setDataPagerListener(DataPagerListener<T> dataPagerListener) {
        this.dataPagerListener = dataPagerListener;
    }

    /**
     * Returns the {@link DataPagerNotifier} which can be used to replace a data or update the whole data.
     * <p>
     * Applicable only at the process that created this {@link DataPager} instance.
     * May return null at the client side
     */
    @ParcelProperty("dataPagerNotifier")
    public DataPagerNotifier<T> getDataPagerNotifier() {
        if (dataProvider instanceof RemoterProxy) {
            return null;
        }
        return dataPagerNotifier;
    }

    /**
     * Close the data source, performs any cleanup as needed
     */
    public void close() {
        dataProvider.close();
    }

    /**
     * internal method
     *
     * @hide
     */
    @ParcelProperty("dataPagerNotifier")
    void setDataPagerNotifier(DataPagerNotifier<T> dataPagerNotifier) {
        this.dataPagerNotifier = dataPagerNotifier;
        dataPagerNotifier.registerListener(new DataPagerListener<T>() {
            @Override
            public void onDataReplaced(T oldData, T newData) {
                pagedDataProvider.onDataReplaced(oldData, newData);
                if (dataPagerListener != null) {
                    dataPagerListener.onDataReplaced(oldData, newData);
                }
            }

            @Override
            public void onDataAdded(T newData, int index) {
                pagedDataProvider.onDataAdded(newData, index);
                if (dataPagerListener != null) {
                    dataPagerListener.onDataAdded(newData, index);
                }
            }

            @Override
            public void onDataRemoved(int index) {
                pagedDataProvider.onDataRemoved(index);
                if (dataPagerListener != null) {
                    dataPagerListener.onDataRemoved(index);
                }
            }

            @Override
            public void onDataSetChanged() {
                pagedDataProvider.onDataSetChanged();
                if (dataPagerListener != null) {
                    dataPagerListener.onDataSetChanged();
                }
            }
        });
    }

    /**
     * Internal method
     *
     * @hide
     */
    @ParcelProperty("dataProvider")
    DataProvider<T> getDataProvider() {
        return dataProvider;
    }
}
