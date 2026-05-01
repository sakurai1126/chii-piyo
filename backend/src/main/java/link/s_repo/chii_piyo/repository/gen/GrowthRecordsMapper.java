package link.s_repo.chii_piyo.repository.gen;

import static link.s_repo.chii_piyo.repository.gen.GrowthRecordsDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import link.s_repo.chii_piyo.model.gen.GrowthRecords;
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
public interface GrowthRecordsMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, measurementDate, height, weight, note, createdAt, updatedAt);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true, keyProperty="row.id", keyColumn="id")
    int insert(InsertStatementProvider<GrowthRecords> insertStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true, keyProperty="records.id", keyColumn="id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<GrowthRecords> records);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="GrowthRecordsResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="measurement_date", property="measurementDate", jdbcType=JdbcType.DATE),
        @Result(column="height", property="height", jdbcType=JdbcType.NUMERIC),
        @Result(column="weight", property="weight", jdbcType=JdbcType.NUMERIC),
        @Result(column="note", property="note", jdbcType=JdbcType.VARCHAR),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="updated_at", property="updatedAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<GrowthRecords> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("GrowthRecordsResult")
    Optional<GrowthRecords> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, growthRecords, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, growthRecords, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(GrowthRecords row) {
        return MyBatis3Utils.insert(this::insert, row, growthRecords, c ->
            c.withMappedColumn(measurementDate)
            .withMappedColumn(height)
            .withMappedColumn(weight)
            .withMappedColumn(note)
            .withMappedColumn(createdAt)
            .withMappedColumn(updatedAt)
        );
    }

    default int insertMultiple(Collection<GrowthRecords> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, growthRecords, c ->
            c.withMappedColumn(measurementDate)
            .withMappedColumn(height)
            .withMappedColumn(weight)
            .withMappedColumn(note)
            .withMappedColumn(createdAt)
            .withMappedColumn(updatedAt)
        );
    }

    default int insertSelective(GrowthRecords row) {
        return MyBatis3Utils.insert(this::insert, row, growthRecords, c ->
            c.withMappedColumnWhenPresent(measurementDate, row::getMeasurementDate)
            .withMappedColumnWhenPresent(height, row::getHeight)
            .withMappedColumnWhenPresent(weight, row::getWeight)
            .withMappedColumnWhenPresent(note, row::getNote)
            .withMappedColumnWhenPresent(createdAt, row::getCreatedAt)
            .withMappedColumnWhenPresent(updatedAt, row::getUpdatedAt)
        );
    }

    default Optional<GrowthRecords> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, growthRecords, completer);
    }

    default List<GrowthRecords> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, growthRecords, completer);
    }

    default List<GrowthRecords> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, growthRecords, completer);
    }

    default Optional<GrowthRecords> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, growthRecords, completer);
    }

    static UpdateDSL updateAllColumns(GrowthRecords row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(measurementDate).equalTo(row::getMeasurementDate)
                .set(height).equalTo(row::getHeight)
                .set(weight).equalTo(row::getWeight)
                .set(note).equalTo(row::getNote)
                .set(createdAt).equalTo(row::getCreatedAt)
                .set(updatedAt).equalTo(row::getUpdatedAt);
    }

    static UpdateDSL updateSelectiveColumns(GrowthRecords row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(measurementDate).equalToWhenPresent(row::getMeasurementDate)
                .set(height).equalToWhenPresent(row::getHeight)
                .set(weight).equalToWhenPresent(row::getWeight)
                .set(note).equalToWhenPresent(row::getNote)
                .set(createdAt).equalToWhenPresent(row::getCreatedAt)
                .set(updatedAt).equalToWhenPresent(row::getUpdatedAt);
    }

    default int updateByPrimaryKey(GrowthRecords row) {
        return update(c ->
            c.set(measurementDate).equalTo(row::getMeasurementDate)
            .set(height).equalTo(row::getHeight)
            .set(weight).equalTo(row::getWeight)
            .set(note).equalTo(row::getNote)
            .set(createdAt).equalTo(row::getCreatedAt)
            .set(updatedAt).equalTo(row::getUpdatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(GrowthRecords row) {
        return update(c ->
            c.set(measurementDate).equalToWhenPresent(row::getMeasurementDate)
            .set(height).equalToWhenPresent(row::getHeight)
            .set(weight).equalToWhenPresent(row::getWeight)
            .set(note).equalToWhenPresent(row::getNote)
            .set(createdAt).equalToWhenPresent(row::getCreatedAt)
            .set(updatedAt).equalToWhenPresent(row::getUpdatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}