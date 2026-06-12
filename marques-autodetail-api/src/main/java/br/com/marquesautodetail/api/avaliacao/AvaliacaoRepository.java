package br.com.marquesautodetail.api.avaliacao;

import br.com.marquesautodetail.api.agendamento.Agendamento;
import br.com.marquesautodetail.api.empresa.Empresa;
import br.com.marquesautodetail.api.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByEmpresa(Empresa empresa);

    List<Avaliacao> findByCliente(Usuario cliente);

    boolean existsByAgendamento(Agendamento agendamento);
}
