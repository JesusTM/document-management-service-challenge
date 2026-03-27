package com.clara.ops.challenge.document_management_service_challenge.repository;

import com.clara.ops.challenge.document_management_service_challenge.dto.DocumentDTO;
import com.clara.ops.challenge.document_management_service_challenge.model.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

import java.sql.Array;
import java.util.*;

@Repository
public class DocumentRepository {

    private final JdbcTemplate jdbcTemplate;

    public DocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*
     * ===============================
     * SAVE DOCUMENT METADATA
     * ===============================
     */
    public void saveDocument(Document document) {

        String sql = """
                INSERT INTO documents
                (id, user_name, document_name, minio_path, file_size, file_type, created_at)
                VALUES (?, ?, ?, ?, ?, ?, NOW())
                """;

        jdbcTemplate.update(
                sql,
                document.getId(),
                document.getUserName(),
                document.getDocumentName(),
                document.getMinioPath(),
                document.getFileSize(),
                document.getFileType()
        );
    }

    /*
     * ===============================
     * SAVE TAGS
     * ===============================
     */
    public void saveTags(List<String> tags) {

        String sql = """
                INSERT INTO tags(name)
                VALUES (?)
                ON CONFLICT(name) DO NOTHING
                """;

        for (String tag : tags) {
            jdbcTemplate.update(sql, tag);
        }
    }

    /*
     * ===============================
     * LINK TAGS WITH DOCUMENT
     * ===============================
     */
    public void saveDocumentTags(UUID documentId, List<String> tags) {

        String tagIdQuery = "SELECT id FROM tags WHERE name = ?";

        String insert = """
                INSERT INTO document_tags(document_id, tag_id)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """;

        for (String tag : tags) {

            Integer tagId = jdbcTemplate.queryForObject(
                    tagIdQuery,
                    Integer.class,
                    tag
            );

            jdbcTemplate.update(insert, documentId, tagId);
        }
    }

    /*
     * ===============================
     * FIND DOCUMENT PATH (DOWNLOAD)
     * ===============================
     */
    public Optional<String> findMinioPathById(UUID id) {

        String sql = "SELECT minio_path FROM documents WHERE id = ?";

        List<String> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("minio_path"),
                id
        );

        return result.stream().findFirst();
    }

    /*
     * ===============================
     * SEARCH DOCUMENTS WITH FILTERS
     * ===============================
     */
    public List<DocumentDTO> searchDocuments(
            String user,
            String name,
            List<String> tags,
            int offset,
            int limit
    ) {

        StringBuilder sql = new StringBuilder("""
                SELECT
                    d.id,
                    d.user_name,
                    d.document_name,
                    d.file_size,
                    d.file_type,
                    d.created_at,
                    ARRAY_REMOVE(ARRAY_AGG(t.name), NULL) AS tags
                FROM documents d
                LEFT JOIN document_tags dt ON d.id = dt.document_id
                LEFT JOIN tags t ON t.id = dt.tag_id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (user != null && !user.isBlank()) {
            sql.append(" AND d.user_name = ?");
            params.add(user);
        }

        if (name != null && !name.isBlank()) {
            sql.append(" AND d.document_name ILIKE ?");
            params.add("%" + name + "%");
        }

        if (tags != null && !tags.isEmpty()) {

            sql.append("""
                    AND d.id IN (
                        SELECT dt2.document_id
                        FROM document_tags dt2
                        JOIN tags t2 ON dt2.tag_id = t2.id
                        WHERE t2.name = ANY (?)
                    )
                    """);

            params.add(tags.toArray(new String[0]));
        }

        sql.append("""
                GROUP BY d.id
                ORDER BY d.created_at DESC
                LIMIT ?
                OFFSET ?
                """);

        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {

                    Array tagsArray = rs.getArray("tags");

                    List<String> tagList = tagsArray != null
                            ? Arrays.asList((String[]) tagsArray.getArray())
                            : List.of();

                    return new DocumentDTO(
                            rs.getString("id"),
                            rs.getString("user_name"),
                            rs.getString("document_name"),
                            tagList,
                            rs.getLong("file_size"),
                            rs.getString("file_type"),
                            rs.getTimestamp("created_at").toInstant()
                    );
                }
        );
    }

    /*
     * ===============================
     * COUNT DOCUMENTS (PAGINATION)
     * ===============================
     */
    public long countDocuments(
            String user,
            String name,
            List<String> tags
    ) {

        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(DISTINCT d.id)
                FROM documents d
                LEFT JOIN document_tags dt ON d.id = dt.document_id
                LEFT JOIN tags t ON t.id = dt.tag_id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (user != null && !user.isBlank()) {
            sql.append(" AND d.user_name = ?");
            params.add(user);
        }

        if (name != null && !name.isBlank()) {
            sql.append(" AND d.document_name ILIKE ?");
            params.add("%" + name + "%");
        }

        if (tags != null && !tags.isEmpty()) {

            sql.append("""
                    AND d.id IN (
                        SELECT dt2.document_id
                        FROM document_tags dt2
                        JOIN tags t2 ON dt2.tag_id = t2.id
                        WHERE t2.name = ANY (?)
                    )
                    """);

            params.add(tags.toArray(new String[0]));
        }

        return jdbcTemplate.queryForObject(
                sql.toString(),
                params.toArray(),
                Long.class
        );
    }
}