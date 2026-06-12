# Marques AutoDetail

Aplicativo Android nativo em Kotlin/XML para clientes e empresas de estética automotiva. O app consome uma API Spring Boot com autenticação JWT; a API persiste os dados no Supabase PostgreSQL.

## Estrutura

- `app`: aplicativo Android, Retrofit, sessão JWT e telas de cliente/empresa.
- `marques-autodetail-api`: API Spring Boot, Spring Security, JPA e PostgreSQL.
- `marques-autodetail-api/database`: criação e atualização do banco Supabase.
- `render.yaml` e `Dockerfile`: deploy da API no Render.
- `INICIAR_APRESENTACAO.bat`: compila, instala e abre o app no emulador configurado.

## API online

- Base: `https://marques-autodetail-api.onrender.com/api/`
- Teste público: `GET /empresas`

O plano gratuito do Render pode levar cerca de um minuto para iniciar após um período sem uso. Abra o endpoint público antes da apresentação.

## Variáveis da API

Crie `marques-autodetail-api/.env` a partir de `.env.example`:

```env
DATABASE_URL=jdbc:postgresql://HOST:5432/postgres?sslmode=require
DATABASE_USERNAME=usuario_do_banco
DATABASE_PASSWORD=senha_do_banco
JWT_SECRET=segredo_aleatorio_com_32_ou_mais_caracteres
PORT=8080
JPA_SHOW_SQL=false
JPA_FORMAT_SQL=false
```

`JWT_SECRET` é obrigatório. O arquivo `.env` e outras credenciais não são versionados.

## Supabase

1. Crie o projeto e abra `SQL Editor`.
2. Em um banco novo, execute `marques-autodetail-api/database/supabase_schema.sql`.
3. Em um banco criado com uma versão anterior, execute também `supabase_hardening_20260611.sql`.
4. Use a conexão direta se o host aceitar IPv6 ou o Session Pooler na porta 5432.
5. Configure no Render a URL em formato JDBC e o usuário/senha do PostgreSQL.

O Android não usa chave Supabase e não acessa as tabelas diretamente. Toda autorização passa pela API Spring Boot.

## Executar o backend

Requisitos: Java 17 e Maven 3.9+.

```powershell
cd marques-autodetail-api
mvn clean package
mvn spring-boot:run
```

Localmente a API fica em `http://localhost:8080/api`. O build completo executa os testes unitários.

## Executar o Android

Requisitos: Android Studio, Android SDK e um emulador ou aparelho Android.

1. Abra a pasta raiz `marques-autodetail`.
2. Aguarde o Gradle Sync.
3. Selecione um dispositivo Android.
4. Execute a configuração `app`.

A URL online fica em `app/build.gradle.kts`, no campo `API_BASE_URL`. Para uma API local no emulador, use `http://10.0.2.2:8080/api/` e permita HTTP apenas no build de debug.

## Fluxo de teste

1. Cadastre uma conta `CLIENTE`.
2. Faça login e confirme a abertura da tela inicial.
3. Abra uma empresa e salve-a nos favoritos.
4. Escolha um serviço, data e horário e crie o agendamento.
5. Saia e entre com uma conta `EMPRESA`.
6. Confirme ou recuse o agendamento recebido.
7. Atualize perfil, serviços e horários da empresa.
8. Volte à conta cliente e consulte o histórico.

Erros esperados:

- senha incorreta: HTTP 401;
- token ausente/inválido/expirado: HTTP 401;
- alteração de recurso de outra empresa/cliente: HTTP 403;
- campos inválidos: HTTP 400;
- e-mail duplicado: HTTP 400;
- conflito de integridade: HTTP 409.

## Endpoints principais

| Método | Endpoint | Acesso | Finalidade |
|---|---|---|---|
| POST | `/auth/login` | Público | Login e geração do JWT |
| POST | `/auth/register/cliente` | Público | Cadastro de cliente |
| POST | `/auth/register/empresa` | Público | Cadastro de empresa |
| GET | `/auth/me` | Autenticado | Dados da sessão |
| GET | `/empresas` | Público | Lista de empresas |
| GET | `/empresas/proximas` | Público | Empresas ordenadas por distância |
| GET | `/empresas/{id}` | Público | Detalhes da empresa |
| PUT | `/empresas/{id}` | Dona da empresa | Atualização do perfil |
| GET/POST | `/empresas/{id}/servicos` | GET público, POST dona | Consulta e cadastro de serviços |
| PUT/DELETE | `/servicos/{id}` | Dona da empresa | Atualização e desativação |
| GET | `/empresas/{id}/horarios/disponiveis` | Público | Horários livres por serviço/data |
| POST | `/agendamentos` | Cliente | Criação de agendamento |
| GET | `/agendamentos/me` | Autenticado | Histórico do cliente ou empresa |
| PUT | `/agendamentos/{id}/{acao}` | Proprietário | Confirmar, recusar, concluir ou cancelar |
| GET/POST/DELETE | `/favoritos/...` | Cliente | Gerenciamento de favoritos |
| GET/POST | `/empresas/{id}/avaliacoes`, `/avaliacoes` | GET público, POST cliente | Avaliações concluídas |

## Deploy

O Render usa `marques-autodetail-api/Dockerfile`. Configure `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` e `JWT_SECRET`; não defina `PORT`, pois o Render injeta esse valor. Depois de enviar a branch ao GitHub, faça um novo deploy para publicar as correções.
