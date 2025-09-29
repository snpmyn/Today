package widget.ion.kit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2025/9/25.
 *
 * @author 郑少鹏
 * @desc ION 错误配套原件
 */
public class IonErrorKit {
    private static final Map<Integer, String> ERROR_MAP;

    static {
        ERROR_MAP = new HashMap<>(192);
        ERROR_MAP.put(1001, "_api_key 不能为空");
        ERROR_MAP.put(1002, "_api_key 错误");
        ERROR_MAP.put(1003, "同步用户信息到 Tracup 出错信息");
        ERROR_MAP.put(1007, "搜索词太短");
        ERROR_MAP.put(1008, "Build Key 和 appKey 不能同时为空");
        ERROR_MAP.put(1009, "没有找到该 App 相关信息");
        ERROR_MAP.put(1010, "App Key 不能为空");
        ERROR_MAP.put(1011, "Build Id 不能为空");
        ERROR_MAP.put(1012, "User key 不能为空");
        ERROR_MAP.put(1013, "没有找到该用户");
        ERROR_MAP.put(1014, "应用类型不能为空");
        ERROR_MAP.put(1015, "文件或 App 类型错误");
        ERROR_MAP.put(1016, "无效的包名");
        ERROR_MAP.put(1017, "短链接已被使用或者不符合规范");
        ERROR_MAP.put(1018, "App 数量超过套餐的上限");
        ERROR_MAP.put(1019, "实名认证身份照片不全，请上传身份证正面、背面及手持身份照片");
        ERROR_MAP.put(1020, "用户被禁止上传");
        ERROR_MAP.put(1021, "文件无效");
        ERROR_MAP.put(1022, "文件过大");
        ERROR_MAP.put(1023, "build key 不能为空");
        ERROR_MAP.put(1024, "发布范围超过限制");
        ERROR_MAP.put(1025, "文件上传失败");
        ERROR_MAP.put(1026, "无效文件，文件类型错误");
        ERROR_MAP.put(1027, "应用名称长度不符合规范");
        ERROR_MAP.put(1028, "未找到应用的标识符");
        ERROR_MAP.put(1029, "短链接已被使用或不符合规范");
        ERROR_MAP.put(1030, "发布企业签名数量超过套餐最大值");
        ERROR_MAP.put(1031, "短链接不能为空");
        ERROR_MAP.put(1032, "短链接无效");
        ERROR_MAP.put(1033, "只能修改自己的应用");
        ERROR_MAP.put(1035, "修改的字段超过限制");
        ERROR_MAP.put(1036, "修改内容不能为空");
        ERROR_MAP.put(1037, "图片必须以数组的形式上传");
        ERROR_MAP.put(1038, "图片数据不能为空");
        ERROR_MAP.put(1039, "应用截图最多不能超过 5 张");
        ERROR_MAP.put(1040, "应用截图上传失败");
        ERROR_MAP.put(1041, "应用截图 key 不能为空");
        ERROR_MAP.put(1042, "反馈内容不能为空");
        ERROR_MAP.put(1043, "反馈内容过长，不能超过 200 字");
        ERROR_MAP.put(1044, "添加反馈出错");
        ERROR_MAP.put(1045, "无效的 user Key");
        ERROR_MAP.put(1047, "该应用收费，请使用手机进行安装");
        ERROR_MAP.put(1048, "应用已过期");
        ERROR_MAP.put(1049, "应用下载次数已用完");
        ERROR_MAP.put(1050, "密码错误");
        ERROR_MAP.put(1051, "应用违规");
        ERROR_MAP.put(1052, "应用仅认证用户可以下载");
        ERROR_MAP.put(1053, "下载速度超过限制");
        ERROR_MAP.put(1054, "今日下载次数已用完");
        ERROR_MAP.put(1055, "无效的 api key");
        ERROR_MAP.put(1056, "请在 iOS 系统中打开该链接");
        ERROR_MAP.put(1057, "同步专家测试企业签名应用参数错误");
        ERROR_MAP.put(1058, "解析应用出错");
        ERROR_MAP.put(1059, "废弃的方法");
        ERROR_MAP.put(1060, "请输入你的邮箱");
        ERROR_MAP.put(1061, "请输入你的密码");
        ERROR_MAP.put(1062, "用户名或者密码不正确");
        ERROR_MAP.put(1063, "该邮箱已存在");
        ERROR_MAP.put(1064, "请输入你的用户名");
        ERROR_MAP.put(1065, "用户名不能超过 15 个字符");
        ERROR_MAP.put(1066, "请输入你的姓名");
        ERROR_MAP.put(1067, "请输入你的公司");
        ERROR_MAP.put(1068, "请输入你的职业");
        ERROR_MAP.put(1069, "该手机号码已存在");
        ERROR_MAP.put(1070, "请输入验证码");
        ERROR_MAP.put(1071, "验证码无效");
        ERROR_MAP.put(1072, "该邮箱不存在");
        ERROR_MAP.put(1073, "请输入正确的账户信息");
        ERROR_MAP.put(1075, "App group key 不能为空");
        ERROR_MAP.put(1076, "App key 不正确");
        ERROR_MAP.put(1079, "录音上传失败");
        ERROR_MAP.put(1080, "反馈失败");
        ERROR_MAP.put(1081, "非法请求");
        ERROR_MAP.put(1082, "Feedback Key 不能为空");
        ERROR_MAP.put(1083, "反馈信息未找到");
        ERROR_MAP.put(1084, "日志信息不能为空");
        ERROR_MAP.put(1085, "系统类型不正确");
        ERROR_MAP.put(1086, "crash id 不能为空");
        ERROR_MAP.put(1087, "crash 信息没有找到");
        ERROR_MAP.put(1089, "获取平台参数不能为空");
        ERROR_MAP.put(1090, "平台参数为 windows 或者 mac");
        ERROR_MAP.put(1091, "版本参数不对");
        ERROR_MAP.put(1092, "版本信息没有找到");
        ERROR_MAP.put(1093, "没有找到信息");
        ERROR_MAP.put(1094, "app group key 不正确");
        ERROR_MAP.put(1095, "应用名称过长");
        ERROR_MAP.put(1096, "错误的方法");
        ERROR_MAP.put(1097, "签名错误");
        ERROR_MAP.put(1098, "Api 请求达到每小时的上限");
        ERROR_MAP.put(1099, "更新 App 失败");
        ERROR_MAP.put(1100, "没有找到 App 分组信息");
        ERROR_MAP.put(1102, "请输入邮箱验证码");
        ERROR_MAP.put(1103, "您输入的验证码不正确");
        ERROR_MAP.put(1104, "您输入的邮箱地址无效");
        ERROR_MAP.put(1105, "该账号已存在");
        ERROR_MAP.put(1106, "真实姓名的长度必须小于 15");
        ERROR_MAP.put(1107, "请填写密码");
        ERROR_MAP.put(1108, "请正确填写您的手机号码");
        ERROR_MAP.put(1109, "请输入您的 6 位验证码");
        ERROR_MAP.put(1110, "注册失败");
        ERROR_MAP.put(1111, "请输入你的账号");
        ERROR_MAP.put(1112, "该手机号码绑定多个账号，请使用密码进行登录");
        ERROR_MAP.put(1113, "请填写邮箱地址");
        ERROR_MAP.put(1115, "用户不存在");
        ERROR_MAP.put(1116, "手机号码或密码不正确");
        ERROR_MAP.put(1117, "邮箱地址或密码不正确");
        ERROR_MAP.put(1118, "请输入手机号码");
        ERROR_MAP.put(1120, "每次发送短信的间隔必须在 30 秒以上");
        ERROR_MAP.put(1121, "验证失败");
        ERROR_MAP.put(1122, "权限不足");
        ERROR_MAP.put(1123, "统计类型出错");
        ERROR_MAP.put(1124, "授权信息失败");
        ERROR_MAP.put(1125, "绑定已有账号");
        ERROR_MAP.put(1126, "邮箱未更改");
        ERROR_MAP.put(1127, "邮箱地址已存在");
        ERROR_MAP.put(1128, "权限不足，只能删除自己上传的应用");
        ERROR_MAP.put(1129, "该账号已绑定微信号，请重新输入账号");
        ERROR_MAP.put(1130, "请输入您的新密码");
        ERROR_MAP.put(1131, "请再次输入密码");
        ERROR_MAP.put(1132, "两次输入密码不一致 请重新输入");
        ERROR_MAP.put(1133, "JSCode 不能为空");
        ERROR_MAP.put(1134, "JSCode 无效");
        ERROR_MAP.put(1135, "没有找到资质文件");
        ERROR_MAP.put(1136, "删除资质文件失败");
        ERROR_MAP.put(1137, "上传资质文件出错");
        ERROR_MAP.put(1138, "文件数量超过最大限制");
        ERROR_MAP.put(1139, "一种类型只能上传两张文件");
        ERROR_MAP.put(1140, "添加资质文件失败");
        ERROR_MAP.put(1141, "交易类型不能为空");
        ERROR_MAP.put(1142, "当前版本不可隐藏");
        ERROR_MAP.put(1143, "encryptedData 和 iv 不能为空");
        ERROR_MAP.put(1144, "请输入安装开始时间及结束时间");
        ERROR_MAP.put(1145, "安装结束时间必须大于开始时间");
        ERROR_MAP.put(1148, "请上传身份证正面照片");
        ERROR_MAP.put(1149, "请上传身份证反面照片");
        ERROR_MAP.put(1150, "请上传手持身份证照片");
        ERROR_MAP.put(1151, "企业名称不能为空");
        ERROR_MAP.put(1152, "营业执照号码不能为空");
        ERROR_MAP.put(1153, "请上传营业执照照片");
        ERROR_MAP.put(1154, "省份不能为空");
        ERROR_MAP.put(1155, "城市不能为空");
        ERROR_MAP.put(1156, "当日自动审核次数已用完");
        ERROR_MAP.put(1157, "自动审核未通过");
        ERROR_MAP.put(1158, "图片不能大于 10M");
        ERROR_MAP.put(1159, "图片不能小于 15k");
        ERROR_MAP.put(1160, "图片类型不正确");
        ERROR_MAP.put(1161, "fileType 不能为空");
        ERROR_MAP.put(1162, "名称不能为空");
        ERROR_MAP.put(1163, "描述不能为空");
        ERROR_MAP.put(1164, "appKeys 不能为空");
        ERROR_MAP.put(1165, "描述文字太长");
        ERROR_MAP.put(1166, "分组名称不符合规则");
        ERROR_MAP.put(1167, "分组的应用，至少两个");
        ERROR_MAP.put(1168, "网址后缀不能为空");
        ERROR_MAP.put(1169, "新号码和旧号码不能一样");
        ERROR_MAP.put(1170, "手机号码错误");
        ERROR_MAP.put(1171, "真实姓名不能为空");
        ERROR_MAP.put(1172, "身份证号不能为空");
        ERROR_MAP.put(1173, "真实姓名不匹配");
        ERROR_MAP.put(1174, "身份证号不匹配");
        ERROR_MAP.put(1175, "发布时间不能为空");
        ERROR_MAP.put(1176, "请输入正确的发布时间");
        ERROR_MAP.put(1177, "获取应用信息失败");
        ERROR_MAP.put(1178, "应用类型不能为空");
        ERROR_MAP.put(1179, "不能合并空白应用");
        ERROR_MAP.put(1180, "应用已合并");
        ERROR_MAP.put(1181, "请合并 iOS 应用");
        ERROR_MAP.put(1182, "请合并 Android 应用");
        ERROR_MAP.put(1183, "必须是自己的应用");
        ERROR_MAP.put(1184, "不能和自己合并");
        ERROR_MAP.put(1185, "图标已存在不等上传图标");
        ERROR_MAP.put(1186, "应用未发布");
        ERROR_MAP.put(1187, "请升级您的版本");
        ERROR_MAP.put(1188, "发生错误");
        ERROR_MAP.put(1189, "暂时不能更改文件");
        ERROR_MAP.put(1190, "请上传文网文");
        ERROR_MAP.put(1191, "请上传 ICP 许可证");
        ERROR_MAP.put(1192, "上传软件著作权登记证");
        ERROR_MAP.put(1193, "上传营业热照");
        ERROR_MAP.put(1194, "请上传金融牌照");
        ERROR_MAP.put(1195, "请上传其他证件");
        ERROR_MAP.put(1196, "请上传有关资质文件");
        ERROR_MAP.put(1197, "请上传信息网络传播视听节目许可证");
        ERROR_MAP.put(1198, "请重新上传审核不通过的文件");
        ERROR_MAP.put(1199, "相关文件数目不匹配");
        ERROR_MAP.put(1200, "申诉理由不能少于 20 个字符,不能多于 500 字符");
        ERROR_MAP.put(1201, "申诉图片不能大于 5 张");
        ERROR_MAP.put(1202, "不能删除所有可下载版本，如需删除应用，可在设置中删除应用");
        ERROR_MAP.put(1203, "不能删除所有显示的版本");
        ERROR_MAP.put(1212, "渠道短链接无效，请检查短链接");
    }

    /**
     * 获取消息
     *
     * @param code 码
     * @return 消息
     */
    public static String getMessage(int code) {
        return ERROR_MAP.getOrDefault(code, "未知错误");
    }
}