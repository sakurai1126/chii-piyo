package link.s_repo.chii_piyo.model.gen;

import java.time.OffsetDateTime;

public class WordRecordMedia {
    private Long id;

    private Long wordRecordId;

    private Long mediaId;

    private OffsetDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWordRecordId() {
        return wordRecordId;
    }

    public void setWordRecordId(Long wordRecordId) {
        this.wordRecordId = wordRecordId;
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