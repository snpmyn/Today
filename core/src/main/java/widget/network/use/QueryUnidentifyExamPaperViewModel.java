package widget.network.use;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.qtone.scandemo.module.scan.bean.UnRecStuPaperBean;
import com.qtone.scandemo.widget.net.NetworkResult;
import com.qtone.scandemo.widget.net.QueryUnidentifyExamPaperRepository;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created on 2026/7/2.
 *
 * @author 郑少鹏
 * @desc 查询未识别试卷 ViewModel
 */
public class QueryUnidentifyExamPaperViewModel extends ViewModel {
    /**
     * 具有生命周期感知能力的可观察数据容器
     */
    public final MutableLiveData<NetworkResult<List<UnRecStuPaperBean>>> liveData = new MutableLiveData<>();
    /**
     * 活动任务
     * <p>
     * 暂存当前正在活跃的异步网络任务指针
     */
    private Call<ResponseBody> activeCall;

    /**
     * 查询未识别试卷
     */
    public void queryUnidentifyExamPaper() {
        // 容错防刷
        // 发起新请求前，若前一次请求因网速慢未结束，则强行取消前一个，防止返回数据多点交织错乱。
        cancelActiveCall();
        // 挂载并同步当前活跃的 Call 引用
        // 解耦接收仓库回调并分发数据
        activeCall = QueryUnidentifyExamPaperRepository.getInstance().queryUnidentifyExamPaper(liveData::postValue);
    }

    /**
     * 取消活动任务
     * <p>
     * 安全取消当前正在请求中的网络任务
     */
    private void cancelActiveCall() {
        if ((activeCall != null) && !activeCall.isCanceled()) {
            activeCall.cancel();
            activeCall = null;
        }
    }

    /**
     * 当对应 Activity 退出销毁、ViewModel 声明周期彻底走向终点时系统自动回调
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        // 彻底取消未完成的网络请求
        // 执行后即便底层返回网络数据
        // Repository 回调中的 call.isCanceled() 也会拦截并直接 return
        // 隐式持有外部引用的闭包逻辑线彻底断开
        // Repository 及其内部类占用的内存能够被 GC 瞬间回收
        // 规避内存泄漏
        cancelActiveCall();
    }
}