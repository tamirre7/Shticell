package expressions.parser;

import expressions.api.Expression;
import expressions.expressionsimpl.*;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellIdentifierImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public enum FunctionParser {
    IDENTITY {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for IDENTITY function. Expected 1, but got " + arguments.size());
            }

            // all is good. create the relevant function instance
            String actualValue = arguments.getFirst().trim();
            if (isBoolean(actualValue)) {
                return new IdentityExpression(Boolean.parseBoolean(actualValue), CellType.BOOLEAN);
            } else if (isNumeric(actualValue)) {
                return new IdentityExpression(Double.parseDouble(actualValue), CellType.NUMERIC);
            } else {
                return new IdentityExpression(actualValue, CellType.STRING);
            }
        }

        private boolean isBoolean(String value) {
            return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
        }


        private boolean isNumeric(String value) {
            try {
                Double.parseDouble(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    },
    PLUS {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function (e.g. number of arguments)
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for PLUS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1).trim(), ReadOnlySheet);

            // more validations on the expected argument types
            if (!left.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC) || !right.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for PLUS function. Expected NUMERIC, but got " + left.getFunctionResultType(ReadOnlySheet) + " and " + right.getFunctionResultType(ReadOnlySheet));
            }

            // all is good. create the relevant function instance
            return new Plus(left, right);
        }
    },
    MINUS {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for MINUS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1).trim(), ReadOnlySheet);

            // more validations on the expected argument types
            if (!left.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC) || !right.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for MINUS function. Expected NUMERIC, but got " + left.getFunctionResultType(ReadOnlySheet) + " and " + right.getFunctionResultType(ReadOnlySheet));
            }

            // all is good. create the relevant function instance
            return new Minus(left, right);
        }
    },

    TIMES {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for TIMES function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1).trim(), ReadOnlySheet);

            // more validations on the expected argument types
            if (!left.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC) || !right.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for TIMES function. Expected NUMERIC, but got " + left.getFunctionResultType(ReadOnlySheet) + " and " + right.getFunctionResultType(ReadOnlySheet));
            }

            // all is good. create the relevant function instance
            return new Times(left, right);
        }

    },

    DIVIDE {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for DIVIDE function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1).trim(), ReadOnlySheet);

            // more validations on the expected argument types
            if (!left.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC) || !right.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for DIVIDE function. Expected NUMERIC, but got " + left.getFunctionResultType(ReadOnlySheet) + " and " + right.getFunctionResultType(ReadOnlySheet));
            }

            // all is good. create the relevant function instance
            return new Divide(left, right);
        }
    },

    MOD {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for MOD function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1).trim(), ReadOnlySheet);

            // more validations on the expected argument types
            if (!left.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC) || !right.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for MOD function. Expected NUMERIC, but got " + left.getFunctionResultType(ReadOnlySheet) + " and " + right.getFunctionResultType(ReadOnlySheet));
            }

            // all is good. create the relevant function instance
            return new Mod(left, right);
        }
    },

    POW {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for POW function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1).trim(), ReadOnlySheet);

            // more validations on the expected argument types
            if (!left.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC) || !right.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for POW function. Expected 2 NUMERIC, but got " + left.getFunctionResultType(ReadOnlySheet) + " and " + right.getFunctionResultType(ReadOnlySheet));
            }

            // all is good. create the relevant function instance
            return new Pow(left, right);
        }
    },

    ABS {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for ABS function. Expected 1, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression exp = parseExpression(arguments.getFirst().trim(), ReadOnlySheet);

            // more validations on the expected argument types
            if (!exp.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for ABS function. Expected NUMERIC, but got " + exp.getFunctionResultType(ReadOnlySheet));
            }

            // all is good. create the relevant function instance
            return new Abs(exp);
        }
    },

    CONCAT {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for CONCAT function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1).trim(), ReadOnlySheet);

            // more validations on the expected argument types
            if (!left.getFunctionResultType(ReadOnlySheet).equals(CellType.STRING) || !right.getFunctionResultType(ReadOnlySheet).equals(CellType.STRING)) {
                throw new IllegalArgumentException("Invalid argument types for CONCAT function. Expected NUMERIC, but got " + left.getFunctionResultType(ReadOnlySheet) + " and " + right.getFunctionResultType(ReadOnlySheet));
            }

            // all is good. create the relevant function instance
            return new Concat(left, right);
        }
    },

    SUB {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 3) {
                throw new IllegalArgumentException("Invalid number of arguments for SUB function. Expected 3, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), ReadOnlySheet);
            Expression middle = parseExpression(arguments.get(1).trim(), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(2).trim(), ReadOnlySheet);

            // more validations on the expected argument types
            if (!left.getFunctionResultType(ReadOnlySheet).equals(CellType.STRING) || !right.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC) || !middle.getFunctionResultType(ReadOnlySheet).equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for SUB function. Expected STRING and 2 NUMERIC, but got " + left.getFunctionResultType(ReadOnlySheet) + " and " + middle.getFunctionResultType(ReadOnlySheet) + "and" + right.getFunctionResultType(ReadOnlySheet));
            }

            // all is good. create the relevant function instance
            return new Sub(left, middle, right);
        }
    },

    REF {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for REF function. Expected 1, but got " + arguments.size());
            }

            // structure is good. parse arguments
            String cellId = arguments.getFirst().trim();

            CellIdentifier cellIdentifier = CellIdentifierImpl.fromString(cellId);;

            // create the relevant Ref function instance
            return new Ref(cellIdentifier);
        }
    };


    abstract public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet);

    public static Expression parseExpression(String input, ReadOnlySpreadSheet ReadOnlySheet) {

        if (input.startsWith("{") && input.endsWith("}")) {

            String functionContent = input.substring(1, input.length() - 1);
            List<String> topLevelParts = parseMainParts(functionContent);


            String functionName = topLevelParts.getFirst().trim().toUpperCase();

            //remove the first element from the array
            topLevelParts.removeFirst();
            return FunctionParser.valueOf(functionName).parse(topLevelParts, ReadOnlySheet);
        }

        // handle identity expression
        return FunctionParser.IDENTITY.parse(List.of(input.trim()), ReadOnlySheet);
    }

    private static List<String> parseMainParts(String input) {
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (char c : input.toCharArray()) {
            if (c == '{')
                stack.push(c);
            else if (c == '}') {
                stack.pop();
            }

            if (c == ',' && stack.isEmpty()) {
                // If we are at a comma and the stack is empty, it's a separator for top-level parts
                parts.add(buffer.toString().trim());
                buffer.setLength(0); // Clear the buffer for the next part
            } else {
                buffer.append(c);
            }
        }

        // Add the last part
        if (!buffer.isEmpty()) {
            parts.add(buffer.toString().trim());
        }

        return parts;
    }

    public static void main(String[] args) {

        //String input = "plus, {plus, 1, 2}, {plus, 1, {plus, 1, 2}}";
//        String input = "1";
//        parseMainParts(input).forEach(System.out::println);

          String input = "{pow, 2, -1}";
      //  String input = "{plus, {divide, 44, 22}, {abs,-2.5}}";
//        String input = "4";
        // String input = "{ref,A17}";
        Expression expression = parseExpression(input, null);
        EffectiveValue result = expression.evaluate(null);
        System.out.println("result: " + result.getValue() + " of type " + result.getCellType());
    }

}