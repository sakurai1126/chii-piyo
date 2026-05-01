package link.s_repo.chii_piyo.repository.gen;

import static link.s_repo.chii_piyo.repository.gen.WordRecordMediaDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import link.s_repo.chii_piyo.model.gen.WordRecordMedia;
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
public interface WordRecordMediaMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, wordRecordId, mediaId, createdAt);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true, keyProperty="row.id", keyColumn="id")
    int insert(InsertStatementProvider<WordRecordMedia> insertStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true, keyProperty="records.id", keyColumn="id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<WordRecordMedia> records);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="WordRecordMediaResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="word_record_id", property="wordRecordId", jdbcType=JdbcType.BIGINT),
        @Result(column="media_id", property="mediaId", jdbcType=JdbcType.BIGINT),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<WordRecordMedia> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("WordRecordMediaResult")
    Optional<WordRecordMedia> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, wordRecordMedia, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, wordRecordMedia, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(WordRecordMedia row) {
        return MyBatis3Utils.insert(this::insert, row, wordRecordMedia, c ->
            c.withMappedColumn(wordRecordId)
            .withMappedColumn(mediaId)
            .withMappedColumn(createdAt)
        );
    }

    default int insertMultiple(Collection<WordRecordMedia> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, wordRecordMedia, c ->
            c.withMappedColumn(wordRecordId)
            .withMappedColumn(mediaId)
            .withMappedColumn(createdAt)
        );
    }

    default int insertSelective(WordRecordMedia row) {
        return MyBatis3Utils.insert(this::insert, row, wordRecordMedia, c ->
            c.withMappedColumnWhenPresent(wordRecordId, row::getWordRecordId)
            .withMappedColumnWhenPresent(mediaId, row::getMediaId)
            .withMappedColumnWhenPresent(createdAt, row::getCreatedAt)
        );
    }

    default Optional<WordRecordMedia> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, wordRecordMedia, completer);
    }

    default List<WordRecordMedia> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, wordRecordMedia, completer);
    }

    default List<WordRecordMedia> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, wordRecordMedia, completer);
    }

    default Optional<WordRecordMedia> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, wordRecordMedia, completer);
    }

    static UpdateDSL updateAllColumns(WordRecordMedia row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(wordRecordId).equalTo(row::getWordRecordId)
                .set(mediaId).equalTo(row::getMediaId)
                .set(createdAt).equalTo(row::getCreatedAt);
    }

    static UpdateDSL updateSelectiveColumns(WordRecordMedia row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(wordRecordId).equalToWhenPresent(row::getWordRecordId)
                .set(mediaId).equalToWhenPresent(row::getMediaId)
                .set(createdAt).equalToWhenPresent(row::getCreatedAt);
    }

    default int updateByPrimaryKey(WordRecordMedia row) {
        return update(c ->
            c.set(wordRecordId).equalTo(row::getWordRecordId)
            .set(mediaId).equalTo(row::getMediaId)
            .set(createdAt).equalTo(row::getCreatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(WordRecordMedia row) {
        return update(c ->
            c.set(wordRecordId).equalToWhenPresent(row::getWordRecordId)
            .set(mediaId).equalToWhenPresent(row::getMediaId)
            .set(createdAt).equalToWhenPresent(row::getCreatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}