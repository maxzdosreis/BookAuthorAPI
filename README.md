# 📚 BookAuthorAPI

Sistema de gerenciamento de autores e suas obras literárias via API REST, com autenticação segura e funcionalidades completas de CRUD para catalogação literária.

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Security** - Autenticação JWT
- **Spring Data JPA** - Persistência de dados
- **Spring Mail** - Envio de emails
- **MySQL** - Banco de dados
- **Flyway** - Controle de versão do banco
- **SpringDoc OpenAPI** - Documentação da API
- **Lombok** - Redução de código boilerplate
- **Maven** - Gerenciamento de dependências

## ✨ Funcionalidades

### 🔐 Autenticação e Segurança
- ✅ **Login JWT** - Autenticação baseada em tokens
- ✅ **Refresh Token** - Renovação automática de sessões
- ✅ **Reset de Senha** - Redefinição segura via email
- ✅ **Criptografia PBKDF2** - Hash seguro de senhas
- ✅ **Rate Limiting** - Proteção contra ataques

### 📖 Gerenciamento de Autores e Livros
- ✅ **CRUD Completo** - Criar, listar, atualizar e deletar
- ✅ **Relacionamentos** - Autores vinculados aos seus livros
- ✅ **Validações** - Dados consistentes e seguros
- ✅ **Paginação** - Listagem otimizada

### 📧 Sistema de Email
- ✅ **Reset de Senha** - Link seguro enviado por email
- ✅ **Templates HTML** - Emails profissionais
- ✅ **Envio Assíncrono** - Performance otimizada

## 📋 Pré-requisitos

- **Java 21+**
- **Maven 3.8+**
- **MySQL 8.0+**
- **Conta Gmail** (para envio de emails)

## ⚙️ Configuração

### 1. Clone o repositório
```bash
git clone https://github.com/maxzdosreis/BookAuthorAPI.git
cd BookAuthorAPI
```

### 2. Configure o banco de dados
```sql
CREATE DATABASE bookauthorapi;
CREATE USER 'bookuser'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON bookauthorapi.* TO 'bookuser'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure o Gmail
1. Ative a **verificação em duas etapas** na sua conta Google
2. Gere uma **senha de app** específica
3. Configure no `application.yml`

### 4. Configure application.properties
```properties
# Database
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/bookauthorapi?useTimezone=true&serverTimezone=UTC
    username: root
    password: root

# Email Configuration
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${EMAIL_USERNAME}
    password: ${EMAIL_PASSWORD}
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enabled: true
      mail.smtp.starttls.required: true

# Password Reset Settings
app:
  password-reset:
  token-expiry-hours: 1
  base-url: http://localhost:8080
  max-attempts-per-hour: 3

# JWT Settings
security:
  jwt:
    token:
      secret-key: 53cr37
      expire-length: 3600000
```

### 5. Execute a aplicação
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 📚 Documentação da API

### Swagger UI
Acesse: `http://localhost:8080/swagger-ui.html`

### Endpoints Principais

#### 🔐 Autenticação
```http
POST /auth/api/v1/signin          # Login
POST /auth/api/v1/refresh-token   # Renovar token
POST /auth/api/v1/create          # Criar usuário
```

#### 🔄 Reset de Senha
```http
POST /auth/api/v1/forgot-password  # Solicitar reset
GET  /auth/api/v1/reset-password   # Validar token
POST /auth/api/v1/reset-password   # Confirmar nova senha
```

#### 👤 Authentication
```http
PUT    /auth/refresh/{username}        # Atualiza token
POST   /auth/signin        # Autentica usuário
POST   /auth/createUser   # Cria usuário
```

#### 👤 Password Ret
```http
GET    /auth/reset-password      # Valida token de reset
POST   /auth/reset-password      # Troca a senha
POST   /auth/forgot-password   # Envia email
```

#### 📚 Pessoas
```http
GET    /api/person/v1        # Lista pessoas
PUT    /api/person/v1        # Atualiza pessoa
POST   /api/person/v1        # Cria pessoa
GET    /api/person/v1/{id}   # Busca pessoa
DELETE /api/person/v1/{id}   # Deleta pessoa
PATCH  /api/person/v1/{id}   # Desabilita uma pessoa
```

#### 📖 Livros
```http
GET    /api/book/v1        # Listar livros
PUT    /api/book/v1        # Atualizar livro
POST   /api/book/v1        # Criar livro
GET    /api/book/v1/{id}   # Buscar livro
DELETE /api/book/v1/{id}   # Deletar livro
```

## 🧪 Exemplos de Uso

### Login
```bash
curl -X POST http://localhost:8080/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### Reset de Senha
```bash
# 1. Solicitar reset
curl -X POST http://localhost:8080/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com"
  }'

# 2. Confirmar nova senha (após receber email)
curl -X POST http://localhost:8080/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "token-do-email",
    "newPassword": "novaSenha123"
  }'
```

### Criar Pessoa (com autenticação)
```bash
curl -X POST http://localhost:8080/api/person/v1 \
  -H "Authorization: Bearer seu-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Machado",
    "lastName": "de Assis",
    "address": "Rua Teste, 123",
    "gender": "Male",
    "enabled": true,
    "profileUrl": "www.linkdeperfil.com",
    "photoUrl": "www.linkfotoautor.com"
  }'
```

## 🗂️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/org/maxzdosreis/bookauthorapi/
│   │   ├── config/          # Configurações
│   │   ├── controller/      # Controllers REST
│   │   ├── data/dto/        # Data Transfer Objects
│   │   ├── exception/       # Exceptions personalizadas
│   │   ├── mapper/          # Mapper 
│   │   ├── model/           # Models de domínio
│   │   ├── repository/      # Repositories JPA
│   │   ├── scheduler/       # Tarefas agendadas
│   │   ├── security/jwt/    # Configurações de segurança
│   │   └── service/         # Regras de negócio
│   └── resources/
│       ├── db/migration/    # Scripts Flyway
│       └── application.properties
└── test/                    # Testes unitários (futuramente)
```

## 🔒 Segurança

### Autenticação JWT
- **Access Token**: Expira em 1 hora
- **Refresh Token**: Permite renovação segura
- **Secret Key**: Configurável via properties

### Reset de Senha
- **Tokens únicos**: UUID seguro
- **Expiração**: 1 hora por padrão
- **Rate Limiting**: Máximo 3 tentativas por hora
- **Uso único**: Token inutilizado após uso

### Criptografia
- **PBKDF2**: 185.000 iterações
- **Salt**: Único por senha
- **SHA-256**: Algoritmo de hash

## 🛠️ Desenvolvimento

### Executar testes
```bash
mvn test
```

### Gerar build
```bash
mvn clean package
```

### Executar com profile específico
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Logs
Os logs são configurados para diferentes níveis:
- `ERROR`: Erros críticos
- `WARN`: Avisos importantes
- `INFO`: Informações gerais
- `DEBUG`: Detalhes de desenvolvimento

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👨‍💻 Autor

**Max dos Reis**
- GitHub: [@maxzdosreis](https://github.com/maxzdosreis)
- Email: maxzdosreis@gmail.com

## 🙏 Agradecimentos

- Spring Boot team
- Comunidade Java
- Contribuidores do projeto

---

⭐ **Se este projeto foi útil, considere dar uma estrela no GitHub!**