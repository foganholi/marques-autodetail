package br.com.marquesautodetail.api.usuario;
import jakarta.persistence.*;import java.time.LocalDateTime;
@Entity @Table(name="usuarios")
public class Usuario{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String nome;
 @Column(nullable=false,unique=true) private String email;
 @Column(nullable=false) private String senha;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
 private String telefone; private Boolean ativo=true; private LocalDateTime criadoEm=LocalDateTime.now();
 public Long getId(){return id;} public void setId(Long id){this.id=id;} public String getNome(){return nome;} public void setNome(String nome){this.nome=nome;} public String getEmail(){return email;} public void setEmail(String email){this.email=email;} public String getSenha(){return senha;} public void setSenha(String senha){this.senha=senha;} public Role getRole(){return role;} public void setRole(Role role){this.role=role;} public String getTelefone(){return telefone;} public void setTelefone(String telefone){this.telefone=telefone;} public Boolean getAtivo(){return ativo;} public void setAtivo(Boolean ativo){this.ativo=ativo;} public LocalDateTime getCriadoEm(){return criadoEm;} public void setCriadoEm(LocalDateTime criadoEm){this.criadoEm=criadoEm;}
}
