package com.heroyf.numa.utils;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public String[] removeTrim(String[] list) {
        List<String> resultList = new ArrayList<>();
        String[] result = {};
        for(String listString: list) {
            if (!listString.trim().isEmpty()) {
                resultList.add(listString.trim());
            }
        }
        return resultList.toArray(result);
    }

}
