package com.twx.marryfriend.dynamic.send.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数组列表帮助类
 * @author Wuczh
 * @date 2022/1/22
 */

public class ArrayUtils {

    private ArrayUtils() {
    }

    /**
     * 去除重复数据
     * @param array 数组
     * @param cls 数组内的属性类型
     */
    public static <T> T[] deduplication(T[] array, Class<? extends T> cls) {
        if (isEmpty(array)){
            return null;
        }

        Set<T> set = new HashSet<>(arrayToList(array));
        List<T> list = new ArrayList<>(set);
        return listToArray(list, cls);
    }

    /**
     * 去除重复数据
     * @param list 列表
     */
    public static <T> List<T> deduplication(List<T> list) {
        if (isEmpty(list)){
            return new ArrayList<>();
        }

        Set<T> set = new HashSet<>(list);
        return new ArrayList<>(set);
    }

    /**
     * 获取列表数据长度
     * @param list 列表
     */
    public static <T> int getSize(List<T> list){
        return isEmpty(list) ? 0 : list.size();
    }

    /**
     * 获取数组数据长度
     * @param array 数组
     */
    public static <T> int getSize(T[] array){
        return isEmpty(array) ? 0 : array.length;
    }

    /**
     * 列表是否为空
     * @param list 列表
     */
    public static <T> boolean isEmpty(List<T> list){
        return list == null || list.size() == 0;
    }

    /**
     * 数组是否为空
     * @param array 数组
     */
    public static <T> boolean isEmpty(T[] array){
        return array == null || array.length == 0;
    }

    /**
     * 数组转列表
     * @param array 数组
     */
    public static <T> List<T> arrayToList(T[] array) {
        if (array == null || array.length == 0) {
            return new ArrayList<>();
        }
        return Arrays.asList(array);
    }

    /**
     * 列表转数组
     * @param list 列表
     * @param cls 列表内的属性类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] listToArray(List<T> list, Class<? extends T> cls) {
        if (list == null || list.size() == 0 || cls == null) {
            return null;
        }
        return list.toArray((T[]) Array.newInstance(cls, list.size()));
    }

    /**
     * 选择排序
     * @param list 列表
     * @param isAsc 是否升序
     */
    public static List<Integer> sortByChoose(List<Integer> list, boolean isAsc) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        array = sortByChoose(array, isAsc);
        List<Integer> result = new ArrayList<>();
        for (int num : array) {
            result.add(num);
        }
        return result;
    }

    /**
     * 选择排序
     * @param array 数组
     * @param isAsc 是否升序
     */
    public static int[] sortByChoose(int[] array, boolean isAsc) {
        for (int i = 0; i < array.length - 1; i++) {
            int middle = array[i];
            int min = 0;
            for (int j = i + 1; j <= array.length - 1; j++) {
                boolean typee = isAsc ? middle > array[j] : middle < array[j];
                if (typee) {
                    middle = array[j];
                    min = j;
                }
            }
            if (min != 0) {
                int f = array[min];
                array[min] = array[i];
                array[i] = f;
            }
        }
        return array;
    }

    /**
     * 插入排序
     * @param list 列表
     * @param isAsc 是否升序
     */
    public static List<Integer> sortByInsert(List<Integer> list, boolean isAsc) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        array = sortByInsert(array, isAsc);
        List<Integer> result = new ArrayList<>();
        for (int num : array) {
            result.add(num);
        }
        return result;
    }

    /**
     * 插入排序
     * @param array 数组
     * @param isAsc 是否升序
     */
    public static int[] sortByInsert(int[] array, boolean isAsc) {
        for (int i = 1; i < array.length; i++) {
            int t = array[i];
            int y = -1;
            for (int j = i - 1; j >= 0; j--) {
                boolean typee = isAsc ? t < array[j] : t > array[j];
                if (!typee) break;
                array[j + 1] = array[j];
                y = j;
            }

            if (y > -1) array[y] = t;
        }
        return array;
    }


    /**
     * 冒泡排序
     * @param list 列表
     * @param isAsc 是否升序
     */
    public static List<Integer> sortByBubble(List<Integer> list, boolean isAsc) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        array = sortByBubble(array, isAsc);
        List<Integer> result = new ArrayList<>();
        for (int num : array) {
            result.add(num);
        }
        return result;
    }

    /**
     * 冒泡排序
     * @param array 数组
     * @param isAsc 是否升序
     */
    public static int[] sortByBubble(int[] array, boolean isAsc) {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - 1; j++) {
                boolean typee = isAsc ? array[j] > array[j + 1] : array[j] < array[j + 1];
                if (typee) {
                    int t = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = t;
                }
            }
        }
        return array;
    }

    /**
     * 获取列表里的最大值
     * @param list 列表
     */
    public static long getMaxLong(List<Long> list){
        long max = 0L;
        for (Long data : list) {
            if (max < data){
                max = data;
            }
        }
        return max;
    }

    /**
     * 获取数组里的最大值
     * @param array 数组
     */
    public static long getMaxLong(Long[] array){
        return getMaxLong(arrayToList(array));
    }

    /**
     * 获取列表里的最大值
     * @param list 列表
     */
    public static int getMaxInt(List<Integer> list){
        int max = 0;
        for (int data : list) {
            if (max < data){
                max = data;
            }
        }
        return max;
    }

    /**
     * 获取数组里的最大值
     * @param array 数组
     */
    public static int getMaxInt(Integer[] array){
        return getMaxInt(arrayToList(array));
    }

    /**
     * 获取列表的最小值
     * @param list 列表
     */
    public static long getMinLong(List<Long> list){
        long min = 0L;
        for (Long data : list) {
            if (min > data){
                min = data;
            }
        }
        return min;
    }

    /**
     * 获取数组的最小值
     * @param array 数组
     */
    public static long getMinLong(Long[] array){
        return getMinLong(arrayToList(array));
    }

    /**
     * 获取列表的最小值
     * @param list 列表
     */
    public static int getMinInt(List<Integer> list){
        int min = 0;
        for (int data : list) {
            if (min > data){
                min = data;
            }
        }
        return min;
    }

    /**
     * 获取数组的最小值
     * @param array 数组
     */
    public static int getMinInt(Integer[] array){
        return getMinInt(arrayToList(array));
    }

}
