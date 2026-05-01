package linketinder.dao

import java.sql.Connection
import java.sql.DriverManager

class ConexaoBanco {

    private static final String URL     = "jdbc:postgresql://localhost:5432/linketinder"
    private static final String USUARIO = "app_user"
    private static final String SENHA   = "1234"

    private static Connection instancia

    static Connection obterConexao() {
        if (instancia == null || instancia.isClosed()) {
            instancia = DriverManager.getConnection(URL, USUARIO, SENHA)
        }
        return instancia
    }
}