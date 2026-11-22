package autotech;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GerenciadorOficina {
    private static final String ARQUIVO_MANUTENCOES = "manutencoes.txt";
    public static final String CABECALHO_TABELA = String.format("%-6s %-9s %-14s %-12s %-6s %12s %-22s %s",
            "Tipo", "Placa", "Marca", "Modelo", "Ano", "Preço (R$)", "Cliente", "Detalhe");

    private final List<Veiculo> listaVeiculos = new ArrayList<>();
    private final List<Cliente> listaClientes = new ArrayList<>();

    // ---------- Carga inicial ----------

    public void popularDadosIniciais() {
        // 7 carros
        adicionarInicial(new Carro("CAR0001", "Fiat", "Argo", 2022, 85000, "Flex", 107, null, 4), 1);
        adicionarInicial(new Carro("CAR0002", "Volkswagen", "Polo", 2023, 98000, "Flex", 128, null, 4), 2);
        adicionarInicial(new Carro("CAR0003", "Chevrolet", "Onix", 2021, 79000, "Flex", 116, null, 4), 3);
        adicionarInicial(new Carro("CAR0004", "Toyota", "Corolla", 2024, 159000, "Híbrido", 177, null, 4), 4);
        adicionarInicial(new Carro("CAR0005", "Honda", "Civic", 2020, 135000, "Flex", 155, null, 4), 5);
        adicionarInicial(new Carro("CAR0006", "Hyundai", "HB20", 2022, 82000, "Flex", 120, null, 4), 6);
        adicionarInicial(new Carro("CAR0007", "Ford", "Ka", 2019, 52000, "Flex", 85, null, 4), 7);

        // 7 motos
        adicionarInicial(new Moto("MOT0001", "Honda", "CG 160", 2023, 16500, "Gasolina", 16, null, 160), 8);
        adicionarInicial(new Moto("MOT0002", "Yamaha", "Fazer 250", 2022, 22000, "Gasolina", 21, null, 250), 9);
        adicionarInicial(new Moto("MOT0003", "Honda", "Biz 125", 2021, 11500, "Gasolina", 11, null, 125), 10);
        adicionarInicial(new Moto("MOT0004", "Yamaha", "XTZ 150", 2024, 19800, "Gasolina", 16, null, 150), 11);
        adicionarInicial(new Moto("MOT0005", "BMW", "G 310 R", 2022, 36900, "Gasolina", 34, null, 313), 12);
        adicionarInicial(new Moto("MOT0006", "Kawasaki", "Ninja 400", 2023, 45900, "Gasolina", 48, null, 399), 13);
        adicionarInicial(new Moto("MOT0007", "Shineray", "Jet 50", 2020, 6900, "Gasolina", 4, null, 50), 14);
    }

    /** Cria um cliente fictício para o veículo, registra-o e adiciona o veículo à lista. */
    private void adicionarInicial(Veiculo v, int indice) {
        Cliente c = new Cliente(String.format("000.000.000-%02d", indice),
                "Cliente " + indice,
                String.format("(11) 99999-%04d", indice));
        listaClientes.add(c);
        listaVeiculos.add(recriarComCliente(v, c));
    }

    private Veiculo recriarComCliente(Veiculo v, Cliente c) {
        if (v instanceof Carro carro) {
            return new Carro(carro.getPlaca(), carro.getMarca(), carro.getModelo(), carro.getAnoFabricacao(),
                    carro.getPreco(), carro.getMotor().getTipoCombustivel(), carro.getMotor().getPotenciaCV(),
                    c, carro.getNumeroPortas());
        }
        Moto moto = (Moto) v;
        return new Moto(moto.getPlaca(), moto.getMarca(), moto.getModelo(), moto.getAnoFabricacao(),
                moto.getPreco(), moto.getMotor().getTipoCombustivel(), moto.getMotor().getPotenciaCV(),
                c, moto.getCilindradas());
    }

    // ---------- Clientes ----------

    public void cadastrarCliente(Cliente c) {
        listaClientes.add(c);
    }

    public Cliente buscarClientePorCpf(String cpf) {
        for (Cliente c : listaClientes) {
            if (c.getCpf().equals(cpf)) {
                return c;
            }
        }
        return null;
    }

    // ---------- Entrada / saída ----------

    public void registrarEntradaVeiculo(Veiculo v, Manutencao m) {
        if (buscarPorPlaca(v.getPlaca()) == null) {
            listaVeiculos.add(v);
        }
        v.adicionarManutencao(m);
        salvarManutencaoEmArquivo(v, m);
    }

    public boolean registrarSaidaVeiculo(String placa, FormaPagamento pag) {
        Veiculo v = buscarPorPlaca(placa);
        if (v == null) {
            return false;
        }
        Manutencao m = v.getManutencaoAtiva();
        if (m == null) {
            return false;
        }
        m.setDataSaida(LocalDate.now());
        double valorBase = m.calcularTotal();
        double valorFinal = pag.calcularValorFinal(valorBase);
        String comprovante = pag.gerarComprovante(valorFinal);
        exportarReciboSaidaArquivo(v, m, comprovante);
        return true;
    }

    // ---------- Alteração / remoção ----------

    public boolean alterarDadosVeiculo(String placa, String marca, String modelo, int ano, double preco) {
        Veiculo v = buscarPorPlaca(placa);
        if (v == null) {
            return false;
        }
        v.setMarca(marca);
        v.setModelo(modelo);
        v.setAnoFabricacao(ano);
        v.setPreco(preco);
        return true;
    }

    public boolean removerVeiculo(String placa) {
        Veiculo v = buscarPorPlaca(placa);
        return v != null && listaVeiculos.remove(v);
    }

    // ---------- Busca ----------

    public Veiculo buscarPorPlaca(String placa) {
        for (Veiculo v : listaVeiculos) {
            if (v.getPlaca().equalsIgnoreCase(placa)) {
                return v;
            }
        }
        return null;
    }

    public List<Veiculo> buscarPorModelo(String modelo) {
        List<Veiculo> resultado = new ArrayList<>();
        for (Veiculo v : listaVeiculos) {
            if (v.getModelo().toLowerCase().contains(modelo.toLowerCase())) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    // ---------- Listagem / ordenação ----------

    public void listarTodosTabular() {
        System.out.println(CABECALHO_TABELA);
        if (listaVeiculos.isEmpty()) {
            System.out.println("(nenhum veículo cadastrado)");
            return;
        }
        for (Veiculo v : listaVeiculos) {
            System.out.println(v.getLinhaTabela());
        }
    }

    public void ordenarPorPreco() {
        listaVeiculos.sort(new ComparadorPorPreco());
    }

    public void ordenarPorAno() {
        listaVeiculos.sort(new ComparadorPorAno());
    }

    public List<Veiculo> getVeiculos() {
        return Collections.unmodifiableList(listaVeiculos);
    }

    // ---------- Persistência em arquivo ----------

    private void salvarManutencaoEmArquivo(Veiculo v, Manutencao m) {
        try (PrintWriter out = new PrintWriter(new FileWriter(ARQUIVO_MANUTENCOES, true))) {
            out.printf("%d;%s;%s;%d;%.2f%n", m.getId(), v.getPlaca(), m.getDataEntrada(),
                    m.getServicos().size(), m.calcularTotal());
        } catch (IOException e) {
            System.out.println("Erro ao salvar manutenção: " + e.getMessage());
        }
    }

    private void exportarReciboSaidaArquivo(Veiculo v, Manutencao m, String comprovante) {
        String nomeArquivo = "recibo_" + v.getPlaca() + "_" + m.getId() + ".txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(nomeArquivo))) {
            out.println("AutoTech - Recibo de saída");
            out.println("Veículo: " + v.getPlaca() + " - " + v.getMarca() + " " + v.getModelo());
            out.println("Cliente: " + v.getCliente().getNome());
            out.println("Entrada: " + m.getDataEntrada() + " | Saída: " + m.getDataSaida());
            out.println("Serviços:");
            for (Servico s : m.getServicos()) {
                out.printf("  - %s (%s): R$ %.2f%n", s.getTipoServico(), s.getDescricao(), s.getPreco());
            }
            out.printf("Total dos serviços: R$ %.2f%n", m.calcularTotal());
            out.println(comprovante);
            System.out.println("Recibo gerado: " + nomeArquivo);
        } catch (IOException e) {
            System.out.println("Erro ao gerar recibo: " + e.getMessage());
        }
    }
}
