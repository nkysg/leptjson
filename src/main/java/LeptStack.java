public class LeptStack {
    private static final int LEPT_PARSE_STACK_INIT_SIZE = 256;
    private int top;
    private char[] stack;
    public LeptStack() {
        top = 0;
        stack = new char[LEPT_PARSE_STACK_INIT_SIZE];
    }

    public void push_char(char ch) {
        if (top + 1 >= stack.length) {
            int new_size = stack.length;
            while (top + 1 >= new_size) {
                new_size += new_size >> 1;
            }
            char[] new_stack = new char[new_size];
            System.arraycopy(stack, 0, new_stack, 0, stack.length);
            stack = new_stack;
        }
        stack[top++] = ch;
    }

    public char[] pop_chars(int size) {
        assert top >= size;
        char[] arr = new char[size];
        top -= size;
        System.arraycopy(stack, top, arr, 0, size);
        return arr;
    }

    public int index_top() {
        return top;
    }

    public void set_index_top(int head) {
        top = head;
    }
}
