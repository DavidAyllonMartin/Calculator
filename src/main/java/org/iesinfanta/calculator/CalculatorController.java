package org.iesinfanta.calculator;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.text.DecimalFormat;

import static org.iesinfanta.calculator.ExpressionEvaluator.*;

public class CalculatorController {

    public Label primaryDisplay;
    public Label secondaryDisplay;
    private boolean lastButtonWasEquals = false;

    @FXML
    private void initialize() {
        StringExpression expression = Bindings.concat(primaryDisplay.textProperty());

        expression.addListener((observable, oldValue, newValue) -> {
            try {
                double result = calculateExpression(newValue);
                DecimalFormat df = new DecimalFormat("#.######");
                secondaryDisplay.setText(df.format(result));
            } catch (UnsupportedOperationException e) {
                secondaryDisplay.setText("SYNTAX ERROR");
            } catch (StringIndexOutOfBoundsException e){
                secondaryDisplay.setText("");
            }
        });
    }

    public void onNumberClick(MouseEvent mouseEvent) {

        if (lastButtonWasEquals){
            primaryDisplay.setText("");
            lastButtonWasEquals = false;
        }

        Button button = (Button) mouseEvent.getSource();
        String str = button.getText();
        String primaryDisplayString = primaryDisplay.getText();
        char lastChar = getLastChar(primaryDisplayString);
        if (lastChar == ')'){
            primaryDisplay.setText(primaryDisplayString + TIMES_OPERATOR + str);
        }else {
            primaryDisplay.setText(primaryDisplay.getText() + str);
        }
    }

    public void onNotMinusOperatorClick(MouseEvent mouseEvent) {

        if (lastButtonWasEquals){
            lastButtonWasEquals = false;
        }

        Button button = (Button) mouseEvent.getSource();
        String operator = button.getText();
        String primaryDisplayString = primaryDisplay.getText();
        char lastChar = getLastChar(primaryDisplayString);
        char preLastChar = getPreLastChar(primaryDisplayString);

        if (Character.isDigit(lastChar) || lastChar == ')'){
            primaryDisplay.setText(primaryDisplayString + operator);
        }else if ((lastChar == MINUS_OPERATOR && Character.isDigit(preLastChar)) || isNotMinusOperator(lastChar)){
            primaryDisplay.setText(primaryDisplayString.substring(0, primaryDisplayString.length() - 1) + operator);
        }
    }

    public void onMinusClick() {

        if (lastButtonWasEquals){
            lastButtonWasEquals = false;
        }

        String primaryDisplayString = primaryDisplay.getText();
        char lastChar = getLastChar(primaryDisplayString);
        char preLastChar = getPreLastChar(primaryDisplayString);

        if (lastChar == PLUS_OPERATOR || lastChar == '.'){
            primaryDisplay.setText(primaryDisplayString.substring(0, primaryDisplayString.length() - 1) + MINUS_OPERATOR);
        } else if (lastChar == MINUS_OPERATOR) {
            if (Character.isDigit(preLastChar)){
                primaryDisplay.setText(primaryDisplayString.substring(0, primaryDisplayString.length() - 1) + PLUS_OPERATOR);
            }
        }else {
            primaryDisplay.setText(primaryDisplayString + MINUS_OPERATOR);
        }
    }

    public void onLeftBracketClick() {

        if (lastButtonWasEquals){
            lastButtonWasEquals = false;
        }

        String primaryDisplayString = primaryDisplay.getText();
        char lastChar = getLastChar(primaryDisplayString);

        if (lastChar == ')' || Character.isDigit(lastChar)){
            primaryDisplay.setText(primaryDisplayString + TIMES_OPERATOR + '(');
        } else if (lastChar == '.') {
            primaryDisplay.setText(primaryDisplayString.substring(0, primaryDisplayString.length() - 1) + TIMES_OPERATOR + "(");
        }else {
            primaryDisplay.setText(primaryDisplayString + "(");
        }
    }

    public void onRightBracketClick() {

        if (lastButtonWasEquals){
            lastButtonWasEquals = false;
        }

        int leftBracket = 0, rightBracket = 0;
        String primaryDisplayString = primaryDisplay.getText();
        for (char c : primaryDisplayString.toCharArray()) {
           if (c == '('){
               leftBracket++;
           }else if (c == ')'){
               rightBracket++;
           }
        }
        if (leftBracket - rightBracket <= 0){
            return;
        }

        char lastChar = getLastChar(primaryDisplayString);
        if (Character.isDigit(lastChar) || lastChar == ')'){
            primaryDisplay.setText(primaryDisplayString + ")");
        }else if (lastChar == '.') {
            primaryDisplay.setText(primaryDisplayString.substring(0, primaryDisplayString.length() - 1) + ")");
        }
    }

    public void onPointClick() {

        if (lastButtonWasEquals){
            primaryDisplay.setText("");
            lastButtonWasEquals = false;
        }

        String primaryDisplayString = primaryDisplay.getText();
        char lastChar = getLastChar(primaryDisplayString);
        if (lastChar == '(' || isOperator(lastChar) || lastChar == ' '){
            primaryDisplay.setText(primaryDisplayString + "0.");
        }else if (lastChar == ')'){
            primaryDisplay.setText(primaryDisplayString + TIMES_OPERATOR + "0.");
        }else if (Character.isDigit(lastChar)){
            for (int i = primaryDisplayString.length() - 1; i >= 0; i--) {
                char c = primaryDisplayString.charAt(i);
                if (c == '.') {
                    return;
                }else if (!Character.isDigit(c) || i == 0){
                    primaryDisplay.setText(primaryDisplayString + ".");
                }
            }
        }
    }

    public void onEqualsClick() {

        lastButtonWasEquals = true;

        try {
            double result = calculateExpression(primaryDisplay.getText());
            DecimalFormat df = new DecimalFormat("#.######");
            primaryDisplay.setText(df.format(result).replaceAll(",", "."));
            secondaryDisplay.setText("");
        } catch (UnsupportedOperationException e) {
            primaryDisplay.setText("");
            secondaryDisplay.setText(e.getMessage());
        } catch (StringIndexOutOfBoundsException e){
            primaryDisplay.setText("");
            secondaryDisplay.setText("");
        }
    }

    public void onAcClick() {

        if (lastButtonWasEquals){
            lastButtonWasEquals = false;
        }

        primaryDisplay.setText("");
        secondaryDisplay.setText("");
    }

    public void OnDelClick() {

        if (lastButtonWasEquals){
            primaryDisplay.setText("");
            lastButtonWasEquals = false;
        }else {
            try {
                primaryDisplay.setText(primaryDisplay.getText().substring(0, primaryDisplay.getText().length() - 1));
            }catch (StringIndexOutOfBoundsException e){
                //Ignored
            }
        }
    }

    private static char getLastChar(String string) {
        char lastChar = ' ';
        try {
            lastChar = string.charAt(string.length() - 1);
        }catch (StringIndexOutOfBoundsException e){
            //Ignored
        }
        return lastChar;
    }

    private static char getPreLastChar(String string) {
        char preLastChar = ' ';
        try {
            preLastChar = string.charAt(string.length() - 2);
        }catch (StringIndexOutOfBoundsException e){
            //Ignored
        }
        return preLastChar;
    }

}