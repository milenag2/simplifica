# Arquitetura da Nova Solução Simplifica

## 1. Introdução

Este documento descreve a arquitetura proposta para a refatoração do projeto "Simplifica" para utilizar exclusivamente as tecnologias Java, Spring Boot, Gradle, Hibernate, HTML e CSS. O objetivo é substituir o backend Python (Flask) e os componentes Android existentes por uma aplicação web unificada, mantendo a funcionalidade principal de gestão de gastos.

## 2. Visão Geral da Arquitetura

A nova arquitetura será baseada em um modelo cliente-servidor, onde o backend será desenvolvido com Spring Boot e Hibernate, expondo uma API RESTful para comunicação. O frontend será uma aplicação web tradicional, construída com HTML e CSS, consumindo os serviços do backend.

### 2.1. Camadas da Aplicação

A aplicação será dividida nas seguintes camadas:

*   **Frontend (Camada de Apresentação):** Responsável pela interface do usuário. Será implementado com HTML para a estrutura e CSS para o estilo. A interatividade será adicionada com JavaScript puro ou uma biblioteca leve, se necessário, para consumir a API RESTful do backend.
*   **Backend (Camada de Lógica de Negócio e Persistência):** Responsável por toda a lógica de negócio, manipulação de dados e exposição da API. Será construído com Spring Boot, utilizando Spring Data JPA e Hibernate para a persistência de dados.
*   **Banco de Dados:** Um banco de dados relacional (ex: PostgreSQL, H2 para desenvolvimento) será utilizado para armazenar os dados da aplicação.

## 3. Detalhes do Backend (Java, Spring Boot, Hibernate)

### 3.1. Estrutura do Projeto

O projeto Spring Boot será organizado em pacotes lógicos:

*   `com.simplifica.controller`: Contém os controladores REST que expõem os endpoints da API.
*   `com.simplifica.service`: Contém a lógica de negócio principal, orquestrando as operações e interagindo com os repositórios.
*   `com.simplifica.repository`: Interfaces Spring Data JPA para acesso a dados, estendendo `JpaRepository`.
*   `com.simplifica.model`: Classes de entidade JPA que mapeiam as tabelas do banco de dados (User, Category, Transaction).
*   `com.simplifica.dto`: Objetos de Transferência de Dados (DTOs) para entrada e saída de dados da API, desacoplando as entidades do modelo de domínio da API.

### 3.2. Mapeamento de Entidades (Hibernate)

As entidades Python existentes (`User`, `Category`, `Transaction`) serão mapeadas para classes Java com anotações JPA para Hibernate.

#### 3.2.1. Entidade `User`

| Campo Original (Python) | Tipo Original | Campo Proposto (Java) | Tipo Proposto | Descrição                               |
| :---------------------- | :------------ | :-------------------- | :------------ | :-------------------------------------- |
| `id`                    | `Integer`     | `id`                  | `Long`        | Identificador único do usuário          |
| `username`              | `String`      | `username`            | `String`      | Nome de usuário, único                  |
| `email`                 | `String`      | `email`               | `String`      | Endereço de e-mail, único               |

#### 3.2.2. Entidade `Category`

| Campo Original (Python) | Tipo Original | Campo Proposto (Java) | Tipo Proposto | Descrição                               |
| :---------------------- | :------------ | :-------------------- | :------------ | :-------------------------------------- |
| `id`                    | `Integer`     | `id`                  | `Long`        | Identificador único da categoria        |
| `user_id`               | `Integer`     | `user`                | `User`        | Referência ao usuário proprietário      |
| `name`                  | `String`      | `name`                | `String`      | Nome da categoria                       |
| `type`                  | `String`      | `type`                | `String`      | Tipo da categoria (`expense`, `income`) |

#### 3.2.3. Entidade `Transaction`

