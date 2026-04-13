package project20280.treapproject;

import project20280.interfaces.Entry;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("TreapMap")
class TreapMapTest {


    // Helpers
    // Returns a fresh treap seeded for determinism.
    private TreapMap<Integer, String> fresh() {
        return new TreapMap<>(42L);
    }

    /** Builds a treap with entries (1,"a"), (2,"b"), ..., (n, letter). */
    private TreapMap<Integer, String> populated(int n) {
        TreapMap<Integer, String> t = fresh();
        for (int i = 1; i <= n; i++) {
            t.put(i, String.valueOf((char) ('a' + (i - 1) % 26)));
        }
        return t;
    }

    /** Collects entry keys from an iterable in iteration order. */
    private <K, V> List<K> keys(Iterable<Entry<K, V>> it) {
        List<K> list = new ArrayList<>();
        for (Entry<K, V> e : it) list.add(e.getKey());
        return list;
    }


    // size() / isEmpty()
    @Test
    @DisplayName("size() on empty treap is 0")
    void testSizeEmpty() {
        assertEquals(0, fresh().size());
    }

    @Test
    @DisplayName("isEmpty() on empty treap is true")
    void testIsEmptyOnEmpty() {
        assertTrue(fresh().isEmpty());
    }

    @Test
    @DisplayName("size() grows with each distinct put()")
    void testSizeGrowsOnPut() {
        TreapMap<Integer, String> t = fresh();
        for (int i = 1; i <= 10; i++) {
            t.put(i, "v");
            assertEquals(i, t.size());
        }
    }

    @Test
    @DisplayName("size() unchanged when put() updates an existing key")
    void testSizeUnchangedOnUpdate() {
        TreapMap<Integer, String> t = populated(5);
        int sizeBefore = t.size();
        t.put(3, "updated");
        assertEquals(sizeBefore, t.size());
    }

    @Test
    @DisplayName("isEmpty() returns false after insertions")
    void testIsEmptyAfterInsert() {
        TreapMap<Integer, String> t = fresh();
        t.put(1, "a");
        assertFalse(t.isEmpty());
    }


    // get()


    @Test
    @DisplayName("get() returns null for a missing key")
    void testGetMissing() {
        assertNull(fresh().get(99));
    }

    @Test
    @DisplayName("get() returns the correct value after put()")
    void testGetPresent() {
        TreapMap<Integer, String> t = fresh();
        t.put(7, "seven");
        assertEquals("seven", t.get(7));
    }

    @Test
    @DisplayName("get() returns updated value after duplicate put()")
    void testGetAfterUpdate() {
        TreapMap<Integer, String> t = fresh();
        t.put(3, "old");
        t.put(3, "new");
        assertEquals("new", t.get(3));
    }

    @Test
    @DisplayName("get() returns null after removing the key")
    void testGetAfterRemove() {
        TreapMap<Integer, String> t = fresh();
        t.put(5, "five");
        t.remove(5);
        assertNull(t.get(5));
    }


    // put()


    @Test
    @DisplayName("put() returns null on first insertion")
    void testPutReturnsNullFirstTime() {
        assertNull(fresh().put(1, "a"));
    }

    @Test
    @DisplayName("put() returns old value when key already exists")
    void testPutReturnsOldValue() {
        TreapMap<Integer, String> t = fresh();
        t.put(1, "old");
        assertEquals("old", t.put(1, "new"));
    }

    @Test
    @DisplayName("put() maintains sorted order (entrySet in ascending key order)")
    void testPutMaintainsSortedOrder() {
        TreapMap<Integer, String> t = fresh();
        int[] insertOrder = {5, 2, 8, 1, 4, 7, 10, 3, 6, 9};
        for (int k : insertOrder) t.put(k, "v");
        List<Integer> keyList = keys(t.entrySet());
        for (int i = 0; i < keyList.size() - 1; i++) {
            assertTrue(keyList.get(i) < keyList.get(i + 1),
                "Expected ascending order but got " + keyList);
        }
    }

