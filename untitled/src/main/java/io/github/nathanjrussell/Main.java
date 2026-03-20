package io.github.nathanjrussell;

import io.github.nathanjrussell.LambdaExpressions;


public class Main {

    public static void main(String[] args) {


        LambdaExpressions demo = new LambdaExpressions();

        demo.demonstrateBasicLambda();
        demo.demonstrateSupplier();
        demo.demonstrateConsumer();
        demo.demonstrateFunction();
        demo.demonstratePredicate();
        demo.demonstrateBiFunction();
        demo.demonstrateUnaryOperator();
        demo.demonstrateBinaryOperator();
        demo.demonstrateCustomFunctionalInterface();
        demo.demonstrateMethodReferences();
        demo.demonstrateYieldKeyword();
        demo.demonstrateLambdaAsParameter();

    }
}
