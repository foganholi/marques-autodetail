# Marques AutoDetail — Backend real implementado

Esta versão deixa o app de ser apenas uma demonstração local e adiciona um backend real em Spring Boot + MySQL.

## Estrutura criada

```text
marques-autodetail-api/
  pom.xml
  src/main/java/br/com/marquesautodetail/api/
    auth/
    usuario/
    empresa/
    servico/
    agendamento/
    favorito/
    avaliacao/
    horario/
    endereco/
    security/
    config/
```

## Como rodar o backend

1. Abra o MySQL.
2. Crie ou deixe o Spring criar o banco:

```sql
CREATE DATABASE marques_autodetail;
```

3. Confira usuário e senha em:

```text
marques-autodetail-api/src/main/resources/application.properties
```

Por padrão está:

```properties
spring.datasource.username=root
spring.datasource.password=root
```

4. Rode a API dentro da pasta `marques-autodetail-api`:

```bash
mvn spring-boot:run
```

A API sobe em:

```text
http://localhost:8080/api
```

No emulador Android, o app acessa essa API por:

```text
http://10.0.2.2:8080/api/
```

## Usuário empresa seed

Ao iniciar com banco vazio, a API cria uma empresa inicial:

```text
E-mail: empresa@marques.com
Senha: 123456
Role: EMPRESA
```

## Fluxos reais implementados

### Autenticação

```http
POST /api/auth/register/cliente
POST /api/auth/register/empresa
POST /api/auth/login
GET  /api/auth/me
```

### Empresas

```http
GET /api/empresas/proximas?lat=-23.55&lng=-46.63
GET /api/empresas/{id}
PUT /api/empresas/{id}
```

### Serviços

```http
GET    /api/empresas/{empresaId}/servicos
POST   /api/empresas/{empresaId}/servicos
PUT    /api/servicos/{id}
DELETE /api/servicos/{id}
```

### Agendamentos

```http
POST /api/agendamentos
GET  /api/agendamentos/me
GET  /api/empresas/{empresaId}/agendamentos
PUT  /api/agendamentos/{id}/confirmar
PUT  /api/agendamentos/{id}/recusar
PUT  /api/agendamentos/{id}/cancelar
PUT  /api/agendamentos/{id}/concluir
```

### Favoritos

```http
POST   /api/favoritos/{empresaId}
GET    /api/favoritos/me
DELETE /api/favoritos/{empresaId}
```

### Avaliações

```http
POST /api/avaliacoes
GET  /api/empresas/{empresaId}/avaliacoes
```

### Horários disponíveis

```http
GET    /api/empresas/{empresaId}/horarios
POST   /api/empresas/{empresaId}/horarios
DELETE /api/horarios/{id}
```

## Android atualizado

O Android agora usa API real por Retrofit e JWT.

Arquivos principais alterados:

```text
app/src/main/java/.../api/ApiService.kt
app/src/main/java/.../network/RetrofitClient.kt
app/src/main/java/.../util/SessionManager.kt
app/src/main/java/.../dto/
app/src/main/java/.../ui/LoginActivity.kt
app/src/main/java/.../ui/CadastroActivity.kt
app/src/main/java/.../ui/ClienteHomeActivity.kt
app/src/main/java/.../ui/EmpresaDetalheActivity.kt
app/src/main/java/.../ui/AgendamentosActivity.kt
app/src/main/java/.../ui/AgendamentosListaActivity.kt
app/src/main/java/.../ui/FavoritosActivity.kt
app/src/main/java/.../ui/EmpresaServicosActivity.kt
app/src/main/java/.../ui/EmpresaPerfilActivity.kt
```

## O que ficou local apenas como fallback temporário

```text
LocalMarketplaceRepository
LocalAgendamentoRepository
SharedPreferences para sessão/token
```

A regra nova é: dados oficiais ficam no backend e no MySQL. Os repositórios locais só ajudam caso a API esteja desligada durante testes.
