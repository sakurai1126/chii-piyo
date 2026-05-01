package link.s_repo.chii_piyo.model.gen;

import java.time.LocalDateTime;

public class SharingGroupMembers {
    private Long id;

    private Long sharingGroupId;

    private Long userId;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSharingGroupId() {
        return sharingGroupId;
    }

    public void setSharingGroupId(Long sharingGroupId) {
        this.sharingGroupId = sharingGroupId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}