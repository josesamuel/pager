package pager;

import org.parceler.Parcel;

@Parcel
public class TestData {

    protected int id;
    protected int intData;

    @Override
    public boolean equals(Object obj) {
        return id == ((TestData) obj).id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return id + " : " + intData;
    }
}
