package linketinder.dao

import linketinder.model.Empresa

interface IEmpresaDAO {
    List<Empresa> listar()
    int inserir(Empresa empresa)
    void atualizar(Empresa empresa)
    boolean existeEmail(String email)
    Empresa buscarPorId(int id)
}