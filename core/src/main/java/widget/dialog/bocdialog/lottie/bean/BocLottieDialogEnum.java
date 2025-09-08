package widget.dialog.bocdialog.lottie.bean;

/**
 * Created on 2022/6/24
 *
 * @author zsp
 * @desc BOC Lottie 对话框枚举
 */
public enum BocLottieDialogEnum {
    /**
     * 点赞一
     */
    LIKE_ONE("lottie_animation_dialog_like.json", 66, 66),
    /**
     * 空一
     */
    EMPTY_ONE("lottie_animation_dialog_empty.json", 66, 66),
    /**
     * 加载一
     */
    LOADING_ONE("lottie_animation_dialog_loading.json", 66, 66),
    /**
     * 成功一
     */
    SUCCESS_ONE("lottie_animation_dialog_success.json", 66, 66);
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
    BocLottieDialogEnum(String assetName, int width, int height) {
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