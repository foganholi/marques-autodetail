# Marques AutoDetail

Aplicativo Android nativo em Kotlin/XML com backend Spring Boot. O Android continua consumindo a API REST do Spring Boot via Retrofit; o banco online passa a ser Supabase PostgreSQL.

## Arquitetura

- `app`: aplicativo Android nativo.
- `marques-autodetail-api`: API Spring Boot com autenticação JWT, Spring Security e Spring Data JPA.
- Banco de dados: Supabase PostgreSQL.

## Variaveis de ambiente da API

Configure estas variaveis no ambiente local e tambem no provedor de deploy:

```env
DATABASE_URL=jdbc:postgresql://db.PROJECT_REF.supabase.co:5432/postgres?sslmode=require
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=sua-senha-do-banco-supabase
JWT_SECRET=um-segredo-longo-com-pelo-menos-32-caracteres
PORT=8080
```

Para ambientes sem IPv6, use a connection string do Supavisor em session mode e mantenha o prefixo JDBC:

```env
DATABASE_URL=jdbc:postgresql://aws-0-REGION.pooler.supabase.com:5432/postgres?sslmode=require
DATABASE_USERNAME=postgres.PROJECT_REF
DATABASE_PASSWORD=sua-senha-do-banco-supabase
```

Evite o pooler em transaction mode para esta API JPA/Hibernate, porque esse modo pode ser incompativel com prepared statements.

## Configurar Supabase

1. Crie um projeto no Supabase.
2. No Dashboard, abra `Project Settings > Database` ou o botao `Connect`.
3. Copie a connection string de `Direct connection` se o deploy suportar IPv6, ou `Session pooler` se precisar de IPv4.
4. Converta a URL para JDBC adicionando `jdbc:` no inicio e `?sslmode=require` no final quando necessario.
5. Configure `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` e `JWT_SECRET` no ambiente da API.

## Executar o SQL

1. Abra `SQL Editor` no Supabase.
2. Cole o conteudo de `marques-autodetail-api/database/supabase_schema.sql`.
3. Execute o script.
4. Confirme que as tabelas `usuarios`, `enderecos`, `empresas`, `servicos`, `agendamentos`, `favoritos`, `avaliacoes` e `horarios_disponiveis` foram criadas.

O script cria constraints, foreign keys, indices, dados iniciais da empresa de demonstracao, servicos e horarios disponiveis. Ele tambem habilita RLS nas tabelas publicas. Como o Android nao acessa Supabase diretamente, a API Spring Boot deve conectar com usuario/senha do banco, nao com anon key.

## Execucao local da API

No diretorio `marques-autodetail-api`, configure as variaveis de ambiente e execute:

```bash
mvn spring-boot:run
```

Ou gere o pacote:

```bash
mvn clean package
java -jar target/marques-autodetail-api-0.0.1-SNAPSHOT.jar
```

A API sobe em `http://localhost:8080/api` por padrao.

## Android

O app permanece usando Retrofit em `app/src/main/java/com/MatheusFoganholi/marquesautodetail/network/RetrofitClient.kt`.

Durante desenvolvimento no emulador:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/api/"
```

Depois do deploy da API Spring Boot, troque para a URL publica do backend:

```kotlin
private const val BASE_URL = "https://sua-api-online.com/api/"
```

## Testes e build

Para validar o backend:

```bash
cd marques-autodetail-api
mvn clean package
```

Para validar o Android, use o Gradle wrapper na raiz:

```bash
./gradlew test
```

## Deploy da API

Qualquer plataforma que execute Java 17 e exponha porta HTTP pode hospedar a API, como Render, Railway, Fly.io, DigitalOcean App Platform ou um VPS.

O backend tambem possui um `Dockerfile` em `marques-autodetail-api/Dockerfile`, entao pode ser publicado como container. Para Render, ha um `render.yaml` na raiz com as variaveis sensiveis marcadas para configuracao manual no painel.

Checklist de deploy:

- Configurar Java 17.
- Build command: `mvn clean package`.
- Start command: `java -jar target/marques-autodetail-api-0.0.1-SNAPSHOT.jar`.
- Definir `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `JWT_SECRET` e `PORT`.
- Executar `marques-autodetail-api/database/supabase_schema.sql` no Supabase antes de iniciar a API com `spring.jpa.hibernate.ddl-auto=validate`.
- Atualizar `BASE_URL` no Android para a URL publica da API.

Checklist para deixar online:

1. Criar o projeto no Supabase.
2. Executar o SQL em `marques-autodetail-api/database/supabase_schema.sql`.
3. Criar o servico web no provedor usando Java 17 ou Docker.
4. Configurar as variaveis de ambiente do backend.
5. Fazer um teste de login em `/api/auth/login` com `empresa@marques.com` e senha `123456`.
6. Atualizar `BASE_URL` no Android para a URL publica do backend.
