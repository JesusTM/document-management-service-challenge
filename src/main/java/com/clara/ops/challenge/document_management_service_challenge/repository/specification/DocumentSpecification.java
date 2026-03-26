package com.clara.ops.challenge.document_management_service_challenge.repository.specification;

import com.clara.ops.challenge.document_management_service_challenge.entity.Document;
import com.clara.ops.challenge.document_management_service_challenge.entity.DocumentTag;
import com.clara.ops.challenge.document_management_service_challenge.entity.Tag;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class DocumentSpecification {

    public static Specification<Document> hasUser(String user) {

        return (root, query, cb) ->
                user == null ? null :
                        cb.equal(root.get("userName"), user);
    }

    public static Specification<Document> hasName(String name) {

        return (root, query, cb) ->
                name == null ? null :
                        cb.like(cb.lower(root.get("documentName")),
                                "%" + name.toLowerCase() + "%");
    }

    public static Specification<Document> hasTags(List<String> tags) {

        return (root, query, cb) -> {

            if (tags == null || tags.isEmpty()) {
                return null;
            }

            Join<Document, DocumentTag> documentTags = root.join("tags");
            Join<DocumentTag, Tag> tag = documentTags.join("tag");

            query.distinct(true);

            return tag.get("name").in(tags);
        };
    }
}
