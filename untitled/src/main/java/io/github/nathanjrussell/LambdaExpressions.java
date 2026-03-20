package io.github.nathanjrussell;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

// ──────────────────────────────────────────────────────────────────────────────
// Custom functional interfaces – any interface with exactly ONE abstract method
// can be used as a lambda target.  The @FunctionalInterface annotation is
// optional but documents intent and causes a compile-time error if the contract
// is broken accidentally.
// ──────────────────────────────────────────────────────────────────────────────

@FunctionalInterface
interface Transformer {
    String transform(String input);
}

@FunctionalInterface
interface MathOperation {
    int operate(int a, int b);
}

/**
 * Demonstrates a broad range of Java lambda expression patterns.
 *
 * <p>Each {@code demonstrate*()} method is self-contained and prints its own
 * section header so output is easy to follow when run from {@link Main}.
 */
public class LambdaExpressions {

    // ──────────────────────────────────────────────────────────────────────
    // 1. Basic Lambda vs. Anonymous Inner Class
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Shows how a lambda replaces a traditional anonymous-class implementation
     * of a single-method interface ({@link Runnable} here).
     *
     * <ul>
     *   <li>A lambda with no parameters uses empty parentheses: {@code () ->}
     *   <li>The body can be a single expression or a {@code { }} block
     * </ul>
     */
    public void demonstrateBasicLambda() {
        System.out.println("=== 1. Basic Lambda vs. Anonymous Inner Class ===");

        // Traditional anonymous inner class
        Runnable traditional = new Runnable() {
            @Override
            public void run() {
                System.out.println("  [Traditional] Running via anonymous inner class");
            }
        };

        // Equivalent lambda expression – much less boilerplate
        Runnable lambda = () -> System.out.println("  [Lambda]      Running via lambda expression");

        // Multi-line lambda body using a block { }
        Runnable block = () -> {
            System.out.println("  [Block Lambda] Line 1");
            System.out.println("  [Block Lambda] Line 2");
        };

        traditional.run();
        lambda.run();
        block.run();
    }

    // ──────────────────────────────────────────────────────────────────────
    // 2. Supplier<T> – no parameters, returns a value
    // ──────────────────────────────────────────────────────────────────────

    /**
     * {@link Supplier}{@code <T>} represents a function that takes no arguments
     * and produces a result of type {@code T}.  Call {@code get()} to invoke it.
     */
    public void demonstrateSupplier() {
        System.out.println("\n=== 2. Supplier<T> – No Parameters, Returns a Value ===");

        Supplier<String>  greeting    = () -> "Hello, Lambda World!";
        Supplier<Integer> answer      = () -> 42;
        Supplier<Double>  randomValue = Math::random;   // method reference (covered later)

        System.out.println("  greeting.get()    → " + greeting.get());
        System.out.println("  answer.get()      → " + answer.get());
        System.out.println("  randomValue.get() → " + randomValue.get());
    }

    // ──────────────────────────────────────────────────────────────────────
    // 3. Consumer<T> – takes one parameter, returns nothing
    // ──────────────────────────────────────────────────────────────────────

    /**
     * {@link Consumer}{@code <T>} accepts a single value and performs a
     * side-effect (e.g., printing).  Consumers can be chained with
     * {@link Consumer#andThen(Consumer)}.
     */
    public void demonstrateConsumer() {
        System.out.println("\n=== 3. Consumer<T> – Takes a Value, Returns Nothing ===");

        Consumer<String>  printLine  = s -> System.out.println("  Consuming: " + s);
        Consumer<Integer> printSquare = n -> System.out.println("  Square of " + n + " = " + (n * n));

        printLine.accept("Hello Consumer");
        printSquare.accept(5);

        // andThen: run first consumer, then second consumer, on the same input
        Consumer<String> toUpper = s -> System.out.println("  Upper:      " + s.toUpperCase());
        Consumer<String> chained = printLine.andThen(toUpper);
        System.out.println("  --- andThen chain ---");
        chained.accept("chained input");
    }

    // ──────────────────────────────────────────────────────────────────────
    // 4. Function<T, R> – takes one parameter, returns a result
    // ──────────────────────────────────────────────────────────────────────

