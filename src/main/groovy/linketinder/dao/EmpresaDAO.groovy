package linketinder.dao

import linketinder.model.Empresa
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class EmpresaDAO {

    static List<Empresa> listar() {
        List<Empresa> empresas = []
        Connection conn = ConexaoBanco.obterConexao()

        String sql = "SELECT * FROM empresas ORDER BY id"
        PreparedStatement stmt = conn.prepareStatement(sql)
        ResultSet rs = stmt.executeQuery()

        while (rs.next()) {
            Empresa e = new Empresa(
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("cnpj"),
                    rs.getString("pais"),
                    rs.getString("estado"),
                    rs.getString("cep"),
                    rs.getString("descricao"),
                    rs.getString("senha")
            )
            e.id = rs.getInt("id")
            empresas.add(e)
        }

        rs.close()
        stmt.close()
        conn.close()
        return empresas
    }

    static int inserir(Empresa empresa) {
        Connection conn = ConexaoBanco.obterConexao()

        String sql = """
            INSERT INTO empresas (nome, email, cnpj, pais, estado, cep, descricao, senha)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """
        PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        stmt.setString(1, empresa.nome)
        stmt.setString(2, empresa.email)
        stmt.setString(3, empresa.cnpj)
        stmt.setString(4, empresa.pais)
        stmt.setString(5, empresa.estado)
        stmt.setString(6, empresa.cep)
        stmt.setString(7, empresa.descricao)
        stmt.setString(8, empresa.senha)
        stmt.executeUpdate()

        ResultSet chaves = stmt.getGeneratedKeys()
        int idGerado = 0
        if (chaves.next()) {
            idGerado = chaves.getInt(1)
        }

        chaves.close()
        stmt.close()
        conn.close()
        return idGerado
    }

    static void atualizar(Empresa empresa) {
        Connection conn = ConexaoBanco.obterConexao()

        String sql = """
            UPDATE empresas
            SET nome=?, email=?, cnpj=?, pais=?, estado=?, cep=?, descricao=?, senha=?
            WHERE id=?
        """
        PreparedStatement stmt = conn.prepareStatement(sql)
        stmt.setString(1, empresa.nome)
        stmt.setString(2, empresa.email)
        stmt.setString(3, empresa.cnpj)
        stmt.setString(4, empresa.pais)
        stmt.setString(5, empresa.estado)
        stmt.setString(6, empresa.cep)
        stmt.setString(7, empresa.descricao)
        stmt.setString(8, empresa.senha)
        stmt.setInt   (9, empresa.id)
        stmt.executeUpdate()

        stmt.close()
        conn.close()
    }

    static void deletar(int id) {
        Connection conn = ConexaoBanco.obterConexao()

        String sql = "DELETE FROM empresas WHERE id = ?"
        PreparedStatement stmt = conn.prepareStatement(sql)
        stmt.setInt(1, id)
        stmt.executeUpdate()

        stmt.close()
        conn.close()
    }

    static Empresa buscarPorId(int id) {
        Connection conn = ConexaoBanco.obterConexao()
        Empresa empresa = null

        String sql = "SELECT * FROM empresas WHERE id = ?"
        PreparedStatement stmt = conn.prepareStatement(sql)
        stmt.setInt(1, id)
        ResultSet rs = stmt.executeQuery()

        if (rs.next()) {
            empresa = new Empresa(
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("cnpj"),
                    rs.getString("pais"),
                    rs.getString("estado"),
                    rs.getString("cep"),
                    rs.getString("descricao"),
                    rs.getString("senha")
            )
            empresa.id = rs.getInt("id")
        }

        rs.close()
        stmt.close()
        conn.close()
        return empresa
    }

    static Empresa buscarPorEmail(String email) {
        Connection conn = ConexaoBanco.obterConexao()
        Empresa empresa = null

        String sql = "SELECT * FROM empresas WHERE LOWER(email) = LOWER(?)"
        PreparedStatement stmt = conn.prepareStatement(sql)
        stmt.setString(1, email)
        ResultSet rs = stmt.executeQuery()

        if (rs.next()) {
            empresa = new Empresa(
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("cnpj"),
                    rs.getString("pais"),
                    rs.getString("estado"),
                    rs.getString("cep"),
                    rs.getString("descricao"),
                    rs.getString("senha")
            )
            empresa.id = rs.getInt("id")
        }

        rs.close()
        stmt.close()
        conn.close()
        return empresa
    }
}