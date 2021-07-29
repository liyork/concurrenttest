package com.wolf.concurrenttest.underjvm.classload.inovketmpcode;

/**
 * Description: 修改常量池部分，将常量池中指定内容的CONSTANT_Utf8_info常量替换为新的字符串
 * 替换符号引用后，与客户端直接引用HackSystem一样，避免了客户端编写临时执行代码时需要依赖特定的类(不然无法引入HackSystem)，又避免了服务端修改标准输出后影响到其他程序的输出
 * <p>
 * CONSTANT_Utf8_info型常量的结构：
 * 类型  名称  数量
 * u1  tag  1
 * u2  length  1
 * u1  bytes  length
 * <p>
 * Created on 2021/7/28 1:51 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ClassModifier {
    // Class文件中常量池的起始偏移
    private static final int CONSTANT_POOL_COUNT_INDEX = 8;
    // CONSTANT_Utf8常量的tag标志
    private static final int CONSTANT_Utf8_info = 1;
    // 常量池中11种常量所占的长度(tag+index等)，CONSTANT_Utf8_info型常量除外，因为它不是定长的
    private static final int[] CONSTANT_ITEM_LENGTH = {-1, -1, -1, 5, 5, 9, 9, 3, 3, 5, 5, 5, 5};

    private static final int u1 = 1;
    private static final int u2 = 2;

    private byte[] classByte;

    public ClassModifier(byte[] classByte) {
        this.classByte = classByte;
    }

    // 修改常量池中CONSTANT_Utf8_info常量的内容
    public byte[] modifyUTF8Constant(String oldStr, String newStr) {
        int cpc = getConstantCount();
        int offset = CONSTANT_POOL_COUNT_INDEX + u2;// 常量开始位置
        for (int i = 0; i < cpc; i++) {// tag(u1)+len(u2)
            int tag = ByteUtils.bytes2Int(classByte, offset, u1);
            if (tag == CONSTANT_Utf8_info) {
                int len = ByteUtils.bytes2Int(classByte, offset + u1, u2);
                offset += (u1 + u2);// 重新定位
                String str = ByteUtils.bytes2String(classByte, offset, len);// 取len的常量值
                if (str.equalsIgnoreCase(oldStr)) {
                    byte[] strBytes = ByteUtils.string2Bytes(newStr);
                    byte[] strLen = ByteUtils.int2Bytes(newStr.length(), u2);// 将len转换为byte[]
                    classByte = ByteUtils.bytesReplace(classByte, offset - u2, u2, strLen);// 替换长度
                    classByte = ByteUtils.bytesReplace(classByte, offset, len, strBytes);// 将从offset开始，一共len的内容替换为strBytes
                    return classByte;
                } else {
                    offset += len;// 向前len找下个常量
                }
            } else {
                offset += CONSTANT_ITEM_LENGTH[tag];// 下个常量
            }
        }
        return classByte;
    }

    // 获取常量池中常量的数量
    private int getConstantCount() {
        return ByteUtils.bytes2Int(classByte, CONSTANT_POOL_COUNT_INDEX, u2);
    }
}
