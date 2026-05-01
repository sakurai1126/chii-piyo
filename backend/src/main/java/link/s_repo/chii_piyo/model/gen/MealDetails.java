package link.s_repo.chii_piyo.model.gen;

import java.time.LocalDateTime;

public class MealDetails {
    private Long id;

    private Long careRecordId;

    private String note;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCareRecordId() {
        return careRecordId;
    }

    public void setCareRecordId(Long careRecordId) {
        this.careRecordId = careRecordId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}