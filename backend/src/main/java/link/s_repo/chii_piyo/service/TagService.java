package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.Tags;
import link.s_repo.chii_piyo.repository.gen.TagsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
