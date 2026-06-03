package br.com.marquesautodetail.api.horario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface HorarioRepository extends JpaRepository<HorarioDisponivel, Long> {
    List<HorarioDisponivel> findByEmpresaIdAndAtivoTrue(Long empresaId);

    List<HorarioDisponivel> findByEmpresaIdAndDiaSemanaAndAtivoTrue(Long empresaId, DayOfWeek diaSemana);
}