    /**
     * {@link Function}{@code <T, R>} maps a value of type {@code T} to a value
     * of type {@code R}.  Functions compose with {@code andThen} and
     * {@code compose}.
     */
    public void demonstrateFunction() {
        System.out.println("\n=== 4. Function<T,R> – Takes One Parameter, Returns a Result ===");

        Function<String, Integer> strLength  = s -> s.length();
        Function<Integer, String> intToLabel = n -> "Number: " + n;

        System.out.println("  strLength.apply(\"Lambda\")  → " + strLength.apply("Lambda"));
        System.out.println("  intToLabel.apply(42)       → " + intToLabel.apply(42));

        // andThen: apply this function first, then pass its result to the next
        Function<String, String> lengthLabel = strLength.andThen(intToLabel);
        System.out.println("  strLength.andThen(intToLabel).apply(\"Hello\") → "
                + lengthLabel.apply("Hello"));

        // compose: apply the argument function first, then this function
        Function<String, String> trimFirst  = String::trim;
        Function<String, String> upperAfter = String::toUpperCase;
        Function<String, String> trimThenUpper = upperAfter.compose(trimFirst);
        System.out.println("  upperAfter.compose(trimFirst).apply(\"  hello  \") → \""
                + trimThenUpper.apply("  hello  ") + "\"");
    }

    // ──────────────────────────────────────────────────────────────────────
    // 5. Predicate<T> – takes one parameter, returns boolean
    // ──────────────────────────────────────────────────────────────────────

    /**
     * {@link Predicate}{@code <T>} tests a condition on a value and returns
     * {@code true} or {@code false}.  Predicates combine with
     * {@code and}, {@code or}, and {@code negate}.
     */
    public void demonstratePredicate() {
        System.out.println("\n=== 5. Predicate<T> – Tests a Condition, Returns boolean ===");

        Predicate<String>  isEmpty    = String::isEmpty;
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isEven     = n -> n % 2 == 0;

        System.out.println("  isEmpty.test(\"\")        → " + isEmpty.test(""));
        System.out.println("  isEmpty.test(\"hello\")   → " + isEmpty.test("hello"));
        System.out.println("  isPositive.test(5)      → " + isPositive.test(5));
        System.out.println("  isPositive.test(-3)     → " + isPositive.test(-3));

        // Combining predicates
        Predicate<Integer> isPositiveAndEven = isPositive.and(isEven);
        Predicate<Integer> isPositiveOrEven  = isPositive.or(isEven);
        Predicate<Integer> isNegative        = isPositive.negate();

        System.out.println("  --- combining predicates ---");
        System.out.println("  isPositive.and(isEven).test(4)    → " + isPositiveAndEven.test(4));
        System.out.println("  isPositive.and(isEven).test(3)    → " + isPositiveAndEven.test(3));
        System.out.println("  isPositive.or(isEven).test(-2)    → " + isPositiveOrEven.test(-2));
        System.out.println("  isPositive.negate().test(-5)      → " + isNegative.test(-5));
    }

    // ──────────────────────────────────────────────────────────────────────
    // 6. BiFunction<T, U, R> – two parameters, one return value
    // ──────────────────────────────────────────────────────────────────────

    /**
     * {@link BiFunction}{@code <T, U, R>} generalises {@link Function} to
     * accept two arguments of potentially different types.
     */
    public void demonstrateBiFunction() {
        System.out.println("\n=== 6. BiFunction<T,U,R> – Two Parameters, One Return Value ===");

        BiFunction<String, Integer, String> repeat = (str, times) -> str.repeat(times);
        BiFunction<Integer, Integer, Integer> add  = (a, b) -> a + b;
        BiFunction<Double, Double, Double>   hyp   =
                (a, b) -> Math.sqrt(a * a + b * b);

        System.out.println("  repeat.apply(\"Hi\", 3)         → " + repeat.apply("Hi", 3));
        System.out.println("  add.apply(10, 20)              → " + add.apply(10, 20));
        System.out.printf ("  hyp.apply(3.0, 4.0)           → %.1f%n", hyp.apply(3.0, 4.0));
    }

    // ──────────────────────────────────────────────────────────────────────
    // 7. UnaryOperator<T> – same type in and out
    // ──────────────────────────────────────────────────────────────────────

