package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.FirstRecordResponseDto;
import link.s_repo.chii_piyo.model.gen.FirstRecords;
import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * FirstRecordエンティティをFirstRecordResponseDtoに変換するロジックを提供する
 */
@Component
public class FirstRecordConverter {
    public FirstRecordResponseDto toFirstRecordResponseDto(FirstRecords record, List<MediaResponseDto> mediaResponseList) {
        return new FirstRecordResponseDto()
            .id(record.getId())
            .title(record.getTitle())
            .achievedDate(record.getAchievedDate())
            .comment(record.getComment())
            .media(mediaResponseList)
            .createdAt(record.getCreatedAt()).
            updatedAt(record.getUpdatedAt());
    }
}
