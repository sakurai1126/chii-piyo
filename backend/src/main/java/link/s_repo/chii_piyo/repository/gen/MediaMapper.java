package link.s_repo.chii_piyo.repository.gen;

import static link.s_repo.chii_piyo.repository.gen.MediaDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import link.s_repo.chii_piyo.model.gen.Media;
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
public interface MediaMapper extends CommonCountMapper, CommonDeleteMapper, CommonUpdateMapper {
    BasicColumn[] selectList = BasicColumn.columnList(id, uploadedBy, mediaType, originalFilename, contentType, fileSize, width, height, s3Key, thumbnailS3Key, takenAt, albumId, sharingGroupId, uploadStatus, createdAt, updatedAt);

    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true, keyProperty="row.id", keyColumn="id")
    int insert(InsertStatementProvider<Media> insertStatement);

    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultipleWithGeneratedKeys")
    @Options(useGeneratedKeys=true, keyProperty="records.id", keyColumn="id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<Media> records);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="MediaResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="uploaded_by", property="uploadedBy", jdbcType=JdbcType.BIGINT),
        @Result(column="media_type", property="mediaType", jdbcType=JdbcType.VARCHAR),
        @Result(column="original_filename", property="originalFilename", jdbcType=JdbcType.VARCHAR),
        @Result(column="content_type", property="contentType", jdbcType=JdbcType.VARCHAR),
        @Result(column="file_size", property="fileSize", jdbcType=JdbcType.BIGINT),
        @Result(column="width", property="width", jdbcType=JdbcType.INTEGER),
        @Result(column="height", property="height", jdbcType=JdbcType.INTEGER),
        @Result(column="s3_key", property="s3Key", jdbcType=JdbcType.VARCHAR),
        @Result(column="thumbnail_s3_key", property="thumbnailS3Key", jdbcType=JdbcType.VARCHAR),
        @Result(column="taken_at", property="takenAt", jdbcType=JdbcType.DATE),
        @Result(column="album_id", property="albumId", jdbcType=JdbcType.BIGINT),
        @Result(column="sharing_group_id", property="sharingGroupId", jdbcType=JdbcType.BIGINT),
        @Result(column="upload_status", property="uploadStatus", jdbcType=JdbcType.VARCHAR),
        @Result(column="created_at", property="createdAt", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="updated_at", property="updatedAt", jdbcType=JdbcType.TIMESTAMP)
    })
    List<Media> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("MediaResult")
    Optional<Media> selectOne(SelectStatementProvider selectStatement);

    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, media, completer);
    }

    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, media, completer);
    }

    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    default int insert(Media row) {
        return MyBatis3Utils.insert(this::insert, row, media, c ->
            c.withMappedColumn(uploadedBy)
            .withMappedColumn(mediaType)
            .withMappedColumn(originalFilename)
            .withMappedColumn(contentType)
            .withMappedColumn(fileSize)
            .withMappedColumn(width)
            .withMappedColumn(height)
            .withMappedColumn(s3Key)
            .withMappedColumn(thumbnailS3Key)
            .withMappedColumn(takenAt)
            .withMappedColumn(albumId)
            .withMappedColumn(sharingGroupId)
            .withMappedColumn(uploadStatus)
            .withMappedColumn(createdAt)
            .withMappedColumn(updatedAt)
        );
    }

    default int insertMultiple(Collection<Media> records) {
        return MyBatis3Utils.insertMultipleWithGeneratedKeys(this::insertMultiple, records, media, c ->
            c.withMappedColumn(uploadedBy)
            .withMappedColumn(mediaType)
            .withMappedColumn(originalFilename)
            .withMappedColumn(contentType)
            .withMappedColumn(fileSize)
            .withMappedColumn(width)
            .withMappedColumn(height)
            .withMappedColumn(s3Key)
            .withMappedColumn(thumbnailS3Key)
            .withMappedColumn(takenAt)
            .withMappedColumn(albumId)
            .withMappedColumn(sharingGroupId)
            .withMappedColumn(uploadStatus)
            .withMappedColumn(createdAt)
            .withMappedColumn(updatedAt)
        );
    }

    default int insertSelective(Media row) {
        return MyBatis3Utils.insert(this::insert, row, media, c ->
            c.withMappedColumnWhenPresent(uploadedBy, row::getUploadedBy)
            .withMappedColumnWhenPresent(mediaType, row::getMediaType)
            .withMappedColumnWhenPresent(originalFilename, row::getOriginalFilename)
            .withMappedColumnWhenPresent(contentType, row::getContentType)
            .withMappedColumnWhenPresent(fileSize, row::getFileSize)
            .withMappedColumnWhenPresent(width, row::getWidth)
            .withMappedColumnWhenPresent(height, row::getHeight)
            .withMappedColumnWhenPresent(s3Key, row::getS3Key)
            .withMappedColumnWhenPresent(thumbnailS3Key, row::getThumbnailS3Key)
            .withMappedColumnWhenPresent(takenAt, row::getTakenAt)
            .withMappedColumnWhenPresent(albumId, row::getAlbumId)
            .withMappedColumnWhenPresent(sharingGroupId, row::getSharingGroupId)
            .withMappedColumnWhenPresent(uploadStatus, row::getUploadStatus)
            .withMappedColumnWhenPresent(createdAt, row::getCreatedAt)
            .withMappedColumnWhenPresent(updatedAt, row::getUpdatedAt)
        );
    }

    default Optional<Media> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, media, completer);
    }

    default List<Media> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, media, completer);
    }

    default List<Media> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, media, completer);
    }

    default Optional<Media> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, media, completer);
    }

    static UpdateDSL updateAllColumns(Media row, UpdateDSL dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(uploadedBy).equalTo(row::getUploadedBy)
                .set(mediaType).equalTo(row::getMediaType)
                .set(originalFilename).equalTo(row::getOriginalFilename)
                .set(contentType).equalTo(row::getContentType)
                .set(fileSize).equalTo(row::getFileSize)
                .set(width).equalTo(row::getWidth)
                .set(height).equalTo(row::getHeight)
                .set(s3Key).equalTo(row::getS3Key)
                .set(thumbnailS3Key).equalTo(row::getThumbnailS3Key)
                .set(takenAt).equalTo(row::getTakenAt)
                .set(albumId).equalTo(row::getAlbumId)
                .set(sharingGroupId).equalTo(row::getSharingGroupId)
                .set(uploadStatus).equalTo(row::getUploadStatus)
                .set(createdAt).equalTo(row::getCreatedAt)
                .set(updatedAt).equalTo(row::getUpdatedAt);
    }

    static UpdateDSL updateSelectiveColumns(Media row, UpdateDSL dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(uploadedBy).equalToWhenPresent(row::getUploadedBy)
                .set(mediaType).equalToWhenPresent(row::getMediaType)
                .set(originalFilename).equalToWhenPresent(row::getOriginalFilename)
                .set(contentType).equalToWhenPresent(row::getContentType)
                .set(fileSize).equalToWhenPresent(row::getFileSize)
                .set(width).equalToWhenPresent(row::getWidth)
                .set(height).equalToWhenPresent(row::getHeight)
                .set(s3Key).equalToWhenPresent(row::getS3Key)
                .set(thumbnailS3Key).equalToWhenPresent(row::getThumbnailS3Key)
                .set(takenAt).equalToWhenPresent(row::getTakenAt)
                .set(albumId).equalToWhenPresent(row::getAlbumId)
                .set(sharingGroupId).equalToWhenPresent(row::getSharingGroupId)
                .set(uploadStatus).equalToWhenPresent(row::getUploadStatus)
                .set(createdAt).equalToWhenPresent(row::getCreatedAt)
                .set(updatedAt).equalToWhenPresent(row::getUpdatedAt);
    }

    default int updateByPrimaryKey(Media row) {
        return update(c ->
            c.set(uploadedBy).equalTo(row::getUploadedBy)
            .set(mediaType).equalTo(row::getMediaType)
            .set(originalFilename).equalTo(row::getOriginalFilename)
            .set(contentType).equalTo(row::getContentType)
            .set(fileSize).equalTo(row::getFileSize)
            .set(width).equalTo(row::getWidth)
            .set(height).equalTo(row::getHeight)
            .set(s3Key).equalTo(row::getS3Key)
            .set(thumbnailS3Key).equalTo(row::getThumbnailS3Key)
            .set(takenAt).equalTo(row::getTakenAt)
            .set(albumId).equalTo(row::getAlbumId)
            .set(sharingGroupId).equalTo(row::getSharingGroupId)
            .set(uploadStatus).equalTo(row::getUploadStatus)
            .set(createdAt).equalTo(row::getCreatedAt)
            .set(updatedAt).equalTo(row::getUpdatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }

    default int updateByPrimaryKeySelective(Media row) {
        return update(c ->
            c.set(uploadedBy).equalToWhenPresent(row::getUploadedBy)
            .set(mediaType).equalToWhenPresent(row::getMediaType)
            .set(originalFilename).equalToWhenPresent(row::getOriginalFilename)
            .set(contentType).equalToWhenPresent(row::getContentType)
            .set(fileSize).equalToWhenPresent(row::getFileSize)
            .set(width).equalToWhenPresent(row::getWidth)
            .set(height).equalToWhenPresent(row::getHeight)
            .set(s3Key).equalToWhenPresent(row::getS3Key)
            .set(thumbnailS3Key).equalToWhenPresent(row::getThumbnailS3Key)
            .set(takenAt).equalToWhenPresent(row::getTakenAt)
            .set(albumId).equalToWhenPresent(row::getAlbumId)
            .set(sharingGroupId).equalToWhenPresent(row::getSharingGroupId)
            .set(uploadStatus).equalToWhenPresent(row::getUploadStatus)
            .set(createdAt).equalToWhenPresent(row::getCreatedAt)
            .set(updatedAt).equalToWhenPresent(row::getUpdatedAt)
            .where(id, isEqualTo(row::getId))
        );
    }
}