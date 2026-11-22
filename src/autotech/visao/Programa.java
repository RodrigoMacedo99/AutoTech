package autotech.visao;

import autotech.entidades.Carro;
import autotech.entidades.Cliente;
import autotech.entidades.Manutencao;
import autotech.entidades.Moto;
import autotech.entidades.Servico;
import autotech.entidades.Veiculo;
import autotech.negocio.FormaPagamento;
import autotech.negocio.GerenciadorOficina;
import autotech.negocio.PagamentoCartao;
import autotech.negocio.PagamentoDinheiro;
import autotech.negocio.PagamentoPix;

import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/** Camada de visão: menu interativo no console. Não contém regras de negócio. */
public class Programa {
    private static final Scanner in = new Scanner(System.in);
    private static final GerenciadorOficina gerenciador = new GerenciadorOficina();

    public static void main(String[] args) {
        gerenciador.popularDadosIniciais();
        int opcao;
        do {
            exibirMenu();
            opcao = lerInt("Escolha uma opção: ");
            try {
                executar(opcao);
            } catch (UncheckedIOException e) {
                System.out.println("Erro de persistência: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private static void executar(int opcao) {
        switch (opcao) {
            case 1 -> cadastrarNovoCliente();
            case 2 -> incluirVeiculoEManutencao();
            case 3 -> alterarVeiculo();
            case 4 -> excluirVeiculo();
            case 5 -> realizarSaidaEPagamento();
            case 6 -> buscarVeiculo();
            case 7 -> listarEOrdenar();
            case 0 -> System.out.println("Encerrando o AutoTech.");
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void exibirMenu() {
        System.out.println("\n===== AutoTech =====");
        System.out.println("1 - Cadastrar novo cliente");
        System.out.println("2 - Incluir veículo e manutenção");
        System.out.println("3 - Alterar dados do veículo");
        System.out.println("4 - Excluir veículo");
        System.out.println("5 - Realizar saída e pagamento");
        System.out.println("6 - Buscar veículo");
        System.out.println("7 - Listar e ordenar veículos");
        System.out.println("0 - Sair");
    }

    private static void cadastrarNovoCliente() {
        String cpf = lerTexto("CPF: ");
        if (gerenciador.buscarClientePorCpf(cpf) != null) {
            System.out.println("Já existe cliente com este CPF.");
            return;
        }
        String nome = lerTexto("Nome: ");
        String telefone = lerTexto("Telefone: ");
        gerenciador.cadastrarCliente(new Cliente(cpf, nome, telefone));
        System.out.println("Cliente cadastrado.");
    }

    private static void incluirVeiculoEManutencao() {
        String placa = lerTexto("Placa: ");
        Veiculo veiculo = gerenciador.buscarPorPlaca(placa);

        if (veiculo == null) {
            int tipo = lerInt("Tipo (1 - Carro, 2 - Moto): ");
            String marca = lerTexto("Marca: ");
            String modelo = lerTexto("Modelo: ");
            int ano = lerInt("Ano de fabricação: ");
            double preco = lerDouble("Preço: ");
            String combustivel = lerTexto("Tipo de combustível: ");
            int cv = lerInt("Potência (CV): ");
            String cpf = lerTexto("CPF do cliente: ");
            Cliente cliente = gerenciador.buscarClientePorCpf(cpf);
            if (cliente == null) {
                System.out.println("Cliente não encontrado. Cadastre o cliente primeiro.");
                return;
            }
            if (tipo == 1) {
                int portas = lerInt("Número de portas: ");
                veiculo = new Carro(placa, marca, modelo, ano, preco, combustivel, cv, cliente, portas);
            } else if (tipo == 2) {
                int cilindradas = lerInt("Cilindradas: ");
                veiculo = new Moto(placa, marca, modelo, ano, preco, combustivel, cv, cliente, cilindradas);
            } else {
                System.out.println("Tipo inválido.");
                return;
            }
        } else {
            System.out.println("Veículo já cadastrado: " + veiculo.getMarca() + " " + veiculo.getModelo());
        }

        Manutencao manutencao = new Manutencao(LocalDate.now());
        System.out.println("Informe os serviços (deixe o tipo em branco para encerrar):");
        while (true) {
            String tipoServico = lerTexto("  Tipo do serviço: ");
            if (tipoServico.isBlank()) {
                break;
            }
            String descricao = lerTexto("  Descrição: ");
            manutencao.adicionarServico(new Servico(tipoServico, descricao));
        }
        if (manutencao.getServicos().isEmpty()) {
            System.out.println("A manutenção precisa de pelo menos um serviço. Operação cancelada.");
            return;
        }

        gerenciador.registrarEntradaVeiculo(veiculo, manutencao);
        System.out.printf("Entrada registrada. Manutenção nº %d, total estimado R$ %.2f%n",
                manutencao.getId(), manutencao.calcularTotal());
    }

    private static void alterarVeiculo() {
        String placa = lerTexto("Placa do veículo: ");
        String marca = lerTexto("Nova marca: ");
        String modelo = lerTexto("Novo modelo: ");
        int ano = lerInt("Novo ano: ");
        double preco = lerDouble("Novo preço: ");
        boolean ok = gerenciador.alterarDadosVeiculo(placa, marca, modelo, ano, preco);
        System.out.println(ok ? "Dados alterados." : "Veículo não encontrado.");
    }

    private static void excluirVeiculo() {
        String placa = lerTexto("Placa do veículo: ");
        boolean ok = gerenciador.removerVeiculo(placa);
        System.out.println(ok ? "Veículo removido." : "Veículo não encontrado.");
    }

    private static void realizarSaidaEPagamento() {
        String placa = lerTexto("Placa do veículo: ");
        System.out.println("Forma de pagamento: 1 - PIX (5% de desconto) | 2 - Cartão (3% de acréscimo) | 3 - Dinheiro (10% de desconto)");
        int forma = lerInt("Escolha: ");
        FormaPagamento pag = switch (forma) {
            case 1 -> new PagamentoPix();
            case 2 -> new PagamentoCartao();
            case 3 -> new PagamentoDinheiro();
            default -> null;
        };
        if (pag == null) {
            System.out.println("Forma de pagamento inválida.");
            return;
        }
        Optional<String> recibo = gerenciador.registrarSaidaVeiculo(placa, pag);
        if (recibo.isPresent()) {
            System.out.println("Saída registrada. Recibo gerado: " + recibo.get());
        } else {
            System.out.println("Veículo não encontrado ou sem manutenção em aberto.");
        }
    }

    private static void buscarVeiculo() {
        System.out.println("1 - Buscar por placa | 2 - Buscar por modelo");
        int opcao = lerInt("Escolha: ");
        if (opcao == 1) {
            Veiculo v = gerenciador.buscarPorPlaca(lerTexto("Placa: "));
            if (v == null) {
                System.out.println("Veículo não encontrado.");
            } else {
                FormatadorVeiculo.imprimirDetalhado(v);
            }
        } else if (opcao == 2) {
            List<Veiculo> encontrados = gerenciador.buscarPorModelo(lerTexto("Modelo: "));
            FormatadorVeiculo.imprimirTabela(encontrados);
        } else {
            System.out.println("Opção inválida.");
        }
    }

    private static void listarEOrdenar() {
        System.out.println("1 - Listar | 2 - Ordenar por preço | 3 - Ordenar por ano");
        int opcao = lerInt("Escolha: ");
        switch (opcao) {
            case 1 -> FormatadorVeiculo.imprimirTabela(gerenciador.getVeiculos());
            case 2 -> {
                gerenciador.ordenarPorPreco();
                FormatadorVeiculo.imprimirTabela(gerenciador.getVeiculos());
            }
            case 3 -> {
                gerenciador.ordenarPorAno();
                FormatadorVeiculo.imprimirTabela(gerenciador.getVeiculos());
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    // ---------- Utilitários de leitura ----------

    private static String lerTexto(String rotulo) {
        System.out.print(rotulo);
        return in.nextLine().trim();
    }

    private static int lerInt(String rotulo) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(rotulo));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro.");
            }
        }
    }

    private static double lerDouble(String rotulo) {
        while (true) {
            try {
                return Double.parseDouble(lerTexto(rotulo).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }
}
