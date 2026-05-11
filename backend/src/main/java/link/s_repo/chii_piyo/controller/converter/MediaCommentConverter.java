package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.MediaCommentResponseDto;
import link.s_repo.chii_piyo.model.gen.MediaComments;
import link.s_repo.chii_piyo.model.gen.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * MediaCommentエンティティをMediaCommentResponseDtoに変換するロジックを提供する
 */
@Component
@RequiredArgsConstructor
public class MediaCommentConverter {
    /**
     * MediaCommentエンティティをMediaCommentResponseDtoに変換する
     *
     * @param mediaComment MediaCommentsエンティティ
     * @return MediaCommentResponseDto
     */
    public MediaCommentResponseDto toMediaCommentResponseDto(
        MediaComments mediaComment,
        Users user) {

        // 必須フィールドを揃えてコンストラクタに渡す
        return new MediaCommentResponseDto(
            mediaComment.getId(),
            mediaComment.getUserId(),
            user.getDisplayName(),
            user.getUserIconUrl(),
            mediaComment.getContent(),
            mediaComment.getCreatedAt(),
            mediaComment.getUpdatedAt()
        );
    }
}
