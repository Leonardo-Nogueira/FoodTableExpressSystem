# 🔐 FoodTableExpress - Serviço de Autenticação e Autorização (`auth-service`)

O **`auth-service`** é o microserviço do ecossistema **FoodTableExpress** encarregado do gerenciamento de identidades, registro de novos usuários, autenticação e emissão de tokens de autorização. 

Para assegurar a comunicação sem estado (stateless) e de alto desempenho entre os diversos microserviços, ele implementa a especificação **OAuth2 Resource Server** utilizando criptografia assimétrica baseada em algoritmos de assinatura **RS256 (RSA Signature com SHA-256)**.

---

## 🚀 Tecnologias Utilizadas

A stack tecnológica do projeto consiste em:
- **Java 21** - Versão de suporte de longo prazo (LTS) utilizada para máxima performance e novos recursos da linguagem.
- **Spring Boot 3.3.1** - Framework base de desenvolvimento rápido e injeção de dependências.
- **Spring Security & Spring OAuth2 Resource Server** - Segurança de endpoints e suporte nativo a tokens JWT.
- **Nimbus JOSE + JWT** - Biblioteca robusta para geração, codificação e validação de chaves e tokens RSA.
- **Spring Data JPA & Hibernate** - Abstração e persistência do banco de dados relacional.
- **MySQL** - Banco de dados relacional padrão para o ambiente de produção.
- **H2 Database** - Banco de dados em memória para testes unitários e de integração locais.
- **Liquibase** - Ferramenta profissional para controle de versão e migração de esquemas de banco de dados.
- **Spring Boot Actuator** - Módulo de monitoramento, métricas de JVM e verificações de integridade (*health checks*).
- **Lombok** - Redução de código repetitivo (*boilerplate*) como getters, setters e builders.

---

## 📂 Estrutura de Diretórios e Componentes

Abaixo está o mapeamento dos componentes mais importantes do projeto:

*   **Configuração de Segurança:** [SecurityConfig.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/java/com/foodtable/express/auth/config/security/SecurityConfig.java) - Gerencia os filtros de segurança do Spring Security, define as rotas públicas, cria o decodificador/codificador de JWT a partir das chaves assimétricas e registra o bean de criptografia de senhas (`BCryptPasswordEncoder`).
*   **Controladores:** [AuthorizationController.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/java/com/foodtable/express/auth/controller/AuthorizationController.java) - Expõe os endpoints REST públicos para cadastro (`/register`) e login (`/token`).
*   **Serviço de Login:** [LoginService.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/java/com/foodtable/express/auth/service/LoginService.java) - Valida as credenciais do usuário informadas e emite o token JWT com claims específicas.
*   **Serviço de Usuários:** [UserService.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/java/com/foodtable/express/auth/service/UserService.java) - Executa as regras de domínio de cadastro de novos usuários, incluindo verificação de duplicidade de e-mail e persistência utilizando criptografia.
*   **Entidades de Domínio (DDD):** [User.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/java/com/foodtable/express/auth/model/User.java) e [Role.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/java/com/foodtable/express/auth/model/Role.java) - Classes de domínio ricas que utilizam construtores explícitos e métodos de fábrica estáticos (*static factory methods*) para prevenir o padrão de modelo anêmico.
*   **Repositórios JPA:** [UserRepository.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/java/com/foodtable/express/auth/repository/UserRepository.java) e [RoleRepository.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/java/com/foodtable/express/auth/repository/RoleRepository.java) - Interfaces de comunicação com o banco de dados.
*   **Tratador Global de Exceções:** [GlobalExceptionHandler.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/java/com/foodtable/express/auth/infra/exception/GlobalExceptionHandler.java) - Intercepta erros lançados pelas validações e lógica de negócio, convertendo-os em payloads estruturados em formato JSON com o código HTTP adequado.

---

## 🛠️ Configuração e Inicialização

### Pré-requisitos
Certifique-se de que o container MySQL está ativo. Você pode rodar a base de dados via Docker Compose a partir da raiz do projeto:

```bash
docker-compose up -d mysql
```

### Propriedades da Aplicação (`application.yml`)
O arquivo de configurações [application.yml](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/resources/application.yml) é preparado para o ambiente de produção, carregando as configurações a partir de variáveis de ambiente com valores padrão (fallbacks) para desenvolvimento local:
- **Porta:** `${PORT:8081}`
- **Banco de Dados MySQL:** `jdbc:mysql://localhost:3306/authdb` (Usuário: `auth`, Senha: `password`).
- **Validação de Hibernate:** `ddl-auto` configurado como `validate`. Isso força o Hibernate apenas a certificar que o esquema confere com o mapeamento Java, delegando modificações ao Liquibase.
- **Chaves de Assinatura JWT:** As chaves pública ([app.pub](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/resources/app.pub)) e privada ([app.key](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/resources/app.key)) são carregadas do classpath.
- **Duração do Token:** Definido em `jwt.expiration-seconds: 600` (10 minutos), customizável dinamicamente.

---

## 🔑 Segurança e Fluxo de Autenticação com Chaves Assimétricas (RS256)

A arquitetura utiliza o algoritmo de segurança assimétrico **RS256**:
1. O **`auth-service`** possui acesso à chave privada (`app.key`), que é utilizada única e exclusivamente para **assinar** e gerar os tokens JWT após o login bem-sucedido.
2. Os outros microserviços do ecossistema precisam apenas da chave pública (`app.pub`) para **decodificar** e validar a assinatura e integridade do token localmente. Não há necessidade de fazer requisições HTTP adicionais ao `auth-service` para verificar se o token é válido, garantindo grande escalabilidade.

