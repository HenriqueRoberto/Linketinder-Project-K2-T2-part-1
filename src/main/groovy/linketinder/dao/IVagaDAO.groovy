package linketinder.dao

import linketinder.model.Competencia
import linketinder.model.Vaga
import java.sql.Connection

interface IVagaDAO {
    List<Vaga> listarPorEmpresa(int idEmpresa)
    List<Vaga> listarTodas()
    int inserir(Vaga vaga)
    void atualizar(Vaga vaga)
    void deletar(int id)
    List<Competencia> buscarCompetenciasDaVaga(Connection conn, int idVaga)
}