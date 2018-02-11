package pager.pagertestclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import pager.Pager;
import pager.PagerListener;
import pager.ITestService;
import pager.ITestService_Proxy;
import pager.TestData;

/**
 * Test {@link Pager} received from another service.
 */
@RunWith(AndroidJUnit4.class)
public class PagerClientTest {

    private ITestService testService;
    private boolean expectReplace;
    private boolean expectUpdate;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            testService = new ITestService_Proxy(iBinder);
            synchronized (PagerClientTest.this) {
                PagerClientTest.this.notify();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Before
    public void setup() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent("pager.pagertestservice.TestService");
        intent.setClassName("pager.pagertestservice", "pager.pagertestservice.TestService");
        synchronized (this) {
            appContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            this.wait();
        }
    }

    @After
    public void tearDown() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.unbindService(serviceConnection);
    }

    @Test
    public void testDataPager() throws Exception {
        Pager<TestData> pagedList = testService.getTestDataPager();
        pagedList.setPagerListener(new PagerListener<TestData>() {
            @Override
            public void onDataReplaced(TestData oldData, TestData newData) {
                System.out.println("NotifyReplaced " + oldData.intData + " " + newData.intData);
                Assert.assertTrue(expectReplace);
            }

            @Override
            public void onDataAdded(TestData newData, int index) {

            }

            @Override
            public void onDataRemoved(int index) {

            }

            @Override
            public void onDataSetChanged() {
                System.out.println("Test notify onDataSetChanged ");
                Assert.assertTrue(expectUpdate);
            }
        });

        Assert.assertEquals(100, pagedList.size());

        for (int i = 0; i < 100; i++) {
            TestData data = pagedList.get(i);
            System.out.println(data.intData);
            Assert.assertEquals(i, data.intData);
        }

        expectReplace = true;
        testService.triggerDataReplace();

        TestData data = pagedList.get(50);
        System.out.println("Replaced : " + data.intData);
        Assert.assertEquals(500, data.intData);

        data = pagedList.get(0);
        System.out.println("Replaced : " + data.intData);
        Assert.assertEquals(500, data.intData);

        expectUpdate = true;
        testService.triggerDataUpdate();

        Assert.assertEquals(105, pagedList.size());
        for (int i = 104; i >= 0; i--) {
            data = pagedList.get(i);
            System.out.println(data.intData);
            if (i == 0 || i == 50) {
                Assert.assertEquals(500, data.intData);
            } else {
                Assert.assertEquals(i, data.intData);
            }
        }
    }


    @Test
    public void testPagerIterate() {

        Pager<TestData> pagedList = testService.getTestDataPager();

        int index = 0;
        for (TestData data : pagedList) {
            Assert.assertEquals(index, data.intData);
            index++;
        }

        Assert.assertEquals(100, index);

        Assert.assertNull(pagedList.getPagerNotifier());
    }
}
