package linketinder.service

import linketinder.dao.ICompetenciaDAO
import linketinder.model.Candidato
import linketinder.model.Competencia

class CompetenciaService {

    private final ICompetenciaDAO competenciaDAO

    CompetenciaService(ICompetenciaDAO competenciaDAO) {
        this.competenciaDAO = competenciaDAO
    }

    void adicionarAoCandidato(Candidato candidato, String nomeCompetencia) {
        int idComp = competenciaDAO.buscarOuInserir(nomeCompetencia)
        competenciaDAO.vincularCandidato(candidato.id, idComp)
        Competencia nova = new Competencia(nomeCompetencia)
        nova.id = idComp
        candidato.competencias.add(nova)
    }

    void editarDoCandidato(Candidato candidato, int indice, String novoNome) {
        Competencia antiga = candidato.competencias[indice]
        competenciaDAO.desvincularCandidato(candidato.id, antiga.id)
        int idNova = competenciaDAO.buscarOuInserir(novoNome)
        competenciaDAO.vincularCandidato(candidato.id, idNova)
        candidato.competencias[indice].nome = novoNome
        candidato.competencias[indice].id   = idNova
    }

    String removerDoCandidato(Candidato candidato, int indice) {
        competenciaDAO.desvincularCandidato(candidato.id, candidato.competencias[indice].id)
        return candidato.competencias.remove(indice).nome
    }
}