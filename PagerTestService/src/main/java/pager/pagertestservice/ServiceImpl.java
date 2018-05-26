package pager.pagertestservice;

import android.util.Log;

import java.util.List;

import pager.ListDataProvider;
import pager.Pager;
import pager.DataProvider;
import pager.ITestService;
import pager.TestData;

/**
 * Sample where {@link Pager} is returned from one process to another process
 */
public class ServiceImpl implements ITestService {

    private static final String TAG = "ServiceImpl";
    private boolean dataReplaced;
    private volatile boolean dataUpdated;
    private Pager<TestData> testDataPager;
    private ListDataProvider<Integer> listDataProvider = new ListDataProvider<>();
    private Pager<Integer> listDataPager = new Pager<>(listDataProvider);


    @Override
    public Pager<TestData> getTestDataPager() {
        dataReplaced = false;
        dataUpdated = false;

        DataProvider<TestData> dataProvider = new DataProvider<TestData>() {
            @Override
            public TestData get(int position) throws IndexOutOfBoundsException {
                TestData testData = new TestData();
                testData.id = position;
                testData.intData = position;
                if (dataReplaced) {
                    if (position == 50 || position == 0) {
                        testData.intData = 500;
                    }
                }


                Log.v(TAG, "Sending data " + testData);
                return testData;
            }

            @Override
            public int getDataSize() {
                Log.v(TAG, "get getDataSize " + dataUpdated);
                return dataUpdated ? 105 : 100;
            }

            @Override
            public void close() {

            }
        };
        testDataPager = new Pager<>(dataProvider);
        return testDataPager;
    }

    /**
     * Trigger a data replace event for testing
     */
    @Override
    public void triggerDataReplace() {
        Log.v(TAG, "Notifying data replaced ");
        dataReplaced = true;

        //get 50th
        TestData data = new TestData();
        data.id = 50;
        data.intData = 50;

        TestData testDataReplace = new TestData();
        testDataReplace.id = 50;
        testDataReplace.intData = 500;

        testDataPager.getPagerNotifier().notifyDataReplaced(data, testDataReplace);

        //replace 0
        data.id = 0;
        data.intData = 0;
        testDataReplace.id = 0;

        testDataPager.getPagerNotifier().notifyDataReplaced(data, testDataReplace);

    }

    /**
     * Trigger a data update event for testing
     */
    @Override
    public void triggerDataUpdate() {
        dataUpdated = true;
        Log.v(TAG, "Notifying data set updated " + dataUpdated);
        testDataPager.getPagerNotifier().notifyDataSetChanged();
    }

    @Override
    public Pager<Integer> getListDataPager() {
        listDataProvider.clear();
        for(int i=0; i<100; i++){
            listDataProvider.add(i);
        }
        return listDataPager;
    }

    @Override
    public void triggerListDataReplace() {
        listDataProvider.set(50, 500);
        listDataProvider.set(0, 500);
    }

    @Override
    public void triggerListDataUpdate() {
        listDataProvider.add(100);
        listDataProvider.add(101);
        listDataProvider.add(102);
        listDataProvider.add(103);
        listDataProvider.add(104);
    }
}
