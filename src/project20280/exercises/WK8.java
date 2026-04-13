package project20280.exercises;

import project20280.hashtable.ChainHashMap;
import project20280.interfaces.Entry;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Solutions for Q3-Q6 of the HashMaps lab.
 */
public class WK8 {


    // Q3: Draw the 11-entry hash table using h(i) = (3i + 5) mod 11
    //     with separate chaining

    public static void q3() {
        System.out.println("=== Q3: h(i) = (3i + 5) mod 11, separate chaining, capacity=11 ===");
        int capacity = 11;
        int[] keys = {12, 44, 13, 88, 23, 94, 11, 39, 20, 16, 5};

        // Build the table as an array of lists
        List<Integer>[] table = new ArrayList[capacity];
        for (int i = 0; i < capacity; i++) table[i] = new ArrayList<>();

        for (int k : keys) {
            int h = (3 * k + 5) % capacity;
            table[h].add(k);
        }

        printTable(table, capacity);
    }


    // Q4: Draw the 19-entry hash table using the default MAD hash function
    //     from AbstractHashMap, with separate chaining.
    //
    //     MAD: h(k) = |k.hashCode() * scale + shift| mod prime  mod capacity
    //
    //     We fix scale=1, shift=0, prime=109345121 so the output is
    //     deterministic and matches a "default" run with those values.
    //     (The actual AbstractHashMap picks random scale/shift at construction;
    //     here we show the formula with fixed values for illustration.)

    public static void q4() {
        System.out.println("\n=== Q4: MAD hash, capacity=19, separate chaining ===");
        int capacity = 19;
        int prime    = 109345121;
        long scale   = 1;   // fixed for reproducible output
        long shift   = 0;

        int[] keys = {12, 44, 13, 88, 23, 94, 11, 39, 20, 16, 5};

        List<Integer>[] table = new ArrayList[capacity];
        for (int i = 0; i < capacity; i++) table[i] = new ArrayList<>();

        System.out.println("MAD formula: h(k) = (|k * " + scale + " + " + shift
                + "| mod " + prime + ") mod " + capacity);

        for (int k : keys) {
            int h = (int) ((Math.abs(k * scale + shift) % prime) % capacity);
            table[h].add(k);
            System.out.println("  h(" + k + ") = " + h);
        }

        printTable(table, capacity);
    }


    // Q5: Word frequency counter using ChainHashMap
    //     Reports the top-10 most frequently used words.

    public static void q5(String filename) throws FileNotFoundException {
        System.out.println("\n=== Q5: Word frequency counter ===");

        ChainHashMap<String, Integer> counter = new ChainHashMap<>();
        Scanner scanner = new Scanner(new File(filename));

        while (scanner.hasNext()) {
            // normalise: lowercase and strip punctuation
            String word = scanner.next().toLowerCase().replaceAll("[^a-z']", "");
            if (word.isEmpty()) continue;

            Integer count = counter.get(word);
            counter.put(word, count == null ? 1 : count + 1);
        }
        scanner.close();

        // Collect all entries and sort by frequency descending
        List<Map.Entry<String, Integer>> entries = new ArrayList<>();
        for (Entry<String, Integer> e : counter.entrySet())
            entries.add(new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()));

        entries.sort((a, b) -> b.getValue() - a.getValue());

        System.out.println("Top 10 most frequent words:");
        for (int i = 0; i < Math.min(10, entries.size()); i++) {
            Map.Entry<String, Integer> e = entries.get(i);
            System.out.printf("  %2d. %-20s %d%n", i + 1, e.getKey(), e.getValue());
        }
    }


    // Q6: Collision analysis with different hash functions on words.txt

    public static void q6(String filename) throws FileNotFoundException {
        System.out.println("\n=== Q6: Collision analysis on words.txt ===");

        // Load all words into a list once
        List<String> words = new ArrayList<>();
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNext()) words.add(scanner.next().trim());
        scanner.close();
        System.out.println("Total words loaded: " + words.size());

        // (a) Polynomial accumulation, a = 41
        System.out.println("\n(a) Polynomial accumulation, a=41:");
        System.out.println("    Collisions: " + countCollisions(words, s -> hashPoly(s, 41)));

        // (b) Polynomial accumulation, a = 17
        System.out.println("\n(b) Polynomial accumulation, a=17:");
        System.out.println("    Collisions: " + countCollisions(words, s -> hashPoly(s, 17)));

        // (c) Cyclic shift, shift = 7
        System.out.println("\n(c) Cyclic shift, shift=7:");
        System.out.println("    Collisions: " + countCollisions(words, s -> hashCyclic(s, 7)));

        // (d) Best shift value in range [0, 31]
        System.out.println("\n(d) Cyclic shift for all shift values 0..31:");
        int bestShift = 0;
        long bestCount = Long.MAX_VALUE;
        for (int shift = 0; shift <= 31; shift++) {
            final int s = shift;
            long collisions = countCollisions(words, str -> hashCyclic(str, s));
            System.out.printf("    shift=%2d  collisions=%d%n", shift, collisions);
            if (collisions < bestCount) {
                bestCount = collisions;
                bestShift = shift;
            }
        }
        System.out.println("  --> Best shift value: " + bestShift
                + " with " + bestCount + " collisions");

        // (e) Old Java hash code
        System.out.println("\n(e) Old Java hash code:");
        System.out.println("    Collisions: " + countCollisions(words, WK8::oldJavaHash));
    }


    // Hash functions


    /** Polynomial accumulation: h = sum( s[i] * a^(n-i-1) ) */
    public static int hashPoly(String s, int a) {
        int h = 0;
        int n = s.length();
        for (int i = 0; i < n; i++) {
            h += (int) s.charAt(i) * (int) Math.pow(a, n - i - 1);
        }
        return h;
    }

    /** Cyclic shift hash */
    public static int hashCyclic(String s, int shift) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = (h << shift) | (h >>> (32 - shift));
            h += (int) s.charAt(i);
        }
        return h;
    }

    /** Old Java hash code (pre-1.2 String.hashCode) */
    public static int oldJavaHash(String s) {
        int hash = 0;
        int skip = Math.max(1, s.length() / 8);
        for (int i = 0; i < s.length(); i += skip)
            hash = (hash * 37) + s.charAt(i);
        return hash;
    }


    // Helpers


    /** Functional interface so we can pass hash functions as lambdas */
    @FunctionalInterface
    interface HashFunction {
        int hash(String s);
    }

    /**
     * Counts collisions for a given hash function over a list of words.
     * A "collision" is any word whose hash value was already produced by an
     * earlier word (i.e. it shares a bucket with at least one other word).
     * Uses a plain HashMap<Integer,Integer> to count hits per hash value.
     */
    static long countCollisions(List<String> words, HashFunction fn) {
        HashMap<Integer, Integer> freq = new HashMap<>();
        for (String w : words) {
            int h = fn.hash(w);
            freq.merge(h, 1, Integer::sum);
        }
        // collisions = total words - number of distinct hash values
        long collisions = 0;
        for (int count : freq.values())
            if (count > 1) collisions += count - 1;
        return collisions;
    }

    /** Pretty-prints a bucket array */
    static void printTable(List<Integer>[] table, int capacity) {
        System.out.println();
        for (int i = 0; i < capacity; i++) {
            System.out.printf("  bucket[%2d]: %s%n", i,
                    table[i].isEmpty() ? "∅" : table[i].toString());
        }
    }



    public static void main(String[] args) throws FileNotFoundException {
        q3();
        q4();

        // Update these paths to match your project layout
        q5("sample_text.txt");
        q6("words.txt");
    }
}