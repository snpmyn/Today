package litepal.kit;

import org.jetbrains.annotations.NotNull;

import litepal.LitePal;
import litepal.crud.LitePalSupport;

import java.util.Collection;
import java.util.List;

/**
 * Created on 2020/12/18
 *
 * @author zsp
 * @desc LitePal 配套元件
 */
public class LitePalKit {
    public static LitePalKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 单个保存
     *
     * @param t   T
     * @param <T> <T>
     * @return 保存成功否
     */
    public <T extends LitePalSupport> boolean singleSave(@NotNull T t) {
        return t.save();
    }

    /**
     * 多个保存
     * <p>
     * {@link LitePal#saveAll(Collection)} 返 true / false 两种返回值。
     * true 表集合中所有数据都存储到数据库；false 表存储过程中发生异常，无任何数据存储到数据库。
     * {@link LitePal#saveAll(Collection)} 内部开启了事务，全部存储成功或失败，无部分存储成功场景。
     *
     * @param collection Collection<? extends T>
     * @param <T>        <T>
     * @return 保存成功否
     */
    public <T extends LitePalSupport> boolean multiSave(Collection<? extends T> collection) {
        return LitePal.saveAll(collection);
    }

    /**
     * 单个删除
     *
     * @param modelClass 模型类
     * @param id         ID
     * @param <T>        <T>
     */
    public <T extends LitePalSupport> void singleDelete(Class<T> modelClass, long id) {
        LitePal.delete(modelClass, id);
    }

    /**
     * 多个删除
     *
     * @param modelClass 模型类
     * @param conditions 条件
     * @param <T>        <T>
     */
    public <T extends LitePalSupport> void multiDelete(Class<T> modelClass, String... conditions) {
        LitePal.deleteAll(modelClass, conditions);
    }

    /**
     * 全部删除
     *
     * @param modelClass 模型类
     * @param <T>        <T>
     */
    public <T extends LitePalSupport> void allDelete(Class<T> modelClass) {
        LitePal.deleteAll(modelClass);
    }

    /**
     * 删除数据库
     *
     * @param dbName 数据库名
     * @return 删除成功否
     */
    public boolean deleteDatabase(String dbName) {
        return LitePal.deleteDatabase(dbName);
    }

    /**
     * 单个更新
     *
     * @param t   T
     * @param id  ID
     * @param <T> <T>
     */
    public <T extends LitePalSupport> void singleUpdate(@NotNull T t, long id) {
        t.update(id);
    }

    /**
     * 多个更新
     *
     * @param t          T
     * @param conditions 条件
     * @param <T>        <T>
     */
    public <T extends LitePalSupport> void multiUpdate(@NotNull T t, String... conditions) {
        t.updateAll(conditions);
    }

    /**
     * 查询头条
     *
     * @param modelClass 模型类
     * @param <T>        <T>
     * @return 头条结果
     */
    public <T extends LitePalSupport> T findFirst(Class<T> modelClass) {
        return LitePal.findFirst(modelClass);
    }

    /**
     * 查询全部
     *
     * @param modelClass 模型类
     * @param <T>        <T>
     * @return 结果集合
     */
    public <T extends LitePalSupport> List<T> findAll(Class<T> modelClass) {
        return LitePal.findAll(modelClass);
    }

    /**
     * 条件查询
     *
     * @param modelClass 模型类
     * @param conditions 条件
     * @param <T>        <T>
     * @return 结果集合
     */
    public <T extends LitePalSupport> List<T> queryByWhere(Class<T> modelClass, String... conditions) {
        return LitePal.where(conditions).find(modelClass);
    }

    /**
     * 排序并且选择查询
     * <p>
     * asc 正序、desc 倒序。
     *
     * @param modelClass 模型类
     * @param column     列
     * @param asc        升序否
     * @param columns    列
     * @param <T>        <T>
     * @return 结果集合
     */
    public <T extends LitePalSupport> List<T> queryByOrderAndSelect(Class<T> modelClass, String column, boolean asc, String... columns) {
        return LitePal.order(column + (asc ? " asc" : " desc")).select(columns).find(modelClass);
    }

    /**
     * 条件并且排序并且选择查询
     * <p>
     * asc 正序、desc 倒序。
     *
     * @param modelClass 模型类
     * @param conditions 条件
     * @param column     列
     * @param asc        升序否
     * @param columns    列
     * @param <T>        <T>
     * @return 结果集合
     */
    public <T extends LitePalSupport> List<T> queryByWhereAndOrderAndSelect(Class<T> modelClass, String[] conditions, String column, boolean asc, String... columns) {
        return LitePal.where(conditions).order(column + (asc ? " asc" : " desc")).select(columns).find(modelClass);
    }

    /**
     * 存在
     *
     * @param modelClass 模型类
     * @param conditions 条件
     * @param <T>        <T>
     * @return 存在
     */
    public <T extends LitePalSupport> Boolean areExit(Class<T> modelClass, String... conditions) {
        return LitePal.isExist(modelClass, conditions);
    }

    /**
     * 计数
     *
     * @param tableName 表名
     * @return 行数
     */
    public int count(String tableName) {
        return LitePal.count(tableName);
    }

    /**
     * 条件计数
     *
     * @param tableName  表名
     * @param conditions 条件
     * @return 行数
     */
    public int countByWhere(String tableName, String... conditions) {
        return LitePal.where(conditions).count(tableName);
    }

    /**
     * 计数
     *
     * @param modelClass 模型类
     * @param <T>        <T>
     * @return 行数
     */
    public <T extends LitePalSupport> int count(Class<T> modelClass) {
        return LitePal.count(modelClass);
    }

