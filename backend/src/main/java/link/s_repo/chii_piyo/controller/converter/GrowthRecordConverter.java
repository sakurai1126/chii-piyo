package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.GrowthRecordResponseDto;
import link.s_repo.chii_piyo.model.gen.GrowthRecords;

import org.springframework.stereotype.Component;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * 成長記録エンティティをGrowthRecordResponseDtoに変換するロジックを提供する
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
        GrowthRecordResponseDto dto = new GrowthRecordResponseDto()
            .id(growthRecord.getId())
            .measurementDate(growthRecord.getMeasurementDate())
            .note(growthRecord.getNote())
            .createdAt(growthRecord.getCreatedAt())
            .updatedAt(growthRecord.getUpdatedAt());

        Double heightDouble = growthRecord.getHeight() != null ? growthRecord.getHeight().doubleValue() : null;
        Double weightDouble = growthRecord.getWeight() != null ? growthRecord.getWeight().doubleValue() : null;
        dto.setHeight(heightDouble);
        dto.setWeight(weightDouble);

        return dto;
    }
}