    /**
     * {@link UnaryOperator}{@code <T>} extends {@code Function<T, T>}: both
     * the input and the output are the same type.  Useful for transformation
     * pipelines over a single type.
     */
    public void demonstrateUnaryOperator() {
        System.out.println("\n=== 7. UnaryOperator<T> – Same Type In and Out ===");

        UnaryOperator<String>  shout   = s -> s.toUpperCase() + "!";
        UnaryOperator<Integer> doubled = n -> n * 2;
        UnaryOperator<Integer> squared = n -> n * n;

        System.out.println("  shout.apply(\"hello\")   → " + shout.apply("hello"));
        System.out.println("  doubled.apply(7)       → " + doubled.apply(7));
        System.out.println("  squared.apply(5)       → " + squared.apply(5));

        // andThen chains two UnaryOperators: square first, then double
        UnaryOperator<Integer> squareThenDouble = n -> doubled.apply(squared.apply(n));
        System.out.println("  square then double 3   → " + squareThenDouble.apply(3));
    }

    // ──────────────────────────────────────────────────────────────────────
    // 8. BinaryOperator<T> – two same-type parameters, same-type result
    // ──────────────────────────────────────────────────────────────────────

    /**
     * {@link BinaryOperator}{@code <T>} extends {@code BiFunction<T, T, T>}:
     * both inputs and the output share the same type.  Common for operations
     * such as addition, multiplication, or string concatenation.
     */
    public void demonstrateBinaryOperator() {
        System.out.println("\n=== 8. BinaryOperator<T> – Two Same-Type Parameters ===");

        BinaryOperator<Integer> add      = Integer::sum;        // static method reference
        BinaryOperator<Integer> multiply = (a, b) -> a * b;
        BinaryOperator<String>  joinWith = (s1, s2) -> s1 + " " + s2;

        System.out.println("  add.apply(15, 25)              → " + add.apply(15, 25));
        System.out.println("  multiply.apply(6, 7)           → " + multiply.apply(6, 7));
        System.out.println("  joinWith.apply(\"Java\",\"24\")    → " + joinWith.apply("Java", "24"));
    }

    // ──────────────────────────────────────────────────────────────────────
    // 9. Custom @FunctionalInterface
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Any interface with exactly one abstract method is a functional interface
     * and can be implemented with a lambda.  Using {@code @FunctionalInterface}
     * makes the intent explicit and triggers a compile-time error if the
     * interface is accidentally given a second abstract method.
     *
     * <p>{@link Transformer} and {@link MathOperation} are defined at the top
     * of this file.
     */
    public void demonstrateCustomFunctionalInterface() {
        System.out.println("\n=== 9. Custom @FunctionalInterface ===");

        // Transformer implementations
        Transformer shout   = s -> s.toUpperCase() + "!!!";
        Transformer whisper = s -> s.toLowerCase() + "...";
        Transformer reverse = s -> new StringBuilder(s).reverse().toString();

        System.out.println("  shout.transform(\"hello\")    → " + shout.transform("hello"));
        System.out.println("  whisper.transform(\"HELLO\")  → " + whisper.transform("HELLO"));
        System.out.println("  reverse.transform(\"Lambda\") → " + reverse.transform("Lambda"));

        // MathOperation implementations
        MathOperation subtract = (a, b) -> a - b;
        MathOperation power    = (base, exp) -> (int) Math.pow(base, exp);

        System.out.println("  subtract.operate(10, 3)     → " + subtract.operate(10, 3));
        System.out.println("  power.operate(2, 8)         → " + power.operate(2, 8));
    }

    // ──────────────────────────────────────────────────────────────────────
    // 10. Method References – four forms
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Method references are shorthand for lambdas that do nothing but forward
     * their arguments to an existing method.  There are four forms:
     * <ol>
     *   <li><b>Static method</b>:          {@code ClassName::staticMethod}
     *   <li><b>Bound instance method</b>:  {@code instance::method}
     *   <li><b>Unbound instance method</b>:{@code ClassName::instanceMethod}
     *   <li><b>Constructor reference</b>:  {@code ClassName::new}
     * </ol>
     */
    public void demonstrateMethodReferences() {
        System.out.println("\n=== 10. Method References – Four Forms ===");

        // 1. Static method reference
        Function<String, Integer> parseInt = Integer::parseInt;
        System.out.println("  [Static]     Integer::parseInt → " + parseInt.apply("42"));

        // 2. Bound instance method reference (method of a specific object)
        String prefix  = "Hello, ";
        Function<String, String> greet = prefix::concat;
        System.out.println("  [Bound]      prefix::concat    → " + greet.apply("World"));

        // 3. Unbound instance method reference (method of an arbitrary instance of the type)
        Function<String, String>  toUpper = String::toUpperCase;
        Function<String, Integer> length  = String::length;
        System.out.println("  [Unbound]    String::toUpperCase → " + toUpper.apply("java"));
        System.out.println("  [Unbound]    String::length      → " + length.apply("Lambda"));

        // 4. Constructor reference
        Function<String, StringBuilder> newSB = StringBuilder::new;
        StringBuilder sb = newSB.apply("Constructor Reference");
        System.out.println("  [Constructor] StringBuilder::new → " + sb);
    }

