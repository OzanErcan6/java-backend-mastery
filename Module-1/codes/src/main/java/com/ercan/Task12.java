package com.ercan;

import java.util.*;

public class Task12 {

    public void copySemantics(){
        int a = 12;
        int b = a;
        b=10;
        System.out.println(a);
        System.out.println(b);

        int[] x = {1,2,3};
        int[] y = x;
        y[0] = 99;
        System.out.println(x[0]);
    }
    public void passByValue(StringBuilder sb, int n, int[] arr){
        System.out.println("inside passByValue");
        sb.append("!");
        sb = new StringBuilder("new");
        System.out.println(sb.toString());

        n = 99;
        arr[0] = 42;
        System.out.println(n);
        System.out.println(arr[0]);
    }

    void run(String label, Runnable snippet) {
        System.out.println("--- " + label);
        try {
            snippet.run();
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getClass().getSimpleName()
                    + (e.getMessage() != null ? " -> " + e.getMessage() : ""));
        }
    }

    static String show(String s) {
        return "[" + s.replace("\n", "\\n").replace(" ", "·") + "]";
    }

    void section(String title) {
        System.out.println("\n==================== " + title + " ====================");
    }

    public void boxing() {
        run("1. Integer a1 = 127, b1 = 127; a1 == b1", () -> {
            Integer a1 = 127, b1 = 127;
            System.out.println(a1 == b1);
        });

        run("2. Integer c = 128, d = 128; c == d", () -> {
            Integer c = 128, d = 128;
            System.out.println(c == d);
            System.out.println("equals: " + c.equals(d));   // the fix
        });

        run("3. Integer a2 = 1000; int b2 = 1000; a2 == b2", () -> {
            Integer a2 = 1000;
            int b2 = 1000;
            System.out.println(a2 == b2);
        });

        run("4. Integer x = null; int y = x;", () -> {
            Integer x = null;
            int y = x;                       // NPE here
            System.out.println(y);
        });

        run("5. int v = m.get(\"missing\")", () -> {
            Map<String, Integer> m = new HashMap<>();
            int v = m.get("missing");        // NPE here
            System.out.println(v);
        });

        run("5b. fix: getOrDefault", () -> {
            Map<String, Integer> m = new HashMap<>();
            System.out.println(m.getOrDefault("missing", 0));
        });

        run("6. Integer n = null; int r = true ? n : 0;", () -> {
            Integer n = null;
            int r = true ? n : 0;            // NPE: ternary unboxes n
            System.out.println(r);
        });

        run("6b. fix: keep both sides Integer", () -> {
            Integer n = null;
            Integer r = true ? n : Integer.valueOf(0);
            System.out.println(r);           // prints null, no exception
        });

        run("7. Long.valueOf(5).equals(5)", () -> {
            System.out.println(Long.valueOf(5).equals(5));    // 5 is Integer
            System.out.println("fixed: " + Long.valueOf(5).equals(5L));
        });

        run("8. list.remove(1)", () -> {
            List<Integer> list = new ArrayList<>(List.of(10, 20, 30));
            list.remove(1);                                   // by INDEX
            System.out.println(list);
            List<Integer> list2 = new ArrayList<>(List.of(10, 20, 30));
            list2.remove(Integer.valueOf(10));                // by VALUE
            System.out.println("by value: " + list2);
        });
    }

    public void strings() {

        run("1. Immutability", () -> {
            String s = "hello";
            System.out.println("id before:            " + System.identityHashCode(s));
            s.toUpperCase();                                    // result thrown away
            System.out.println("after toUpperCase():  " + s + " (unchanged)");
            s = s.toUpperCase();
            System.out.println("after reassignment:   " + s
                    + " id: " + System.identityHashCode(s));    // different id = new object
        });

        run("2. Interning and ==", () -> {
            String a = "java";
            String b = "java";
            String c = new String("java");
            System.out.println("a == b            : " + (a == b));            // true
            System.out.println("a == c            : " + (a == c));            // false
            System.out.println("a.equals(c)       : " + a.equals(c));         // true
            System.out.println("a == c.intern()   : " + (a == c.intern()));   // true

            String d = "ja" + "va";                             // constant folded
            String e = "ja";
            String f = e + "va";                                // runtime concat
            final String g = "ja";
            String h = g + "va";                                // final constant: folded
            System.out.println("a == \"ja\"+\"va\"      : " + (a == d));       // true
            System.out.println("a == e + \"va\"       : " + (a == f));       // false
            System.out.println("a == final g + \"va\" : " + (a == h));       // true
            System.out.println("a == f.intern()   : " + (a == f.intern()));  // true
        });

        run("3. += in a loop vs StringBuilder (50_000 chars)", () -> {
            int n = 50_000;

            long t1 = System.nanoTime();
            String s = "";
            for (int i = 0; i < n; i++) s += "x";
            long plusMs = (System.nanoTime() - t1) / 1_000_000;

            long t2 = System.nanoTime();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) sb.append("x");
            long sbMs = (System.nanoTime() - t2) / 1_000_000;

            System.out.println("+=            : " + plusMs + " ms (length " + s.length() + ")");
            System.out.println("StringBuilder : " + sbMs + " ms (length " + sb.length() + ")");
            // Try n = 200_000 to see += get dramatically worse (O(n^2))
        });

        run("4. StringBuilder operations", () -> {
            StringBuilder sb = new StringBuilder("hello");
            sb.append(" world");
            System.out.println("append        : " + sb);
            sb.insert(0, ">> ");
            System.out.println("insert        : " + sb);
            sb.reverse();
            System.out.println("reverse       : " + sb);
            sb.reverse().deleteCharAt(0);
            System.out.println("deleteCharAt  : " + sb);
            sb.setCharAt(0, '#');
            System.out.println("setCharAt     : " + sb);
            sb.setLength(0);
            System.out.println("setLength(0)  : [" + sb + "] (reuse the builder)");

            String word = "level";
            boolean palindrome = new StringBuilder(word).reverse().toString().equals(word);
            System.out.println("palindrome(" + word + "): " + palindrome);
        });

        run("5. split surprises", () -> {
            System.out.println("\"a,b,,\".split(\",\")      -> "
                    + Arrays.toString("a,b,,".split(",")) + " length " + "a,b,,".split(",").length);
            System.out.println("\"a,b,,\".split(\",\", -1)  -> "
                    + Arrays.toString("a,b,,".split(",", -1)) + " length " + "a,b,,".split(",", -1).length);
            System.out.println("\"abc\".split(\"\")        -> " + Arrays.toString("abc".split("")));
            System.out.println("\"\".split(\",\").length   -> " + "".split(",").length);   // 1, not 0
        });

        run("6. Other String gotchas", () -> {
            String n = null;
            System.out.println("null + \"x\"  : " + (n + "x"));         // "nullx", no NPE
            System.out.println("strip vs trim: " + show("\u2003hi\u2003".strip())
                    + " vs " + show("\u2003hi\u2003".trim()));          // strip is Unicode-aware
            System.out.println("isBlank/isEmpty on \"  \": " + "  ".isBlank() + " / " + "  ".isEmpty());
            System.out.println("n.length()  : ");
            System.out.println(n.length());                             // NPE
        });

        run("7. Text blocks", () -> {
            String json = """
                    {
                      "name": "%s",
                      "age": %d
                    }
                    """.formatted("Ana", 30);
            System.out.println(json);
            System.out.println(show(json));

            String oneLine = """
                    This is one \
                    line.
                    """;
            System.out.println("backslash : " + show(oneLine));         // no newline after "one"

            String keepSpace = """
                    trailing\s\s
                    kept""";
            System.out.println("\\s       : " + show(keepSpace));
        });

        run("8. Text block indentation experiment", () -> {
            // Closing delimiter left of content: content keeps the extra indent
            String closeLeft = """
                    hello
                world
                """;
            System.out.println("closing left of content: " + show(closeLeft));

            // Closing delimiter on the last content line: no trailing newline
            String noNewline = """
                    hello
                    world""";
            System.out.println("closing on same line   : " + show(noNewline));
        });
    }

}
