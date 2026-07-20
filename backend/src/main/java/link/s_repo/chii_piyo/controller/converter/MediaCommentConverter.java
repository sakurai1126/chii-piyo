package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.MediaCommentResponseDto;
import link.s_repo.chii_piyo.model.gen.MediaComments;
import link.s_repo.chii_piyo.model.gen.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * MediaCommentエンティティをMediaCommentResponseDtoに変換するコンバーター
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
        MediaComments mediaComment, Users user) {
        return new MediaCommentResponseDto()
            .id(mediaComment.getId())
            .userId(mediaComment.getUserId())
            .displayName(user.getDisplayName())
            .content(mediaComment.getContent())
            .createdAt(mediaComment.getCreatedAt())
            .updatedAt(mediaComment.getUpdatedAt());
    }
}
