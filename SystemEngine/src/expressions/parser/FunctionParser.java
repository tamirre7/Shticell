package expressions.parser;

import expressions.api.Expression;
import expressions.expressionsimpl.*;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.api.Range;
import java.util.ArrayList;
import java.util.Arrays;
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
            String actualValue = arguments.getFirst();
            if (actualValue.length() == 0)
                return new IdentityExpression("", CellType.NOT_INIT);
            if (isBoolean(actualValue)) {
                return new IdentityExpression(Boolean.parseBoolean(actualValue), CellType.BOOLEAN);
            } else if (isNumeric(actualValue)) {
                return new IdentityExpression(Double.parseDouble(actualValue), CellType.NUMERIC);
            } else {
                return new IdentityExpression(actualValue.trim(), CellType.STRING);
            }
        }

        private boolean isBoolean(String value) {
            return "TRUE".equalsIgnoreCase(value) || "FALSE".equalsIgnoreCase(value);
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
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1), ReadOnlySheet);

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
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1), ReadOnlySheet);

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
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1), ReadOnlySheet);


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
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1), ReadOnlySheet);


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
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1), ReadOnlySheet);

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
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1), ReadOnlySheet);

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
            Expression exp = parseExpression(arguments.getFirst(), ReadOnlySheet);

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
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1), ReadOnlySheet);

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
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression middle = parseExpression(arguments.get(1), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(2), ReadOnlySheet);

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
            String cellId = arguments.getFirst().toUpperCase();

            CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellId);

            // create the relevant Ref function instance
            return new Ref(cellIdentifier);
        }
    },

    EQUAL {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for EQUAL function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1), ReadOnlySheet);

            return new Equal(left, right);
        }
    },

    AND {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for AND function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1), ReadOnlySheet);

            return new And(left, right);
        }
    },

    BIGGER{
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for BIGGER function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1), ReadOnlySheet);

            return new Bigger(left, right);
        }
    },

    LESS{
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for LESS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1), ReadOnlySheet);

            return new Less(left, right);
        }
    },

    NOT{
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for NOT function. Expected 1, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression exp = parseExpression(arguments.get(0), ReadOnlySheet);

            return new Not(exp);
        }
    },

    OR{
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for OR function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression right = parseExpression(arguments.get(1), ReadOnlySheet);

            return new Or(left, right);
        }
    },

    PERCENT{
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for PERCENT function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression part = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression whole = parseExpression(arguments.get(1), ReadOnlySheet);

            // all is good. create the relevant function instance
            return new Percent(part, whole);
        }
    },
    IF {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 3) {
                throw new IllegalArgumentException("Invalid number of arguments for IF function. Expected 3, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression condition = parseExpression(arguments.get(0), ReadOnlySheet);
            Expression thanValue = parseExpression(arguments.get(1), ReadOnlySheet);
            Expression elseValue = parseExpression(arguments.get(2), ReadOnlySheet);

            // all is good. create the relevant function instance
            return new If(condition, thanValue,elseValue);
        }
    },
    SUM {
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for SUM function. Expected 1, but got " + arguments.size());
            }

            String rangeName = arguments.getFirst();
            Range range = ReadOnlySheet.getRange(rangeName);

            return new Sum(range);
        }
    },
    AVERAGE{
        @Override
        public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet) {
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for AVERAGE function. Expected 1, but got " + arguments.size());
            }

            String rangeName = arguments.getFirst();
            Range range = ReadOnlySheet.getRange(rangeName);

            return new Average(range);
        }
    };

    // Abstract method to parse function arguments into an Expression
    // Parameters:
    // - arguments: List of function arguments as strings
    // - ReadOnlySheet: Reference to the spreadsheet for context
    // Returns: Parsed Expression object
    abstract public Expression parse(List<String> arguments, ReadOnlySpreadSheet ReadOnlySheet);

    // Static method to parse a string input into an Expression object
    // Handles both function expressions (enclosed in {}) and identity expressions
    // Parameters:
    // - input: String to parse (e.g., "{SUM,A1,B1}" or "42")
    // - ReadOnlySheet: Reference to the spreadsheet for context
    // Returns: Parsed Expression object
    // Throws: IllegalArgumentException if function name is unknown
    public static Expression parseExpression(String input, ReadOnlySpreadSheet ReadOnlySheet) {
        // Check if input is a function (enclosed in curly braces)
        if (input.startsWith("{") && input.endsWith("}")) {
            // Extract function content without braces
            String functionContent = input.substring(1, input.length() - 1);

            // Parse function into parts (function name and arguments)
            List<String> topLevelParts = parseMainParts(functionContent);

            // Get function name and convert to uppercase for case-insensitive matching
            String functionName = topLevelParts.getFirst().toUpperCase();
            topLevelParts.removeFirst(); // Remove function name from arguments list

            try {
                // Attempt to get the corresponding function parser
                FunctionParser functionParser = FunctionParser.valueOf(functionName);
                return functionParser.parse(topLevelParts, ReadOnlySheet);
            } catch (IllegalArgumentException e) {
                // Check if exception was due to unknown function name
                if (Arrays.stream(FunctionParser.values()).noneMatch(f -> f.name().equals(functionName))) {
                    throw new IllegalArgumentException("Unknown function: " + functionName);
                } else {
                    throw e; // Rethrow if exception was due to other reasons
                }
            }
        }
        // If not a function, treat as identity expression (direct value)
        return FunctionParser.IDENTITY.parse(List.of(input), ReadOnlySheet);
    }

    // Helper method to parse function content into main parts while respecting nested functions
    // Handles nested curly braces and splits only on top-level commas
    // Parameters:
    // - input: Function content string to parse (without outer braces)
    // Returns: List of function parts (function name and arguments)
    private static List<String> parseMainParts(String input) {
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        // Iterate through each character
        for (char c : input.toCharArray()) {
            // Track nested braces using stack
            if (c == '{')
                stack.push(c);
            else if (c == '}') {
                stack.pop();
            }

            // Split on commas only at top level (when stack is empty)
            if (c == ',' && stack.isEmpty()) {
                parts.add(buffer.toString());
                buffer.setLength(0); // Reset buffer for next part
            } else {
                buffer.append(c);
            }
        }

        // Add final part if buffer not empty
        if (!buffer.isEmpty()) {
            parts.add(buffer.toString());
        }

        return parts;
    }
}