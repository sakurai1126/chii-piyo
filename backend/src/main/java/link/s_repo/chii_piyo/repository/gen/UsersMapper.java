package link.s_repo.chii_piyo.repository.gen;

import static link.s_repo.chii_piyo.repository.gen.UsersDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import link.s_repo.chii_piyo.model.gen.Users;
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
public interface UsersMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, cognitoUserId, displayName, email, userIconKey, isDarkMode, isEasyMode, role, createdAt, updatedAt);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true, keyProperty="row.id", keyColumn="id")
    int insert(InsertStatementProvider<Users> insertStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true, keyProperty="records.id", keyColumn="id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<Users> records);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="UsersResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="cognito_user_id", property="cognitoUserId", jdbcType=JdbcType.VARCHAR),
        @Result(column="display_name", property="displayName", jdbcType=JdbcType.VARCHAR),
        @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
        @Result(column="user_icon_key", property="userIconKey", jdbcType=JdbcType.VARCHAR),
        @Result(column="is_dark_mode", property="isDarkMode", jdbcType=JdbcType.BIT),
        @Result(column="is_easy_mode", property="isEasyMode", jdbcType=JdbcType.BIT),
        @Result(column="role", property="role", jdbcType=JdbcType.VARCHAR),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="updated_at", property="updatedAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<Users> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("UsersResult")
    Optional<Users> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, users, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, users, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(Users row) {
        return MyBatis3Utils.insert(this::insert, row, users, c ->
            c.withMappedColumn(cognitoUserId)
            .withMappedColumn(displayName)
            .withMappedColumn(email)
            .withMappedColumn(userIconKey)
            .withMappedColumn(isDarkMode)
            .withMappedColumn(isEasyMode)
            .withMappedColumn(role)
            .withMappedColumn(createdAt)
            .withMappedColumn(updatedAt)
        );
    }

    default int insertMultiple(Collection<Users> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, users, c ->
            c.withMappedColumn(cognitoUserId)
            .withMappedColumn(displayName)
            .withMappedColumn(email)
            .withMappedColumn(userIconKey)
            .withMappedColumn(isDarkMode)
            .withMappedColumn(isEasyMode)
            .withMappedColumn(role)
            .withMappedColumn(createdAt)
            .withMappedColumn(updatedAt)
        );
    }

    default int insertSelective(Users row) {
        return MyBatis3Utils.insert(this::insert, row, users, c ->
            c.withMappedColumnWhenPresent(cognitoUserId, row::getCognitoUserId)
            .withMappedColumnWhenPresent(displayName, row::getDisplayName)
            .withMappedColumnWhenPresent(email, row::getEmail)
            .withMappedColumnWhenPresent(userIconKey, row::getUserIconKey)
            .withMappedColumnWhenPresent(isDarkMode, row::getIsDarkMode)
            .withMappedColumnWhenPresent(isEasyMode, row::getIsEasyMode)
            .withMappedColumnWhenPresent(role, row::getRole)
            .withMappedColumnWhenPresent(createdAt, row::getCreatedAt)
            .withMappedColumnWhenPresent(updatedAt, row::getUpdatedAt)
        );
    }

    default Optional<Users> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, users, completer);
    }

    default List<Users> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, users, completer);
    }

    default List<Users> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, users, completer);
    }

    default Optional<Users> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, users, completer);
    }

    static UpdateDSL updateAllColumns(Users row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(cognitoUserId).equalTo(row::getCognitoUserId)
                .set(displayName).equalTo(row::getDisplayName)
                .set(email).equalTo(row::getEmail)
                .set(userIconKey).equalTo(row::getUserIconKey)
                .set(isDarkMode).equalTo(row::getIsDarkMode)
                .set(isEasyMode).equalTo(row::getIsEasyMode)
                .set(role).equalTo(row::getRole)
                .set(createdAt).equalTo(row::getCreatedAt)
                .set(updatedAt).equalTo(row::getUpdatedAt);
    }

    static UpdateDSL updateSelectiveColumns(Users row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(cognitoUserId).equalToWhenPresent(row::getCognitoUserId)
                .set(displayName).equalToWhenPresent(row::getDisplayName)
                .set(email).equalToWhenPresent(row::getEmail)
                .set(userIconKey).equalToWhenPresent(row::getUserIconKey)
                .set(isDarkMode).equalToWhenPresent(row::getIsDarkMode)
                .set(isEasyMode).equalToWhenPresent(row::getIsEasyMode)
                .set(role).equalToWhenPresent(row::getRole)
                .set(createdAt).equalToWhenPresent(row::getCreatedAt)
                .set(updatedAt).equalToWhenPresent(row::getUpdatedAt);
    }

    default int updateByPrimaryKey(Users row) {
        return update(c ->
            c.set(cognitoUserId).equalTo(row::getCognitoUserId)
            .set(displayName).equalTo(row::getDisplayName)
            .set(email).equalTo(row::getEmail)
            .set(userIconKey).equalTo(row::getUserIconKey)
            .set(isDarkMode).equalTo(row::getIsDarkMode)
            .set(isEasyMode).equalTo(row::getIsEasyMode)
            .set(role).equalTo(row::getRole)
            .set(createdAt).equalTo(row::getCreatedAt)
            .set(updatedAt).equalTo(row::getUpdatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(Users row) {
        return update(c ->
            c.set(cognitoUserId).equalToWhenPresent(row::getCognitoUserId)
            .set(displayName).equalToWhenPresent(row::getDisplayName)
            .set(email).equalToWhenPresent(row::getEmail)
            .set(userIconKey).equalToWhenPresent(row::getUserIconKey)
            .set(isDarkMode).equalToWhenPresent(row::getIsDarkMode)
            .set(isEasyMode).equalToWhenPresent(row::getIsEasyMode)
            .set(role).equalToWhenPresent(row::getRole)
            .set(createdAt).equalToWhenPresent(row::getCreatedAt)
            .set(updatedAt).equalToWhenPresent(row::getUpdatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}