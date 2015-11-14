package coup;

import java.util.Stack;

public class Utilities {
	
	public static <T> Stack<T> copyStack(Stack<T> originalStack) {
		Stack<T> tempStack = new Stack<T>();
		Stack<T> newStack = new Stack<T>();
		
		while(!originalStack.isEmpty()) {
			tempStack.add(originalStack.pop());
		}
		
		while(!tempStack.isEmpty()) {
			T data = tempStack.pop();
			originalStack.add(data);
			newStack.add(data);
		}
		
		return newStack;
	}

}
