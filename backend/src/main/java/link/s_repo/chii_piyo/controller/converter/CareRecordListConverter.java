package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * 各育児記録エンティティをCareRecordListResponseDtoに変換するロジックを提供する
 */
@Component
public class CareRecordListConverter {
    public CareRecordListResponseDto toCareRecordListResponseDto(
        List<CareRecords> careRecords,
        List<MealDetails> mealRecords,
        List<MilkDetails> milkRecords,
        List<DiaperDetails> diaperRecords,
        List<HealthDetails> healthRecords) {

        List<CareRecordResponseDto> recordsDto = careRecords.stream().map(
            c -> {
                CareRecordResponseDto dto = new CareRecordResponseDto(
                    c.getId(),
                    c.getRecordedBy(),
                    CareRecordResponseDto.RecordTypeEnum.valueOf(c.getRecordType()),
                    c.getRecordedAt(),
                    c.getCreatedAt(),
                    c.getUpdatedAt()
                );

                // 食事記録のセット
                mealRecords.stream()
                    .filter(m -> m.getCareRecordId().equals(c.getId()))
                    .findFirst()
                    .map(m -> new MealDetailDto().note(m.getNote()))
                    .ifPresent(mealDto -> dto.setMealDetail(JsonNullable.of(mealDto)));

                // ミルク記録のセット
                milkRecords.stream()
                    .filter(m -> m.getCareRecordId().equals(c.getId()))
                    .findFirst()
                    .map(m -> new MilkDetailDto()
                        .amountMl(m.getAmountMl())
                        .note(m.getNote()))
                    .ifPresent(milkDto -> dto.setMilkDetail(JsonNullable.of(milkDto)));

                // 排泄記録のセット
                diaperRecords.stream()
                    .filter(m -> m.getCareRecordId().equals(c.getId()))
                    .findFirst()
                    .map(m -> new DiaperDetailDto()
                        .diaperType(DiaperDetailDto.DiaperTypeEnum.valueOf(m.getDiaperType()))
                        .note(m.getNote()))
                    .ifPresent(diaperDto -> dto.setDiaperDetail(JsonNullable.of(diaperDto)));

                // 体調記録のセット
                healthRecords.stream()
                    .filter(m -> m.getCareRecordId().equals(c.getId()))
                    .findFirst()
                    .map(m -> new HealthDetailDto()
                        .temperature(m.getTemperature() != null ? m.getTemperature().doubleValue() : null)
                        .note(m.getNote()))
                    .ifPresent(healthDto -> dto.setHealthDetail(JsonNullable.of(healthDto)));

                return dto;
            }).toList();

        return new CareRecordListResponseDto(recordsDto);
    }
}
