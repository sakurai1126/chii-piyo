package link.s_repo.chii_piyo.repository.gen;

import static link.s_repo.chii_piyo.repository.gen.TrashItemsDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import link.s_repo.chii_piyo.model.gen.TrashItems;
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
public interface TrashItemsMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, mediaId, deletedBy, expiresAt, createdAt);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true, keyProperty="row.id", keyColumn="id")
    int insert(InsertStatementProvider<TrashItems> insertStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true, keyProperty="records.id", keyColumn="id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<TrashItems> records);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TrashItemsResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="media_id", property="mediaId", jdbcType=JdbcType.BIGINT),
        @Result(column="deleted_by", property="deletedBy", jdbcType=JdbcType.BIGINT),
        @Result(column="expires_at", property="expiresAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<TrashItems> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TrashItemsResult")
    Optional<TrashItems> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, trashItems, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, trashItems, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(TrashItems row) {
        return MyBatis3Utils.insert(this::insert, row, trashItems, c ->
            c.withMappedColumn(mediaId)
            .withMappedColumn(deletedBy)
            .withMappedColumn(expiresAt)
            .withMappedColumn(createdAt)
        );
    }

    default int insertMultiple(Collection<TrashItems> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, trashItems, c ->
            c.withMappedColumn(mediaId)
            .withMappedColumn(deletedBy)
            .withMappedColumn(expiresAt)
            .withMappedColumn(createdAt)
        );
    }

    default int insertSelective(TrashItems row) {
        return MyBatis3Utils.insert(this::insert, row, trashItems, c ->
            c.withMappedColumnWhenPresent(mediaId, row::getMediaId)
            .withMappedColumnWhenPresent(deletedBy, row::getDeletedBy)
            .withMappedColumnWhenPresent(expiresAt, row::getExpiresAt)
            .withMappedColumnWhenPresent(createdAt, row::getCreatedAt)
        );
    }

    default Optional<TrashItems> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, trashItems, completer);
    }

    default List<TrashItems> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, trashItems, completer);
    }

    default List<TrashItems> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, trashItems, completer);
    }

    default Optional<TrashItems> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, trashItems, completer);
    }

    static UpdateDSL updateAllColumns(TrashItems row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(mediaId).equalTo(row::getMediaId)
                .set(deletedBy).equalTo(row::getDeletedBy)
                .set(expiresAt).equalTo(row::getExpiresAt)
                .set(createdAt).equalTo(row::getCreatedAt);
    }

    static UpdateDSL updateSelectiveColumns(TrashItems row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(mediaId).equalToWhenPresent(row::getMediaId)
                .set(deletedBy).equalToWhenPresent(row::getDeletedBy)
                .set(expiresAt).equalToWhenPresent(row::getExpiresAt)
                .set(createdAt).equalToWhenPresent(row::getCreatedAt);
    }

    default int updateByPrimaryKey(TrashItems row) {
        return update(c ->
            c.set(mediaId).equalTo(row::getMediaId)
            .set(deletedBy).equalTo(row::getDeletedBy)
            .set(expiresAt).equalTo(row::getExpiresAt)
            .set(createdAt).equalTo(row::getCreatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(TrashItems row) {
        return update(c ->
            c.set(mediaId).equalToWhenPresent(row::getMediaId)
            .set(deletedBy).equalToWhenPresent(row::getDeletedBy)
            .set(expiresAt).equalToWhenPresent(row::getExpiresAt)
            .set(createdAt).equalToWhenPresent(row::getCreatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}