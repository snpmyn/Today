package widget.tbs.value;

/**
 * Created on 2021/3/26
 *
 * @author zsp
 * @desc TBS 枚举
 */
public enum TbsEnum {
    /**
     * 打开失败
     */
    OPEN_FAIL(-1, "打开失败"),
    /**
     * 用 QQ 浏览器打开
     */
    QQ(1, "用 QQ 浏览器打开"),
    /**
     * 用 MiniQB 打开
     */
    MINI_QB(2, "用 MiniQB 打开"),
    /**
     * 调阅读器弹框
     */
    READER(3, "调阅读器弹框");
    /**
     * 类型
     */
    private final int type;
    /**
     * 提示
     */
    private final String hint;

    /**
     * constructor
     *
     * @param type 类型
     * @param hint 提示
     */
    TbsEnum(int type, String hint) {
        this.type = type;
        this.hint = hint;
    }

    public int getType() {
        return type;
    }

    public String getHint() {
        return hint;
    }
}
