package com.example.barogo.type;

public enum UserType {
    CUSTOMER("10"),
    ADMIN("20"),
    COURIER("30");

    private final String code;

    UserType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static UserType fromCode(String code) {
        for (UserType type : UserType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown user type: " + code);
    }

}
