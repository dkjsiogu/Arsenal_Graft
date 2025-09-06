package io.github.dkjsiogu.arsenalgraft.api.config;

/**
 * 验证结果
 */
public class ValidationResult {
    private final boolean valid;
    private final String message;
    
    private ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
    
    public static ValidationResult success() {
        return new ValidationResult(true, "");
    }
    
    public static ValidationResult failure(String message) {
        return new ValidationResult(false, message);
    }
    
    public boolean isValid() { return valid; }
    public String getMessage() { return message; }
}
