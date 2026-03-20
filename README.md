# Lambda Expressions – Java Demo

A Maven-based Java project that demonstrates the full spectrum of **lambda expression** patterns available in Java 24.  The project has **zero external dependencies** – it uses only the Java standard library.

---

## Project Structure

```
LambdaExpressions/
├── pom.xml                            
├── README.md                            
└── src/
    └── main/
        └── java/
            └── com/
                └── example/
                    ├── Main.java        Driver / entry point
                    └── LambdaExpressions.java   All demonstrations
```

---

## Prerequisites

| Requirement | Version |
|-------------|---------|
| JDK         | 24 or later |
| Maven       | 3.8 or later |

---

## Building and Running

```bash
# Compile and package
mvn clean package

# Run the demo
java -jar target/lambda-expressions-1.0-SNAPSHOT.jar
```

---

## What Is a Lambda Expression?

A **lambda expression** is an anonymous function – a block of code with parameters and a body that can be stored in a variable or passed to a method.  In Java, every lambda is an instance of a **functional interface** (any interface with exactly one abstract method).

```
(parameters) -> expression
(parameters) -> { statements; }
```

The Java compiler infers the functional-interface type from context, so you rarely need to write the type explicitly.

---

## Concepts Demonstrated

### 1 · Basic Lambda vs. Anonymous Inner Class

Before lambdas, single-method interfaces were implemented via anonymous inner classes – verbose and hard to read.  A lambda collapses all that boilerplate to a single line.

```java
// Anonymous inner class
Runnable r1 = new Runnable() {
    @Override public void run() {
        System.out.println("Anonymous");
    }
};

// Equivalent lambda
Runnable r2 = () -> System.out.println("Lambda");

// Multi-line body uses { }
Runnable r3 = () -> {
    System.out.println("Line 1");
    System.out.println("Line 2");
};
```

**Key points**
- `()` – empty parentheses when there are no parameters.
- A single-expression body needs no braces or semicolons.
- A block body `{ ... }` allows multiple statements.

---

### 2 · `Supplier<T>` – No Parameters, Returns a Value

```java
Supplier<String>  greeting = () -> "Hello, Lambda World!";
Supplier<Integer> answer   = () -> 42;

System.out.println(greeting.get());   // Hello, Lambda World!
System.out.println(answer.get());     // 42
```

- Invoke with `get()`.
- Useful for lazy evaluation – the value is only computed when `get()` is called.

---

### 3 · `Consumer<T>` – Takes a Value, Returns Nothing

```java
Consumer<String>  print  = s -> System.out.println("Consuming: " + s);
Consumer<Integer> square = n -> System.out.println(n + "² = " + (n * n));

print.accept("Hello");   // Consuming: Hello
square.accept(5);        // 5² = 25
```

**Chaining with `andThen`**

```java
Consumer<String> toUpper = s -> System.out.println(s.toUpperCase());
Consumer<String> both    = print.andThen(toUpper);
both.accept("chained");   // runs print, then toUpper on the same input
```

---

### 4 · `Function<T, R>` – Takes One Parameter, Returns a Result

```java
Function<String, Integer> length     = s -> s.length();
Function<Integer, String> intToLabel = n -> "Number: " + n;

System.out.println(length.apply("Lambda"));    // 6
System.out.println(intToLabel.apply(42));      // Number: 42
```

**`andThen` vs `compose`**

```java
// andThen: apply THIS function first, then pass the result to the next
Function<String, String> lengthThenLabel = length.andThen(intToLabel);
lengthThenLabel.apply("Hello");   // Number: 5

// compose: apply the ARGUMENT function first, then apply THIS function
Function<String, String> trimThenUpper = String::toUpperCase.compose(String::trim);
trimThenUpper.apply("  hello  "); // HELLO
```

---

### 5 · `Predicate<T>` – Tests a Condition, Returns `boolean`

