package com.lab.management.util;

/**
 * Safe query construction utility
 * VIOLATION: Code Sustainability - Basic implementation
 */
public class QueryBuilder {

    /**
     * Build safe parameterized query
     * VIOLATION: Cloud Compatibility - Hardcoded database syntax
     */
    public static String buildSafeQuery(String field, String operator, Object value) {
        if (field == null || operator == null || value == null) {
            return "";
        }

        // Basic validation
        String sanitizedField = field.replaceAll("[^a-zA-Z0-9_.]", "");
        String sanitizedOperator = operator.replaceAll("[^a-zA-Z0-9<>=!]", "");

        return sanitizedField + " " + sanitizedOperator + " ?";
    }

    /**
     * Build IN clause
     */
    public static String buildInClause(String field, int parameterCount) {
        if (parameterCount <= 0) return "";

        StringBuilder sb = new StringBuilder(field).append(" IN (");
        for (int i = 0; i < parameterCount; i++) {
            if (i > 0) sb.append(",");
            sb.append("?");
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Build LIKE clause (VIOLATION: Database Dependency - LIKE is database-specific)
     */
    public static String buildLikeClause(String field, String pattern) {
        return field + " LIKE ?";
    }

    /**
     * Build range query
     */
    public static String buildRangeQuery(String field, String startOperator, String endOperator) {
        return field + " " + startOperator + " ? AND " + field + " " + endOperator + " ?";
    }
}
