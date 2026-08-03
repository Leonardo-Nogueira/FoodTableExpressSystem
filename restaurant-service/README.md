# 🏢 FoodTableExpress - Serviço de Restaurantes (`restaurant-service`)

O **`restaurant-service`** é o microserviço do ecossistema **FoodTableExpress** responsável pelo cadastro, manutenção e consulta de estabelecimentos (restaurantes), suas mesas e cardápios (itens de menu).

Este módulo atua como um recurso protegido (*OAuth2 Resource Server*). Ele valida de forma autônoma e descentralizada a assinatura dos tokens JWT emitidos pelo `auth-service` usando criptografia assimétrica (chaves RSA), autorizando operações de escrita de forma restrita a administradores.

---

## 🚀 Tecnologias Utilizadas

A stack tecnológica do projeto consiste em:
- **Java 21** - Versão de suporte de longo prazo (LTS).
- **Spring Boot 3.3.1** - Framework base de desenvolvimento rápido e injeção de dependências.
- **Spring Security & Spring OAuth2 Resource Server** - Filtros de segurança e validação local de chaves públicas do JWT.
- **Spring Data JPA & Hibernate** - Abstração e persistência do banco de dados relacional.
- **MySQL** - Banco de dados relacional padrão para o ambiente de produção.
- **H2 Database** - Disponível para execução de testes unitários e de integração locais.
- **Spring Boot Actuator** - Módulo de monitoramento de integridade e métricas do sistema.
- **Lombok** - Redução de código repetitivo (*boilerplate*).

---

## 📂 Estrutura de Diretórios e Componentes

Abaixo está o mapeamento dos componentes mais importantes do projeto:

*   **Configuração de Segurança:** [SecurityConfig.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/java/com/foodtable/express/restaurant/config/SecurityConfig.java) - Registra o `JwtDecoder` utilizando a chave pública ([app.pub](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/resources/app.pub)) e cria um `JwtAuthenticationConverter` customizado para ler a claim `"roles"` e convertê-la em authorities com o prefixo `ROLE_`.
*   **Entidades de Domínio:**
    *   [Restaurant.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/java/com/foodtable/express/restaurant/model/Restaurant.java) - Representa o restaurante. Mapeado para a tabela `rs_restaurants`.
    *   [RestaurantTable.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/java/com/foodtable/express/restaurant/model/RestaurantTable.java) - Representa as mesas. Mapeado para a tabela `rs_restaurant_tables`.
    *   [MenuItem.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/java/com/foodtable/express/restaurant/model/MenuItem.java) - Representa os itens de menu. Mapeado para a tabela `rs_menu_items`.
*   **Serviços de Negócio:**
    *   [RestaurantService.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/java/com/foodtable/express/restaurant/service/RestaurantService.java) - Executa a lógica de CRUD de restaurantes, paginação, regras de validação e **exclusão lógica encadeada** (ao desativar um restaurante, inativa automaticamente todas as suas mesas e itens de menu).
    *   [RestaurantTableService.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/java/com/foodtable/express/restaurant/service/RestaurantTableService.java) - Gerencia mesas ativas de cada estabelecimento.
    *   [MenuItemService.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/java/com/foodtable/express/restaurant/service/MenuItemService.java) - Gerencia os itens ativos de cardápio.
*   **Controladores REST:** Mapeiam os endpoints expostos:
    *   [RestaurantController.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/java/com/foodtable/express/restaurant/controller/RestaurantController.java)
    *   [TableController.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/java/com/foodtable/express/restaurant/controller/TableController.java)
    *   [MenuItemController.java](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/java/com/foodtable/express/restaurant/controller/MenuItemController.java)

---

## 🔒 Regras de Segurança e Autorização (RBAC)

O `restaurant-service` protege seus recursos verificando localmente o token JWT enviado no header da requisição (`Authorization: Bearer <token>`):
- **Rotas de Leitura (GET):** São **públicas** para permitir que qualquer usuário (logado ou não) possa navegar e visualizar os restaurantes, cardápios e mesas.
- **Rotas de Escrita (POST, PUT, DELETE):** Exigem autenticação e permissão de administrador (**`ROLE_admin`**). O Spring Security intercepta e valida se a claim `"roles"` do JWT contém `"admin"`.

---

## 🛠️ Configuração e Setup

### Propriedades da Aplicação (`application.yml`)
O arquivo de configurações em [application.yml](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/resources/application.yml) define:
- Porta padrão: `8082`
- **Banco de Dados local:** MySQL (`jdbc:mysql://localhost:3306/restaurantdb` com usuário `auth` e senha `password`).
- Chave pública de assinatura: `app.pub` carregada do classpath.

---

## 💾 Migrações de Banco de Dados (Liquibase)

