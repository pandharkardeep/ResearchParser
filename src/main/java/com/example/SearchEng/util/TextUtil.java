package com.example.SearchEng.util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class TextUtil {
    private static final Pattern WORD = Pattern.compile("[A-Za-z0-9]+");
    private static final Set<String> STOP = new HashSet<>();

    static{
        try{
            var lines = Files.readAllLines(Path.of("src/main/resources/stopwords.txt"), StandardCharsets.UTF_8);
            for(var l: lines) STOP.add(l.trim().toLowerCase());
        } catch (Exception e) {
            STOP.addAll(Arrays.asList("a an the of in on for to and or is are was were be been being by with as at from this that these those into than over under it its it's"
                    .toLowerCase().split("\\s+")));
        }
    }

    public static List<String> tokenize(String text, boolean dropStop){
        if(text == null) return List.of();
        text = text.toLowerCase(Locale.ROOT);
        var m = WORD.matcher(text);;
        List<String> toks = new ArrayList<>();
        while(m.find()){
            String t = m.group();
            if(dropStop && STOP.contains(t)) continue;
            toks.add(t);
        }
        return toks;
    }
}
