package io.samancore.template_builder.model;

import lombok.Getter;

@Getter
public enum ConditionType {
    VALUE(0, "_value"),
    VISIBLE(1, "_visible"),
    DISABLE(2, "_disable"),
    ALERT(3, "_alert"),
    VALIDATE(4, "_validate");

    private final int value;
    private final String suffix;

    ConditionType(int value, String suffix) {
        this.value = value;
        this.suffix = suffix;
    }

    public static ConditionType bySuffix(String suffix) {
        for (ConditionType c : values()) {
            if (c.getSuffix().equals(suffix)) {
                return c;
            }
        }
        return null;
    }
}
