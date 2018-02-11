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
public final class Pager<T> implements Iterable<T> {

    private DataProvider<T> dataProvider;
    private PagerNotifier<T> pagerNotifier;
    @Transient
    private PagerProvider<T> pagedDataProvider;
    @Transient
    private PagerListener<T> pagerListener;

    /**
     * Creates an instance of {@link Pager}
     *
     * @param dataProvider The {@link DataProvider} that provides the actual data.
     */
    @ParcelConstructor
    public Pager(@NonNull @ParcelProperty("dataProvider") DataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
        this.pagedDataProvider = new PagerProvider<>(dataProvider);
        setPagerNotifier(new PagerNotifier<T>() {
            PagerListener<T> listener;

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
            public void registerListener(PagerListener<T> listener) {
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
     * Sets a {@link PagerListener} to listen for data replacements or data updates
     */
    public void setPagerListener(PagerListener<T> pagerListener) {
        this.pagerListener = pagerListener;
    }

    /**
     * Returns the {@link PagerNotifier} which can be used to replace a data or update the whole data.
     * <p>
     * Applicable only at the process that created this {@link Pager} instance.
     * May return null at the client side
     */
    @ParcelProperty("pagerNotifier")
    public PagerNotifier<T> getPagerNotifier() {
        if (dataProvider instanceof RemoterProxy) {
            return null;
        }
        return pagerNotifier;
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
    @ParcelProperty("pagerNotifier")
    void setPagerNotifier(PagerNotifier<T> pagerNotifier) {
        this.pagerNotifier = pagerNotifier;
        pagerNotifier.registerListener(new PagerListener<T>() {
            @Override
            public void onDataReplaced(T oldData, T newData) {
                pagedDataProvider.onDataReplaced(oldData, newData);
                if (pagerListener != null) {
                    pagerListener.onDataReplaced(oldData, newData);
                }
            }

            @Override
            public void onDataAdded(T newData, int index) {
                pagedDataProvider.onDataAdded(newData, index);
                if (pagerListener != null) {
                    pagerListener.onDataAdded(newData, index);
                }
            }

            @Override
            public void onDataRemoved(int index) {
                pagedDataProvider.onDataRemoved(index);
                if (pagerListener != null) {
                    pagerListener.onDataRemoved(index);
                }
            }

            @Override
            public void onDataSetChanged() {
                pagedDataProvider.onDataSetChanged();
                if (pagerListener != null) {
                    pagerListener.onDataSetChanged();
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
