package com.heroyf.numa.numaer;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Numaer numa = new Numaer();
        System.out.println(numa.isNuma());
    }
}

class Numaer {
    public boolean isNuma() {
        File folder = new File("/proc/zoneinfo");
        if (!folder.exists() && !folder.isDirectory()) {
            return false;
        }
        return true;
    }
    public
}


