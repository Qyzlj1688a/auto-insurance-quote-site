package com.example.quote.validation;

/**
 * Validation group marker interfaces.
 *
 * <p>These markers keep the validation layer visible before concrete request
 * rules are added.
 */
public final class ValidationGroups {

    private ValidationGroups() {
    }

    public interface Create {
    }

    public interface Search {
    }
}
