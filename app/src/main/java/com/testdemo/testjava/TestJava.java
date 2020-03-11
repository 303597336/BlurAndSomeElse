package com.testdemo.testjava;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Greyson on 2018/2/1.
 */
public class TestJava {

    public static void main(String[] args) {
        System.out.println("hello java!\\!!" + Math.pow(2, 3));

        String cardNo = "12098915703";
        System.out.println(cardNo + " - " + isPhoneNumber(cardNo));

        ArrayList<String> a = new ArrayList<>();
        a.add("ea");
        a.add("ea");
        System.out.println("a = " + a + " - " + a.subList(0, 1));

        ArrayList<? super Goods> list = new ArrayList<Thing>();//todo 系统学习一下泛型吧小弟！
//        list.add(new Thing());//虽然它现在是是ArrayList<Object>对象，但还是不能传入Thing对象？？
        list.add(new Paint());
        list.add(new Goods());
        System.out.println("11list's class= " + list.getClass().getName()
                + ", list's object: " + list.get(0));
        list = new ArrayList<Thing>();
//        list.add(new Thing());//虽然它现在是是ArrayList<Thing>对象，但还是不能传入Thing对象？？
        list.add(new Paint());
        list.add(new Goods());
        System.out.println("22list's class= " + list.getClass()
                + ", list's object: " + list.get(0));

        ArrayList<? extends Goods> list2 = new ArrayList<Paint>();
//        list2.add(new Book());
//        list2.add(new Goods());//什么都不能加。。。？？？

        testStringRegex();
    }

    public String getInstance(int type) {
        if (type == 1) {
            return null;
        } else {
            return "SuccessString";
        }
    }

    public static boolean isPhoneNumber(String str) {
        Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static void testRegularExpression() {
        String test1 = "13";
        String test2 = "13 7";
        String test3 = "137 ";
        String test4 = "137";
        Pattern pattern2 = Pattern.compile("^\\d{3} \\d{4} \\d{1,4}$");
        Pattern pattern = Pattern.compile("^\\d{1,3}$");

        System.out.println(pattern.matcher(test1).matches() + "\n" + pattern.matcher(test2).matches()
                + "\n" + pattern.matcher(test3).matches() + "\n" + pattern.matcher(test4).matches());
    }

    public static void testStringRegex() {
        String[] list = { "yang-vo,id", "yangw/oid", "yang,uo1id"};
        for (int i = 0; i < list.length; i++) {
            System.out.println(list[i] = list[i].replaceAll("[^a-z]", ""));
        }
        Arrays.sort(list, 0, list.length, String::compareToIgnoreCase);
        System.out.println("排序后：");
        for (String s : list) {
            System.out.println(s);
        }
    }

    public static void testDecimalFormat() {
        DecimalFormat df = new DecimalFormat();
        double data = 1234.56789;
        System.out.println("格式化之前的数字: " + data);


        String style = "0.0";//定义要显示的数字的格式
        df.applyPattern(style);// 将格式应用于格式化器
        System.out.println("采用style: " + style + "格式化之后: " + df.format(data));


        style = "00000.000 kg";//在格式后添加诸如单位等字符
        df.applyPattern(style);
        System.out.println("采用style: " + style + "格式化之后: " + df.format(data));


        // 模式中的"#"表示如果该位存在字符，则显示字符，如果不存在，则不显示。
        style = "##000.000 kg";
        df.applyPattern(style);
        System.out.println("采用style: " + style + "格式化之后: " + df.format(data));


        // 模式中的"-"表示输出为负数，要放在最前面
        style = "-000.000";
        df.applyPattern(style);
        System.out.println("采用style: " + style + "格式化之后: " + df.format(data));


        // 模式中的","在数字中添加逗号，方便读数字
        style = "-0,000.0#";
        df.applyPattern(style);
        System.out.println("采用style: " + style + "格式化之后: " + df.format(data));


        // 模式中的"E"表示输出为指数，"E"之前的字符串是底数的格式，
        // "E"之后的是字符串是指数的格式
        style = "0.00E000";
        df.applyPattern(style);
        System.out.println("采用style: " + style + "格式化之后: " + df.format(data));


        // 模式中的"%"表示乘以100并显示为百分数，要放在最后。
        style = "0.00%";
        df.applyPattern(style);
        System.out.println("采用style: " + style + "格式化之后: " + df.format(data));


        // 模式中的"\u2030"表示乘以1000并显示为千分数，要放在最后。
        style = "0.00\u2030";
        //在构造函数中设置数字格式
        DecimalFormat df1 = new DecimalFormat(style);
        //df.applyPattern(style);
        System.out.println("采用style: " + style + "格式化之后: " + df1.format(data));
    }
}
