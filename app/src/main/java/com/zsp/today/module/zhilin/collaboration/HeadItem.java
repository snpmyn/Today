package com.zsp.today.module.zhilin.collaboration;

/**
 * Created on 2026/4/23.
 *
 * @author 郑少鹏
 * @desc 头条目
 */
public class HeadItem extends ListItem {
    /**
     * 识别状态
     */
    public int recognizeState;
    /**
     * 需要展开
     */
    public boolean needExpand;
    /**
     * 数量
     */
    public int count;

    /**
     * constructor
     *
     * @param recognizeState 识别状态
     */
    public HeadItem(int recognizeState) {
        this.recognizeState = recognizeState;
    }

    /**
     * 获取标题
     *
     * @return 标题
     */
    public String getTitle() {
        String title;
        switch (recognizeState) {
            case 0:
                title = "未识别";
                break;
            case 1:
                title = "已识别";
                break;
            case 2:
                title = "已提交";
                break;
            default:
                title = "";
        }
        return title + " (" + count + ")";
    }

    /**
     * 获取类型
     *
     * @return 类型
     */
    @Override
    public int getType() {
        return TYPE_HEADER;
    }
}