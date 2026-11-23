# Diagrama de classes — AutoTech

Modelo atual do sistema, organizado em camadas (`entidades`, `negocio`, `persistencia`, `visao`).

```mermaid
classDiagram
    direction TB

    %% ---------- Negócio ----------

    class GerenciadorOficina {
        -Map~String, Cliente~ clientesPorCpf
        -Map~String, Veiculo~ veiculosPorPlaca
        -List~Servico~ catalogo
        +popularDadosIniciais() void
        +cadastrarCliente(Cliente c) boolean
        +buscarClientePorCpf(String cpf) Cliente
        +buscarServicoVigente(String tipo, LocalDate data) Optional~Servico~
        +registrarPrecoServico(String tipo, String descricao, double preco, LocalDate vigencia) boolean
        +registrarEntradaVeiculo(Veiculo v, Manutencao m) void
        +registrarSaidaVeiculo(String placa, FormaPagamento pag) Optional~String~
        +alterarDadosVeiculo(String placa, String marca, String modelo, int ano, double preco) boolean
        +removerVeiculo(String placa) boolean
        +buscarPorPlaca(String placa) Veiculo
        +buscarPorModelo(String modelo) List~Veiculo~
        +listarOrdenadoPorPreco() List~Veiculo~
        +listarOrdenadoPorAno() List~Veiculo~
        +listarTodos() List~Veiculo~
        +listarClientes() List~Cliente~
        +listarServicosVigentes(LocalDate data) List~Servico~
        +listarCatalogo() List~Servico~
    }

    class FormaPagamento {
        <<interface>>
        +getNome() String
        +calcularValorFinal(double valorBase) double
        +gerarComprovante(double valorFinal) String
    }

    class PagamentoPix {
        +getNome() String
        +calcularValorFinal(double valorBase) double
        +gerarComprovante(double valorFinal) String
    }

    class PagamentoCartao {
        +getNome() String
        +calcularValorFinal(double valorBase) double
        +gerarComprovante(double valorFinal) String
    }

    class PagamentoDinheiro {
        +getNome() String
        +calcularValorFinal(double valorBase) double
        +gerarComprovante(double valorFinal) String
    }

    class Comparator~T~ {
        <<interface>>
        +compare(T a, T b) int
    }

    class ComparadorPorPreco {
        +compare(Veiculo a, Veiculo b) int
    }

    class ComparadorPorAno {
        +compare(Veiculo a, Veiculo b) int
    }

    %% ---------- Entidades ----------

    class Cliente {
        -String cpf
        -String nome
        -String telefone
        -List~Veiculo~ veiculos
        +adicionarVeiculo(Veiculo v) void
        +removerVeiculo(Veiculo v) void
    }

    class Veiculo {
        <<abstract>>
        -String placa
        -String marca
        -String modelo
        -int anoFabricacao
        -double preco
        -Motor motor
        -Cliente cliente
        -List~Manutencao~ historicoManutencoes
        +adicionarManutencao(Manutencao m) void
        +getManutencaoAtiva() Manutencao
        +getTipoVeiculo()* String
        +acelerar()* String
        +frear()* String
    }

    class Carro {
        -int numeroPortas
    }

    class Moto {
        -int cilindradas
    }

    class Motor {
        -String tipoCombustivel
        -int potenciaCV
    }

    class Manutencao {
        -int id
        -LocalDate dataEntrada
        -LocalDate dataSaida
        -List~Servico~ servicos
        -String formaPagamento
        -double valorBase
        -double valorPago
        +adicionarServico(Servico s) void
        +calcularTotal() double
        +registrarPagamento(String formaPagamento, double valorBase, double valorPago) void
        +getAjuste() double
    }

    class Servico {
        -String tipoServico
        -String descricao
        -double precoTabelado
        -LocalDate dataVigencia
    }

    %% ---------- Persistência e visão ----------

    class ArquivoManutencoes {
        +salvar(Veiculo v, Manutencao m) void
    }

    class ArquivoRecibos {
        +gerar(Veiculo v, Manutencao m, String comprovante) String
    }

    class FormatadorVeiculo {
        <<utility>>
        +linhaTabela(Veiculo v) String
        +imprimirDetalhado(Veiculo v) void
        +imprimirTabela(Iterable~Veiculo~ veiculos) void
    }

    class Programa {
        +main(String[] args) void
    }

    %% ---------- Relações: entidades ----------

    Cliente "1" --> "*" Veiculo : possui
    Veiculo "*" --> "1" Cliente : pertence a
    Veiculo "1" *-- "1" Motor : composição
    Veiculo "1" *-- "*" Manutencao : composição
    Manutencao "*" -- "*" Servico : inclui
    Veiculo <|-- Carro
    Veiculo <|-- Moto

    %% ---------- Relações: negócio ----------

    FormaPagamento <|.. PagamentoPix
    FormaPagamento <|.. PagamentoCartao
    FormaPagamento <|.. PagamentoDinheiro
    Comparator <|.. ComparadorPorPreco
    Comparator <|.. ComparadorPorAno

    GerenciadorOficina "1" o-- "*" Cliente : agrega
    GerenciadorOficina "1" o-- "*" Veiculo : agrega
    GerenciadorOficina "1" o-- "*" Servico : catálogo
    GerenciadorOficina ..> FormaPagamento : dependência
    GerenciadorOficina ..> ComparadorPorPreco : usa
    GerenciadorOficina ..> ComparadorPorAno : usa
    GerenciadorOficina ..> ArquivoManutencoes : usa
    GerenciadorOficina ..> ArquivoRecibos : usa

    %% ---------- Relações: visão ----------

    Programa ..> GerenciadorOficina : usa
    Programa ..> FormatadorVeiculo : usa
```

## Notas

- `GerenciadorOficina` indexa clientes por CPF e veículos por placa (chave normalizada: sem espaços, em maiúsculas). O catálogo é uma lista de versões de `Servico`: alterar um preço cria uma nova entrada com nova `dataVigencia`, e a entrada vigente em uma data é a de maior vigência não posterior a essa data.
- `Manutencao` referencia itens do catálogo (associação, não composição): um mesmo `Servico` pode estar em várias manutenções. Por isso `Servico` é imutável.
- A relação `Cliente`–`Veiculo` é mantida nos dois sentidos pelo gerenciador, ao registrar a entrada de um veículo novo e ao removê-lo.
- Na saída, `registrarSaidaVeiculo` registra na manutenção a forma de pagamento, o valor base e o valor pago. `getAjuste()` é o desconto (negativo) ou os juros/acréscimo (positivo).
- O diagrama mostra apenas os membros relevantes; os getters e setters foram omitidos.
