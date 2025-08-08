package widget.dialog.bocdialog.lottie.bean;

/**
 * Created on 2022/6/24
 *
 * @author zsp
 * @desc 对话框 Lottie 动画枚举
 */
public enum DialogLottieAnimationEnum {
    /**
     * 点赞一
     */
    LIKE_ONE("lottie_animation_dialog_like_one.json", 66, 66),
    /**
     * 加载一
     */
    LOADING_ONE("lottie_animation_dialog_loading_one.json", 66, 66),
    /**
     * 加载二
     */
    LOADING_TWO("lottie_animation_dialog_loading_two.json", 66, 66),
    /**
     * 成功一
     */
    SUCCESS_ONE("lottie_animation_dialog_success_one.json", 66, 66),
    /**
     * 成功二
     */
    SUCCESS_TWO("lottie_animation_dialog_success_two.json", 66, 66),
    /**
     * 第一套结果警告
     */
    FIRST_SET_RESULT_WARN("lottie_animation_dialog_first_set_result_warn.json", 56, 56),
    /**
     * 第一套结果失败
     */
    FIRST_SET_RESULT_FAIL("lottie_animation_dialog_first_set_result_fail.json", 56, 56),
    /**
     * 第一套结果成功
     */
    FIRST_SET_RESULT_SUCCESS("lottie_animation_dialog_first_set_result_success.json", 56, 56),
    /**
     * 第二套结果失败
     */
    SECOND_SET_RESULT_FAIL("lottie_animation_dialog_second_set_result_fail.json", 56, 56),
    /**
     * 第二套结果成功
     */
    SECOND_SET_RESULT_SUCCESS("lottie_animation_dialog_second_set_result_success.json", 56, 56);
    /**
     * 资产名
     * <p>
     * 如 "camera.json"
     */
    private final String assetName;
    /**
     * 宽
     */
    private final int width;
    /**
     * 高
     */
    private final int height;

    /**
     * constructor
     *
     * @param assetName 资产名
     *                  如 "camera.json"
     * @param width     宽
     * @param height    高
     */
    DialogLottieAnimationEnum(String assetName, int width, int height) {
        this.assetName = assetName;
        this.width = width;
        this.height = height;
    }

    public String getAssetName() {
        return assetName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
