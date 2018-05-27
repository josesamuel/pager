package pager.pagertestclient;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import pager.ListDataProvider;
import pager.Pager;
import pager.PagerListener;

/**
 * Test {@link Pager} received from another service.
 */
public class LocalPagerClientTest {

    private Pager<Integer> localPager;
    private ListDataProvider<Integer> dataProvider;
    private boolean expectReplace;
    private boolean expectUpdate;


    @Before
    public void setup() throws Exception {
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            data.add(i);
        }
        dataProvider = new ListDataProvider<>(data);
        localPager = new Pager<>(dataProvider);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testListDataPager() throws Exception {
        Pager<Integer> pagedList = localPager;
        pagedList.setPagerListener(new PagerListener<Integer>() {
            @Override
            public void onDataReplaced(Integer oldData, Integer newData) {
                System.out.println("NotifyReplaced " + oldData + " " + newData);
                Assert.assertTrue(expectReplace);
            }

            @Override
            public void onDataAdded(Integer newData, int index) {

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
            Integer data = pagedList.get(i);
            System.out.println(data);
            Assert.assertEquals(i, data.intValue());
        }

        expectReplace = true;
        dataProvider.set(50, 500);
        dataProvider.set(0, 500);

        Integer data = pagedList.get(50);
        System.out.println("Replaced : " + data);
        Assert.assertEquals(500, data.intValue());

        data = pagedList.get(0);
        System.out.println("Replaced : " + data);
        Assert.assertEquals(500, data.intValue());

        expectUpdate = true;
        dataProvider.add(100);
        dataProvider.add(101);
        dataProvider.add(102);
        dataProvider.add(103);
        dataProvider.add(104);

        System.out.println("Dataprovider size : " + dataProvider.size());
        System.out.println("Dataprovider getDataSize : " + dataProvider.getDataSize());
        System.out.println("pagedList size : " + pagedList.size());

        Assert.assertEquals(105, dataProvider.size());
        Assert.assertEquals(105, dataProvider.getDataSize());
        Assert.assertEquals(105, pagedList.size());
        for (int i = 104; i >= 0; i--) {
            data = pagedList.get(i);
            System.out.println(data);
            if (i == 0 || i == 50) {
                Assert.assertEquals(500, data.intValue());
            } else {
                Assert.assertEquals(i, data.intValue());
            }
        }
    }


    @Test
    public void testIntPagerIterate() {

        Pager<Integer> pagedList = localPager;

        int index = 0;
        for (Integer data : pagedList) {
            Assert.assertEquals(index, data.intValue());
            index++;
        }

        Assert.assertEquals(100, index);

        Assert.assertNotNull(pagedList.getPagerNotifier());
    }
}
