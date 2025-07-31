package widget.adapttemplate.bean;

/**
 * Created on 2021/12/2
 *
 * @author zsp
 * @desc 功能
 */
public class FunctionBean {
    /**
     * 功能 ID
     */
    private final int functionId;
    /**
     * 功能名称
     */
    private final String functionName;
    /**
     * 功能显示
     */
    private boolean functionShow;

    /**
     * constructor
     *
     * @param functionId   功能 ID
     * @param functionName 功能名称
     * @param functionShow 功能显示
     */
    public FunctionBean(int functionId, String functionName, boolean functionShow) {
        this.functionId = functionId;
        this.functionName = functionName;
        this.functionShow = functionShow;
    }

    public int getFunctionId() {
        return functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public boolean isFunctionShow() {
        return functionShow;
    }

    public void setFunctionShow(boolean functionShow) {
        this.functionShow = functionShow;
    }
}
