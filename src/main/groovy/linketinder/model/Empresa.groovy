package linketinder.model

class Empresa extends PessoaJuridica {
    List<Vaga> vagas = []

    Empresa(String nome, String email, String cnpj, String pais, String estado, String cep, String descricao, String senha) {
        super(nome, email, cnpj, pais, estado, cep, descricao, senha)
    }

    static class Builder {
        private String nome
        private String email
        private String cnpj
        private String pais
        private String estado
        private String cep
        private String descricao
        private String senha

        Builder nome(String nome)           { this.nome = nome;           return this }
        Builder email(String email)         { this.email = email;         return this }
        Builder cnpj(String cnpj)           { this.cnpj = cnpj;           return this }
        Builder pais(String pais)           { this.pais = pais;           return this }
        Builder estado(String estado)       { this.estado = estado;       return this }
        Builder cep(String cep)             { this.cep = cep;             return this }
        Builder descricao(String descricao) { this.descricao = descricao; return this }
        Builder senha(String senha)         { this.senha = senha;         return this }

        Empresa build() {
            return new Empresa(nome, email, cnpj, pais, estado, cep, descricao, senha)
        }
    }
}