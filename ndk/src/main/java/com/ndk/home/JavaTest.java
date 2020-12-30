package com.ndk.home;

import com.base.common.util.LogUtilKt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * 冒泡排序  稳定  O(N2)
 * 两层循环，比较相邻两两交换
 * <p>
 * 选择排序  不稳定  O(N2)
 * 两层循环，每次找到最小(最大)值，放到起始(末尾)位置，重复进行
 * <p>
 * 插入排序  稳定  O(N2)
 * 两层循环，每次取值后遍历回顾之前元素，放到该放的位置，被超过的元素依次后移一位
 * <p>
 * 希尔排序  不稳定  O(NlogN)
 * 根据希尔增量分组，各组进行插入排序，直到增量为1那次排完
 * <p>
 * 快速排序  不稳定  O(NlogN)
 */
public class JavaTest {
    public void start() {
        replaceSpace();
        reverseOrder();
        linkedReverse();
        findNoRepetitionString();
        quickSort();
    }

    private void replaceSpace() {
        String a0 = "123 456 789";

        //NDK
        long time = System.nanoTime();
        String a1 = NDKHelper.replaceSpaceC(a0);
        LogUtilKt.log("replaceSpace", (System.nanoTime() - time) + " -- " + a1);

        //java内置
        time = System.nanoTime();
        String a2 = a0.replace(" ", "%2");
        LogUtilKt.log("replaceSpace", (System.nanoTime() - time) + " -- " + a2);

        //手动
        time = System.nanoTime();
        char[] chars = a0.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char aChar : chars) {
            if (aChar == ' ') {
                sb.append("%2");
            } else {
                sb.append(aChar);
            }
        }
        LogUtilKt.log("replaceSpace", (System.nanoTime() - time) + " -- " + sb.toString());
    }

    private void reverseOrder() {
        int[] a = {1, 2, 3, 4, 5};
        LogUtilKt.log("reverseOrder", Arrays.toString(a));
        //NDK
        long time = System.nanoTime();
        NDKHelper.reverseOrderC(a);
        LogUtilKt.log("reverseOrder", (System.nanoTime() - time) + " -- " + Arrays.toString(a));

        //手动
        a = new int[]{1, 2, 3, 4, 5};
        time = System.nanoTime();
        int midpoint = a.length >> 1;
        int t;
        for (int i = 0, j = a.length - 1; i < midpoint; i++, j--) {
            t = a[i];
            a[i] = a[j];
            a[j] = t;
        }
        LogUtilKt.log("reverseOrder", (System.nanoTime() - time) + " -- " + Arrays.toString(a));
    }

    private void linkedReverse() {
        ListNode l1 = new ListNode(1);
        ListNode l2 = new ListNode(2);
        ListNode l3 = new ListNode(3);
        l1.next = l2;
        l2.next = l3;

        logLinked(l1);

        reverseLinked(l1, null);

        logLinked(l3);
    }

    private void reverseLinked(ListNode my, ListNode top) {
        if (my.next != null) {
            reverseLinked(my.next, my);
        }
        my.next = top;
    }

    private void logLinked(ListNode l) {
        LogUtilKt.log("LinkedReverse", l.a);
        if (l.next != null) {
            logLinked(l.next);
        }
    }

    private void findNoRepetitionString() {
        String s = "abcade";// 3
        int len = s.length();

        //方法1
        long time = System.nanoTime();
        LinkedList<Character> linkedList = new LinkedList<>();
        int size = 0;
        int index = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            int indexOf = linkedList.indexOf(c);
            if (index != -1) {
                linkedList.subList(0, indexOf + 1).clear();
            }
            linkedList.add(c);
            size = Math.max(size, linkedList.size());
        }

        LogUtilKt.log("findNoRepetitionString", (System.nanoTime() - time) + " -- " + size);

        //方法2  num实际记录的是不重复长度
        time = System.nanoTime();
        HashMap<Character, Integer> map = new HashMap<>();
        size = 0;
        int num = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            Integer v = map.get(c);
            map.put(c, i);
            num++;
            if (v != null) num = Math.min(i - v, num);
            size = Math.max(size, num);
        }
        LogUtilKt.log("findNoRepetitionString", (System.nanoTime() - time) + " -- " + size);

        //方法3 num实际记录的是子串左起点下标
        time = System.nanoTime();
        map = new HashMap<>();
        num = -1;
        size = 0;
        for (int i = 0; i < len; i++) {
            if (map.containsKey(s.charAt(i))) num = Math.max(num, map.get(s.charAt(i))); // 更新左指针 i
            map.put(s.charAt(i), i); // 哈希表记录
            size = Math.max(size, i - num); // 更新结果
        }

        LogUtilKt.log("findNoRepetitionString", (System.nanoTime() - time) + " -- " + size);
    }

    private void quickSort() {
        int[] a = {9, 8, 7, 6, 5, 4, 3, 2, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 6, 6, 6};

        long time = System.nanoTime();
        beginSort1(a, 0, a.length - 1);
//        beginSort2(a, 0, a.length - 1);
        LogUtilKt.log("quickSort", (System.nanoTime() - time) + " -- " + Arrays.toString(a));
    }

    /**
     * 快速排序 1
     *
     * @param start 起始下标
     * @param end   结束下标
     */
    private void beginSort1(int[] a, int start, int end) {
        if (start >= end) return;

        //以最右边为基准点
        int pivot = a[end];
        //ij以最左边为起点
        int i = start;
        int j = start;

        while (j < end) {
            //如果j对应值小于基准值，交换ij
            while (j < end && a[j] < pivot) {
                swap(a, i, j);
                i++;
                j++;
            }
            //上面小while循环结束后，j继续平移，
            //i将指向剩余的第一个大于等于基准值的位置，如果剩余的都不大于基准值，将指向最右侧前一位
            j++;
        }
        //循环结束i将指向第一个大于基准值的下标，
        //如果所有的值全小于基准值，那么i指向的就是基准值
        swap(a, i, end);

        LogUtilKt.log("beginSort1", i + " ---- " + j + " -- " + Arrays.toString(a));
        beginSort1(a, start, i - 1);
        beginSort1(a, i + 1, end);
    }

    /**
     * 快速排序 2
     *
     * @param start 起始下标
     * @param end   结束下标
     */
    private void beginSort2(int[] a, int start, int end) {
        if (start >= end) return;

        //以最左边为基准点
        int pivot = a[start];
        //i以最左边为起点
        int i = start;
        //j以最右边为起点
        int j = end;
        while (j > i) {
            //j向前遍历直到找到小于基准点值
            while (j > i && a[j] >= pivot) {
                j--;
            }
            //i向后遍历直到找到大于基准点值
            while (j > i && a[i] <= pivot) {
                i++;
            }
            swap(a, i, j);
        }
        LogUtilKt.log("beginSort2", i + " ---- " + j + " -- " + Arrays.toString(a));

        //循环结束i将指向最后一个小于基准值的下标，
        //如果所有的值全大于基准值，那么i指向的就是基准值
        swap(a, i, start);

        beginSort2(a, start, i - 1);
        beginSort2(a, i + 1, end);
    }

    private void swap(int[] a, int i, int j) {
        //避免不必要的交换
        if (i != j) {
            int temp = a[j];
            a[j] = a[i];
            a[i] = temp;
        }
    }
}