```java
Predicate<Integer> isPositive = n -> n > 0;
Predicate<Integer> isEven     = n -> n % 2 == 0;

isPositive.test(5);   // true
isPositive.test(-3);  // false
```

**Combining predicates**

| Method | Meaning |
|--------|---------|
| `p1.and(p2)` | both must be true |
| `p1.or(p2)` | at least one must be true |
| `p.negate()` | inverts the result |

```java
Predicate<Integer> positiveEven = isPositive.and(isEven);
positiveEven.test(4);   // true
positiveEven.test(3);   // false  (odd)
positiveEven.test(-2);  // false  (negative)
```

---

### 6 · `BiFunction<T, U, R>` – Two Parameters, One Return Value

```java
BiFunction<String, Integer, String> repeat =
        (str, times) -> str.repeat(times);

repeat.apply("Hi", 3);   // HiHiHi
```

Use `BiFunction` when `Function` is not enough because you need two inputs of potentially different types.

---

### 7 · `UnaryOperator<T>` – Same Type In and Out

`UnaryOperator<T>` extends `Function<T, T>`.  Both the input and the output share the same type, making it ideal for transformation pipelines.

```java
UnaryOperator<String>  shout   = s -> s.toUpperCase() + "!";
UnaryOperator<Integer> doubled = n -> n * 2;
UnaryOperator<Integer> squared = n -> n * n;

shout.apply("hello");    // HELLO!
doubled.apply(7);        // 14
squared.apply(5);        // 25
```

---

### 8 · `BinaryOperator<T>` – Two Same-Type Parameters

`BinaryOperator<T>` extends `BiFunction<T, T, T>`: both inputs and the result share the same type.

```java
BinaryOperator<Integer> add      = Integer::sum;
BinaryOperator<Integer> multiply = (a, b) -> a * b;
BinaryOperator<String>  joinWith = (s1, s2) -> s1 + " " + s2;

add.apply(15, 25);            // 40
multiply.apply(6, 7);         // 42
joinWith.apply("Java", "24"); // Java 24
```

---

### 9 · Custom `@FunctionalInterface`

You are not limited to the interfaces in `java.util.function`.  Any interface with **exactly one abstract method** is a functional interface.  The `@FunctionalInterface` annotation is optional but documents the intent and prevents accidental additions.

```java
@FunctionalInterface
interface Transformer {
    String transform(String input);
}

@FunctionalInterface
interface MathOperation {
    int operate(int a, int b);
}
```

Usage:

```java
Transformer shout   = s -> s.toUpperCase() + "!!!";
Transformer reverse = s -> new StringBuilder(s).reverse().toString();

MathOperation power = (base, exp) -> (int) Math.pow(base, exp);

shout.transform("hello");     // HELLO!!!
reverse.transform("Lambda");  // adbmaL
power.operate(2, 8);          // 256
```

---

### 10 · Method References – Four Forms

A **method reference** is syntactic sugar for a lambda that simply calls an existing method.  Use `ClassName::method` or `instance::method`.

| Form | Syntax | Equivalent lambda |
|------|--------|-------------------|
| Static method | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| Bound instance | `"Hello, "::concat` | `s -> "Hello, ".concat(s)` |
| Unbound instance | `String::toUpperCase` | `s -> s.toUpperCase()` |
| Constructor | `StringBuilder::new` | `s -> new StringBuilder(s)` |

```java
// 1. Static
Function<String, Integer> parseInt = Integer::parseInt;
parseInt.apply("42");   // 42

// 2. Bound instance (method of a specific captured object)
String prefix = "Hello, ";
Function<String, String> greet = prefix::concat;
greet.apply("World");   // Hello, World

// 3. Unbound instance (method of whichever object is passed as the argument)
Function<String, String> toUpper = String::toUpperCase;
toUpper.apply("java");  // JAVA

// 4. Constructor reference
Function<String, StringBuilder> newSB = StringBuilder::new;
newSB.apply("text");    // StringBuilder containing "text"
```

