package pager;


import remoter.annotations.Remoter;

@Remoter
public interface ITestService {

    DataPager<TestData> getDataPager();

    void triggerDataReplace();

    void triggerDataUpdate();


}
