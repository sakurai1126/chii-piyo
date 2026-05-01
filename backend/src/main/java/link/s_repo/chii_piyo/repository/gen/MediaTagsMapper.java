package link.s_repo.chii_piyo.repository.gen;

import static link.s_repo.chii_piyo.repository.gen.MediaTagsDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import link.s_repo.chii_piyo.model.gen.MediaTags;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.dsl.CountDSLCompleter;
import org.mybatis.dynamic.sql.dsl.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.dsl.SelectDSLCompleter;
import org.mybatis.dynamic.sql.dsl.UpdateDSL;
import org.mybatis.dynamic.sql.dsl.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface MediaTagsMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, mediaId, tagId, createdAt);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true, keyProperty="row.id", keyColumn="id")
    int insert(InsertStatementProvider<MediaTags> insertStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true, keyProperty="records.id", keyColumn="id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<MediaTags> records);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="MediaTagsResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="media_id", property="mediaId", jdbcType=JdbcType.BIGINT),
        @Result(column="tag_id", property="tagId", jdbcType=JdbcType.BIGINT),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<MediaTags> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("MediaTagsResult")
    Optional<MediaTags> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, mediaTags, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, mediaTags, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(MediaTags row) {
        return MyBatis3Utils.insert(this::insert, row, mediaTags, c ->
            c.withMappedColumn(mediaId)
            .withMappedColumn(tagId)
            .withMappedColumn(createdAt)
        );
    }

    default int insertMultiple(Collection<MediaTags> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, mediaTags, c ->
            c.withMappedColumn(mediaId)
            .withMappedColumn(tagId)
            .withMappedColumn(createdAt)
        );
    }

    default int insertSelective(MediaTags row) {
        return MyBatis3Utils.insert(this::insert, row, mediaTags, c ->
            c.withMappedColumnWhenPresent(mediaId, row::getMediaId)
            .withMappedColumnWhenPresent(tagId, row::getTagId)
            .withMappedColumnWhenPresent(createdAt, row::getCreatedAt)
        );
    }

    default Optional<MediaTags> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, mediaTags, completer);
    }

    default List<MediaTags> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, mediaTags, completer);
    }

    default List<MediaTags> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, mediaTags, completer);
    }

    default Optional<MediaTags> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, mediaTags, completer);
    }

    static UpdateDSL updateAllColumns(MediaTags row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(mediaId).equalTo(row::getMediaId)
                .set(tagId).equalTo(row::getTagId)
                .set(createdAt).equalTo(row::getCreatedAt);
    }

    static UpdateDSL updateSelectiveColumns(MediaTags row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(mediaId).equalToWhenPresent(row::getMediaId)
                .set(tagId).equalToWhenPresent(row::getTagId)
                .set(createdAt).equalToWhenPresent(row::getCreatedAt);
    }

    default int updateByPrimaryKey(MediaTags row) {
        return update(c ->
            c.set(mediaId).equalTo(row::getMediaId)
            .set(tagId).equalTo(row::getTagId)
            .set(createdAt).equalTo(row::getCreatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(MediaTags row) {
        return update(c ->
            c.set(mediaId).equalToWhenPresent(row::getMediaId)
            .set(tagId).equalToWhenPresent(row::getTagId)
            .set(createdAt).equalToWhenPresent(row::getCreatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}