---

### 11 · Switch Expressions and the `yield` Keyword

Java 14 introduced **switch expressions** – a switch that produces a value.  The `yield` keyword is used inside a **block case** to supply that value.

#### Arrow form – no `yield` needed

```java
Function<Integer, String> dayType = day -> switch (day) {
    case 1, 7 -> "Weekend";
    case 2, 3, 4, 5, 6 -> "Weekday";
    default -> "Invalid day number";
};

dayType.apply(1);   // Weekend
dayType.apply(3);   // Weekday
```

Arrow cases (`->`) return their right-hand-side value implicitly.

#### Block form – `yield` is mandatory

When the case body is a block `{ }`, the compiler cannot automatically extract a return value, so you must use `yield` to explicitly produce one.

```java
Function<String, Integer> gradePoints = grade -> switch (grade.toUpperCase()) {
    case "A" -> 4;
    case "B" -> 3;
    case "C" -> 2;
    case "D" -> 1;
    case "F" -> 0;
    default -> {
        // Block case: use yield to produce the switch-expression value.
        System.out.println("Unknown grade, defaulting to -1");
        yield -1;   // <-- mandatory here
    }
};

gradePoints.apply("A");   // 4
gradePoints.apply("X");   // prints message, returns -1
```

> **`yield` vs `return`** – `yield` only exits the current switch expression and provides its value.  It does **not** return from the enclosing method.

#### `yield` with conditional logic

```java
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

classify.apply(0);    // Zero
classify.apply(42);   // Positive (42)
classify.apply(-7);   // Negative (-7)
```

---

### 12 · Passing Lambdas as Method Parameters

Because a lambda is just an instance of a functional interface, any method that declares a functional-interface parameter can accept a lambda at the call site.  This is the foundation of **higher-order programming** in Java.

```java
// Method that accepts a Function lambda
private void applyAndPrint(String input, Function<String, String> fn) {
    System.out.println(fn.apply(input));
}

// Method that accepts a Predicate lambda
private boolean validate(int value, Predicate<Integer> predicate) {
    return predicate.test(value);
}

// Callers pass lambdas inline – no need for a subclass
applyAndPrint("java lambda",  s -> s.toUpperCase());  // JAVA LAMBDA
applyAndPrint("hello world",  s -> s.replace(" ", "_")); // hello_world

validate(10, n -> n > 0 && n < 100);    // true
validate(200, n -> n > 0 && n < 100);   // false
```

---

## Built-in Functional Interfaces Summary

| Interface | Signature | Description |
|-----------|-----------|-------------|
| `Runnable` | `() -> void` | No args, no return (from `java.lang`) |
| `Supplier<T>` | `() -> T` | No args, returns T |
| `Consumer<T>` | `T -> void` | Takes T, returns nothing |
| `BiConsumer<T,U>` | `(T, U) -> void` | Takes T and U, returns nothing |
| `Function<T,R>` | `T -> R` | Takes T, returns R |
| `BiFunction<T,U,R>` | `(T, U) -> R` | Takes T and U, returns R |
| `Predicate<T>` | `T -> boolean` | Tests a condition |
| `BiPredicate<T,U>` | `(T, U) -> boolean` | Tests a condition on two values |
| `UnaryOperator<T>` | `T -> T` | Transforms T to T |
| `BinaryOperator<T>` | `(T, T) -> T` | Combines two T values into T |

---

## Key Takeaways

1. **Lambdas = anonymous functions** stored in functional-interface variables.
2. **Method references** are compact lambdas that call an existing method.
3. **`@FunctionalInterface`** enforces the single-abstract-method contract at compile time.
4. **`yield`** provides a value from a block case inside a switch *expression* (not a switch *statement*).
5. Passing lambdas to methods enables **higher-order functions** and eliminates the need for ceremony-heavy subclasses.
