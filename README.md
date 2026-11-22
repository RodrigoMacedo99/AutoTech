# AutoTech

Sistema de linha de comando para gerenciamento de uma oficina mecânica. Permite cadastrar clientes, registrar a entrada de veículos (carros e motos) com suas manutenções, calcular o valor de saída com diferentes formas de pagamento, buscar, alterar, remover e ordenar veículos.

O projeto foi modelado a partir de um diagrama de classes UML (PlantUML) e implementado em Java, organizado em camadas.

## Requisitos

- Java 21 ou superior (JDK, para compilar)
- Não há dependências externas

## Estrutura do projeto

```
AutoTech/
├── README.md
└── src/
    └── autotech/
        ├── entidades/        # Modelo de domínio (sem I/O)
        │   ├── Cliente.java
        │   ├── Motor.java
        │   ├── Servico.java
        │   ├── Manutencao.java
        │   ├── Veiculo.java      # Classe abstrata base dos veículos
        │   ├── Carro.java
        │   └── Moto.java
        ├── negocio/          # Regras de negócio e casos de uso
        │   ├── GerenciadorOficina.java
        │   ├── FormaPagamento.java   # Interface (estratégia de pagamento)
        │   ├── PagamentoPix.java
        │   ├── PagamentoCartao.java
        │   ├── PagamentoDinheiro.java
        │   ├── ComparadorPorPreco.java
        │   └── ComparadorPorAno.java
        ├── persistencia/     # Gravação em arquivos
        │   ├── ArquivoManutencoes.java
        │   └── ArquivoRecibos.java
        └── visao/            # Interface de console
            ├── Programa.java         # Ponto de entrada (menu interativo)
            └── FormatadorVeiculo.java
```

Dependências entre camadas: `visao` → `negocio` → `persistencia` → `entidades`. As entidades não dependem de nenhuma outra camada.

## Como compilar e executar

Na raiz do projeto:

```bash
javac -d out $(find src -name '*.java')
java -cp out autotech.visao.Programa
```

Ao iniciar, o sistema carrega automaticamente **14 veículos** de exemplo (7 carros e 7 motos), cada um com um cliente fictício.

## Uso

O programa exibe o seguinte menu:

| Opção | Funcionalidade |
|-------|----------------|
| 1 | Cadastrar novo cliente (CPF, nome, telefone). CPFs duplicados são recusados. |
| 2 | Incluir veículo e manutenção. Se a placa já existir, o veículo existente é usado e apenas uma nova manutenção é registrada. |
| 3 | Alterar dados do veículo (marca, modelo, ano e preço), identificado pela placa. |
| 4 | Excluir veículo, identificado pela placa. |
| 5 | Realizar saída e pagamento: calcula o total das manutenções em aberto, aplica a forma de pagamento e gera o recibo. |
| 6 | Buscar veículo por placa ou por modelo (busca parcial, sem diferenciar maiúsculas de minúsculas). |
| 7 | Listar veículos em tabela, ou listar ordenados por preço ou por ano. |
| 0 | Sair. |

### Fluxo típico

1. **Entrada:** opção `2` com a placa do veículo. Informe os serviços (tipo e descrição) até deixar o tipo em branco. A manutenção precisa de pelo menos um serviço.
2. **Saída:** opção `5` com a placa e a forma de pagamento. O sistema marca a data de saída e gera um recibo.

## Regras de negócio

### Preço dos serviços

| Tipo de serviço | Preço (R$) |
|-----------------|-----------:|
| Troca de óleo | 150,00 |
| Revisão | 350,00 |
| Freio | 280,00 |
| Alinhamento | 120,00 |
| Troca de pneus | 400,00 |
| Qualquer outro tipo | 200,00 |

O total de uma manutenção é a soma dos preços dos seus serviços.

### Formas de pagamento

| Forma | Ajuste sobre o total |
|-------|----------------------|
| PIX | 5% de desconto |
| Cartão | 3% de acréscimo |
| Dinheiro | 10% de desconto |

## Arquivos gerados

Os arquivos são criados no diretório de execução:

- **`manutencoes.txt`**: uma linha por manutenção registrada no formato `id;placa;dataEntrada;quantidadeServicos;total`. O arquivo é acumulado entre execuções.
- **`recibo_<placa>_<id>.txt`**: recibo emitido na saída, com dados do veículo, cliente, datas, serviços, total e comprovante da forma de pagamento.

## Arquitetura

### Entidades (`entidades`)

- **`Veiculo`** é abstrata. `Carro` (com número de portas) e `Moto` (com cilindradas) são as especializações. Cada veículo possui um `Motor` próprio e um `Cliente` associado, e mantém o histórico de `Manutencao`.
- **`Manutencao`** agrega `Servico`s e controla as datas de entrada e saída. A manutenção "em aberta" é aquela sem data de saída.
- **`Servico`** define seu preço pelo tipo.

### Negócio (`negocio`)

- **`GerenciadorOficina`** concentra as operações sobre veículos e clientes (cadastro, entrada, saída, busca, alteração, remoção e ordenação). É o único ponto que a visão chama.
- **`FormaPagamento`** é a estratégia de cálculo do valor final e de geração de comprovante. As três implementações são intercambiáveis.
- **`ComparadorPorPreco`** e **`ComparadorPorAno`** são usados para ordenação.

### Persistência (`persistencia`)

- **`ArquivoManutencoes`** grava o histórico de manutenções em `manutencoes.txt`.
- **`ArquivoRecibos`** gera o recibo de saída em arquivo de texto.

Erros de escrita são lançados como `UncheckedIOException` e tratados pela visão, que os exibe ao usuário.

### Visão (`visao`)

- **`Programa`** contém o menu e a leitura da entrada. Não contém regras de negócio.
- **`FormatadorVeiculo`** monta as linhas de tabela e a exibição detalhada dos veículos.

## Observações

- O projeto não possui testes automatizados.
- Não há persistência dos clientes e veículos entre execuções: a carga inicial é recriada a cada início. Apenas o histórico de manutenções é gravado em arquivo.
- Não há validação de formato para placa, CPF ou telefone.
