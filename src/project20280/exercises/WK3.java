package project20280.exercises;
import project20280.interfaces.Stack;
import project20280.stacksqueues.ArrayStack;
import javax.lang.model.element.Element;
import java.util.EmptyStackException;




public class WK3 {
    public static void main(String[] args) {
        {

        }
    }

    //Q2
    class QueueWithTwoStacks {
        Stack stack1 = new Stack() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public void push(Object o) {
            }

            @Override
            public Object top() {
                return null;
            }

            @Override
            public Object pop() {
                return null;
            }
        };
        Stack stack2 = new Stack() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public void push(Object o) {
            }

            @Override
            public Object top() {
                return null;
            }

            @Override
            public Object pop() {
                return null;
            }
        };

        void enqueue(Element element) {
            // Push the element onto the first stack
            stack1.push(element);
        }

        Element dequeue() {
            // If both stacks are empty, throw an error,
            if (stack1.isEmpty() && stack2.isEmpty())
                throw new EmptyStackException(); // supposedly EmptyQueueException();

            // If the second stack is empty, transfer elements from the first stack to the second stack
            if (stack2.isEmpty()) while (!stack1.isEmpty()) stack2.push(stack1.pop());

            // Pop and return the top element from the second stack
            return (Element) stack2.pop();
        }
    }

    //Q3
    class Q3 {
        static void reverseStack(Stack stack) {
            Stack tempStack1 = new Stack() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public void push(Object o) {
                }

                @Override
                public Object top() {
                    return null;
                }

                @Override
                public Object pop() {
                    return null;
                }
            };
            Stack tempStack2 = new Stack() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public void push(Object o) {
                }

                @Override
                public Object top() {
                    return null;
                }

                @Override
                public Object pop() {
                    return null;
                }
            };
            // Transfer elements from the original stack to tempStack1
            while (!stack.isEmpty()) tempStack1.push(stack.pop());

            // Transfer elements from tempStack1 to tempStack2 (effectively reversing their order)
            while (!tempStack1.isEmpty()) tempStack2.push(tempStack1.pop());

            // Transfer elements back from tempStack2 to the original stack
            while (!tempStack2.isEmpty()) stack.push(tempStack2.pop());

        }
    }


    //Q4
    class BaseConverter {

        static String convertToBinary(long dec_num) {
            if (dec_num == 0) return "0";

            Stack<Long> stack = new ArrayStack<>();
            long n = dec_num;

            while (n > 0) {
                stack.push(n % 2);
                n /= 2;
            }

            StringBuilder sb = new StringBuilder();
            while (!stack.isEmpty()) {
                sb.append(stack.pop());
            }
            return sb.toString();
        }
    }
}










