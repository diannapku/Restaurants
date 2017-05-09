package pku.sei.restaurants;
//import java.util.HashMap;

/**
 * Created by heyunqi on 17/4/15.
 */
public class AppConsts {


    public static final String LOCAL_IP = "127.0.0.1";

    public static final int START_PORT = 1700;

    public static final int END_PORT = 1800;

    public static final String API_PORTER = "APIPorter";

    public static final String METHOD_GET_PROCESS_NAME = "getProcessName";

    public static final String[] APP_PACKAGES = new String[] {
            "com.sankuai.meituan.takeoutnew", "me.ele", "com.baidu.lbs.waimai"
    };

    public static final String WEIGHT = "weight";

    public static final String DISTANCE = "distance";

    public static final String SCORE = "score";

    public static final String ORDERS = "orders";

    public static final String DTIME = "delivery time";

    public static final String[] DIMENSIONS = new String[] {
            "weight", "distance", "score", "orders", "delivery time"
    };

//    public static final HashMap<String, String> APP_THREAD = new HashMap<>() {
//        {
//            put("com.sankuai.meituan.takeoutnew", "MeiTuanThread");
//            put("me.ele", "EleMeThread");
//            put("com.baidu.lbs.waimai", "BaiduThread");
//        }
//    };

}