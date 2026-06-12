package br.com.marquesautodetail.api.agendamento;

import br.com.marquesautodetail.api.agendamento.dto.AgendamentoRequest;
import br.com.marquesautodetail.api.empresa.Empresa;
import br.com.marquesautodetail.api.empresa.EmpresaRepository;
import br.com.marquesautodetail.api.horario.HorarioService;
import br.com.marquesautodetail.api.security.AuthenticatedUserService;
import br.com.marquesautodetail.api.servico.Servico;
import br.com.marquesautodetail.api.servico.ServicoRepository;
import br.com.marquesautodetail.api.usuario.Role;
import br.com.marquesautodetail.api.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentos;
    @Mock
    private EmpresaRepository empresas;
    @Mock
    private ServicoRepository servicos;
    @Mock
    private HorarioService horarios;
    @Mock
    private AuthenticatedUserService authenticatedUser;

    private AgendamentoService service;

    @BeforeEach
    void setUp() {
        service = new AgendamentoService(agendamentos, empresas, servicos, horarios, authenticatedUser);
    }

    @Test
    void deveRecusarServicoDeOutraEmpresa() {
        Usuario cliente = usuario(1L, Role.CLIENTE);
        Empresa empresaSolicitada = empresa(10L);
        Empresa empresaDoServico = empresa(20L);
        Servico servico = new Servico();
        servico.setId(30L);
        servico.setEmpresa(empresaDoServico);
        servico.setAtivo(true);

        when(authenticatedUser.clienteAtual()).thenReturn(cliente);
        when(empresas.findById(10L)).thenReturn(Optional.of(empresaSolicitada));
        when(servicos.findById(30L)).thenReturn(Optional.of(servico));

        AgendamentoRequest request = new AgendamentoRequest(
                10L,
                30L,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                null
        );

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pertence");
    }

    @Test
    void clienteNaoPodeCancelarAgendamentoDeOutroCliente() {
        Usuario clienteLogado = usuario(1L, Role.CLIENTE);
        Usuario outroCliente = usuario(2L, Role.CLIENTE);
        Agendamento agendamento = new Agendamento();
        agendamento.setId(50L);
        agendamento.setCliente(outroCliente);
        agendamento.setStatus(StatusAgendamento.PENDENTE);

        when(agendamentos.findById(50L)).thenReturn(Optional.of(agendamento));
        when(authenticatedUser.usuarioAtual()).thenReturn(clienteLogado);

        assertThatThrownBy(() -> service.alterarStatus(50L, StatusAgendamento.CANCELADO))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void empresaNaoPodeConfirmarAgendamentoDeOutraEmpresa() {
        Usuario usuarioEmpresa = usuario(1L, Role.EMPRESA);
        Empresa empresaLogada = empresa(10L);
        Empresa outraEmpresa = empresa(20L);
        Agendamento agendamento = new Agendamento();
        agendamento.setId(50L);
        agendamento.setEmpresa(outraEmpresa);
        agendamento.setStatus(StatusAgendamento.PENDENTE);

        when(agendamentos.findById(50L)).thenReturn(Optional.of(agendamento));
        when(authenticatedUser.usuarioAtual()).thenReturn(usuarioEmpresa);
        when(authenticatedUser.empresaAtual()).thenReturn(empresaLogada);

        assertThatThrownBy(() -> service.alterarStatus(50L, StatusAgendamento.CONFIRMADO))
                .isInstanceOf(AccessDeniedException.class);
    }

    private Usuario usuario(Long id, Role role) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setRole(role);
        return usuario;
    }

    private Empresa empresa(Long id) {
        Empresa empresa = new Empresa();
        empresa.setId(id);
        return empresa;
    }
}
