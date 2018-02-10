package pager;


import junit.framework.Assert;

import org.junit.Test;


public class DataPagerTest {

    private boolean expectPagedFill;
    private boolean expectReplace;
    private boolean expectUpdate;

    @Test
    public void testPagerTraversal() {
        DataProvider<Integer> dataProvider = new DataProvider<Integer>() {
            @Override
            public Integer get(int position) throws IndexOutOfBoundsException {
                Assert.assertTrue(expectPagedFill);
                return position;
            }

            @Override
            public int size() {
                return 100;
            }
        };

        DataPager<Integer> pagedList = new DataPager<>(dataProvider);

        Assert.assertEquals(100, pagedList.size());

        for (int i = 0; i < 100; i++) {
            if (i == 0 || i == 30 || i == 45 || i == 60 || i == 75 || i == 90) {
                expectPagedFill = true;
            }
            Integer data = pagedList.get(i);
            expectPagedFill = false;
            System.out.println(data);
            Assert.assertEquals((Integer) i, data);
        }

        for (int i = 99; i >= 0; i--) {
            if (i == 10 || i == 26 || i == 42 || i == 58 || i == 74) {
                expectPagedFill = true;
            }
            Integer data = pagedList.get(i);
            expectPagedFill = false;
            System.out.println(data);
            Assert.assertEquals((Integer) i, data);
        }
    }


    @Test
    public void testPagerUpdate() {
        DataProvider<TestData> dataProvider = new DataProvider<TestData>() {
            @Override
            public TestData get(int position) throws IndexOutOfBoundsException {
                TestData testData = new TestData();
                testData.id = position;
                testData.intData = position;
                if (expectReplace) {
                    if (position == 50 || position == 0) {
                        testData.intData = 500;
                    }
                }
                return testData;
            }

            @Override
            public int size() {
                return expectUpdate ? 105 : 100;
            }
        };

        DataPager<TestData> pagedList = new DataPager<>(dataProvider);
        pagedList.setDataPagerListener(new DataPagerListener<TestData>() {
            @Override
            public void onDataReplaced(TestData oldData, TestData newData) {
                System.out.println("NotifyReplaced " + oldData.intData + " " + newData.intData);
                Assert.assertTrue(expectReplace);
            }

            @Override
            public void onDataSetChanged() {
                System.out.println("onDataSetChanged ");
                Assert.assertTrue(expectUpdate);
            }
        });

        Assert.assertEquals(100, pagedList.size());

        for (int i = 0; i < 100; i++) {
            TestData data = pagedList.get(i);
            System.out.println(data.intData);
            Assert.assertEquals(i, data.intData);
        }

        //get 50th
        TestData data = pagedList.get(50);
        System.out.println(data.intData);
        Assert.assertEquals(50, data.intData);

        TestData testDataReplace = new TestData();
        testDataReplace.id = 50;
        testDataReplace.intData = 500;
        expectReplace = true;

        pagedList.getDataPagerNotifier().notifyDataReplaced(data, testDataReplace);

        //replace 0
        data.id = 0;
        data.intData = 0;
        testDataReplace.id = 0;


        pagedList.getDataPagerNotifier().notifyDataReplaced(data, testDataReplace);

        data = pagedList.get(50);
        System.out.println("Replaced : " + data.intData);
        Assert.assertEquals(500, data.intData);

        data = pagedList.get(0);
        System.out.println("Replaced : " + data.intData);
        Assert.assertEquals(500, data.intData);

        expectUpdate = true;
        pagedList.getDataPagerNotifier().notifyDataSetChanged();

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
        DataProvider<Integer> dataProvider = new DataProvider<Integer>() {
            @Override
            public Integer get(int position) throws IndexOutOfBoundsException {
                return position;
            }

            @Override
            public int size() {
                return 100;
            }
        };

        DataPager<Integer> pagedList = new DataPager<>(dataProvider);

        int index = 0;
        for (int data : pagedList) {
            Assert.assertEquals(index, data);
            index++;
        }

        Assert.assertEquals(100, index);

    }
}