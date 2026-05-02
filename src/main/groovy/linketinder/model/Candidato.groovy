package linketinder.model

class Candidato extends PessoaFisica {

    Candidato(String nome, String email, String cpf, int idade, String estado, String cep, String descricao, List<Competencia> competencias, String senha) {
        super(nome, email, cpf, idade, estado, cep, descricao, competencias, senha)
    }

    static class Builder {
        private String nome
        private String email
        private String cpf
        private int idade
        private String estado
        private String cep
        private String descricao
        private List<Competencia> competencias = []
        private String senha

        Builder nome(String nome)                  { this.nome = nome;               return this }
        Builder email(String email)                { this.email = email;             return this }
        Builder cpf(String cpf)                    { this.cpf = cpf;                 return this }
        Builder idade(int idade)                   { this.idade = idade;             return this }
        Builder estado(String estado)              { this.estado = estado;           return this }
        Builder cep(String cep)                    { this.cep = cep;                 return this }
        Builder descricao(String descricao)        { this.descricao = descricao;     return this }
        Builder competencias(List<Competencia> c)  { this.competencias = c;          return this }
        Builder senha(String senha)                { this.senha = senha;             return this }

        Candidato build() {
            return new Candidato(nome, email, cpf, idade, estado, cep, descricao, competencias, senha)
        }
    }
}