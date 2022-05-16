import java.util.*;
import java.io.*;

public class interpreter {

    private String s;
    private int currIndex;
    private char inputToken;
    private HashMap<String, Integer> map = new HashMap<String, Integer>();

    /**
     * method to read the input files using a while hasnextline loop with scanner class
     * calls interpretor and assignment method
     * interpretor method takes in a string from the nextline of scanner class
     * @param sc
     */
    public void read(Scanner sc){
        while(sc.hasNextLine()) {
            String line = sc.nextLine().trim();

            while (line.length() == 0) {
                line = sc.nextLine().trim();
            }

            interpreter(line);
            assignment();
        }
    }

    /**
     * constructor method
     * initialize String s and currentIndex
     * removes spaces from input string
     * calls nextToken method
     * @param s
     */
    void interpreter(String s) {
        this.s = s.replaceAll("\\s", "");
        currIndex = 0;
        nextToken();
    }

    /**
     * method to take in next element in the interpreter string
     * check if the string ends with ;
     * throw exception if ; is missing
     * increase currIndex counter for next Character
     * set current inputToken to character at currentIndex
     */
    void nextToken(){
        char c;

        if(s.charAt(s.length() - 1) != ';') {
            throw new RuntimeException("Missing ';' token exptected");
        }

        c = s.charAt(currIndex++);
        inputToken = c;
    }

    /**
     * method to start identifier method, 
     * which then assign variable value/name and then evaluate the expression
     * then add the var and operand to a hashmap for token matching
     * lastly print out var and operand
     */
    void assignment() {
        String var = identifier();
        int operand = eval();
        map.put(var, operand);
        System.out.println(var + " = " + operand);

    }

    /**
     * method to identify if the inputToken is a valid varaible name
     * must start with a letter, then append to stringbuilder
     * else throw an exception
     * 
     * character appended to the string afterwards must be a digit, _ , or a digit
     * then validate the value assigned to the variable
     * 
     * the variable assignment must be followed up with a nextToken assign with the operator '='
     * else throw an exception
     * 
     * @return StringBuilder sb
     */
    String identifier(){
        StringBuilder sb = new StringBuilder();

        if (Character.isLetter(inputToken)) {
            sb.append(inputToken);
        }
        else
            throw new RuntimeException("Invalid variable name");
        nextToken();

        while (Character.isLetter(inputToken) || Character.isDigit(inputToken) || inputToken == '_' ) {
            sb.append(inputToken);
            nextToken();
        }

        if (inputToken != '=') {
            throw new RuntimeException("Not an assignment statement");
        }
        nextToken();
        return sb.toString();
    }

    /**
     * calls exp function to evaluate the operator
     * 
     * determines the assignment if it ends with ;
     * else throw error exception
     * @return x
     */
    int eval() {
        int x = exp();
        if (inputToken == ';') {     
            return x;
        } else {
            throw new RuntimeException("Missing ';' token expected ");
        }
    }

    /**
     * method to evaluate if the operator is + or -
     * 
     * if it is then the function calls the term and apply function recursively if there are multiple assignment declaration
     * 
     * the term function is applied instead if the operator is * or /
     * 
     * the apply function will then compute the expression depending on the operator
     * @return
     */
    int exp() {
        int t = term();
        while (inputToken == '+' || inputToken == '-') {
            char input = inputToken;
            nextToken();
            int t2 = term();
            t = apply(input, t, t2);
        }
        return t;
    }

    /**
     * the term function is applied instead of exp() function if the operator is * or /
     * 
     * first call the factor function to check if there are multiple assignment declaration
     * 
     * then calls the term and apply function if the operator is * or /, 
     * will call recursively if there are multiple assignment declaration within the factor() function
     * 
     * the apply function will then compute the expression depending on the operator
     * @return
     */
    int term() {
        int f = factor();
        while (inputToken == '*' || inputToken == '/') {
            char input = inputToken;
            nextToken();
            int f2 = factor();
            f = apply(input, f, f2);
        }
        return f;
    }

    /**
     * this method will recursively handle multple assignment declaration
     * 
     * store and excecute the operators according to the PEMDAS rule
     * 
     * checks for duplicate operators, changing the expression to positive or negative
     * checks parenthesis with match method call
     * */ 

    int factor() {
        int x = 0;
        String temp = String.valueOf(inputToken);

        if (map.containsKey(temp)) {
            x = map.get(temp).intValue();
            nextToken();
            return x;
        } else if (inputToken == '(') {
            nextToken();
            x = exp();
            match(')');
            return x;
        } else if (inputToken == '-') {
            nextToken();
            x = factor();
            return -x;
        } else if (inputToken == '+') {
            nextToken();
            x = factor();
            return x;
        } else if (inputToken == '0') {
            nextToken();
            if (Character.isDigit(inputToken))
                throw new RuntimeException("ERROR !! Invalid value assignment, starts with '0'");
            return 0;
        }
        temp = "";

        while (Character.isDigit(inputToken)) {
            temp += inputToken;
            nextToken();
        }
        return Integer.parseInt(temp);
    }

    /**
     * method to check if there is a closing parenthesis ) for each opening parenthesis (
     * @param token
     */
    void match(char token) {
        if (inputToken == token) {
            nextToken();
        } else {
            throw new RuntimeException("Missing Closing Parenthesis");    // throw a RuntimeException if the closing Parenthesis is missing
        }
    }

    /**
     * method to compute based on the operator
     * @param ch
     * @param t
     * @param t2
     * @return
     */
    static int apply(char ch, int t, int t2) {
        int t3 = 0;

        switch (ch) {
            case '+':
                t3 = t + t2;
                break;
            case '-':
                t3 = t - t2;
                break;
            case '*':
                t3 = t * t2;
                break;
            case '/':
                t3 = t / t2;
                break;
        }
        return t3;
    }
    
    public static void main(String args[]) {

        /**
         * Input files parameter --> input.txt - input4.txt
         */

        try {
            Scanner sc = new Scanner(new FileInputStream("input4.txt"));
            interpreter evalExp = new interpreter();
            evalExp.read(sc);
        }
        
        catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
    }
}