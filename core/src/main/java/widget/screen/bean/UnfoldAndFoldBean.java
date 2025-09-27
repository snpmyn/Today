package widget.screen.bean;

import java.util.List;

/**
 * Created on 2019/9/28.
 *
 * @author 郑少鹏
 * @desc 展开 / 折叠数据
 */
public class UnfoldAndFoldBean {
    /**
     * 主控类别
     */
    private final String activeControlClassification;
    /**
     * 主控条件
     */
    private final String activeControlCondition;
    /**
     * 被控类别数据
     */
    private final List<String> passiveControlClassificationList;

    /**
     * constructor
     *
     * @param activeControlClassification      主控类别
     * @param activeControlCondition           主控条件
     * @param passiveControlClassificationList 被控类别数据
     */
    public UnfoldAndFoldBean(String activeControlClassification, String activeControlCondition, List<String> passiveControlClassificationList) {
        this.activeControlClassification = activeControlClassification;
        this.activeControlCondition = activeControlCondition;
        this.passiveControlClassificationList = passiveControlClassificationList;
    }

    public String getActiveControlClassification() {
        return activeControlClassification;
    }

    public String getActiveControlCondition() {
        return activeControlCondition;
    }

    public List<String> getPassiveControlClassificationList() {
        return passiveControlClassificationList;
    }
}