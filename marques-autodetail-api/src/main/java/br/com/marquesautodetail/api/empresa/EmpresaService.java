package br.com.marquesautodetail.api.empresa;

import br.com.marquesautodetail.api.empresa.dto.EmpresaRequest;
import br.com.marquesautodetail.api.empresa.dto.EmpresaResponse;
import br.com.marquesautodetail.api.endereco.Endereco;
import br.com.marquesautodetail.api.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class EmpresaService {

    private final EmpresaRepository repo;
    private final AuthenticatedUserService authenticatedUser;

    public EmpresaService(EmpresaRepository repo, AuthenticatedUserService authenticatedUser) {
        this.repo = repo;
        this.authenticatedUser = authenticatedUser;
    }

    public List<EmpresaResponse> listarProximas(Double latitude, Double longitude) {
        return repo.findAll().stream()
                .map(empresa -> toResponse(empresa, latitude, longitude))
                .sorted(Comparator.comparing(EmpresaResponse::distanciaKm, Comparator.nullsLast(Double::compareTo)))
                .toList();
    }

    public Empresa buscar(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
    }

    public EmpresaResponse buscarResponse(Long id) {
        return toResponse(buscar(id), null, null);
    }

    @Transactional
    public EmpresaResponse atualizar(Long id, EmpresaRequest request) {
        Empresa empresa = authenticatedUser.exigirEmpresa(id);
        aplicar(empresa, request);
        return toResponse(repo.save(empresa), null, null);
    }

    private void aplicar(Empresa empresa, EmpresaRequest request) {
        if (request.nomeFantasia() != null && !request.nomeFantasia().isBlank()) {
            empresa.setNomeFantasia(request.nomeFantasia().trim());
        }
        if (request.descricao() != null) empresa.setDescricao(request.descricao().trim());
        if (request.telefone() != null) empresa.setTelefone(request.telefone().trim());
        if (request.cnpj() != null) empresa.setCnpj(request.cnpj().trim());
        if (request.latitude() != null) empresa.setLatitude(request.latitude());
        if (request.longitude() != null) empresa.setLongitude(request.longitude());
        if (request.aberta() != null) empresa.setAberta(request.aberta());

        Endereco endereco = empresa.getEndereco() != null ? empresa.getEndereco() : new Endereco();
        if (request.cep() != null) endereco.setCep(request.cep().trim());
        if (request.rua() != null) endereco.setRua(request.rua().trim());
        if (request.numero() != null) endereco.setNumero(request.numero().trim());
        if (request.bairro() != null) endereco.setBairro(request.bairro().trim());
        if (request.cidade() != null) endereco.setCidade(request.cidade().trim());
        if (request.estado() != null) endereco.setEstado(request.estado().trim().toUpperCase());
        empresa.setEndereco(endereco);
    }

    public EmpresaResponse toResponse(Empresa empresa, Double latitude, Double longitude) {
        Endereco endereco = empresa.getEndereco();
        String enderecoFormatado = endereco == null ? "" : String.join(", ",
                java.util.stream.Stream.of(endereco.getRua(), endereco.getNumero())
                        .filter(Objects::nonNull)
                        .filter(valor -> !valor.isBlank())
                        .toList());
        String bairro = endereco != null ? endereco.getBairro() : null;
        Double distancia = latitude != null
                && longitude != null
                && empresa.getLatitude() != null
                && empresa.getLongitude() != null
                ? distanciaKm(latitude, longitude, empresa.getLatitude(), empresa.getLongitude())
                : null;

        return new EmpresaResponse(
                empresa.getId(),
                empresa.getNomeFantasia(),
                empresa.getDescricao(),
                empresa.getTelefone(),
                enderecoFormatado,
                bairro,
                empresa.getLatitude(),
                empresa.getLongitude(),
                empresa.getMediaAvaliacao(),
                empresa.getAberta(),
                distancia
        );
    }

    private double distanciaKm(double latitude1, double longitude1, double latitude2, double longitude2) {
        double raioTerraKm = 6371.0;
        double deltaLatitude = Math.toRadians(latitude2 - latitude1);
        double deltaLongitude = Math.toRadians(longitude2 - longitude1);
        double haversine = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2)
                + Math.cos(Math.toRadians(latitude1))
                * Math.cos(Math.toRadians(latitude2))
                * Math.sin(deltaLongitude / 2)
                * Math.sin(deltaLongitude / 2);
        double distancia = raioTerraKm * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
        return Math.round(distancia * 10.0) / 10.0;
    }
}
