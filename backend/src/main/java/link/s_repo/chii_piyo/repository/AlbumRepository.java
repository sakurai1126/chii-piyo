package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.Albums;
import link.s_repo.chii_piyo.repository.gen.AlbumsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static link.s_repo.chii_piyo.repository.gen.AlbumsDynamicSqlSupport.id;

/**
 * アルバム関連リポジトリ<br>
 * アルバムに関するDB操作を提供
 */
@Repository
@RequiredArgsConstructor
public class AlbumRepository {
    private final AlbumsMapper albumsMapper;

    /**
     * アルバムをDBに保存
     *
     * @param album アルバムエンティティ
     */
    public void save(Albums album) {
        album.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        album.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        albumsMapper.insertSelective(album);
    }

    /**
     * アルバム一覧を取得する
     *
     * @return アルバムエンティティのリスト
     */
    public List<Albums> findAll() {
        return albumsMapper.select(c -> c.orderBy(id.descending()));
    }

    /**
     * アルバムをID指定で1件取得する
     *
     * @param id 対象アルバムのID
     * @return アルバムデータ
     */
    public Optional<Albums> findById(Long id) {
        return albumsMapper.selectByPrimaryKey(id);
    }

    /**
     * ID指定でアルバムを削除する
     *
     * @param id 削除対象のID
     */
    public void deleteById(Long id) {
        albumsMapper.deleteByPrimaryKey(id);
    }

    /**
     * アルバムを更新する
     *
     * @param album アルバムエンティティ
     */
    public void update(Albums album) {
        album.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        albumsMapper.updateByPrimaryKeySelective(album);
    }
}
