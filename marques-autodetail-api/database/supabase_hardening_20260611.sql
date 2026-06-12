-- Marques AutoDetail - reforço de integridade para bancos já existentes.
-- Execute depois de supabase_schema.sql em projetos criados antes de 11/06/2026.

alter table empresas
    alter column usuario_id set not null,
    alter column endereco_id set not null;

alter table servicos
    alter column duracao_minutos set not null,
    alter column ativo set not null,
    alter column empresa_id set not null;

alter table agendamentos
    alter column data set not null,
    alter column hora set not null,
    alter column status set not null,
    alter column cliente_id set not null,
    alter column empresa_id set not null,
    alter column servico_id set not null,
    alter column criado_em set not null;

alter table favoritos
    alter column cliente_id set not null,
    alter column empresa_id set not null;

alter table avaliacoes
    alter column nota set not null,
    alter column criado_em set not null,
    alter column cliente_id set not null,
    alter column empresa_id set not null,
    alter column agendamento_id set not null;

alter table horarios_disponiveis
    alter column dia_semana set not null,
    alter column hora_inicio set not null,
    alter column hora_fim set not null,
    alter column ativo set not null,
    alter column empresa_id set not null;

alter table empresas drop constraint if exists fk_empresas_usuario;
alter table empresas
    add constraint fk_empresas_usuario
    foreign key (usuario_id) references usuarios(id) on delete cascade;

alter table empresas drop constraint if exists fk_empresas_endereco;
alter table empresas
    add constraint fk_empresas_endereco
    foreign key (endereco_id) references enderecos(id) on delete restrict;

alter table agendamentos drop constraint if exists fk_agendamentos_cliente;
alter table agendamentos
    add constraint fk_agendamentos_cliente
    foreign key (cliente_id) references usuarios(id) on delete cascade;

alter table agendamentos drop constraint if exists fk_agendamentos_servico;
alter table agendamentos
    add constraint fk_agendamentos_servico
    foreign key (servico_id) references servicos(id) on delete restrict;

alter table avaliacoes drop constraint if exists fk_avaliacoes_cliente;
alter table avaliacoes
    add constraint fk_avaliacoes_cliente
    foreign key (cliente_id) references usuarios(id) on delete cascade;

alter table avaliacoes drop constraint if exists fk_avaliacoes_agendamento;
alter table avaliacoes
    add constraint fk_avaliacoes_agendamento
    foreign key (agendamento_id) references agendamentos(id) on delete cascade;

create index if not exists idx_agendamentos_empresa_data_status
    on agendamentos(empresa_id, data, status);

create index if not exists idx_horarios_empresa_dia_ativo
    on horarios_disponiveis(empresa_id, dia_semana)
    where ativo = true;

create unique index if not exists uk_usuarios_email_lower
    on usuarios(lower(email));