    @Test
    @DisplayName("put() maintains valid Treap invariants after every insert")
    void testPutMaintatinsInvariants() {
        TreapMap<Integer, String> t = fresh();
        for (int i = 10; i >= 1; i--) {
            t.put(i, "v");
            assertTrue(t.isValid(), "Invariant violated after inserting " + i);
        }
    }

    @Test
    @DisplayName("put() works with sorted (ascending) input")
    void testPutSortedAscending() {
        TreapMap<Integer, String> t = fresh();
        for (int i = 1; i <= 20; i++) t.put(i, "v");
        assertEquals(20, t.size());
        assertTrue(t.isValid());
    }

    @Test
    @DisplayName("put() works with sorted (descending) input")
    void testPutSortedDescending() {
        TreapMap<Integer, String> t = fresh();
        for (int i = 20; i >= 1; i--) t.put(i, "v");
        assertEquals(20, t.size());
        assertTrue(t.isValid());
    }


    // remove()


    @Test
    @DisplayName("remove() returns null for a missing key")
    void testRemoveMissing() {
        assertNull(fresh().remove(42));
    }

    @Test
    @DisplayName("remove() returns the value of the removed entry")
    void testRemoveReturnsValue() {
        TreapMap<Integer, String> t = fresh();
        t.put(7, "seven");
        assertEquals("seven", t.remove(7));
    }

    @Test
    @DisplayName("remove() decrements size")
    void testRemoveDecrementsSize() {
        TreapMap<Integer, String> t = populated(5);
        int before = t.size();
        t.remove(3);
        assertEquals(before - 1, t.size());
    }

    @Test
    @DisplayName("remove() preserves valid Treap invariants")
    void testRemovePreservesInvariants() {
        TreapMap<Integer, String> t = populated(15);
        List<Integer> toRemove = Arrays.asList(3, 7, 1, 12, 15, 5);
        for (int k : toRemove) {
            t.remove(k);
            assertTrue(t.isValid(), "Invariant violated after removing " + k);
        }
    }

    @Test
    @DisplayName("remove() on the only element leaves the treap empty")
    void testRemoveSingleElement() {
        TreapMap<Integer, String> t = fresh();
        t.put(1, "a");
        t.remove(1);
        assertTrue(t.isEmpty());
        assertNull(t.get(1));
    }

    @Test
    @DisplayName("remove() all elements leaves the treap empty")
    void testRemoveAll() {
        int n = 10;
        TreapMap<Integer, String> t = populated(n);
        for (int i = 1; i <= n; i++) t.remove(i);
        assertTrue(t.isEmpty());
        assertEquals(0, t.size());
    }

    @Test
    @DisplayName("remove() non-existent key leaves size unchanged")
    void testRemoveNonExistentLeavesSize() {
        TreapMap<Integer, String> t = populated(5);
        int before = t.size();
        t.remove(99);
        assertEquals(before, t.size());
    }


    // firstEntry() / lastEntry()


    @Test
    @DisplayName("firstEntry() returns null on empty treap")
    void testFirstEntryEmpty() {
        assertNull(fresh().firstEntry());
    }

    @Test
    @DisplayName("lastEntry() returns null on empty treap")
    void testLastEntryEmpty() {
        assertNull(fresh().lastEntry());
    }

    @Test
    @DisplayName("firstEntry() returns the minimum key")
    void testFirstEntry() {
        TreapMap<Integer, String> t = populated(10);
        assertEquals(1, t.firstEntry().getKey());
    }

    @Test
    @DisplayName("lastEntry() returns the maximum key")
    void testLastEntry() {
        TreapMap<Integer, String> t = populated(10);
        assertEquals(10, t.lastEntry().getKey());
    }

    @Test
    @DisplayName("firstEntry() and lastEntry() agree for a singleton treap")
    void testFirstLastSingleton() {
        TreapMap<Integer, String> t = fresh();
        t.put(42, "x");
        assertEquals(42, t.firstEntry().getKey());
        assertEquals(42, t.lastEntry().getKey());
    }


    // ceilingEntry()
    @Test
    @DisplayName("ceilingEntry() returns null when all keys are smaller")
    void testCeilingNull() {
        TreapMap<Integer, String> t = populated(5);   // keys 1..5
        assertNull(t.ceilingEntry(6));
    }

