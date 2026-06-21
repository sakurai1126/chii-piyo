package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.TagMediaCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * MediaTagsMapperで提供されない、MediaTagsテーブルに対するカスタムクエリを定義するマッパーインターフェース
 */
@Mapper
public interface MediaTagsCustomMapper {

    // タグIDごとのメディア数を取得するクエリ
    // tagIdとmediaCountのセットを格納したクラスのリストを返す
    @Select("SELECT tag_id AS tagId, COUNT(*) AS mediaCount "
        + "FROM media_tags "
        + "WHERE media_id NOT IN (SELECT media_id FROM trash_items) "
        + "GROUP BY tag_id")
    List<TagMediaCount> selectMediaCountByTagId();
}
