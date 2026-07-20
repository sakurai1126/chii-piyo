package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 各育児記録エンティティリストをCareRecordListResponseDtoに変換するコンバーター
 */
@Component
public class CareRecordListConverter {
    /**
     * 各育児記録エンティティリストをCareRecordListResponseDtoに変換する
     *
     * @param careRecords   育児記録のリスト
     * @param mealRecords   食事記録のリスト
     * @param milkRecords   ミルク記録のリスト
     * @param diaperRecords 排泄記録のリスト
     * @param healthRecords 体調記録のリスト
     * @return CareRecordListResponseDto
     */
    public CareRecordListResponseDto toCareRecordListResponseDto(
        List<CareRecords> careRecords,
        List<MealDetails> mealRecords,
        List<MilkDetails> milkRecords,
        List<DiaperDetails> diaperRecords,
        List<HealthDetails> healthRecords) {

        List<CareRecordResponseDto> recordsDto = careRecords.stream().map(
            careRecord -> {
                CareRecordResponseDto dto = new CareRecordResponseDto()
                    .id(careRecord.getId())
                    .recordedBy(careRecord.getRecordedBy())
                    .recordType(CareRecordResponseDto.RecordTypeEnum.valueOf(careRecord.getRecordType()))
                    .recordedAt(careRecord.getRecordedAt())
                    .createdAt(careRecord.getCreatedAt())
                    .updatedAt(careRecord.getUpdatedAt());

                // 食事記録のセット
                mealRecords.stream()
                    .filter(mealRecord -> mealRecord.getCareRecordId().equals(careRecord.getId()))
                    .findFirst()
                    .map(mealRecord -> new MealDetailDto().note(mealRecord.getNote()))
                    .ifPresent(mealDto -> dto.setMealDetail(JsonNullable.of(mealDto)));

                // ミルク記録のセット
                milkRecords.stream()
                    .filter(milkRecord -> milkRecord.getCareRecordId().equals(careRecord.getId()))
                    .findFirst()
                    .map(milkRecord -> new MilkDetailDto()
                        .amountMl(milkRecord.getAmountMl())
                        .note(milkRecord.getNote()))
                    .ifPresent(milkDto -> dto.setMilkDetail(JsonNullable.of(milkDto)));

                // 排泄記録のセット
                diaperRecords.stream()
                    .filter(diaperRecord -> diaperRecord.getCareRecordId().equals(careRecord.getId()))
                    .findFirst()
                    .map(diaperRecord -> new DiaperDetailDto()
                        .diaperType(DiaperDetailDto.DiaperTypeEnum.valueOf(diaperRecord.getDiaperType()))
                        .note(diaperRecord.getNote()))
                    .ifPresent(diaperDto -> dto.setDiaperDetail(JsonNullable.of(diaperDto)));

                // 体調記録のセット
                healthRecords.stream()
                    .filter(healthRecord -> healthRecord.getCareRecordId().equals(careRecord.getId()))
                    .findFirst()
                    .map(healthRecord -> new HealthDetailDto()
                        .temperature(healthRecord.getTemperature() != null ? healthRecord.getTemperature().doubleValue() : null)
                        .note(healthRecord.getNote()))
                    .ifPresent(healthDto -> dto.setHealthDetail(JsonNullable.of(healthDto)));

                return dto;
            }).toList();

        return new CareRecordListResponseDto(recordsDto);
    }
}
