package link.s_repo.chii_piyo.model.gen;

import java.time.LocalDateTime;

public class WordRecordMedia {
    private Long id;

    private Long wordRecordId;

    private Long mediaId;

    private LocalDateTime createdAt;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}