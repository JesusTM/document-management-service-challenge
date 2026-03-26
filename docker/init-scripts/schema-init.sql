--The script to initialize the schema was sourced from the Spring Batch Core dependency: org.springframework.batch.core.

CREATE SCHEMA document_schema;
SET SCHEMA 'document_schema';

CREATE TABLE documents (
    id UUID PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    document_name VARCHAR(255) NOT NULL,
    minio_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(100),
    created_at TIMESTAMP
);

CREATE TABLE tags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE document_tags (
    document_id UUID REFERENCES documents(id),
    tag_id INT REFERENCES tags(id),
    PRIMARY KEY(document_id, tag_id)
);

CREATE INDEX idx_documents_user ON documents(user_name);
CREATE INDEX idx_documents_created ON documents(created_at DESC);
CREATE INDEX idx_tags_name ON tags(name);
CREATE INDEX idx_document_tags_document ON document_tags(document_id);
CREATE INDEX idx_document_tags_tag ON document_tags(tag_id);