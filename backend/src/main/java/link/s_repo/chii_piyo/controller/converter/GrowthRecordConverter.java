package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.GrowthRecordResponseDto;
import link.s_repo.chii_piyo.model.gen.GrowthRecords;
import org.springframework.stereotype.Component;

/**
 * 成長記録エンティティをGrowthRecordResponseDtoに変換するコンバーター
 */
@Component
public class GrowthRecordConverter {
    /**
     * GrowthRecordsエンティティをGrowthRecordResponseDtoに変換する
     *
     * @param growthRecord GrowthRecordsエンティティ
     * @return GrowthRecordResponseDto
     */
    public GrowthRecordResponseDto toGrowthRecordResponseDto(GrowthRecords growthRecord) {
        return new GrowthRecordResponseDto()
            .id(growthRecord.getId())
            .measurementDate(growthRecord.getMeasurementDate())
            .note(growthRecord.getNote())
            .height(growthRecord.getHeight() != null ? growthRecord.getHeight().doubleValue() : null)
            .weight(growthRecord.getWeight() != null ? growthRecord.getWeight().doubleValue() : null)
            .createdAt(growthRecord.getCreatedAt())
            .updatedAt(growthRecord.getUpdatedAt());
    }
}
