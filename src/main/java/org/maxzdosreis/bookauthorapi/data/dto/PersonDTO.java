package org.maxzdosreis.bookauthorapi.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.maxzdosreis.bookauthorapi.model.Book;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.util.List;

@Relation(collectionRelation = "people")
@Data
@NoArgsConstructor
public class PersonDTO extends RepresentationModel<PersonDTO> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String gender;

    private Boolean enabled;

    private String profileUrl;
    private String photoUrl;

    @JsonIgnore
    private List<Book> books;

    @JsonIgnore
    public String getName(){
        return (firstName != null ? firstName : "") +
                (lastName != null ? " " + lastName : "");
    }
}
