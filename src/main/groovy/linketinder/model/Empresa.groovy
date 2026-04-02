package linketinder.model

class Empresa extends PessoaJuridica {
    int id
    List<Vaga> vagas = []

    Empresa(String nome, String email, String cnpj, String pais, String estado, String cep, String descricao, String senha) {
        super(nome, email, cnpj, pais, estado, cep, descricao, senha)
    }
}