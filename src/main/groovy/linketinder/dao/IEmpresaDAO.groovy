package linketinder.dao

import linketinder.model.Empresa

interface IEmpresaDAO {
    List<Empresa> listar()
    int inserir(Empresa empresa)
    void atualizar(Empresa empresa)
    void deletar(int id)
    boolean existeEmail(String email)
    Empresa buscarPorId(int id)
    Empresa buscarPorEmail(String email)
}