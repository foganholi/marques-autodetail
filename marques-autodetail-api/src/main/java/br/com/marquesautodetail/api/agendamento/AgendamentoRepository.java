package br.com.marquesautodetail.api.agendamento;

import br.com.marquesautodetail.api.empresa.Empresa;
import br.com.marquesautodetail.api.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByClienteOrderByDataDescHoraDesc(Usuario cliente);

    List<Agendamento> findByEmpresaOrderByDataDescHoraDesc(Empresa empresa);

    List<Agendamento> findByEmpresaAndDataAndStatusIn(Empresa empresa, LocalDate data, List<StatusAgendamento> status);
}
