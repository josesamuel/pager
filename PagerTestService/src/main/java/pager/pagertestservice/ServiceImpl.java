package pager.pagertestservice;

import android.util.Log;

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
            public int size() {
                Log.v(TAG, "get size " + dataUpdated);
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
}
