package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.Albums;
import link.s_repo.chii_piyo.repository.gen.AlbumsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static link.s_repo.chii_piyo.repository.gen.AlbumsDynamicSqlSupport.id;

/**
 * アルバム管理サービス<br>
 * アルバムの取得・作成およびメディアとのアルバム紐付けを担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumsMapper albumsMapper;

    /**
     * アルバム一覧を取得する<br>
     * 全件をID昇順で返す
     *
     * @return アルバムエンティティの一覧
     */
    @Transactional(readOnly = true)
    public List<Albums> findAll() {
        return albumsMapper.select(c -> c.orderBy(id));
    }
}
