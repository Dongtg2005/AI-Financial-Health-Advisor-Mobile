package com.finance.api.dto.response;

public class DebtAlertDTO {
    private String type; // "CRITICAL", "WARNING"
    private String message;

    public DebtAlertDTO() {}

    public DebtAlertDTO(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
