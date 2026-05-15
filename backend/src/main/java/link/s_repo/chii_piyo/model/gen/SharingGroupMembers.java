package link.s_repo.chii_piyo.model.gen;

import java.time.OffsetDateTime;

public class SharingGroupMembers {
    private Long id;

    private Long sharingGroupId;

    private Long userId;

    private OffsetDateTime createdAt;

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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}