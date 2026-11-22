package autotech.entidades;

public class Motor {
    private String tipoCombustivel;
    private int potenciaCV;

    public Motor(String tipoCombustivel, int potenciaCV) {
        this.tipoCombustivel = tipoCombustivel;
        this.potenciaCV = potenciaCV;
    }

    public String getTipoCombustivel() {
        return tipoCombustivel;
    }

    public int getPotenciaCV() {
        return potenciaCV;
    }

    public void setTipoCombustivel(String tipoCombustivel) {
        this.tipoCombustivel = tipoCombustivel;
    }

    public void setPotenciaCV(int potenciaCV) {
        this.potenciaCV = potenciaCV;
    }
}
