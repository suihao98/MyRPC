package com.test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class Main {
    public static void main(String[] args) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] ids = bean.getAllThreadIds();
        ThreadInfo[] infos = bean.getThreadInfo(ids);
        for (ThreadInfo info : infos) {
            System.out.println(info.getThreadName());
        }
    }
}