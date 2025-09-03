package org.maxzdosreis.bookauthorapi.service;

import org.maxzdosreis.bookauthorapi.controller.BookController;
import org.maxzdosreis.bookauthorapi.data.dto.BookDTO;
import org.maxzdosreis.bookauthorapi.exception.RequiredObjectIsNullException;
import org.maxzdosreis.bookauthorapi.exception.ResourceNotFoundException;
import org.maxzdosreis.bookauthorapi.model.Book;
import org.maxzdosreis.bookauthorapi.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import static org.maxzdosreis.bookauthorapi.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {

    private Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    BookRepository repository;

    @Autowired
    PagedResourcesAssembler<BookDTO> assembler;

    public PagedModel<EntityModel<BookDTO>> findAll(Pageable pageable) {
        logger.info("Finding all Books");

        var books = repository.findAll(pageable);

        var booksWithLinks = books.map(book -> {
            var dto = parseObject(book, BookDTO.class);
            addHateoasLinks(dto);
            return dto;
        });

        Link findAllLink = linkTo(methodOn(BookController.class)
                .findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())))
                .withSelfRel();
        return assembler.toModel(booksWithLinks, findAllLink);
    }

    public BookDTO findById(Long id) {
        logger.info("Finding one Book");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        var dto = parseObject(entity, BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public BookDTO create(BookDTO book) {

        if(book == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one Book");
        var entity = parseObject(book, Book.class);

        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public BookDTO update(BookDTO book) {

        if(book == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one Book");
        Book entity = repository.findById(book.getId()).orElseThrow(() -> new ResourceNotFoundException("Book not found for this ID!"));
        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        var dto =  parseObject(repository.save(entity), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Deleting one Book");
        Book entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found for this ID!"));
        repository.delete(entity);
    }


    private void addHateoasLinks(BookDTO dto) {
        dto.add(linkTo(methodOn(BookController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).findAll(1, 12, "asc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(BookController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(BookController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
