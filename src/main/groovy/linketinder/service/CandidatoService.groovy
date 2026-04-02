package linketinder.service

import linketinder.dao.CandidatoDAO
import linketinder.model.Candidato

class CandidatoService {

    static void cadastrar(Candidato candidato) {
        boolean emailExiste = CandidatoDAO.listar().any { it.email.equalsIgnoreCase(candidato.email) }

        if (emailExiste) {
            throw new IllegalArgumentException("Erro: O e-mail " + candidato.email + " já está cadastrado.")
        }

        int idGerado = CandidatoDAO.inserir(candidato)
        candidato.id = idGerado
    }

    static List<Candidato> listar() {
        return CandidatoDAO.listar()
    }

    static void atualizar(Candidato candidato) {
        CandidatoDAO.atualizar(candidato)
    }

    static void deletar(int id) {
        CandidatoDAO.deletar(id)
    }
}