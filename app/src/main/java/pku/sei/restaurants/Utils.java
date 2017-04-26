package pku.sei.restaurants;

import android.util.Log;

import com.yancloud.android.reflection.get.YanCloudGet;

import java.util.HashMap;


/**
 * Created by yue on 17-4-16.
 */
public class Utils {

    /**
     * 扫描端口 获取活动APP
     * @return
     */
    public static HashMap<String, Integer> scanPort() {
        // 扫描端口 获取启动应用列表
        Log.v("ZZZZSSSSYYYY", "scan the port start: ");
        HashMap<String, Integer> aliveApps = new HashMap<String, Integer>();
        try {
            int startPort = AppConsts.START_PORT;
            for (; startPort < AppConsts.END_PORT; startPort++) {
//                YanCloud yanCloud = YanCloud.fromGet(AppConsts.LOCAL_IP, startPort);
//                String processName = yanCloud.get(AppConsts.API_PORTER, AppConsts.METHOD_GET_PROCESS_NAME, null);
                String processName = new YanCloudGet(AppConsts.LOCAL_IP, startPort)
                        .get(AppConsts.API_PORTER, AppConsts.METHOD_GET_PROCESS_NAME, null);
                Log.d("ZZZZSSSSYYYY", processName + "::" + startPort);
                for(String packageName : AppConsts.APP_PACKAGES){
                    if(packageName.equals(processName)){
                        aliveApps.put(processName, startPort);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aliveApps;
    }

}
