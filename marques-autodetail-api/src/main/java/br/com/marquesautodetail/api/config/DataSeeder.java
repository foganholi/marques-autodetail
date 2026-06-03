package br.com.marquesautodetail.api.config;

import br.com.marquesautodetail.api.empresa.Empresa;
import br.com.marquesautodetail.api.empresa.EmpresaRepository;
import br.com.marquesautodetail.api.endereco.Endereco;
import br.com.marquesautodetail.api.horario.HorarioDisponivel;
import br.com.marquesautodetail.api.horario.HorarioRepository;
import br.com.marquesautodetail.api.servico.Servico;
import br.com.marquesautodetail.api.servico.ServicoRepository;
import br.com.marquesautodetail.api.usuario.Role;
import br.com.marquesautodetail.api.usuario.Usuario;
import br.com.marquesautodetail.api.usuario.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(
            UsuarioRepository usuarios,
            EmpresaRepository empresas,
            ServicoRepository servicos,
            HorarioRepository horarios,
            PasswordEncoder encoder
    ) {
        return args -> {
            Empresa empresaBase;

            if (empresas.count() == 0) {
                Usuario u = new Usuario();
                u.setNome("Marques AutoDetail");
                u.setEmail("empresa@marques.com");
                u.setSenha(encoder.encode("123456"));
                u.setRole(Role.EMPRESA);
                usuarios.save(u);

                Empresa e = new Empresa();
                e.setUsuario(u);
                e.setNomeFantasia("Marques AutoDetail");
                e.setDescricao("Estética automotiva premium com lavagem técnica, polimento e proteção.");
                e.setTelefone("1199999-9999");
                e.setLatitude(-23.5632);
                e.setLongitude(-46.6544);
                e.setMediaAvaliacao(4.9);
                e.setAberta(true);

                Endereco end = new Endereco();
                end.setRua("Av. Paulista");
                end.setNumero("1000");
                end.setBairro("Bela Vista");
                end.setCidade("São Paulo");
                end.setEstado("SP");
                e.setEndereco(end);
                empresaBase = empresas.save(e);

                criar(servicos, empresaBase, "Lavagem completa", "Lavagem externa, interna e acabamento dos detalhes.", 80, 90);
                criar(servicos, empresaBase, "Polimento técnico", "Correção de pintura, brilho e proteção da lataria.", 150, 180);
                criar(servicos, empresaBase, "Higienização interna", "Limpeza profunda de bancos, carpetes, painel e teto.", 120, 150);
                criar(servicos, empresaBase, "Vitrificação", "Proteção premium da pintura com maior durabilidade.", 450, 360);
            } else {
                empresaBase = empresas.findAll().get(0);
            }

            if (horarios.findByEmpresaIdAndAtivoTrue(empresaBase.getId()).isEmpty()) {
                criarHorario(horarios, empresaBase, DayOfWeek.MONDAY, "08:00", "18:00");
                criarHorario(horarios, empresaBase, DayOfWeek.TUESDAY, "08:00", "18:00");
                criarHorario(horarios, empresaBase, DayOfWeek.WEDNESDAY, "08:00", "18:00");
                criarHorario(horarios, empresaBase, DayOfWeek.THURSDAY, "08:00", "18:00");
                criarHorario(horarios, empresaBase, DayOfWeek.FRIDAY, "08:00", "18:00");
                criarHorario(horarios, empresaBase, DayOfWeek.SATURDAY, "09:00", "14:00");
            }
        };
    }

    private void criar(ServicoRepository repo, Empresa e, String nome, String desc, double preco, int duracao) {
        Servico s = new Servico();
        s.setEmpresa(e);
        s.setNome(nome);
        s.setDescricao(desc);
        s.setPreco(BigDecimal.valueOf(preco));
        s.setDuracaoMinutos(duracao);
        s.setAtivo(true);
        repo.save(s);
    }

    private void criarHorario(HorarioRepository repo, Empresa e, DayOfWeek dia, String inicio, String fim) {
        HorarioDisponivel h = new HorarioDisponivel();
        h.setEmpresa(e);
        h.setDiaSemana(dia);
        h.setHoraInicio(LocalTime.parse(inicio));
        h.setHoraFim(LocalTime.parse(fim));
        h.setAtivo(true);
        repo.save(h);
    }
}
