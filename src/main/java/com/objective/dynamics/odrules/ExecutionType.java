package com.objective.dynamics.odrules;

import com.objective.dynamics.util.enumeration.Tolerance;
import com.objective.dynamics.util.string.StringUtil;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 1:52 PM
 */
public enum ExecutionType {

    SEQUENCE("sequence"),
    PARALLEL("parallel");

    // nameColumn:
    private String executionType;

    private ExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public static ExecutionType fromExecutionType(String execution) {
        return fromExecutionType(execution, Tolerance.STRICT);
    }

    public static ExecutionType fromExecutionType(String execution, Tolerance tolerance) {
        if (tolerance == Tolerance.TOLERATE_EMPTY && StringUtil.isEmpty(execution)) {
            return null;
        }
        for (ExecutionType c : ExecutionType.values()) {
            if (c.executionType.equals(execution)) {
                return c;
            }
        }
        if (tolerance != Tolerance.TOLERATE_ALL) {
            throw new IllegalArgumentException("Unsupported RuleContainerType execution: " + execution);
        }
        return null;
    }


    //
    // execution:
    //
    public String getType() {
        return this.executionType;
    }


    /**
     * Tests if value is in the "in" array
     *
     * @param value value to test
     * @param in    "in" array to test against
     * @return true if value is in the "in" array. If value == null returns false.
     */
    public static boolean isIn(ExecutionType value, ExecutionType... in) {
        if (value == null) {
            return false;
        }
        for (ExecutionType inEnum : in) {
            if (value == inEnum) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests if "this" enumeration is in the "in" array
     *
     * @param in "in" array to test against
     * @return true if "this" enumeration is in the "in" array
     */
    public boolean in(ExecutionType... in) {
        for (ExecutionType inEnum : in) {
            if (this == inEnum) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.getClass().getSimpleName())
                .append("{");
        toStringFields(buffer);
        buffer.append("}");
        return buffer.toString();
    }

    protected void toStringFields(StringBuffer buffer) {
        buffer.append("executionType").append(" = '").append(this.executionType).append("'; ");
    }

}
