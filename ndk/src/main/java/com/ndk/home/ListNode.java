package com.ndk.home;

import com.base.common.util.LogUtilKt;

public class ListNode {
    int val;
    ListNode next = null;

    static ListNode getList(int[] array) {
        ListNode node = null;
        ListNode first = null;
        for (int i = 0; i < array.length; i++) {
            if (node == null) {
                node = new ListNode();
                first = node;

            } else {
                node.next = new ListNode();
                node = node.next;
            }
            node.val = array[i];
        }
        return first;
    }

    static void logLinked(ListNode l) {
        LogUtilKt.log("logLinked", l.val);
        if (l.next != null) {
            logLinked(l.next);
        }
    }

    static void logLinked(ListNode l, String msg) {
        LogUtilKt.log(msg, l.val);
        if (l.next != null) {
            logLinked(l.next, msg);
        }
    }
}
