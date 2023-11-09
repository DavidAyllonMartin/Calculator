package org.iesinfanta.calculator;

import java.util.Map;
import java.util.Stack;
import java.util.function.BinaryOperator;

public class ExpressionEvaluator {

    public static final char TIMES_OPERATOR = 'x';
    public static final char DIVIDE_OPERATOR = '÷';
    public static final char PLUS_OPERATOR = '+';
    public static final char MINUS_OPERATOR = '-';
    private static final Map<Character, BinaryOperator<Double>> OPERATIONS = Map.of(
            PLUS_OPERATOR, (a, b) -> a + b,
            MINUS_OPERATOR, (a, b) -> a - b,
            TIMES_OPERATOR, (a, b) -> a * b,
            DIVIDE_OPERATOR, (a, b) -> {
                if (b == 0) {
                    throw new UnsupportedOperationException("Can't divide by zero");
                }
                return a / b;
            }
    );
    public static double calculateExpression(String expression) {
        expression = cleanExpression(expression);
        char[] tokens = expression.toCharArray();
        boolean lastPushIsOperator = true;

        Stack<Double> numbers       = new Stack<>();
        Stack<Character> operators  = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if (Character.isDigit(tokens[i]) || (tokens[i] == MINUS_OPERATOR && lastPushIsOperator) || tokens[i] == '(') {
                StringBuilder sb = new StringBuilder();
                //Un menos de apertura siempre pertenece al número
                if (tokens[i] == MINUS_OPERATOR){
                    sb.append(tokens[i++]);
                }
                //Si me encuentro un paréntesis busco su cierre y analizo lo que hay dentro de forma recursiva
                if (tokens[i] == '('){
                    int equilibrate = 1;
                    while (equilibrate != 0){
                        i++;
                        if (tokens[i] == '('){
                            equilibrate++;
                        } else if (tokens[i] == ')') {
                            equilibrate--;
                        }
                        sb.append(tokens[i]);
                    }
                    numbers.push(calculateExpression(sb.substring(0, sb.length() - 1)));
                    lastPushIsOperator = false;
                    //Y si no añado el número
                }else {
                    while (i < tokens.length && (Character.isDigit(tokens[i]) || tokens[i] == '.')) {
                        sb.append(tokens[i++]);
                    }
                    numbers.push(Double.parseDouble(sb.toString()));
                    lastPushIsOperator = false;
                    i--;
                }
            } else if (isOperator(tokens[i])) {
                while (!operators.empty() && hasPrecedence(tokens[i], operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(tokens[i]);
                lastPushIsOperator = true;
            }
        }

        while (!operators.empty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    public static double applyOperation(char operator, double b, double a) {
        return OPERATIONS.get(operator).apply(a, b);
    }
    public static boolean hasPrecedence(char op1, char op2) {
        return (op1 != TIMES_OPERATOR && op1 != DIVIDE_OPERATOR) || (op2 != PLUS_OPERATOR && op2 != MINUS_OPERATOR);
    }
    public static boolean isOperator(char c){
        return c == PLUS_OPERATOR || c == MINUS_OPERATOR || c == DIVIDE_OPERATOR || c == TIMES_OPERATOR;
    }
    public static boolean isNotMinusOperator(char c){
        return c == PLUS_OPERATOR || c == DIVIDE_OPERATOR || c == TIMES_OPERATOR;
    }
    public static String cleanExpression(String expression) {
        StringBuilder balancedExpression = new StringBuilder(expression);
        if (expression.startsWith("-(")){
            balancedExpression.replace(0, 1, "0-(");
        }

        while (true){
            char lastChar = balancedExpression.charAt(balancedExpression.length() - 1);
            if ( isOperator(lastChar) || lastChar == '(') {
                balancedExpression.deleteCharAt(balancedExpression.length() - 1);
            }else{
                break;
            }
        }

        int openBrackets = 0;
        int closedBrackets = 0;

        for (char c : balancedExpression.toString().toCharArray()) {
            if (c == '(') {
                openBrackets++;
            } else if (c == ')') {
                closedBrackets++;
            }
        }

        for (int i = 0; i < openBrackets - closedBrackets; i++) {
            balancedExpression.append(')');
        }

        return balancedExpression.toString();
    }
}
