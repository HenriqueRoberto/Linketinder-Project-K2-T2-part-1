package linketinder.dao

import linketinder.model.Candidato
import linketinder.model.Competencia
import java.sql.Connection

interface ICandidatoDAO {
    List<Candidato> listar()
    int inserir(Candidato candidato)
    void atualizar(Candidato candidato)
    void deletar(int id)
    boolean existeEmail(String email)
    List<Competencia> buscarCompetenciasDoCandidato(Connection conn, int idCandidato)
}