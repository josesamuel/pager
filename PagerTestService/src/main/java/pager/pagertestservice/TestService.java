package pager.pagertestservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import pager.ITestService_Stub;

/**
 * Created by jmails on 2/9/18.
 */

public class TestService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ITestService_Stub(new ServiceImpl());
    }
}