    /**
     * 条件计数
     *
     * @param modelClass 模型类
     * @param conditions 条件
     * @param <T>        <T>
     * @return 行数
     */
    public <T extends LitePalSupport> int countByWhere(Class<T> modelClass, String... conditions) {
        return LitePal.where(conditions).count(modelClass);
    }

    /**
     * 求和
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return 和
     */
    public int sum(String tableName, String columnName) {
        return LitePal.sum(tableName, columnName, int.class);
    }

    /**
     * 条件求和
     *
     * @param tableName  表名
     * @param columnName 列名
     * @param conditions 条件
     * @return 和
     */
    public int sumByWhere(String tableName, String columnName, String... conditions) {
        return LitePal.where(conditions).sum(tableName, columnName, Integer.TYPE);
    }

    /**
     * 求和
     *
     * @param modelClass 模型类
     * @param columnName 列名
     * @param <T>        <T>
     * @return 和
     */
    public <T extends LitePalSupport> int sum(Class<T> modelClass, String columnName) {
        return LitePal.sum(modelClass, columnName, int.class);
    }

    /**
     * 条件求和
     *
     * @param modelClass 模型类
     * @param columnName 列名
     * @param conditions 条件
     * @param <T>        <T>
     * @return 和
     */
    public <T extends LitePalSupport> int sumByWhere(Class<T> modelClass, String columnName, String... conditions) {
        return LitePal.where(conditions).sum(modelClass, columnName, Integer.TYPE);
    }

    /**
     * 平均
     * <p>
     * 具有运算能力列可求平均值。
     * 字符串类型列无法求平均值，返 0。
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return 均值
     */
    public Double average(String tableName, String columnName) {
        return LitePal.average(tableName, columnName);
    }

    /**
     * 条件平均
     * <p>
     * 具有运算能力列可求平均值。
     * 字符串类型列无法求平均值，返 0。
     *
     * @param tableName  表名
     * @param columnName 列名
     * @param conditions 条件
     * @return 均值
     */
    public Double averageByWhere(String tableName, String columnName, String... conditions) {
        return LitePal.where(conditions).average(tableName, columnName);
    }

    /**
     * 平均
     * <p>
     * 具有运算能力列可求平均值。
     * 字符串类型列无法求平均值，返 0。
     *
     * @param modelClass 模型类
     * @param columnName 列名
     * @param <T>        <T>
     * @return 均值
     */
    public <T extends LitePalSupport> Double average(Class<T> modelClass, String columnName) {
        return LitePal.average(modelClass, columnName);
    }

    /**
     * 条件平均
     * <p>
     * 具有运算能力列可求平均值。
     * 字符串类型列无法求平均值，返 0。
     *
     * @param modelClass 模型类
     * @param columnName 列名
     * @param conditions 条件
     * @param <T>        <T>
     * @return 均值
     */
    public <T extends LitePalSupport> Double averageByWhere(Class<T> modelClass, String columnName, String... conditions) {
        return LitePal.where(conditions).average(modelClass, columnName);
    }

    /**
     * 求最大值
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return 最大值
     */
    public int max(String tableName, String columnName) {
        return LitePal.max(tableName, columnName, int.class);
    }

    /**
     * 条件求最大值
     *
     * @param tableName  表名
     * @param columnName 列名
     * @param conditions 条件
     * @return 最大值
     */
    public int maxByWhere(String tableName, String columnName, String... conditions) {
        return LitePal.where(conditions).max(tableName, columnName, Integer.TYPE);
    }

    /**
     * 求最大值
     *
     * @param modelClass 模型类
     * @param columnName 列名
     * @param <T>        <T>
     * @return 最大值
     */
    public <T extends LitePalSupport> int max(Class<T> modelClass, String columnName) {
        return LitePal.max(modelClass, columnName, int.class);
    }

    /**
     * 条件求最大值
     *
     * @param modelClass 模型类
     * @param columnName 列名
     * @param conditions 条件
     * @param <T>        <T>
     * @return 最大值
     */
    public <T extends LitePalSupport> int maxByWhere(Class<T> modelClass, String columnName, String... conditions) {
        return LitePal.where(conditions).max(modelClass, columnName, int.class);
    }

    /**
     * 求最小值
     *
     * @param tableName  表名
     * @param columnName 列名
     * @return 最小值
     */
    public int min(String tableName, String columnName) {
        return LitePal.min(tableName, columnName, int.class);
    }

    /**
     * 条件求最小值
     *
     * @param tableName  表名
     * @param columnName 列名
     * @param conditions 条件
     * @return 最小值
     */
    public int minByWhere(String tableName, String columnName, String... conditions) {
        return LitePal.where(conditions).min(tableName, columnName, Integer.TYPE);
    }

    /**
     * 求最小值
     *
     * @param modelClass 模型类
     * @param columnName 列名
     * @param <T>        <T>
     * @return 最小值
     */
    public <T extends LitePalSupport> int min(Class<T> modelClass, String columnName) {
        return LitePal.min(modelClass, columnName, int.class);
    }

    /**
     * 条件求最小值
     *
     * @param modelClass 模型类
     * @param columnName 列名
     * @param conditions 条件
     * @param <T>        <T>
     * @return 最小值
     */
    public <T extends LitePalSupport> int minByWhere(Class<T> modelClass, String columnName, String... conditions) {
        return LitePal.where(conditions).min(modelClass, columnName, int.class);
    }

    private static final class InstanceHolder {
        static final LitePalKit INSTANCE = new LitePalKit();
    }
}
