package com.wdong.basic;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class Common {

    @Test
    public void test1() {
        List<Integer> original = Arrays.asList(12,16,17,19,101);
        List<Integer> selected = Arrays.asList(16,19,107,108,109);

        ArrayList<Integer> add = new ArrayList<Integer>(selected);
        add.removeAll(original);
        System.out.println("Add: " + add);

        ArrayList<Integer> remove = new ArrayList<Integer>(original);
        remove.removeAll(selected);
        System.out.println("Remove: " + remove);
    }

    @Test
    public void test2() {
        StringJoiner joiner = new StringJoiner(" ");

        String title = "mis sta";

        for (String s : title.split(" ")) {
            joiner.add(s + "*");
        }
        System.out.println(joiner.toString());
    }

    @Test
    public void test3() {
        assert (int) Math.ceil(("1".length() - 2) / 3.0f) == 0;
        assert (int) Math.ceil(("12".length() - 2) / 3.0f) == 0;
        assert (int) Math.ceil(("123".length() - 2) / 3.0f) == 1;
        assert (int) Math.ceil(("1234".length() - 2) / 3.0f) == 1;
        assert (int) Math.ceil(("123456".length() - 2) / 3.0f) == 2;
        assert (int) Math.ceil(("1234567".length() - 2) / 3.0f) == 2;
        assert (int) Math.ceil(("12345678".length() - 2) / 3.0f) == 2;
        assert (int) Math.ceil(("123456789".length() - 2) / 3.0f) == 3;
        assert (int) Math.ceil(("1234567891".length() - 2) / 3.0f) == 3;
        assert (int) Math.ceil(("12345678912".length() - 2) / 3.0f) == 3;
    }
}
