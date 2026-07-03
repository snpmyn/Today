package widget.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created on 2026/7/2.
 *
 * @author 郑少鹏
 * @desc API 服务
 * <p>
 * Call - 同步
 * Call - 异步
 * Call - 取消
 */
public interface ApiService {
    /**
     * 查询未识别试卷
     *
     * @param requestBody 请求体
     * @return 任务
     */
    @POST("device/unrecognizedStuNamePaper/v2")
    Call<ResponseBody> queryUnidentifyExamPaper(@Body RequestBody requestBody);
}