package linketinder.dao

import linketinder.model.Competencia

interface ICompetenciaDAO {
    List<Competencia> listar()
    int buscarOuInserir(String nome)
    void vincularCandidato(int idCandidato, int idCompetencia)
    void desvincularCandidato(int idCandidato, int idCompetencia)
    void desvincularTodasDoCandidato(int idCandidato)
    void vincularVaga(int idVaga, int idCompetencia)
    void desvincularVaga(int idVaga, int idCompetencia)
    void desvincularTodasDaVaga(int idVaga)
}