package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.FirstRecordResponseDto;
import link.s_repo.chii_piyo.model.gen.FirstRecords;
import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FirstRecordエンティティをFirstRecordResponseDtoに変換するコンバーター
 */
@Component
public class FirstRecordConverter {
    /**
     * FirstRecordsエンティティをFirstRecordResponseDtoに変換する
     *
     * @param record            FirstRecordsエンティティ
     * @param mediaResponseList 記録に紐づくメディアのレスポンスリスト
     * @return FirstRecordResponseDto
     */
    public FirstRecordResponseDto toFirstRecordResponseDto(
        FirstRecords record, List<MediaResponseDto> mediaResponseList) {
        return new FirstRecordResponseDto()
            .id(record.getId())
            .title(record.getTitle())
            .recordedDate(record.getRecordedDate())
            .comment(record.getComment())
            .media(mediaResponseList)
            .createdAt(record.getCreatedAt()).
            updatedAt(record.getUpdatedAt());
    }
}
