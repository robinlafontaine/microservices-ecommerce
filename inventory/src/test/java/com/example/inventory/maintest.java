package com.example.inventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class maintest {
    public static void main(String[] args) {
        Set<Integer> test = new HashSet<Integer>();
        test.add(1);
        test.add(2);
        test.add(3);
        test.add(2);

        List<Integer> test2 = new ArrayList<Integer>();

        test2.add(1);
        test2.add(2);
        test2.add(3);
        test2.add(2);

        System.out.println(test);
        System.out.println(test2);
    }
}
