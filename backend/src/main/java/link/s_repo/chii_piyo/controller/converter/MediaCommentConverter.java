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
            mediaComment.getId(), // ID
            mediaComment.getUserId(), // コメントしたユーザーID
            user.getDisplayName(), // コメントしたユーザーの表示名
            mediaComment.getContent(), // コメント本文
            mediaComment.getCreatedAt(), // 作成日時
            mediaComment.getUpdatedAt() // 更新日時
        );
    }
}
