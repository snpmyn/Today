package widget.network;

import com.qtone.scandemo.application.MyApp;
import com.qtone.scandemo.common.Constant;
import com.qtone.scandemo.module.scan.bean.UnRecStuPaperBean;
import com.qtone.scandemo.net.RetrofitClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created on 2026/7/3.
 *
 * @author 郑少鹏
 * @desc 查询未识别试卷仓库
 */
public class QueryUnidentifyExamPaperRepository extends BaseRepository {
    /**
     * 单例
     */
    private static final QueryUnidentifyExamPaperRepository INSTANCE = new QueryUnidentifyExamPaperRepository();

    /**
     * constructor
     * <p>
     * 杜绝外部通过 new 实例化 + 强制解耦
     */
    private QueryUnidentifyExamPaperRepository() {
        super();
    }

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static QueryUnidentifyExamPaperRepository getInstance() {
        return INSTANCE;
    }

    /**
     * 查询未识别试卷
     * <p>
     * 允许外部进行 cancel 从而断开强引用闭包
     *
     * @param repositoryCallback 仓库回调
     * @return 当前网络请求的 Call 对象
     */
    public Call<ResponseBody> queryUnidentifyExamPaper(RepositoryCallback<List<UnRecStuPaperBean>> repositoryCallback) {
        Call<ResponseBody> call = null;
        try {
            // ==========================================
            // 1. 组装请求参数
            // ==========================================
            HashMap<String, Object> map = new HashMap<>();
            map.put(Constant.BRAND_CODE, Constant.brandCode);
            map.put(Constant.MODE_CODE, Constant.modeCode);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constant.BatchCode, MyApp.getInstance().getCurrentBatchCode());
            // 核心业务数据通过 AES 加密包装
            map.put(Constant.DATA, CryptoManager.getInstance().encryptRequest(jsonObject));
            // 将参数 Map 转换为标准的 RequestBody
            RequestBody requestBody = RetrofitClient.createRequestBody(map);
            // ==========================================
            // 2. 创建并执行异步请求
            // ==========================================
            // 直接复用基类的 ApiService 实例
            call = apiService.queryUnidentifyExamPaper(requestBody);
            // 引入基类网络回调处理
            // 彻底抹平网络层状态码拦截、错误流关闭与异常处理的样板代码
            call.enqueue(new NetworkCallback<List<UnRecStuPaperBean>>(repositoryCallback) {
                @Override
                protected List<UnRecStuPaperBean> onParseData(String dataJsonString) {
                    // AES 解密核心内层数据
                    String decrypt = CryptoManager.getInstance().decryptResponse(dataJsonString);
                    // Gson 泛型反射解析为具体的泛型 List 并回传基类
                    return JsonMapper.fromListJson(decrypt, UnRecStuPaperBean.class);
                }
            });
        } catch (Exception e) {
            // 捕获请求在参数构建阶段的未知异常
            repositoryCallback.onResult(NetworkExceptionHandler.handleException(e));
        }
        return call;
    }
}