### 🔑 Como Gerar as Chaves Pública e Privada (RSA 2048)

O `auth-service` requer um par de chaves RSA de 2048 bits no formato DER/PKCS8 (adequado para leitura do Java/Spring Security). Para gerá-las e salvá-las no diretório `src/main/resources/`, execute os seguintes comandos do OpenSSL no terminal:

1.  **Gerar a chave privada original (PEM):**
    ```bash
    openssl genrsa -out app.pem 2048
    ```
2.  **Converter a chave privada para o formato PKCS#8 (DER) legível pelo Java:**
    ```bash
    openssl pkcs8 -topk8 -inform PEM -outform DER -in app.pem -out app.key -nocrypt
    ```
3.  **Extrair a chave pública correspondente no formato X.509 (DER):**
    ```bash
    openssl rsa -in app.pem -pubout -outform DER -out app.pub
    ```
4.  **Mover os arquivos:**
    Mova os arquivos `app.key` (chave privada) e `app.pub` (chave pública) gerados para a pasta:
    `auth-service/src/main/resources/`

> [!WARNING]
> Nunca compartilhe ou faça commit da chave privada (`app.key`) em repositórios públicos. Ela deve ser guardada com o maior nível de restrição e sigilo. Em produção, ela deve ser injetada de forma segura (como secrets ou volumes restritos).

---

## 💾 Migrações de Banco de Dados (Liquibase)

O banco de dados do `auth-service` é inicializado e mantido por meio do controle de migrações estruturado do Liquibase. O arquivo mestre de changelog está em [db.changelog-master.xml](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/resources/db/changelog/db.changelog-master.xml) e executa os seguintes passos:

1. **[001-create-schema.xml](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/resources/db/changelog/changes/001-create-schema.xml)**: 
   * Cria a tabela `as_roles` (perfil de usuário) com auto-incremento de ID.
   * Cria a tabela `as_users` com restrições rígidas (e-mail único, não nulo e índices adequados para busca).
   * Cria a tabela de junção `as_user_roles` com chaves primárias compostas e restrições de chave estrangeira com deleção em cascata (`ON DELETE CASCADE`).
2. **[002-insert-initial-data.xml](file:///c:/Users/User/Documents/projects/FoodTableExpress/auth-service/src/main/resources/db/changelog/changes/002-insert-initial-data.xml)**:
   * Insere os perfis básicos na tabela de papéis: `admin` (ID 1) e `basic` (ID 2).

---

## 🛣️ API Endpoints

### 1. Cadastro de Novo Usuário (Register)
Cria um novo usuário na base com perfil de acesso padrão (`basic`).

*   **URL:** `/register`
*   **Método HTTP:** `POST`
*   **Headers:** `Content-Type: application/json`
*   **Corpo da Requisição (JSON):**
    ```json
    {
      "name": "Leonardo Nogueira",
      "email": "leonardo@exemplo.com",
      "password": "senha_segura_123"
    }
    ```
*   **Resposta de Sucesso (201 Created):**
    ```json
    {
      "id": "e9b5f543-982d-4bfd-a719-75fb3e1981a8",
      "email": "leonardo@exemplo.com"
    }
    ```
*   **Resposta de Erro de Validação (400 Bad Request):**
    ```json
    {
      "timestamp": "2026-08-03T21:30:00Z",
      "status": 400,
      "error": "Validation Error",
      "message": "There are validation errors in the request fields.",
      "errors": {
        "email": "Invalid email format",
        "password": "Password must be at least 6 characters"
      }
    }
    ```

### 2. Autenticação de Usuário (Login / Geração de Token)
Valida a senha criptografada do usuário e gera um token JWT de acesso.

*   **URL:** `/token`
*   **Método HTTP:** `POST`
*   **Headers:** `Content-Type: application/json`
*   **Corpo da Requisição (JSON):**
    ```json
    {
      "email": "leonardo@exemplo.com",
      "password": "senha_segura_123"
    }
    ```
*   **Resposta de Sucesso (200 OK):**
    ```json
    {
      "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJBdXRoLVNlcnZpY2UiLCJzdWIiOiJlOWI1ZjU0My05ODJkLTRiZmQtYTcxOS03NWZiM2UxOTgxYTgiLCJleHAiOjE3ODkxMDQwMDB9...",
      "expiresIn": 600
    }
    ```

---

## 📈 Observabilidade e Rastreamento

### Métricas e Status de Saúde (Actuator)
Com o Spring Boot Actuator configurado, é possível monitorar a saúde da aplicação através dos seguintes endpoints expostos:
- **Verificação de Saúde (Integridade):** `GET http://localhost:8081/actuator/health`
- **Métricas do Sistema (Memória, CPU, GC):** `GET http://localhost:8081/actuator/metrics`

### Níveis de Log (Logback/SLF4J)
O nível de logs do microserviço é configurado no `application.yml`:
- Root logger configurado no nível `INFO`.
- Classes internas do pacote `com.foodtable.express.auth` configuradas em `DEBUG` para fornecer detalhes completos de tentativas de login, logins bem sucedidos, novos cadastros e rastreamento de exceções.
