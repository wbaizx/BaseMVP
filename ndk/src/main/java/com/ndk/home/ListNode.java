package com.ndk.home;

import com.base.common.util.LogUtilKt;

public class ListNode {
    int val;
    ListNode next;

    public ListNode() {
        this(0, null);
    }

    public ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }

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
        logLinked(l, "logLinked");
    }

    static void logLinked(ListNode l, String msg) {
        StringBuilder sb = new StringBuilder();
        while (l != null) {
            sb.append(l.val);
            sb.append(" - ");
            l = l.next;
        }
        LogUtilKt.log(msg, sb.toString());
    }
}
