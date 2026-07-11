package link.s_repo.chii_piyo.model;

import java.time.LocalDate;
import java.util.List;

/**
 * メディアの検索条件をまとめるレコードクラス
 *
 * @param offset         取得位置
 * @param limit          最大件数
 * @param mediaType      メディアの種類(画像/動画)
 * @param albumId        アルバムID
 * @param excludeAlbumId 除外するアルバムID
 * @param tagId          タグID
 * @param sharingGroupId 共有グループID
 * @param startDate      日時指定開始日
 * @param endDate        日時指定開始日
 * @param isFavorite     お気に入りのみかどうかのフラグ
 * @param currentUserId  現在のユーザーID
 */
public record MediaSearchCriteria(
    Integer offset,
    Integer limit,
    String mediaType,
    Long albumId,
    Long excludeAlbumId,
    List<Long> tagId,
    Long sharingGroupId,
    LocalDate startDate,
    LocalDate endDate,
    Boolean isFavorite,
    Long currentUserId
) {
}
