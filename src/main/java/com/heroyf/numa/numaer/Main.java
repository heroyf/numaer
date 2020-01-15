package com.heroyf.numa.numaer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.heroyf.numa.utils.ExecLinuxCMD;
import com.heroyf.numa.utils.Util;

import org.slf4j.LoggerFactory;


public class Main {
    public static void main(String[] args) throws Exception {
        var logger = LoggerFactory.getLogger(Main.class);
        try{
            Numaer numa = new Numaer();
            if (numa.isNuma()) {
                logger.info("os is NUMA");
            } else {
                logger.info("os is not NUMA");
            }
            logger.info("NUMA Node Num: " + numa.NumNode());
            Map<String, Map<String, String>> map = numa.ZoneInfo();
            map.forEach((k, v)->{
                System.out.println("Zone: " + k + "\n" + "Zone Type: " + v.get("Type") + "\n" + "Zone PageFree: " + v.get("PageFree"));
                System.out.println("------------------------");
            });
        } catch (Exception e){
            logger.error(String.valueOf(e));
        }

    }
}

class Numaer {
    private Util tool = new Util();
    public boolean isNuma() {
        //查看是否是numa架构
        File folder = new File("/proc/zoneinfo");
        if (!folder.exists() && !folder.isDirectory()) {
            return false;
        }
        return true;
    }
    private Map<String, Set<String>> Nodes() throws Exception {
        //获取当前系统 内存节点 node 信息
        if (!isNuma()) {
            throw new Exception("OS is not NUMA!");
        }
        List<String> nodeNameList= new ArrayList<>();
        List<String> nodeNumList= new ArrayList<>();
        try (Reader reader = new FileReader("/proc/buddyinfo", StandardCharsets.UTF_8)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String n;
            while ((n = bufferedReader.readLine()) != null) {
                String n_split = n.split(",")[0];
                String nodeName = n_split.replace(" ", "");
                String nodeNum = n_split.split(" ")[1];
                nodeNameList.add(nodeName);
                nodeNumList.add(nodeNum);
            }
        }
        var NodeNameSet = Set.copyOf(nodeNameList);
        var NodeNumSet = Set.copyOf(nodeNumList);
        Map<String, Set<String>> map = new HashMap<>() {{
            put("node ID", NodeNumSet);
            put("node Name", NodeNameSet);
        }};
        return map;
    }
    public int NumNode() throws Exception {
        //返回node的数量
        return Nodes().get("node Name").size();
    }
    public Map<String, Map<String, String>> ZoneInfo() throws IOException {
        //获取内存节点 node 的 zone 区域信息
        Map<String, Map<String, String>> map = new HashMap<>();
        try (Reader reader = new FileReader("/proc/zoneinfo", StandardCharsets.UTF_8)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String n, tmpZoneType = null;
            Boolean zoneFlag = false;
            int zoneID = 0;
            while ((n = bufferedReader.readLine()) != null) {
                n = n.trim(); // 去除收尾空格
                String[] fields = n.split(" ");
                fields = tool.removeTrim(fields);
                Map<String, String> zoneMap = new HashMap<>();
                if (fields.length >=4 && fields[0].equals("Node") && fields[2].equals("zone")) {
                    tmpZoneType = fields[fields.length-1];
                    zoneFlag = true;
                }
                if (zoneFlag && fields[0].equals("pages") && fields.length >=3 && fields[1].equals("free")) {
                    zoneMap.put("Type",tmpZoneType);
                    zoneMap.put("PageFree", fields[fields.length-1]);
                    map.put("zone"+zoneID, zoneMap);
                    zoneID += 1;
                    zoneFlag = false;
                }
            }
        }
        return map;
    }
}


