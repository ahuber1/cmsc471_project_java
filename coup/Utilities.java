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
	
	public static <T> T xthLastItemOfStack(Stack<T> originalStack, int x) {
		Stack<T> copyStack = Utilities.copyStack(originalStack);
		
		while(copyStack.size() > x)
			copyStack.pop();
		
		return copyStack.pop();
	}

}
