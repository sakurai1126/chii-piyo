package link.s_repo.chii_piyo.repository.gen;

import static link.s_repo.chii_piyo.repository.gen.MilkDetailsDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import link.s_repo.chii_piyo.model.gen.MilkDetails;
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
public interface MilkDetailsMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, careRecordId, amountMl, note, createdAt, updatedAt);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true, keyProperty="row.id", keyColumn="id")
    int insert(InsertStatementProvider<MilkDetails> insertStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true, keyProperty="records.id", keyColumn="id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<MilkDetails> records);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="MilkDetailsResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="care_record_id", property="careRecordId", jdbcType=JdbcType.BIGINT),
        @Result(column="amount_ml", property="amountMl", jdbcType=JdbcType.INTEGER),
        @Result(column="note", property="note", jdbcType=JdbcType.VARCHAR),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="updated_at", property="updatedAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<MilkDetails> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("MilkDetailsResult")
    Optional<MilkDetails> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, milkDetails, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, milkDetails, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(MilkDetails row) {
        return MyBatis3Utils.insert(this::insert, row, milkDetails, c ->
            c.withMappedColumn(careRecordId)
            .withMappedColumn(amountMl)
            .withMappedColumn(note)
            .withMappedColumn(createdAt)
            .withMappedColumn(updatedAt)
        );
    }

    default int insertMultiple(Collection<MilkDetails> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, milkDetails, c ->
            c.withMappedColumn(careRecordId)
            .withMappedColumn(amountMl)
            .withMappedColumn(note)
            .withMappedColumn(createdAt)
            .withMappedColumn(updatedAt)
        );
    }

    default int insertSelective(MilkDetails row) {
        return MyBatis3Utils.insert(this::insert, row, milkDetails, c ->
            c.withMappedColumnWhenPresent(careRecordId, row::getCareRecordId)
            .withMappedColumnWhenPresent(amountMl, row::getAmountMl)
            .withMappedColumnWhenPresent(note, row::getNote)
            .withMappedColumnWhenPresent(createdAt, row::getCreatedAt)
            .withMappedColumnWhenPresent(updatedAt, row::getUpdatedAt)
        );
    }

    default Optional<MilkDetails> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, milkDetails, completer);
    }

    default List<MilkDetails> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, milkDetails, completer);
    }

    default List<MilkDetails> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, milkDetails, completer);
    }

    default Optional<MilkDetails> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, milkDetails, completer);
    }

    static UpdateDSL updateAllColumns(MilkDetails row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(careRecordId).equalTo(row::getCareRecordId)
                .set(amountMl).equalTo(row::getAmountMl)
                .set(note).equalTo(row::getNote)
                .set(createdAt).equalTo(row::getCreatedAt)
                .set(updatedAt).equalTo(row::getUpdatedAt);
    }

    static UpdateDSL updateSelectiveColumns(MilkDetails row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(careRecordId).equalToWhenPresent(row::getCareRecordId)
                .set(amountMl).equalToWhenPresent(row::getAmountMl)
                .set(note).equalToWhenPresent(row::getNote)
                .set(createdAt).equalToWhenPresent(row::getCreatedAt)
                .set(updatedAt).equalToWhenPresent(row::getUpdatedAt);
    }

    default int updateByPrimaryKey(MilkDetails row) {
        return update(c ->
            c.set(careRecordId).equalTo(row::getCareRecordId)
            .set(amountMl).equalTo(row::getAmountMl)
            .set(note).equalTo(row::getNote)
            .set(createdAt).equalTo(row::getCreatedAt)
            .set(updatedAt).equalTo(row::getUpdatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(MilkDetails row) {
        return update(c ->
            c.set(careRecordId).equalToWhenPresent(row::getCareRecordId)
            .set(amountMl).equalToWhenPresent(row::getAmountMl)
            .set(note).equalToWhenPresent(row::getNote)
            .set(createdAt).equalToWhenPresent(row::getCreatedAt)
            .set(updatedAt).equalToWhenPresent(row::getUpdatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}