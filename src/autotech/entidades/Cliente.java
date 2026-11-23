package autotech.entidades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente {
    private final String cpf;
    private String nome;
    private String telefone;
    private final List<Veiculo> veiculos = new ArrayList<>();

    public Cliente(String cpf, String nome, String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /** Vínculo bidirecional: chamado pelo gerenciador ao registrar o veículo. */
    public void adicionarVeiculo(Veiculo v) {
        if (!veiculos.contains(v)) {
            veiculos.add(v);
        }
    }

    public void removerVeiculo(Veiculo v) {
        veiculos.remove(v);
    }

    public List<Veiculo> getVeiculos() {
        return Collections.unmodifiableList(veiculos);
    }
}
