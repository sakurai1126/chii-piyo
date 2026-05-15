package link.s_repo.chii_piyo.model.gen;

import java.time.OffsetDateTime;

public class Users {
    private Long id;

    private String cognitoUserId;

    private String displayName;

    private String email;

    private String userIconUrl;

    private Boolean isDarkMode;

    private Boolean isEasyMode;

    private String role;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCognitoUserId() {
        return cognitoUserId;
    }

    public void setCognitoUserId(String cognitoUserId) {
        this.cognitoUserId = cognitoUserId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserIconUrl() {
        return userIconUrl;
    }

    public void setUserIconUrl(String userIconUrl) {
        this.userIconUrl = userIconUrl;
    }

    public Boolean getIsDarkMode() {
        return isDarkMode;
    }

    public void setIsDarkMode(Boolean isDarkMode) {
        this.isDarkMode = isDarkMode;
    }

    public Boolean getIsEasyMode() {
        return isEasyMode;
    }

    public void setIsEasyMode(Boolean isEasyMode) {
        this.isEasyMode = isEasyMode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}