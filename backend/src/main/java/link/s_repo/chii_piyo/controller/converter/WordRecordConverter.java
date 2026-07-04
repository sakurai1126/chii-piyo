package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import link.s_repo.chii_piyo.model.gen.WordRecordResponseDto;
import link.s_repo.chii_piyo.model.gen.WordRecords;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * WordRecordエンティティをWordRecordResponseDtoに変換するロジックを提供する
 */
@Component
public class WordRecordConverter {
    public WordRecordResponseDto toWordRecordResponseDto(
        WordRecords record, List<MediaResponseDto> mediaResponseList) {
        return new WordRecordResponseDto()
            .id(record.getId())
            .title(record.getTitle())
            .recordedDate(record.getRecordedDate())
            .comment(record.getComment())
            .media(mediaResponseList)
            .createdAt(record.getCreatedAt()).
            updatedAt(record.getUpdatedAt());
    }
}
