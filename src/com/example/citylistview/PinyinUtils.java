package com.example.citylistview;

import java.io.UnsupportedEncodingException;

/**
版权所有：版权所有(C)2013，固派软件
文件名称：com.goopai.obd.util.PinyinUtils.java
系统编号：
系统名称：OBD
模块编号：
模块名称：
设计文档：
创建日期：2013-12-2 下午4:36:44
作 者：何鹏程
Version: 1.0
内容摘要：获取汉子首个拼音英文字母
类中的代码包括三个区段：类变量区、类属性区、类方法区。
文件调用:
 */
public class PinyinUtils {

	/**
     * 取得给定汉字的首字母,即大写声母
     *
     * @param chinese 给定的汉字
     * @return 给定汉字的声母
     */
    public static String getFirstLetter(String chinese)
    {
        if (chinese == null || chinese.trim().length() == 0)
        {
            return "";
        }
        chinese = conversionStr(chinese, "GB2312", "ISO8859-1");

        if (chinese.length() > 1) //判断是不是汉字
        {
            int li_SectorCode = (int) chinese.charAt(0); //汉字区码
            int li_PositionCode = (int) chinese.charAt(1); //汉字位码
            li_SectorCode = li_SectorCode - 160;
            li_PositionCode = li_PositionCode - 160;
            int li_SecPosCode = li_SectorCode * 100 + li_PositionCode; //汉字区位码
            if (li_SecPosCode > 1600 && li_SecPosCode < 5590)
            {
                for (int i = 0; i < 23; i++)
                {
                    if (li_SecPosCode >= li_SecPosValue[i] &&
                            li_SecPosCode < li_SecPosValue[i + 1])
                    {
                        chinese = lc_FirstLetter[i];
                        break;
                    }
                }
            }
            else //非汉字字符,如图形符号或ASCII码
            {
                chinese = conversionStr(chinese, "ISO8859-1", "GB2312");
                chinese = chinese.substring(0, 1);
            }
        }

        return chinese;
    }
    
    /**
     * 字符串编码转换
     *
     * @param str           要转换编码的字符串
     * @param charsetName   原来的编码
     * @param toCharsetName 转换后的编码
     * @return 经过编码转换后的字符串
     */
    private static String conversionStr(String str, String charsetName, String toCharsetName)
    {
        try{
            str = new String(str.getBytes(charsetName), toCharsetName);
        }
        catch (UnsupportedEncodingException ex){
            System.out.println("字符串编码转换异常：" + ex.getMessage());
        }

        return str;
    }
    
    private final static int[] li_SecPosValue =
        {
                1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472,
                3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590
        };
    private final static String[] lc_FirstLetter =
        {
                "a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m", "n", "o", "p",
                "q", "r", "s", "t", "w", "x", "y", "z"
        };
}
