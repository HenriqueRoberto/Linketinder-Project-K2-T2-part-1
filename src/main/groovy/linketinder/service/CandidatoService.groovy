package linketinder.service

import linketinder.dao.ICandidatoDAO
import linketinder.model.Candidato

class CandidatoService {

    private final ICandidatoDAO candidatoDAO

    CandidatoService(ICandidatoDAO candidatoDAO) {
        this.candidatoDAO = candidatoDAO
    }

    void cadastrar(Candidato candidato) {
        if (candidatoDAO.existeEmail(candidato.email)) {
            throw new IllegalArgumentException("Erro: O e-mail " + candidato.email + " já está cadastrado.")
        }
        int idGerado = candidatoDAO.inserir(candidato)
        candidato.id = idGerado
    }

    List<Candidato> listar() {
        return candidatoDAO.listar()
    }

    void atualizar(Candidato candidato) {
        candidatoDAO.atualizar(candidato)
    }

    void deletar(int id) {
        candidatoDAO.deletar(id)
    }

    void aplicarEdicao(Candidato candidato, Map<String, String> dados) {
        if (!dados.nome.isEmpty())      candidato.nome      = dados.nome
        if (!dados.cpf.isEmpty())       candidato.cpf       = dados.cpf
        if (!dados.idade.isEmpty())     candidato.idade     = dados.idade.toInteger()
        if (!dados.estado.isEmpty())    candidato.estado    = dados.estado
        if (!dados.cep.isEmpty())       candidato.cep       = dados.cep
        if (!dados.descricao.isEmpty()) candidato.descricao = dados.descricao
        if (!dados.senha.isEmpty())     candidato.senha     = dados.senha
        candidatoDAO.atualizar(candidato)
    }
}