    @Test
    @DisplayName("ceilingEntry() returns exact match when key exists")
    void testCeilingExact() {
        TreapMap<Integer, String> t = populated(10);
        assertEquals(5, t.ceilingEntry(5).getKey());
    }

    @Test
    @DisplayName("ceilingEntry() returns next key when given key is absent")
    void testCeilingAbove() {
        TreapMap<Integer, String> t = fresh();
        for (int k : new int[]{1, 3, 5, 7, 9}) t.put(k, "v");
        assertEquals(5, t.ceilingEntry(4).getKey());
    }


    // floorEntry()


    @Test
    @DisplayName("floorEntry() returns null when all keys are larger")
    void testFloorNull() {
        TreapMap<Integer, String> t = populated(5);   // keys 1..5
        assertNull(t.floorEntry(0));
    }

    @Test
    @DisplayName("floorEntry() returns exact match when key exists")
    void testFloorExact() {
        TreapMap<Integer, String> t = populated(10);
        assertEquals(5, t.floorEntry(5).getKey());
    }

    @Test
    @DisplayName("floorEntry() returns previous key when given key is absent")
    void testFloorBelow() {
        TreapMap<Integer, String> t = fresh();
        for (int k : new int[]{1, 3, 5, 7, 9}) t.put(k, "v");
        assertEquals(3, t.floorEntry(4).getKey());
    }


    // lowerEntry()
    @Test
    @DisplayName("lowerEntry() returns null when key is at or below minimum")
    void testLowerNull() {
        TreapMap<Integer, String> t = populated(5);
        assertNull(t.lowerEntry(1));
    }

    @Test
    @DisplayName("lowerEntry() returns predecessor of existing key")
    void testLowerExisting() {
        TreapMap<Integer, String> t = populated(10);
        assertEquals(4, t.lowerEntry(5).getKey());
    }

    @Test
    @DisplayName("lowerEntry() returns largest key strictly less than query")
    void testLowerAbsent() {
        TreapMap<Integer, String> t = fresh();
        for (int k : new int[]{1, 3, 5, 7, 9}) t.put(k, "v");
        assertEquals(3, t.lowerEntry(4).getKey());
    }


    // higherEntry()
    @Test
    @DisplayName("higherEntry() returns null when key is at or above maximum")
    void testHigherNull() {
        TreapMap<Integer, String> t = populated(5);
        assertNull(t.higherEntry(5));
    }

    @Test
    @DisplayName("higherEntry() returns successor of existing key")
    void testHigherExisting() {
        TreapMap<Integer, String> t = populated(10);
        assertEquals(6, t.higherEntry(5).getKey());
    }

    @Test
    @DisplayName("higherEntry() returns smallest key strictly greater than query")
    void testHigherAbsent() {
        TreapMap<Integer, String> t = fresh();
        for (int k : new int[]{1, 3, 5, 7, 9}) t.put(k, "v");
        assertEquals(5, t.higherEntry(4).getKey());
    }


    // subMap()
    @Test
    @DisplayName("subMap() returns keys in [fromKey, toKey)")
    void testSubMapBasic() {
        TreapMap<Integer, String> t = populated(10);
        List<Integer> sub = keys(t.subMap(3, 7));
        assertEquals(Arrays.asList(3, 4, 5, 6), sub);
    }

    @Test
    @DisplayName("subMap() returns empty when fromKey >= toKey")
    void testSubMapEmpty() {
        TreapMap<Integer, String> t = populated(10);
        assertFalse(t.subMap(5, 5).iterator().hasNext());
        assertFalse(t.subMap(7, 3).iterator().hasNext());
    }

