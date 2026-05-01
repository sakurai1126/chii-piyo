package link.s_repo.chii_piyo.repository.gen;

import static link.s_repo.chii_piyo.repository.gen.FavoritesDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import link.s_repo.chii_piyo.model.gen.Favorites;
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
public interface FavoritesMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, userId, mediaId, createdAt);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true, keyProperty="row.id", keyColumn="id")
    int insert(InsertStatementProvider<Favorites> insertStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true, keyProperty="records.id", keyColumn="id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<Favorites> records);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="FavoritesResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.BIGINT),
        @Result(column="media_id", property="mediaId", jdbcType=JdbcType.BIGINT),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<Favorites> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("FavoritesResult")
    Optional<Favorites> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, favorites, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, favorites, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(Favorites row) {
        return MyBatis3Utils.insert(this::insert, row, favorites, c ->
            c.withMappedColumn(userId)
            .withMappedColumn(mediaId)
            .withMappedColumn(createdAt)
        );
    }

    default int insertMultiple(Collection<Favorites> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, favorites, c ->
            c.withMappedColumn(userId)
            .withMappedColumn(mediaId)
            .withMappedColumn(createdAt)
        );
    }

    default int insertSelective(Favorites row) {
        return MyBatis3Utils.insert(this::insert, row, favorites, c ->
            c.withMappedColumnWhenPresent(userId, row::getUserId)
            .withMappedColumnWhenPresent(mediaId, row::getMediaId)
            .withMappedColumnWhenPresent(createdAt, row::getCreatedAt)
        );
    }

    default Optional<Favorites> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, favorites, completer);
    }

    default List<Favorites> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, favorites, completer);
    }

    default List<Favorites> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, favorites, completer);
    }

    default Optional<Favorites> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, favorites, completer);
    }

    static UpdateDSL updateAllColumns(Favorites row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(userId).equalTo(row::getUserId)
                .set(mediaId).equalTo(row::getMediaId)
                .set(createdAt).equalTo(row::getCreatedAt);
    }

    static UpdateDSL updateSelectiveColumns(Favorites row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(userId).equalToWhenPresent(row::getUserId)
                .set(mediaId).equalToWhenPresent(row::getMediaId)
                .set(createdAt).equalToWhenPresent(row::getCreatedAt);
    }

    default int updateByPrimaryKey(Favorites row) {
        return update(c ->
            c.set(userId).equalTo(row::getUserId)
            .set(mediaId).equalTo(row::getMediaId)
            .set(createdAt).equalTo(row::getCreatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(Favorites row) {
        return update(c ->
            c.set(userId).equalToWhenPresent(row::getUserId)
            .set(mediaId).equalToWhenPresent(row::getMediaId)
            .set(createdAt).equalToWhenPresent(row::getCreatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}