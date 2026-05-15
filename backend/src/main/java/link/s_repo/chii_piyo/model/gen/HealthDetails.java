package link.s_repo.chii_piyo.model.gen;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class HealthDetails {
    private Long id;

    private Long careRecordId;

    private BigDecimal temperature;

    private String note;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

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

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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