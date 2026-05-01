package linketinder.model

class Vaga {

    int id
    String nome
    String descricao
    String horario
    String localizacao
    String remuneracao
    List<Competencia> competencias
    int idEmpresa

    Vaga(String nome, String descricao, String horario, String localizacao, String remuneracao, List<Competencia> competencias, int idEmpresa) {
        this.nome = nome
        this.descricao = descricao
        this.horario = horario
        this.localizacao = localizacao
        this.remuneracao = remuneracao
        this.competencias = competencias
        this.idEmpresa = idEmpresa
    }

    static class Builder {
        private String nome
        private String descricao
        private String horario
        private String localizacao
        private String remuneracao
        private List<Competencia> competencias = []
        private int idEmpresa

        Builder nome(String nome)               { this.nome = nome;               return this }
        Builder descricao(String descricao)     { this.descricao = descricao;     return this }
        Builder horario(String horario)         { this.horario = horario;         return this }
        Builder localizacao(String localizacao) { this.localizacao = localizacao; return this }
        Builder remuneracao(String remuneracao) { this.remuneracao = remuneracao; return this }
        Builder competencias(List<Competencia> c) { this.competencias = c;        return this }
        Builder idEmpresa(int idEmpresa)        { this.idEmpresa = idEmpresa;     return this }

        Vaga build() {
            return new Vaga(nome, descricao, horario, localizacao, remuneracao, competencias, idEmpresa)
        }
    }

    @Override
    String toString() {
        String compTexto = (competencias == null || competencias.isEmpty()) ?
                "sem competências cadastradas" : competencias.collect { it.nome }.join(", ")

        return "Nome: " + nome +
                "\nDescrição: " + descricao +
                "\nHorário: " + horario +
                "\nLocalização: " + localizacao +
                "\nRemuneração: " + remuneracao +
                "\nCompetências: " + compTexto
    }
}