    // ──────────────────────────────────────────────────────────────────────
    // 11. Switch Expressions and the yield Keyword
    // ──────────────────────────────────────────────────────────────────────

    /**
     * The {@code yield} keyword (Java 14+) produces a value from a
     * <em>block</em> case inside a switch <em>expression</em>.
     *
     * <p>Arrow-form cases ({@code ->}) return their value implicitly; no
     * {@code yield} is needed.  Block-form cases ({@code :}) <em>require</em>
     * a {@code yield} statement to supply the value.
     *
     * <p>Switch expressions are often used directly inside lambdas, which is
     * demonstrated below.
     */
    public void demonstrateYieldKeyword() {
        System.out.println("\n=== 11. Switch Expressions and the yield Keyword ===");

        // ── Arrow-form switch expression (no yield needed) ────────────────
        System.out.println("  -- Arrow form (no yield required) --");
        Function<Integer, String> dayType = day -> switch (day) {
            case 1, 7 -> "Weekend";
            case 2, 3, 4, 5, 6 -> "Weekday";
            default -> "Invalid day number";
        };

        System.out.println("  dayType(1) → " + dayType.apply(1));
        System.out.println("  dayType(3) → " + dayType.apply(3));
        System.out.println("  dayType(9) → " + dayType.apply(9));

        // ── Block-form switch expression – yield is mandatory ─────────────
        System.out.println("  -- Block form (yield required) --");
        Function<String, Integer> gradePoints = grade -> switch (grade.toUpperCase()) {
            case "A" -> 4;
            case "B" -> 3;
            case "C" -> 2;
            case "D" -> 1;
            case "F" -> 0;
            default -> {
                // A block case must use yield to produce the switch value.
                // yield is NOT a return statement; it only exits the switch.
                System.out.println("    (Unknown grade \"" + grade + "\", yielding -1)");
                yield -1;
            }
        };

        System.out.println("  gradePoints(\"A\") → " + gradePoints.apply("A"));
        System.out.println("  gradePoints(\"C\") → " + gradePoints.apply("C"));
        System.out.println("  gradePoints(\"X\") → " + gradePoints.apply("X"));

        // ── yield with an if/else inside the block ────────────────────────
        System.out.println("  -- yield with conditional logic in the block --");
        Function<Integer, String> classify = n -> switch (n) {
            case 0 -> "Zero";
            default -> {
                if (n > 0) {
                    yield "Positive (" + n + ")";
                } else {
                    yield "Negative (" + n + ")";
                }
            }
        };

        System.out.println("  classify(0)   → " + classify.apply(0));
        System.out.println("  classify(42)  → " + classify.apply(42));
        System.out.println("  classify(-7)  → " + classify.apply(-7));
    }

    // ──────────────────────────────────────────────────────────────────────
    // 12. Passing Lambdas as Method Parameters
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Because lambdas are instances of functional interfaces, any method that
     * accepts a functional interface type can receive a lambda at the call
     * site.  This enables flexible, higher-order behaviour without extra
     * subclasses.
     */
    public void demonstrateLambdaAsParameter() {
        System.out.println("\n=== 12. Passing Lambdas as Method Parameters ===");

        applyAndPrint("java lambda",  s -> s.toUpperCase());
        applyAndPrint("SHOUT",        s -> s.toLowerCase());
        applyAndPrint("hello world",  s -> s.replace(" ", "_"));

        System.out.println("  validate(10, 0<n<100)  → " + validate(10,  n -> n > 0 && n < 100));
        System.out.println("  validate(-5, 0<n<100)  → " + validate(-5,  n -> n > 0 && n < 100));
        System.out.println("  validate(200, 0<n<100) → " + validate(200, n -> n > 0 && n < 100));
    }

    /** Helper: applies a {@link Function} to {@code input} and prints the result. */
    private void applyAndPrint(String input, Function<String, String> fn) {
        System.out.println("  applyAndPrint(\"" + input + "\") → " + fn.apply(input));
    }

    /** Helper: tests a {@link Predicate} against {@code value}. */
    private boolean validate(int value, Predicate<Integer> predicate) {
        return predicate.test(value);
    }
}
