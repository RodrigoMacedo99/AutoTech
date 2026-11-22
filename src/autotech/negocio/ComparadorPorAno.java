package autotech.negocio;

import autotech.entidades.Veiculo;

import java.util.Comparator;

public class ComparadorPorAno implements Comparator<Veiculo> {
    @Override
    public int compare(Veiculo a, Veiculo b) {
        return Integer.compare(a.getAnoFabricacao(), b.getAnoFabricacao());
    }
}