    @Test
    @DisplayName("subMap() returns all entries when range covers the whole treap")
    void testSubMapAll() {
        TreapMap<Integer, String> t = populated(5);
        List<Integer> sub = keys(t.subMap(1, 6));
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), sub);
    }

    @Test
    @DisplayName("subMap() returns sorted results")
    void testSubMapSorted() {
        TreapMap<Integer, String> t = fresh();
        int[] insertOrder = {8, 3, 6, 1, 9, 4, 7, 2, 5};
        for (int k : insertOrder) t.put(k, "v");
        List<Integer> sub = keys(t.subMap(3, 8));
        for (int i = 0; i < sub.size() - 1; i++) {
            assertTrue(sub.get(i) < sub.get(i + 1));
        }
    }


    // entrySet()
    @Test
    @DisplayName("entrySet() returns empty list for empty treap")
    void testEntrySetEmpty() {
        assertFalse(fresh().entrySet().iterator().hasNext());
    }

    @Test
    @DisplayName("entrySet() returns all entries in ascending key order")
    void testEntrySetOrder() {
        TreapMap<Integer, String> t = fresh();
        int[] insertOrder = {5, 2, 8, 1, 4, 7, 10, 3, 6, 9};
        for (int k : insertOrder) t.put(k, "v");
        List<Integer> keyList = keys(t.entrySet());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), keyList);
    }

    @Test
    @DisplayName("entrySet() size matches treap size")
    void testEntrySetSize() {
        TreapMap<Integer, String> t = populated(7);
        int count = 0;
        for (Entry<Integer, String> ignored : t.entrySet()) count++;
        assertEquals(7, count);
    }


    // keySet() / values()


    @Test
    @DisplayName("keySet() returns keys in sorted order")
    void testKeySet() {
        TreapMap<Integer, String> t = populated(5);
        List<Integer> ks = new ArrayList<>();
        for (Integer k : t.keySet()) ks.add(k);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), ks);
    }

    @Test
    @DisplayName("values() returns values in key-sorted order")
    void testValues() {
        TreapMap<Integer, String> t = fresh();
        t.put(1, "alpha");
        t.put(2, "beta");
        t.put(3, "gamma");
        List<String> vals = new ArrayList<>();
        for (String v : t.values()) vals.add(v);
        assertEquals(Arrays.asList("alpha", "beta", "gamma"), vals);
    }


    // Structural validity
    @Test
    @DisplayName("isValid() is true for an empty treap")
    void testIsValidEmpty() {
        assertTrue(fresh().isValid());
    }

    @Test
    @DisplayName("isValid() holds after many random insertions and deletions")
    void testIsValidRandomOps() {
        TreapMap<Integer, String> t = new TreapMap<>(0L);
        Random rng = new Random(123);
        for (int i = 0; i < 200; i++) {
            int k = rng.nextInt(100);
            if (rng.nextBoolean()) {
                t.put(k, "v");
            } else {
                t.remove(k);
            }
            assertTrue(t.isValid(), "Invariant broken at step " + i);
        }
    }


    // height() — probabilistic bound
    @Test
    @DisplayName("height() is at most 3 * log2(n) with high probability (n=1000)")
    void testHeightBound() {
        TreapMap<Integer, String> t = new TreapMap<>(7L);
        int n = 1000;
        List<Integer> keys = new ArrayList<>();
        for (int i = 1; i <= n; i++) keys.add(i);
        Collections.shuffle(keys, new Random(7L));
        for (int k : keys) t.put(k, "v");
        int h = t.height();
        int bound = (int) (3 * (Math.log(n) / Math.log(2)));
        assertTrue(h <= bound, "Height " + h + " exceeded bound " + bound);
    }


    // Custom comparator
    @Test
    @DisplayName("TreapMap with reverse comparator stores keys in descending order")
    void testCustomComparator() {
        TreapMap<Integer, String> t =
            new TreapMap<>(Comparator.<Integer>naturalOrder().reversed());
        for (int k : new int[]{5, 3, 8, 1, 7}) t.put(k, "v");
        // firstEntry() should be the numerically largest key
        assertEquals(8, t.firstEntry().getKey());
        assertEquals(1, t.lastEntry().getKey());
    }


    // toString()
    @Test
    @DisplayName("toString() shows sorted keys in bracket notation")
    void testToString() {
        TreapMap<Integer, String> t = populated(5);
        assertEquals("[1, 2, 3, 4, 5]", t.toString());
    }

    @Test
    @DisplayName("toString() on empty treap is '[]'")
    void testToStringEmpty() {
        assertEquals("[]", fresh().toString());
    }
}
