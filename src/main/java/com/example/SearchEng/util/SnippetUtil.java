package com.example.SearchEng.util;

import java.util.HashSet;
import java.util.List;

public class SnippetUtil {
    public static String makeSnippet(String title, String abs, List<String> terms, int width) {
        String text = ((title == null ? "": title) + " " + (abs == null ? "" : abs)).trim();
        String low = text.toLowerCase();
        int pos = Integer.MAX_VALUE;
        for(String t : terms){
            int i = low.indexOf(t);
            if(i >= 0 && i < pos) pos = i;
        }
        if(pos == Integer.MAX_VALUE) pos = 0;
        int start = Math.max(0, pos - width / 4);
        int end = Math.min(text.length(), start - width);
        String snip = text.substring(start, end);
        for(String t : new HashSet<>(terms)){
            snip = snip.replace(t, "**" + t + "**");
            String cap = t.substring(0, 1).toUpperCase() + (t.length() > 1 ? t.substring(1) : "");
            snip = snip.replace(cap, "**" + cap + "**");
        }
        return snip;
    }
}
