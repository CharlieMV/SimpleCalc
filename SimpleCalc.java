import java.util.List;		// used by expression evaluator

/**
 *	<Description goes here>
 *
 *	@author	
 *	@since	
 */
public class SimpleCalc {
	
	private ExprUtils utils;	// expression utilities
	
	private ArrayStack<Double> valueStack;		// value stack
	private ArrayStack<String> operatorStack;	// operator stack

	// constructor	
	public SimpleCalc() {
		utils = new ExprUtils();
		valueStack = new ArrayStack<Double>();
		operatorStack = new ArrayStack<String>();
	}
	
	public static void main(String[] args) {
		SimpleCalc sc = new SimpleCalc();
		sc.run();
	}
	
	public void run() {
		System.out.println("\nWelcome to SimpleCalc!!!\n");
		runCalc();
		System.out.println("\nThanks for using SimpleCalc! Goodbye.\n");
	}
	
	/**
	 *	Prompt the user for expressions, run the expression evaluator,
	 *	and display the answer.
	 */
	public void runCalc() {
		boolean running = true;
			while (running) {
			//	Prompt user to get expression
			Prompt pr = new Prompt();
			String expression = pr.getString("");
			//	Help menu and quit
			if (expression.equals("h")) printHelp();
			else if (expression.equals("q")) running = false;
			else {
				//	Tokenize
				List<String> tokens = utils.tokenizeExpression(expression);
				//	Evaluate and print
				System.out.println(evaluateExpression(tokens));
			}
		}
	}
	
	/**	Print help */
	public void printHelp() {
		System.out.println("Help:");
		System.out.println("  h - this message\n  q - quit\n");
		System.out.println("Expressions can contain:");
		System.out.println("  integers or decimal numbers");
		System.out.println("  arithmetic operators +, -, *, /, %, ^");
		System.out.println("  parentheses '(' and ')'");
	}
	
	/**
	 *	Evaluate expression and return the value
	 *	@param tokens	a List of String tokens making up an arithmetic expression
	 *	@return			a double value of the evaluated expression
	 */
	public double evaluateExpression(List<String> tokens) {
		//	Keep adding tokens and evaluating when needed
		while (!tokens.isEmpty()) {
			//	Keep track of tokens to add
			String tempToken = tokens.get(0);
			//	Check if any operations are needed
			if (!operatorStack.isEmpty() && utils.isOperator(
												tempToken.charAt(0))) {
				//	If next operator is ), evaluate until ( reached
				if (tempToken.equals(")")) {
					while (!operatorStack.peek().equals("(")) {
						evaluateOnce();
					}
					operatorStack.pop();
				}
				else {
					//	If previous operator has precedence or is of the same
					//	precedence, evaluate first
					while (!operatorStack.isEmpty() && !operatorStack.
							peek().equals("(") && !tempToken.equals("(") 
							&& hasPrecedence(tempToken, operatorStack.peek())) {
						evaluateOnce();
					}
				}
			}
			//	Add token for next iteration
			if (!tokens.get(0).equals(")"))
				addToken(tokens.remove(0));
			else tokens.remove(0);
		}
		
		//	Evaluate everything left
		while (!operatorStack.isEmpty()) {
			evaluateOnce();
		}
		
		return valueStack.pop();
	}
	
	//	Evaluates using the top 2 numbers on value stack and top of operator stack
	public void evaluateOnce() {
		//	Values to operate on
		double value2 = valueStack.pop();
		double value1 = valueStack.pop();
		System.out.println(value1 + operatorStack.peek() + value2);
		//	Check what expression in being used
		switch (operatorStack.pop()) {
			case "+":	valueStack.push(value1 + value2);	return;
			case "-":	valueStack.push(value1 - value2);	return;
			case "*":	valueStack.push(value1 * value2);	return;
			case "/":	valueStack.push(value1 / value2);	return;
			case "%":	valueStack.push(value1 % value2);	return;
			case "^":	valueStack.push(Math.pow(value1, value2));	return;
		}
	}
	
	/**	Adds a token into either value or operator stack
	 * 	@param	String		token to add
	 */
	public void addToken(String token) {
		try {
			valueStack.push(Double.parseDouble(token));
		} catch (NumberFormatException e) {
			operatorStack.push(token);
		}
	}
	
	/**
	 *	Precedence of operators
	 *	@param op1	operator 1
	 *	@param op2	operator 2
	 *	@return		true if op2 has higher or same precedence as op1; false otherwise
	 *	Algorithm:
	 *		if op1 is exponent, then false
	 *		if op2 is either left or right parenthesis, then false
	 *		if op1 is multiplication or division or modulus and 
	 *				op2 is addition or subtraction, then false
	 *		otherwise true
	 */
	private boolean hasPrecedence(String op1, String op2) {
		if (op1.equals("^")) return false;
		if (op2.equals("(") || op2.equals(")")) return false;
		if ((op1.equals("*") || op1.equals("/") || op1.equals("%")) 
				&& (op2.equals("+") || op2.equals("-")))
			return false;
		return true;
	}
	 
}
