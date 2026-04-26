package linketinder.dao

import linketinder.model.Competencia
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CompetenciaDAO implements ICompetenciaDAO {

    @Override
    List<Competencia> listar() {
        List<Competencia> competencias = []
        Connection conn = ConexaoBanco.obterConexao()

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM competencias ORDER BY nome")
        ResultSet rs = stmt.executeQuery()

        while (rs.next()) {
            Competencia c = new Competencia(rs.getString("nome"))
            c.id = rs.getInt("id")
            competencias.add(c)
        }

        rs.close(); stmt.close(); conn.close()
        return competencias
    }

    @Override
    int buscarOuInserir(String nome) {
        Connection conn = ConexaoBanco.obterConexao()
        int id = 0

        PreparedStatement stmtBusca = conn.prepareStatement("SELECT id FROM competencias WHERE LOWER(nome) = LOWER(?)")
        stmtBusca.setString(1, nome)
        ResultSet rs = stmtBusca.executeQuery()

        if (rs.next()) {
            id = rs.getInt("id")
        } else {
            PreparedStatement stmtInsert = conn.prepareStatement(
                    "INSERT INTO competencias (nome) VALUES (?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            )
            stmtInsert.setString(1, nome)
            stmtInsert.executeUpdate()

            ResultSet chaves = stmtInsert.getGeneratedKeys()
            if (chaves.next()) id = chaves.getInt(1)
            chaves.close(); stmtInsert.close()
        }

        rs.close(); stmtBusca.close(); conn.close()
        return id
    }

    @Override
    void vincularCandidato(int idCandidato, int idCompetencia) {
        executarUpdate(
                "INSERT INTO candidato_competencia (id_candidato, id_competencia) VALUES (?, ?) ON CONFLICT DO NOTHING",
                idCandidato, idCompetencia
        )
    }

    @Override
    void desvincularCandidato(int idCandidato, int idCompetencia) {
        executarUpdate(
                "DELETE FROM candidato_competencia WHERE id_candidato = ? AND id_competencia = ?",
                idCandidato, idCompetencia
        )
    }

    @Override
    void desvincularTodasDoCandidato(int idCandidato) {
        executarUpdateSimples("DELETE FROM candidato_competencia WHERE id_candidato = ?", idCandidato)
    }

    @Override
    void vincularVaga(int idVaga, int idCompetencia) {
        executarUpdate(
                "INSERT INTO vaga_competencia (id_vaga, id_competencia) VALUES (?, ?) ON CONFLICT DO NOTHING",
                idVaga, idCompetencia
        )
    }

    @Override
    void desvincularVaga(int idVaga, int idCompetencia) {
        executarUpdate(
                "DELETE FROM vaga_competencia WHERE id_vaga = ? AND id_competencia = ?",
                idVaga, idCompetencia
        )
    }

    @Override
    void desvincularTodasDaVaga(int idVaga) {
        executarUpdateSimples("DELETE FROM vaga_competencia WHERE id_vaga = ?", idVaga)
    }

    private static void executarUpdate(String sql, int param1, int param2) {
        Connection conn = ConexaoBanco.obterConexao()
        PreparedStatement stmt = conn.prepareStatement(sql)
        stmt.setInt(1, param1)
        stmt.setInt(2, param2)
        stmt.executeUpdate()
        stmt.close(); conn.close()
    }

    private static void executarUpdateSimples(String sql, int param) {
        Connection conn = ConexaoBanco.obterConexao()
        PreparedStatement stmt = conn.prepareStatement(sql)
        stmt.setInt(1, param)
        stmt.executeUpdate()
        stmt.close(); conn.close()
    }
}