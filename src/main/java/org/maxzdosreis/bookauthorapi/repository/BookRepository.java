package org.maxzdosreis.bookauthorapi.repository;

import org.maxzdosreis.bookauthorapi.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
