package com.ercan;

public class Main {
    public static void main(String[] args) {
        Task12 task = new Task12();
        task.copySemantics();

        System.out.println("//////////////");
        StringBuilder sb = new StringBuilder("Ozan");
        int n = 1;
        int[] arr = {1,2,3};
        task.passByValue(sb, n, arr);
        System.out.println("after function call");
        System.out.println(sb.toString());
        System.out.println(n);
        System.out.println(arr[0]);

        task.section("boxing");
        task.boxing();
        task.section("strings");
        task.strings();

    }
}