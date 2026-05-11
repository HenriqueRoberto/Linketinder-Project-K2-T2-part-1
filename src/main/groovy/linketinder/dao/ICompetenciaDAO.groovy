package linketinder.dao

import linketinder.model.Competencia

interface ICompetenciaDAO {
    List<Competencia> listar()
    int buscarOuInserir(String nome)
    void vincularCandidato(int idCandidato, int idCompetencia)
    void desvincularCandidato(int idCandidato, int idCompetencia)
    void vincularVaga(int idVaga, int idCompetencia)
    void desvincularTodasDaVaga(int idVaga)
}