package link.s_repo.chii_piyo.repository.gen;

import static link.s_repo.chii_piyo.repository.gen.WordRecordsDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import link.s_repo.chii_piyo.model.gen.WordRecords;
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
public interface WordRecordsMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, word, recordedDate, comment, createdAt, updatedAt);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true, keyProperty="row.id", keyColumn="id")
    int insert(InsertStatementProvider<WordRecords> insertStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true, keyProperty="records.id", keyColumn="id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<WordRecords> records);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="WordRecordsResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="word", property="word", jdbcType=JdbcType.VARCHAR),
        @Result(column="recorded_date", property="recordedDate", jdbcType=JdbcType.DATE),
        @Result(column="comment", property="comment", jdbcType=JdbcType.VARCHAR),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="updated_at", property="updatedAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<WordRecords> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("WordRecordsResult")
    Optional<WordRecords> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, wordRecords, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, wordRecords, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(WordRecords row) {
        return MyBatis3Utils.insert(this::insert, row, wordRecords, c ->
            c.withMappedColumn(word)
            .withMappedColumn(recordedDate)
            .withMappedColumn(comment)
            .withMappedColumn(createdAt)
            .withMappedColumn(updatedAt)
        );
    }

    default int insertMultiple(Collection<WordRecords> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, wordRecords, c ->
            c.withMappedColumn(word)
            .withMappedColumn(recordedDate)
            .withMappedColumn(comment)
            .withMappedColumn(createdAt)
            .withMappedColumn(updatedAt)
        );
    }

    default int insertSelective(WordRecords row) {
        return MyBatis3Utils.insert(this::insert, row, wordRecords, c ->
            c.withMappedColumnWhenPresent(word, row::getWord)
            .withMappedColumnWhenPresent(recordedDate, row::getRecordedDate)
            .withMappedColumnWhenPresent(comment, row::getComment)
            .withMappedColumnWhenPresent(createdAt, row::getCreatedAt)
            .withMappedColumnWhenPresent(updatedAt, row::getUpdatedAt)
        );
    }

    default Optional<WordRecords> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, wordRecords, completer);
    }

    default List<WordRecords> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, wordRecords, completer);
    }

    default List<WordRecords> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, wordRecords, completer);
    }

    default Optional<WordRecords> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, wordRecords, completer);
    }

    static UpdateDSL updateAllColumns(WordRecords row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(word).equalTo(row::getWord)
                .set(recordedDate).equalTo(row::getRecordedDate)
                .set(comment).equalTo(row::getComment)
                .set(createdAt).equalTo(row::getCreatedAt)
                .set(updatedAt).equalTo(row::getUpdatedAt);
    }

    static UpdateDSL updateSelectiveColumns(WordRecords row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(word).equalToWhenPresent(row::getWord)
                .set(recordedDate).equalToWhenPresent(row::getRecordedDate)
                .set(comment).equalToWhenPresent(row::getComment)
                .set(createdAt).equalToWhenPresent(row::getCreatedAt)
                .set(updatedAt).equalToWhenPresent(row::getUpdatedAt);
    }

    default int updateByPrimaryKey(WordRecords row) {
        return update(c ->
            c.set(word).equalTo(row::getWord)
            .set(recordedDate).equalTo(row::getRecordedDate)
            .set(comment).equalTo(row::getComment)
            .set(createdAt).equalTo(row::getCreatedAt)
            .set(updatedAt).equalTo(row::getUpdatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(WordRecords row) {
        return update(c ->
            c.set(word).equalToWhenPresent(row::getWord)
            .set(recordedDate).equalToWhenPresent(row::getRecordedDate)
            .set(comment).equalToWhenPresent(row::getComment)
            .set(createdAt).equalToWhenPresent(row::getCreatedAt)
            .set(updatedAt).equalToWhenPresent(row::getUpdatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}