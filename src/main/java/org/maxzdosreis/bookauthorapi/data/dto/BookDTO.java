package org.maxzdosreis.bookauthorapi.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.util.Date;

@Relation(collectionRelation = "books")
@Data
@NoArgsConstructor
public class BookDTO extends RepresentationModel<BookDTO> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String author;
    private Date launchDate;
    private Double price;
}
