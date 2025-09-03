ALTER TABLE tb_person
ADD COLUMN wikipedia_profile_url VARCHAR(255) DEFAULT 'https://github.com/maxzdosreis/BookAuthorAPI',
ADD COLUMN photo_url VARCHAR(255) DEFAULT 'https://raw.githubusercontent.com/leandrocgsi/rest-with-spring-boot-and-java-erudio/refs/heads/main/photos/00_some_person.jpg';