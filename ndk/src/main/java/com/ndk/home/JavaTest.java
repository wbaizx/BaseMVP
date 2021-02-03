package com.ndk.home;

import com.base.common.util.LogUtilKt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

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
 * 1.基准值定为最右边，i和j从最左边开始，如果j小于基准值，则i和j交换位置，并且i++，j++。否则i保持不动，j++。
 * 最终当j移动到基准值所在位置后，基准值与i交换位置
 * 2.基准值在最左边，“哨兵”i在最左边，“哨兵”j在最右边，从右边（注意要从右边开始）先开始（j--），
 * 如果“哨兵”j所在的数据小于基准值则停止；“哨兵”i开始（i++），如果“哨兵”i所在的数据大于基准值则停止，i与j交换位置；
 * 如果i和j相遇，则基准值与i或j（因为两者现在一致）交换位置
 */
public class JavaTest {
    public void start() {
        replaceSpace();
        reverseOrder();
        linkedReverse();
        findNoRepetitionString();
        quickSort();
        hillSort();
        fib(2);
        ListNode.logLinked(rotateRight(ListNode.getList(new int[]{1, 2, 3, 4, 5}), 2), "rotateRight");
        ListNode.logLinked(sortLinkedList(ListNode.getList(new int[]{-1, 5, 3, 4, 0, 8, 1, 0, 2, 0, 0, 3})), "sortLinkedList");
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
        ListNode l1 = ListNode.getList(new int[]{1, 2, 3});

        ListNode.logLinked(l1);

        l1 = reverseLinked(l1, null);

        ListNode.logLinked(l1);
    }

    private ListNode reverseLinked(ListNode my, ListNode top) {
        ListNode head;
        if (my.next != null) {
            head = reverseLinked(my.next, my);
        } else {
            head = my;
        }
        my.next = top;
        return head;
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
        int a[] = getArray();

        long time = System.nanoTime();
        beginSort1(a, 0, a.length - 1);
//        beginSort2(a, 0, a.length - 1);
        LogUtilKt.log("quickSort", (System.nanoTime() - time) + " -- " + Arrays.toString(a));
    }

    private int[] getArray() {
        int num = 5000;
        int[] a = new int[num];
        Random r = new Random();
        for (int i = 0; i < num; i++) {
            a[i] = r.nextInt();
        }

        return a;
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

    private void hillSort() {
        int[] a = getArray();

        long time = System.nanoTime();

        int gap = a.length / 2;

        while (gap > 0) {
            for (int i = 0; i < gap; i++) {
                for (int j = i; j < a.length; j += gap) {
                    int index = j;
                    int t = a[index];
                    while (index - gap >= i && a[index - gap] > t) {
                        a[index] = a[index - gap];
                        index -= gap;
                    }
                    a[index] = t;
                }
            }
            gap /= 2;
        }

        LogUtilKt.log("hillSort", (System.nanoTime() - time) + " -- " + Arrays.toString(a));
    }

    private void fib(int n) {
        int n0 = 0, n1 = 1;
        int num = 0;

        if (n == 0) {
            num = n0;

        } else if (n == 1) {
            num = n1;

        } else {
            for (int i = 2; i <= n; i++) {
                num = n0 + n1;
                n0 = n1;
                n1 = num;
            }
        }
        LogUtilKt.log("fib", num);
    }

    private ListNode rotateRight(ListNode head, int k) {
        if (head == null) return head;

        ListNode h = head;
        int len = 1;
        while (h.next != null) {
            h = h.next;
            len++;
        }
        int move = k % len;

        if (len == 1 || move == 0) return head;

        h.next = head;
        h = head;
        for (int i = 0; i < len - move - 1; i++) {
            h = h.next;
        }
        head = h.next;
        h.next = null;
        return head;
    }

    private ListNode sortLinkedList(ListNode head) {
        if (head == null) return head;

        ListNode h = new ListNode(Integer.MIN_VALUE, null);
        while (head != null) {
            ListNode root = h;
            while (root.next != null && head.val > root.next.val) {
                root = root.next;
            }
            ListNode node = head.next;
            head.next = root.next;
            root.next = head;
            head = node;
            ListNode.logLinked(h, "sortLinkedList----------");
        }
        return h.next;
    }
}