| Campo Original (Python) | Tipo Original | Campo Proposto (Java) | Tipo Proposto | Descrição                               |
| :---------------------- | :------------ | :-------------------- | :------------ | :-------------------------------------- |
| `id`                    | `Integer`     | `id`                  | `Long`        | Identificador único da transação        |
| `user_id`               | `Integer`     | `user`                | `User`        | Referência ao usuário                   |
| `category_id`           | `Integer`     | `category`            | `Category`    | Referência à categoria                  |
| `description`           | `String`      | `description`         | `String`      | Descrição da transação                  |
| `amount`                | `Float`       | `amount`              | `Double`      | Valor da transação                      |
| `type`                  | `String`      | `type`                | `String`      | Tipo da transação (`expense`, `income`) |
| `date`                  | `DateTime`    | `date`                | `LocalDateTime` | Data da transação                       |
| `is_recurring`          | `Boolean`     | `recurring`           | `Boolean`     | Indica se é recorrente                  |
| `recurring_frequency`   | `String`      | `recurringFrequency`  | `String`      | Frequência da recorrência               |
| `recurring_end_date`    | `DateTime`    | `recurringEndDate`    | `LocalDateTime` | Data de término da recorrência          |

### 3.3. Endpoints da API RESTful

Os endpoints da API Python serão traduzidos para controladores Spring Boot. Exemplos:

*   **Usuários:**
    *   `GET /api/users`: Listar todos os usuários.
    *   `GET /api/users/{id}`: Obter usuário por ID.
    *   `POST /api/users`: Criar novo usuário.
    *   `PUT /api/users/{id}`: Atualizar usuário.
    *   `DELETE /api/users/{id}`: Deletar usuário.

*   **Categorias:**
    *   `GET /api/categories?userId={userId}`: Listar categorias por usuário.
    *   `POST /api/categories`: Criar nova categoria.
    *   `PUT /api/categories/{id}`: Atualizar categoria.
    *   `DELETE /api/categories/{id}`: Deletar categoria.

*   **Transações:**
    *   `GET /api/transactions?userId={userId}&startDate={startDate}&endDate={endDate}&type={type}&categoryId={categoryId}`: Listar transações com filtros.
    *   `GET /api/transactions/{id}`: Obter transação por ID.
    *   `POST /api/transactions`: Criar nova transação.
    *   `PUT /api/transactions/{id}`: Atualizar transação.
    *   `DELETE /api/transactions/{id}`: Deletar transação.

*   **Relatórios:**
    *   `GET /api/reports/balance/monthly?userId={userId}&year={year}&month={month}`: Saldo mensal.
    *   `GET /api/reports/daily?userId={userId}&date={date}`: Relatório diário.
    *   `GET /api/reports/monthly?userId={userId}&year={year}&month={month}`: Relatório mensal.
    *   `GET /api/reports/annual?userId={userId}&year={year}`: Relatório anual.
    *   `GET /api/reports/categories?userId={userId}&startDate={startDate}&endDate={endDate}&type={type}`: Relatório por categoria.

## 4. Detalhes do Frontend (HTML, CSS)

O frontend será composto por páginas HTML estáticas estilizadas com CSS. A interatividade e a comunicação com o backend serão realizadas via JavaScript (Fetch API ou XMLHttpRequest) para consumir os endpoints RESTful.

### 4.1. Estrutura de Páginas

As páginas HTML serão criadas para replicar a funcionalidade da interface do usuário existente (se houver) e para interagir com as APIs de gestão de gastos. Exemplos de páginas:

*   `index.html`: Página inicial/Dashboard.
*   `transactions.html`: Página para listar, adicionar, editar e remover transações.
*   `categories.html`: Página para listar, adicionar, editar e remover categorias.
*   `reports.html`: Página para exibir os relatórios financeiros.

### 4.2. Estilização (CSS)

O arquivo `styles.css` será utilizado para estilizar as páginas HTML, garantindo uma experiência de usuário consistente e agradável.

## 5. Ferramentas e Tecnologias

*   **Linguagem:** Java 17+
*   **Framework:** Spring Boot 3.x
*   **Build Tool:** Gradle
*   **ORM:** Hibernate (via Spring Data JPA)
*   **Banco de Dados:** H2 (para desenvolvimento), PostgreSQL (para produção)
*   **Frontend:** HTML5, CSS3, JavaScript
*   **Controle de Versão:** Git (implícito)

## 6. Próximos Passos

1.  Configurar o ambiente de desenvolvimento e criar o projeto Spring Boot com Gradle.
2.  Definir as entidades JPA (`User`, `Category`, `Transaction`).
3.  Implementar os repositórios Spring Data JPA.
4.  Desenvolver os serviços de negócio.
5.  Criar os controladores RESTful.
6.  Desenvolver as páginas HTML e CSS para o frontend.
7.  Integrar o frontend com o backend via JavaScript.
8.  Realizar testes e depuração. 

## Referências

N/A
