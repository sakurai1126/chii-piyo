package link.s_repo.chii_piyo.model;

import lombok.Getter;
import lombok.Setter;

/**
 * タグIDとそのタグが紐付いているメディア数を保持するモデル<br>
 * タグIDごとのメディア数を取得するクエリの結果をマッピングするために使用する
 */
@Getter
@Setter
public class TagMediaCount {
    private Long tagId;
    private Long mediaCount;
}
