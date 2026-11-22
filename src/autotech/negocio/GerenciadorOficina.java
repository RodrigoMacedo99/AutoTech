package autotech.negocio;

import autotech.entidades.Carro;
import autotech.entidades.Cliente;
import autotech.entidades.Manutencao;
import autotech.entidades.Moto;
import autotech.entidades.Veiculo;
import autotech.persistencia.ArquivoManutencoes;
import autotech.persistencia.ArquivoRecibos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/** Camada de negócio: regras de entrada, saída, busca, alteração e ordenação. */
public class GerenciadorOficina {
    private final List<Veiculo> listaVeiculos = new ArrayList<>();
    private final List<Cliente> listaClientes = new ArrayList<>();

    private final ArquivoManutencoes arquivoManutencoes = new ArquivoManutencoes();
    private final ArquivoRecibos arquivoRecibos = new ArquivoRecibos();

    // ---------- Carga inicial ----------

    public void popularDadosIniciais() {
        // 7 carros
        adicionarInicial(1, c -> new Carro("CAR0001", "Fiat", "Argo", 2022, 85000, "Flex", 107, c, 4));
        adicionarInicial(2, c -> new Carro("CAR0002", "Volkswagen", "Polo", 2023, 98000, "Flex", 128, c, 4));
        adicionarInicial(3, c -> new Carro("CAR0003", "Chevrolet", "Onix", 2021, 79000, "Flex", 116, c, 4));
        adicionarInicial(4, c -> new Carro("CAR0004", "Toyota", "Corolla", 2024, 159000, "Híbrido", 177, c, 4));
        adicionarInicial(5, c -> new Carro("CAR0005", "Honda", "Civic", 2020, 135000, "Flex", 155, c, 4));
        adicionarInicial(6, c -> new Carro("CAR0006", "Hyundai", "HB20", 2022, 82000, "Flex", 120, c, 4));
        adicionarInicial(7, c -> new Carro("CAR0007", "Ford", "Ka", 2019, 52000, "Flex", 85, c, 4));

        // 7 motos
        adicionarInicial(8, c -> new Moto("MOT0001", "Honda", "CG 160", 2023, 16500, "Gasolina", 16, c, 160));
        adicionarInicial(9, c -> new Moto("MOT0002", "Yamaha", "Fazer 250", 2022, 22000, "Gasolina", 21, c, 250));
        adicionarInicial(10, c -> new Moto("MOT0003", "Honda", "Biz 125", 2021, 11500, "Gasolina", 11, c, 125));
        adicionarInicial(11, c -> new Moto("MOT0004", "Yamaha", "XTZ 150", 2024, 19800, "Gasolina", 16, c, 150));
        adicionarInicial(12, c -> new Moto("MOT0005", "BMW", "G 310 R", 2022, 36900, "Gasolina", 34, c, 313));
        adicionarInicial(13, c -> new Moto("MOT0006", "Kawasaki", "Ninja 400", 2023, 45900, "Gasolina", 48, c, 399));
        adicionarInicial(14, c -> new Moto("MOT0007", "Shineray", "Jet 50", 2020, 6900, "Gasolina", 4, c, 50));
    }

    /** Cria um cliente fictício, registra-o e adiciona o veículo produzido pela fábrica. */
    private void adicionarInicial(int indice, Function<Cliente, Veiculo> fabrica) {
        Cliente c = new Cliente(String.format("000.000.000-%02d", indice),
                "Cliente " + indice,
                String.format("(11) 99999-%04d", indice));
        listaClientes.add(c);
        listaVeiculos.add(fabrica.apply(c));
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
        arquivoManutencoes.salvar(v, m);
    }

    /**
     * Fecha a manutenção em aberto do veículo, aplica a forma de pagamento e gera o recibo.
     * @return nome do recibo gerado, ou vazio se o veículo não existe ou não tem manutenção em aberto
     */
    public Optional<String> registrarSaidaVeiculo(String placa, FormaPagamento pag) {
        Veiculo v = buscarPorPlaca(placa);
        if (v == null) {
            return Optional.empty();
        }
        Manutencao m = v.getManutencaoAtiva();
        if (m == null) {
            return Optional.empty();
        }
        m.setDataSaida(LocalDate.now());
        double valorFinal = pag.calcularValorFinal(m.calcularTotal());
        String comprovante = pag.gerarComprovante(valorFinal);
        return Optional.of(arquivoRecibos.gerar(v, m, comprovante));
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

    // ---------- Ordenação / leitura ----------

    public void ordenarPorPreco() {
        listaVeiculos.sort(new ComparadorPorPreco());
    }

    public void ordenarPorAno() {
        listaVeiculos.sort(new ComparadorPorAno());
    }

    public List<Veiculo> getVeiculos() {
        return Collections.unmodifiableList(listaVeiculos);
    }
}
