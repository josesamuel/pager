package pager;


import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class PagerTest {

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
            public int getDataSize() {
                return 100;
            }

            @Override
            public void close() {

            }
        };

        Pager<Integer> pagedList = new Pager<>(dataProvider);

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
            public int getDataSize() {
                return expectUpdate ? 105 : 100;
            }

            @Override
            public void close() {

            }
        };

        Pager<TestData> pagedList = new Pager<>(dataProvider);
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

        pagedList.getPagerNotifier().notifyDataReplaced(data, testDataReplace);

        //replace 0
        data.id = 0;
        data.intData = 0;
        testDataReplace.id = 0;


        pagedList.getPagerNotifier().notifyDataReplaced(data, testDataReplace);

        data = pagedList.get(50);
        System.out.println("Replaced : " + data.intData);
        Assert.assertEquals(500, data.intData);

        data = pagedList.get(0);
        System.out.println("Replaced : " + data.intData);
        Assert.assertEquals(500, data.intData);

        expectUpdate = true;
        pagedList.getPagerNotifier().notifyDataSetChanged();

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
            public int getDataSize() {
                return 100;
            }

            @Override
            public void close() {

            }
        };

        Pager<Integer> pagedList = new Pager<>(dataProvider);

        int index = 0;
        for (int data : pagedList) {
            Assert.assertEquals(index, data);
            index++;
        }

        Assert.assertEquals(100, index);

    }

    @Test
    public void testAdd() {
        final List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            data.add(i);
        }

        DataProvider<Integer> dataProvider = new DataProvider<Integer>() {
            @Override
            public Integer get(int position) throws IndexOutOfBoundsException {
                return data.get(position);
            }

            @Override
            public int getDataSize() {
                return data.size();
            }

            @Override
            public void close() {
            }
        };

        Pager<Integer> pagedList = new Pager<>(dataProvider);

        int index = 0;
        for (int readData : pagedList) {
            Assert.assertEquals(index, readData);
            index++;
        }

        Assert.assertEquals(100, index);

        //add data
        for (int i = 50; i < 60; i++) {
            data.add(i, i);
            pagedList.getPagerNotifier().notifyDataAdded(i, i);
        }

        //add data
        for (int i = 0; i < 5; i++) {
            data.add(i, i);
            pagedList.getPagerNotifier().notifyDataAdded(i, i);
        }

        for (int i = 0; i < 5; i++) {
            int addIndex = data.size();
            data.add(addIndex, i);
            pagedList.getPagerNotifier().notifyDataAdded(addIndex, i);
        }


        Assert.assertEquals(data.size(), pagedList.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertEquals(data.get(i), pagedList.get(i));
        }

    }

    @Test
    public void testRemove() {
        final List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            data.add(i);
        }

        DataProvider<Integer> dataProvider = new DataProvider<Integer>() {
            @Override
            public Integer get(int position) throws IndexOutOfBoundsException {
                return data.get(position);
            }

            @Override
            public int getDataSize() {
                return data.size();
            }

            @Override
            public void close() {
            }
        };

        Pager<Integer> pagedList = new Pager<>(dataProvider);

        int index = 0;
        for (int readData : pagedList) {
            Assert.assertEquals(index, readData);
            index++;
        }

        Assert.assertEquals(100, index);

        //add data
        for (int i = 50; i < 60; i++) {
            data.remove(i);
            pagedList.getPagerNotifier().notifyDataRemoved(i);
        }

        //add data
        for (int i = 0; i < 5; i++) {
            data.remove(0);
            pagedList.getPagerNotifier().notifyDataRemoved(0);
        }

        for (int i = 0; i < 5; i++) {
            int removeIndex = data.size() - 1;
            data.remove(removeIndex);
            pagedList.getPagerNotifier().notifyDataRemoved(removeIndex);
        }


        Assert.assertEquals(data.size(), pagedList.size());

        for (int i = 0; i < data.size(); i++) {
            Assert.assertEquals(data.get(i), pagedList.get(i));
        }

    }

}