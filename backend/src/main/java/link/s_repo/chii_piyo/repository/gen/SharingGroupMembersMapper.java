package link.s_repo.chii_piyo.repository.gen;

import static link.s_repo.chii_piyo.repository.gen.SharingGroupMembersDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import link.s_repo.chii_piyo.model.gen.SharingGroupMembers;
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
public interface SharingGroupMembersMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, sharingGroupId, userId, createdAt);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true, keyProperty="row.id", keyColumn="id")
    int insert(InsertStatementProvider<SharingGroupMembers> insertStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true, keyProperty="records.id", keyColumn="id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<SharingGroupMembers> records);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="SharingGroupMembersResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="sharing_group_id", property="sharingGroupId", jdbcType=JdbcType.BIGINT),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.BIGINT),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<SharingGroupMembers> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("SharingGroupMembersResult")
    Optional<SharingGroupMembers> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, sharingGroupMembers, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, sharingGroupMembers, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(SharingGroupMembers row) {
        return MyBatis3Utils.insert(this::insert, row, sharingGroupMembers, c ->
            c.withMappedColumn(sharingGroupId)
            .withMappedColumn(userId)
            .withMappedColumn(createdAt)
        );
    }

    default int insertMultiple(Collection<SharingGroupMembers> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, sharingGroupMembers, c ->
            c.withMappedColumn(sharingGroupId)
            .withMappedColumn(userId)
            .withMappedColumn(createdAt)
        );
    }

    default int insertSelective(SharingGroupMembers row) {
        return MyBatis3Utils.insert(this::insert, row, sharingGroupMembers, c ->
            c.withMappedColumnWhenPresent(sharingGroupId, row::getSharingGroupId)
            .withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(createdAt, row::getCreatedAt)
        );
    }

    default Optional<SharingGroupMembers> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, sharingGroupMembers, completer);
    }

    default List<SharingGroupMembers> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, sharingGroupMembers, completer);
    }

    default List<SharingGroupMembers> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, sharingGroupMembers, completer);
    }

    default Optional<SharingGroupMembers> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, sharingGroupMembers, completer);
    }

    static UpdateDSL updateAllColumns(SharingGroupMembers row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(sharingGroupId).equalTo(row::getSharingGroupId)
                .set(userId).equalTo(row::getUserId)
                .set(createdAt).equalTo(row::getCreatedAt);
    }

    static UpdateDSL updateSelectiveColumns(SharingGroupMembers row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(sharingGroupId).equalToWhenPresent(row::getSharingGroupId)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(createdAt).equalToWhenPresent(row::getCreatedAt);
    }

    default int updateByPrimaryKey(SharingGroupMembers row) {
        return update(c ->
            c.set(sharingGroupId).equalTo(row::getSharingGroupId)
            .set(userId).equalTo(row::getUserId)
            .set(createdAt).equalTo(row::getCreatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(SharingGroupMembers row) {
        return update(c ->
            c.set(sharingGroupId).equalToWhenPresent(row::getSharingGroupId)
            .set(userId).equalToWhenPresent(row::getUserId)
            .set(createdAt).equalToWhenPresent(row::getCreatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}