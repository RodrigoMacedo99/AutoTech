package autotech.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Manutencao {
    private static int proximoId = 1;

    private final int id;
    private final LocalDate dataEntrada;
    private LocalDate dataSaida;
    private final List<Servico> servicos = new ArrayList<>();

    public Manutencao(LocalDate dataEntrada) {
        this.id = proximoId++;
        this.dataEntrada = dataEntrada;
    }

    public void adicionarServico(Servico s) {
        servicos.add(s);
    }

    public double calcularTotal() {
        double total = 0;
        for (Servico s : servicos) {
            total += s.getPreco();
        }
        return total;
    }

    public int getId() {
        return id;
    }

    public List<Servico> getServicos() {
        return Collections.unmodifiableList(servicos);
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDate dataSaida) {
        this.dataSaida = dataSaida;
    }
}
