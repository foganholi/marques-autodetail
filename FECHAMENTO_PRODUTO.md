# Marques AutoDetail — versão fechada funcional

Esta versão fecha o fluxo principal do produto sem complicar a arquitetura.

## Fluxos finalizados

### Cliente
- Login/cadastro com backend real.
- Home com empresas próximas via API.
- Detalhe da empresa.
- Serviços reais da empresa.
- Agendamento por calendário e horários disponíveis.
- Histórico de agendamentos.
- Favoritos.
- Perfil.

### Empresa
- Login/cadastro de empresa.
- Dashboard.
- Agendamentos recebidos com confirmar/recusar.
- Perfil editável da empresa.
- Cadastro, edição e exclusão de serviços.
- Cadastro e remoção de dias/horários disponíveis.

## Como rodar

1. Criar usuário do banco, se ainda não existir:

```sql
CREATE DATABASE IF NOT EXISTS marques_autodetail;
CREATE USER IF NOT EXISTS 'marques_user'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON marques_autodetail.* TO 'marques_user'@'localhost';
FLUSH PRIVILEGES;
```

2. Rodar backend:

```bash
cd marques-autodetail-api
mvn spring-boot:run
```

3. Backend esperado:

```text
Tomcat started on port 8080 with context path '/api'
```

4. Abrir Android Studio na pasta raiz do projeto e rodar o app no emulador.

5. URL usada pelo Android em emulador:

```text
http://10.0.2.2:8080/api/
```

## Conta inicial

```text
E-mail: empresa@marques.com
Senha: 123456
```

## Teste principal

1. Entrar como empresa.
2. Editar perfil.
3. Gerenciar dias e horários.
4. Gerenciar serviços.
5. Cadastrar cliente.
6. Entrar como cliente.
7. Escolher empresa/serviço.
8. Escolher data no calendário.
9. Selecionar horário disponível.
10. Confirmar agendamento.
11. Voltar como empresa e confirmar/recusar.