O banco de dados do `restaurant-service` é inicializado e mantido por meio do controle de migrações estruturado do Liquibase. O arquivo mestre de changelog está em [db.changelog-master.xml](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/resources/db/changelog/db.changelog-master.xml) e executa os seguintes passos:

1. **[001-create-rs-schema.xml](file:///c:/Users/User/Documents/projects/FoodTableExpress/restaurant-service/src/main/resources/db/changelog/changes/001-create-rs-schema.xml)**:
   * Cria a tabela `rs_restaurants` (estabelecimentos cadastrados).
   * Cria a tabela `rs_restaurant_tables` (mesas do restaurante) com integridade referencial para `rs_restaurants` (`ON DELETE CASCADE`).
   * Cria a tabela `rs_menu_items` (itens de cardápio) com integridade referencial para `rs_restaurants` (`ON DELETE CASCADE`).

---

## 🛣️ API Endpoints

### 1. Restaurantes (`/api/restaurants`)
- **Criar Restaurante** (`POST /api/restaurants` - **Admin**)
  - **Request Body (JSON):**
    ```json
    {
      "name": "Restaurante Italiano de Milão",
      "address": "Av. Paulista, 1000 - São Paulo",
      "openingTime": "11:30:00",
      "closingTime": "23:00:00"
    }
    ```
  - **Success Response (201 Created):**
    ```json
    {
      "id": "7dc2adfb-2936-411a-8219-9430c5e7b233",
      "name": "Restaurante Italiano de Milão",
      "address": "Av. Paulista, 1000 - São Paulo",
      "openingTime": "11:30:00",
      "closingTime": "23:00:00",
      "status": "ACTIVE"
    }
    ```
- **Listar Restaurantes** (`GET /api/restaurants` - **Público**)
  - Parâmetros opcionais: `name` (filtro por nome) e paginação padrão do Spring Data (`page`, `size`, `sort`).
- **Detalhes do Restaurante** (`GET /api/restaurants/{id}` - **Público**)
- **Atualizar Restaurante** (`PUT /api/restaurants/{id}` - **Admin**)
- **Inativar Restaurante (Soft Delete)** (`DELETE /api/restaurants/{id}` - **Admin**)
  - Retorna `204 No Content`. Inativa o restaurante, todas as suas mesas e itens de menu.

---

### 2. Mesas (`/api/restaurants/{id}/tables`)
- **Criar Mesa** (`POST /api/restaurants/{id}/tables` - **Admin**)
  - **Request Body (JSON):**
    ```json
    {
      "label": "Mesa Interna 04",
      "capacity": 4
    }
    ```
  - **Success Response (201 Created):**
    ```json
    {
      "id": "e4f8d9c5-8422-4820-94e8-8a9d3fcffb01",
      "restaurantId": "7dc2adfb-2936-411a-8219-9430c5e7b233",
      "label": "Mesa Interna 04",
      "capacity": 4,
      "status": "ACTIVE"
    }
    ```
- **Listar Mesas Ativas** (`GET /api/restaurants/{id}/tables` - **Público**)
- **Atualizar Mesa** (`PUT /api/restaurants/{id}/tables/{tableId}` - **Admin**)
- **Inativar Mesa** (`DELETE /api/restaurants/{id}/tables/{tableId}` - **Admin**)

---

### 3. Cardápio (`/api/restaurants/{id}/menu-items`)
- **Adicionar Item** (`POST /api/restaurants/{id}/menu-items` - **Admin**)
  - **Request Body (JSON):**
    ```json
    {
      "name": "Pizza Margherita",
      "description": "Molho de tomate artesanal, muçarela de búfala e manjericão fresco",
      "price": 54.90
    }
    ```
  - **Success Response (201 Created):**
    ```json
    {
      "id": "3bf7d9c5-8422-4820-94e8-8a9d3fcffa99",
      "restaurantId": "7dc2adfb-2936-411a-8219-9430c5e7b233",
      "name": "Pizza Margherita",
      "description": "Molho de tomate artesanal, muçarela de búfala e manjericão fresco",
      "price": 54.90,
      "status": "AVAILABLE"
    }
    ```
- **Listar Cardápio** (`GET /api/restaurants/{id}/menu-items` - **Público**)
- **Atualizar Item** (`PUT /api/restaurants/{id}/menu-items/{itemId}` - **Admin**)
- **Remover Item (Soft Delete)** (`DELETE /api/restaurants/{id}/menu-items/{itemId}` - **Admin**)

---

## 📈 Observabilidade e Métricas

O microserviço expõe métricas e informações por meio do Spring Boot Actuator:
*   **Status de Saúde:** `GET http://localhost:8082/actuator/health`
*   **Métricas de JVM/Sistema:** `GET http://localhost:8082/actuator/metrics`
