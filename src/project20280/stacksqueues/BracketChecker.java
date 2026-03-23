package project20280.stacksqueues;

import project20280.interfaces.Stack;

class BracketChecker {
    private final String input;

    public BracketChecker(String in) {
        input = in;
    }

    public void check() {
        Stack<Character> stack = new ArrayStack<>(); // or LinkedStack<>

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            // opening delimiters
            if (isOpening(ch)) stack.push(ch);

            // closing delimiters
            else if (isClosing(ch)) {
                if (stack.isEmpty()) {
                    System.out.println("Error: missing left delimiter for " + ch);
                    return;
                }

                char open = stack.pop();
                if (!matches(open, ch)) {
                    System.out.println(
                            "Error: " + ch + " does not match " + open
                    );
                    return;
                }
            }
        }

        if (!stack.isEmpty()) System.out.println("Error: missing right delimiter(s)");
        else System.out.println("Correct");
    }

    private boolean isOpening(char c) {
        return c == '(' || c == '[' || c == '{';
    }

    private boolean isClosing(char c) {
        return c == ')' || c == ']' || c == '}';
    }

    private boolean matches(char open, char close) {
        return (open == '(' && close == ')') || (open == '[' && close == ']') || (open == '{' && close == '}');
    }

    public static void main(String[] args) {
        String[] inputs = {
                "[]]()()", // not correct
                "c[d]", // correct\n" +
                "a{b[c]d}e", // correct\n" +
                "a{b(c]d}e", // not correct; ] doesn't match (\n" +
                "a[b{c}d]e}", // not correct; nothing matches final }\n" +
                "a{b(c) ", // // not correct; Nothing matches opening {
        };

        for (String input : inputs) {
            BracketChecker checker = new BracketChecker(input);
            System.out.println("checking: " + input);
            checker.check();
        }
    }
}