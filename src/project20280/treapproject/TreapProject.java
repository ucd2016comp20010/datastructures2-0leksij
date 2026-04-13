package project20280.treapproject;

/**
 * Entry point for the Treap project.
 *
 * <p>Runs the TreapMap smoke test, then both benchmarks.</p>
 *
 * <p>To run individual components:
 * <ul>
 *   <li>{@link TreapMap#main}           – quick functional demo</li>
 *   <li>{@link PerformanceBenchmark#main} – Treap vs AVL vs JDK TreeMap</li>
 *   <li>{@link SortingBenchmark#main}   – TreapSort vs PQSort vs TimSort vs QS vs MS</li>
 *   <li>{@link TreapMapTest}            – JUnit 5 unit tests (run via your IDE / Maven)</li>
 * </ul>
 */
public class TreapProject {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║           Treap Project — UCD            ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();

        System.out.println("▶ TreapMap smoke test");
        System.out.println("─".repeat(50));
        TreapMap.main(new String[]{});
        System.out.println();

        System.out.println("▶ Sorting Benchmark");
        System.out.println("─".repeat(50));
        SortingBenchmark.main(new String[]{});
        System.out.println();

        System.out.println("▶ Performance Benchmark (this may take a moment...)");
        System.out.println("─".repeat(50));
        PerformanceBenchmark.main(new String[]{});
    }
}
