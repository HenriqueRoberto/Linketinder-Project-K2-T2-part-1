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