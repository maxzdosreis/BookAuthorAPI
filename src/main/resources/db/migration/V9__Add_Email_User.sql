ALTER TABLE tb_users
ADD COLUMN email VARCHAR(255) UNIQUE;

CREATE INDEX idx_email ON tb_users(email);