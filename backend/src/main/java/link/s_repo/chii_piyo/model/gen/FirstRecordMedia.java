package link.s_repo.chii_piyo.model.gen;

import java.time.OffsetDateTime;

public class FirstRecordMedia {
    private Long id;

    private Long firstRecordId;

    private Long mediaId;

    private OffsetDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFirstRecordId() {
        return firstRecordId;
    }

    public void setFirstRecordId(Long firstRecordId) {
        this.firstRecordId = firstRecordId;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}