package pager;


import remoter.annotations.Remoter;

@Remoter
public interface ITestService {

    Pager<TestData> getTestDataPager();

    void triggerDataReplace();

    void triggerDataUpdate();

}
