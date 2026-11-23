package autotech.negocio;

import autotech.entidades.Carro;
import autotech.entidades.Cliente;
import autotech.entidades.Manutencao;
import autotech.entidades.Moto;
import autotech.entidades.Servico;
import autotech.entidades.Veiculo;
import autotech.persistencia.ArquivoManutencoes;
import autotech.persistencia.ArquivoRecibos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/** Camada de negócio: cadastros, catálogo de serviços, entrada/saída, busca, alteração e ordenação. */
public class GerenciadorOficina {
    private static final LocalDate VIGENCIA_INICIAL = LocalDate.of(2026, 1, 1);

    private final Map<String, Cliente> clientesPorCpf = new LinkedHashMap<>();
    private final Map<String, Veiculo> veiculosPorPlaca = new LinkedHashMap<>();
    private final List<Servico> catalogo = new ArrayList<>();

    private final ArquivoManutencoes arquivoManutencoes = new ArquivoManutencoes();
    private final ArquivoRecibos arquivoRecibos = new ArquivoRecibos();

    // ---------- Carga inicial ----------

    public void popularDadosIniciais() {
        popularCatalogoInicial();

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

    private void popularCatalogoInicial() {
        catalogo.add(new Servico("Troca de óleo", "Troca de óleo e filtro", 150.0, VIGENCIA_INICIAL));
        catalogo.add(new Servico("Revisão", "Revisão geral", 350.0, VIGENCIA_INICIAL));
        catalogo.add(new Servico("Freio", "Revisão do sistema de freio", 280.0, VIGENCIA_INICIAL));
        catalogo.add(new Servico("Alinhamento", "Alinhamento e balanceamento", 120.0, VIGENCIA_INICIAL));
        catalogo.add(new Servico("Troca de pneus", "Troca de pneus (por unidade)", 400.0, VIGENCIA_INICIAL));
    }

    /** Cria um cliente fictício, registra-o e cadastra o veículo produzido pela fábrica. */
    private void adicionarInicial(int indice, Function<Cliente, Veiculo> fabrica) {
        Cliente c = new Cliente(String.format("000.000.000-%02d", indice),
                "Cliente " + indice,
                String.format("(11) 99999-%04d", indice));
        clientesPorCpf.put(c.getCpf(), c);
        Veiculo v = fabrica.apply(c);
        veiculosPorPlaca.put(chave(v.getPlaca()), v);
        c.adicionarVeiculo(v);
    }

    // ---------- Clientes ----------

    /** @return false se já existe cliente com o mesmo CPF */
    public boolean cadastrarCliente(Cliente c) {
        if (clientesPorCpf.containsKey(c.getCpf())) {
            return false;
        }
        clientesPorCpf.put(c.getCpf(), c);
        return true;
    }

    public List<Cliente> listarClientes() {
        return Collections.unmodifiableList(new ArrayList<>(clientesPorCpf.values()));
    }

    public Cliente buscarClientePorCpf(String cpf) {
        return clientesPorCpf.get(cpf.trim());
    }

    // ---------- Catálogo de serviços ----------

    /** Serviço do catálogo vigente na data: a entrada de tipo igual com a maior vigência não posterior à data. */
    public Optional<Servico> buscarServicoVigente(String tipo, LocalDate data) {
        Servico melhor = null;
        for (Servico s : catalogo) {
            boolean mesmoTipo = s.getTipoServico().equalsIgnoreCase(tipo.trim());
            if (mesmoTipo && !s.getDataVigencia().isAfter(data)
                    && (melhor == null || s.getDataVigencia().isAfter(melhor.getDataVigencia()))) {
                melhor = s;
            }
        }
        return Optional.ofNullable(melhor);
    }

    /**
     * Registra um novo preço para o serviço a partir de uma data de vigência.
     * @return false se já existe entrada do mesmo tipo com a mesma vigência
     */
    public boolean registrarPrecoServico(String tipo, String descricao, double preco, LocalDate vigencia) {
        for (Servico s : catalogo) {
            if (s.getTipoServico().equalsIgnoreCase(tipo.trim()) && s.getDataVigencia().equals(vigencia)) {
                return false;
            }
        }
        catalogo.add(new Servico(tipo.trim(), descricao, preco, vigencia));
        return true;
    }

    /** Um serviço por tipo: a versão vigente na data informada. */
    public List<Servico> listarServicosVigentes(LocalDate data) {
        List<Servico> resultado = new ArrayList<>();
        for (Servico s : catalogo) {
            Servico vigente = buscarServicoVigente(s.getTipoServico(), data).orElse(null);
            if (vigente == s && !resultado.contains(s)) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    public List<Servico> listarCatalogo() {
        return Collections.unmodifiableList(catalogo);
    }

    // ---------- Entrada / saída ----------

    public void registrarEntradaVeiculo(Veiculo v, Manutencao m) {
        Veiculo alvo = veiculosPorPlaca.get(chave(v.getPlaca()));
        if (alvo == null) {
            alvo = v;
            veiculosPorPlaca.put(chave(v.getPlaca()), alvo);
            alvo.getCliente().adicionarVeiculo(alvo);
        }
        alvo.adicionarManutencao(m);
        arquivoManutencoes.salvar(alvo, m);
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
        double valorBase = m.calcularTotal();
        double valorFinal = pag.calcularValorFinal(valorBase);
        m.registrarPagamento(pag.getNome(), valorBase, valorFinal);
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
        if (v == null) {
            return false;
        }
        veiculosPorPlaca.remove(chave(placa));
        v.getCliente().removerVeiculo(v);
        return true;
    }

    // ---------- Busca ----------

    public Veiculo buscarPorPlaca(String placa) {
        return veiculosPorPlaca.get(chave(placa));
    }

    public List<Veiculo> buscarPorModelo(String modelo) {
        List<Veiculo> resultado = new ArrayList<>();
        for (Veiculo v : veiculosPorPlaca.values()) {
            if (v.getModelo().toLowerCase().contains(modelo.trim().toLowerCase())) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    // ---------- Listagem / ordenação (retornam cópias ordenadas) ----------

    public List<Veiculo> listarTodos() {
        return Collections.unmodifiableList(new ArrayList<>(veiculosPorPlaca.values()));
    }

    public List<Veiculo> listarOrdenadoPorPreco() {
        List<Veiculo> lista = new ArrayList<>(veiculosPorPlaca.values());
        lista.sort(new ComparadorPorPreco());
        return lista;
    }

    public List<Veiculo> listarOrdenadoPorAno() {
        List<Veiculo> lista = new ArrayList<>(veiculosPorPlaca.values());
        lista.sort(new ComparadorPorAno());
        return lista;
    }

    private static String chave(String placa) {
        return placa.trim().toUpperCase();
    }
}
