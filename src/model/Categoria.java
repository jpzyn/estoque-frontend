public class Categoria {
    private String nome;
    private Tamanho tamanho;
    private Embalagem embalagem;

    public enum Tamanho {
        PEQUENO,
        MEDIO,
        GRANDE
    }

    public enum Embalagem {
        LATA,
        VIDRO,
        PLASTICO
    }

    public Categoria() {
    }

    public Categoria(String nome, Tamanho tamanho, Embalagem embalagem) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.embalagem = embalagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Tamanho getTamanho() {
        return tamanho;
    }

    public void setTamanho(Tamanho tamanho) {
        this.tamanho = tamanho;
    }

    public Embalagem getEmbalagem() {
        return embalagem;
    }

    public void setEmbalagem(Embalagem embalagem) {
        this.embalagem = embalagem;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "nome='" + nome + '\'' +
                ", tamanho=" + tamanho +
                ", embalagem=" + embalagem +
                '}';
    }
}