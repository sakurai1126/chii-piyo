package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.SharingGroupResponseDto;
import link.s_repo.chii_piyo.model.gen.SharingGroups;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * SharingGroupsエンティティをSharingGroupResponseDtoに変換するロジックを提供する
 */
@Component
public class SharingGroupConverter {
    /**
     * SharingGroupsエンティティをSharingGroupResponseDtoに変換する
     *
     * @param sharingGroup SharingGroupsエンティティ
     * @return SharingGroupResponseDto
     */
    public SharingGroupResponseDto toSharingGroupResponseDto(SharingGroups sharingGroup) {
        return new SharingGroupResponseDto(
            sharingGroup.getId(), // ID
            sharingGroup.getName(), // 共有グループ名
            Collections.emptyList(), // メンバーのリスト
            sharingGroup.getCreatedAt(), // 作成日時
            sharingGroup.getUpdatedAt() // 更新日時
        );
    }
}
