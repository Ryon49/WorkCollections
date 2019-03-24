package com.wdong.xml;

import com.wdong.config.IdGenerator;
import com.wdong.model.simple.SimpleStar;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.HashSet;

public class ActorHandler extends DefaultHandler {

    private String value;

    private boolean allowAdd = false;

    private HashMap<String, SimpleStar> stars = new HashMap<>();

    private HashSet<String> invalids = new HashSet<>();

    private SimpleStar star;

    public HashMap<String, SimpleStar> getStars() {
        return stars;
    }

    public HashSet<String> getInvalids() {
        return invalids;
    }

    @Override
    public void endElement(String s, String s1, String element) {
        switch (element) {
            case "actor":
                if (allowAdd) {
                    stars.put(star.getName(), star);
                    allowAdd = false;
                }
                break;
            case "stagename":
                if (stars.containsKey(value)) {
                    allowAdd = false;
                    invalids.add(value);
                } else {
                    allowAdd = true;
                    star = new SimpleStar(IdGenerator.getStringId(IdGenerator.type.Star));
                    star.setName(value);
                }
                break;
            case "dob":
                if (allowAdd) {
                    star.setBirthYear(value);
                }
                break;
        }
    }

    @Override
    public void characters(char[] ac, int i, int j) {
        value = new String(ac, i, j);
    }
}
