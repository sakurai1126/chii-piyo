package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.Tags;
import link.s_repo.chii_piyo.repository.gen.TagsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static link.s_repo.chii_piyo.repository.gen.TagsDynamicSqlSupport.id;

/**
 * タグ管理サービス<br>
 * タグの取得・作成およびメディアとのタグ紐付けを担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagsMapper tagsMapper;

    /**
     * タグを新規作成する<br>
     *
     * @param name 追加するタグ名
     * @return 作成されたタグエンティティ
     */
    @Transactional
    public Tags createTag(String name) {
        Tags tags = new Tags();

        // タグエンティティに値をセット
        tags.setName(name);
        tags.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // タグをDBに保存
        tagsMapper.insert(tags);
        return tags;
    }

    /**
     * タグ一覧を取得する<br>
     * 全件をID昇順で返す
     *
     * @return タグエンティティの一覧
     */
    @Transactional(readOnly = true)
    public List<Tags> findAll() {
        return tagsMapper.select(c -> c.orderBy(id));
    }
}
