package linketinder.service

import linketinder.dao.ICompetenciaDAO
import linketinder.dao.IEmpresaDAO
import linketinder.dao.IVagaDAO
import linketinder.model.Competencia
import linketinder.model.Empresa
import linketinder.model.Vaga

class EmpresaService {

    private final IEmpresaDAO empresaDAO
    private final IVagaDAO vagaDAO
    private final ICompetenciaDAO competenciaDAO

    EmpresaService(IEmpresaDAO empresaDAO, IVagaDAO vagaDAO, ICompetenciaDAO competenciaDAO) {
        this.empresaDAO = empresaDAO
        this.vagaDAO = vagaDAO
        this.competenciaDAO = competenciaDAO
    }

    void cadastrar(Empresa empresa) {
        if (empresaDAO.existeEmail(empresa.email)) {
            throw new IllegalArgumentException("Erro: O e-mail " + empresa.email + " já está cadastrado.")
        }
        int idGerado = empresaDAO.inserir(empresa)
        empresa.id = idGerado
    }

    List<Empresa> listar() {
        return empresaDAO.listar()
    }

    Empresa buscarPorEmail(String email) {
        return empresaDAO.buscarPorEmail(email)
    }

    Empresa buscarPorId(int id) {
        return empresaDAO.buscarPorId(id)
    }

    void atualizar(Empresa empresa) {
        empresaDAO.atualizar(empresa)
    }

    List<Vaga> listarTodasVagas() {
        return vagaDAO.listarTodas()
    }

    void criarVaga(int idEmpresa, Vaga vaga) {
        Empresa empresa = empresaDAO.buscarPorId(idEmpresa)
        if (empresa == null) throw new IllegalArgumentException("Empresa não encontrada.")

        int idVaga = vagaDAO.inserir(vaga)
        vaga.id = idVaga

        for (Competencia comp : vaga.competencias) {
            int idComp = competenciaDAO.buscarOuInserir(comp.nome)
            competenciaDAO.vincularVaga(idVaga, idComp)
        }
    }

    List<Vaga> listarVagasDaEmpresa(int idEmpresa) {
        return vagaDAO.listarPorEmpresa(idEmpresa)
    }

    void editarVaga(int idEmpresa, int indice, Vaga vagaAtualizada) {
        List<Vaga> vagas = vagaDAO.listarPorEmpresa(idEmpresa)

        if (indice < 0 || indice >= vagas.size()) {
            throw new IllegalArgumentException("Índice de vaga inválido.")
        }

        vagaAtualizada.id = vagas[indice].id
        vagaDAO.atualizar(vagaAtualizada)

        competenciaDAO.desvincularTodasDaVaga(vagaAtualizada.id)
        for (Competencia comp : vagaAtualizada.competencias) {
            int idComp = competenciaDAO.buscarOuInserir(comp.nome)
            competenciaDAO.vincularVaga(vagaAtualizada.id, idComp)
        }
    }

    void excluirVaga(int idEmpresa, int indice) {
        List<Vaga> vagas = vagaDAO.listarPorEmpresa(idEmpresa)

        if (indice < 0 || indice >= vagas.size()) {
            throw new IllegalArgumentException("Índice de vaga inválido.")
        }

        vagaDAO.deletar(vagas[indice].id)
    }

    void aplicarEdicao(Empresa empresa, Map<String, String> dados) {
        if (!dados.nome.isEmpty())      empresa.nome      = dados.nome
        if (!dados.cnpj.isEmpty())      empresa.cnpj      = dados.cnpj
        if (!dados.pais.isEmpty())      empresa.pais      = dados.pais
        if (!dados.estado.isEmpty())    empresa.estado    = dados.estado
        if (!dados.cep.isEmpty())       empresa.cep       = dados.cep
        if (!dados.descricao.isEmpty()) empresa.descricao = dados.descricao
        if (!dados.senha.isEmpty())     empresa.senha     = dados.senha
        empresaDAO.atualizar(empresa)
    }
}