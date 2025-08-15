package fragmentation.fragmentation.debug;

import java.util.List;

/**
 * @decs: 调试碎片记录
 * @author: 郑少鹏
 * @date: 2019/5/20 9:24
 */
class DebugFragmentRecord {
    CharSequence fragmentName;
    List<DebugFragmentRecord> debugFragmentRecords;

    DebugFragmentRecord(CharSequence fragmentName, List<DebugFragmentRecord> debugFragmentRecords) {
        this.fragmentName = fragmentName;
        this.debugFragmentRecords = debugFragmentRecords;
    }
}