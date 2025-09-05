package com.zsp.today.widget.excel;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import com.zsp.today.application.App;
import com.zsp.today.module.account.database.AccountDataBaseTable;
import com.zsp.today.module.account.value.AccountCondition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import litepal.kit.LitePalKit;
import util.data.BigDecimalUtils;
import util.log.LogUtils;

/**
 * Created on 2021/1/20
 *
 * @author zsp
 * @desc excel 配套元件
 */
public class ExcelKit {
    private WritableCellFormat writableCellFormatTitle = null;
    private WritableCellFormat writableCellFormatContent = null;

    /**
     * 初始化字体和格式
     */
    private void initFontAndFormat() {
        try {
            // 标题（字体、格式）
            WritableFont writableFontTitle = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            writableFontTitle.setColour(Colour.WHITE);
            writableCellFormatTitle = new WritableCellFormat(writableFontTitle);
            writableCellFormatTitle.setBackground(Colour.LIGHT_BLUE);
            writableCellFormatTitle.setAlignment(Alignment.CENTRE);
            writableCellFormatTitle.setBorder(Border.ALL, BorderLineStyle.THIN);
            // 内容（字体、格式）
            WritableFont writableFontContent = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            writableFontContent.setColour(Colour.GRAY_50);
            writableCellFormatContent = new WritableCellFormat(writableFontContent);
            writableCellFormatContent.setBackground(Colour.BLACK);
            writableCellFormatContent.setAlignment(Alignment.CENTRE);
            writableCellFormatContent.setBorder(Border.ALL, BorderLineStyle.THIN);
        } catch (WriteException e) {
            LogUtils.exception(e);
        }
    }

    /**
     * 初始化 excel
     *
     * @param fullPath 全路径
     * @param head     头
     * @param titles   标题
     */
    public void initExcel(String fullPath, String head, List<String> titles) {
        initFontAndFormat();
        WritableWorkbook writableWorkbook = null;
        try {
            writableWorkbook = Workbook.createWorkbook(new File(fullPath));
            WritableSheet writableSheet = writableWorkbook.createSheet(head, 0);
            // 标题
            for (int i = 0; i < titles.size(); i++) {
                writableSheet.addCell(new Label(i + 1, 0, titles.get(i), writableCellFormatTitle));
            }
            // 头行高
            writableSheet.setRowView(0, 500);
            writableWorkbook.write();
        } catch (Exception e) {
            LogUtils.exception(e);
        } finally {
            if (null != writableWorkbook) {
                try {
                    writableWorkbook.close();
                } catch (Exception e) {
                    LogUtils.exception(e);
                }
            }
        }
    }

    /**
     * 写入 excel
     *
     * @param appCompatActivity 活动
     * @param fullPath          全路径
     * @param columnHeads       列头
     * @param titles            标题
     * @param excelKitInterface excel 配套元件接口
     */
    @SuppressLint("NewApi")
    public void writeToExcel(AppCompatActivity appCompatActivity, String fullPath, List<String> columnHeads, List<String> titles, ExcelKitInterface excelKitInterface) {
        InputStream inputStream = null;
        File file = new File(fullPath);
        WritableWorkbook writableWorkbook = null;
        try {
            WorkbookSettings workbookSettings = new WorkbookSettings();
            workbookSettings.setEncoding("UTF-8");
            inputStream = Files.newInputStream(file.toPath());
            Workbook workbook = Workbook.getWorkbook(inputStream);
            writableWorkbook = Workbook.createWorkbook(file, workbook);
            WritableSheet writableSheet = writableWorkbook.getSheet(0);
            // 头列宽
            writableSheet.setColumnView(0, 16);
            for (int i = 0; i < columnHeads.size(); i++) {
                // 其它行高（i + 1 排除头行）
                writableSheet.setRowView(i + 1, 500);
                // 头列日期
                writableSheet.addCell(new Label(0, i + 1, columnHeads.get(i), writableCellFormatContent));
                // 根据去重后日期查询处理每日期下账目数据
                List<AccountDataBaseTable> accountDataBaseTableList = LitePalKit.getInstance().queryByWhere(AccountDataBaseTable.class, AccountCondition.ACCOUNT_PHONE_NUMBER_AND_DATE, App.getAppInstance().getPhoneNumber(), columnHeads.get(i));
                for (int j = 0; j < accountDataBaseTableList.size(); j++) {
                    // 其它列宽（j + 1 排除头列）
                    writableSheet.setColumnView(j + 1, 12);
                    // 每行账目
                    AccountDataBaseTable accountDataBaseTable = accountDataBaseTableList.get(j);
                    // 其它列金额
                    writableSheet.addCell(new Label(titles.indexOf(accountDataBaseTable.getCategory()) + 1, i + 1, BigDecimalUtils.bigDecimalToString(BigDecimal.valueOf(accountDataBaseTable.getAmount())), writableCellFormatContent));
                }
            }
            writableWorkbook.write();
            excelKitInterface.writeToExcelSuccessful(appCompatActivity, fullPath);
        } catch (Exception e) {
            LogUtils.exception(e);
        } finally {
            if (null != writableWorkbook) {
                try {
                    writableWorkbook.close();
                } catch (Exception e) {
                    LogUtils.exception(e);
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogUtils.exception(e);
                }
            }
        }
    }

    /**
     * excel 配套元件接口
     */
    public interface ExcelKitInterface {
        /**
         * 写入 excel 成功
         *
         * @param appCompatActivity 活动
         * @param fullPath          全路径
         */
        void writeToExcelSuccessful(AppCompatActivity appCompatActivity, String fullPath);
    }
}