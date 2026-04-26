package linketinder.service

import spock.lang.Specification
import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.dao.ICandidatoDAO
import linketinder.dao.IEmpresaDAO
import linketinder.dao.IVagaDAO
import linketinder.dao.ICompetenciaDAO

class LoginServiceSpec extends Specification {

    CandidatoService candidatoService
    EmpresaService empresaService
    LoginService loginService

    def setup() {
        def mockCandidatoDAO   = Mock(ICandidatoDAO)
        def mockEmpresaDAO     = Mock(IEmpresaDAO)
        def mockVagaDAO        = Mock(IVagaDAO)
        def mockCompetenciaDAO = Mock(ICompetenciaDAO)

        def candidato = new Candidato("Ana Silva", "ana@email.com", "11111111111", 22, "SC", "88000-000", "Dev", [], "senha123")
        def empresa   = new Empresa("TechSul", "rh@techsul.com", "11111111000101", "Brasil", "RS", "90000-100", "Desc", "corp123")

        mockCandidatoDAO.listar() >> [candidato]
        mockEmpresaDAO.listar()   >> [empresa]

        candidatoService = new CandidatoService(mockCandidatoDAO)
        empresaService   = new EmpresaService(mockEmpresaDAO, mockVagaDAO, mockCompetenciaDAO)
        loginService     = new LoginService(candidatoService, empresaService)
    }

    def "Deve retornar null ao tentar login com credenciais inválidas"() {
        when: "Login com credenciais que não existem"
        def resultado = loginService.realizarLogin("inexistente@email.com", "errada")

        then: "Deve retornar null"
        resultado == null
    }

    def "Deve encontrar candidato com e-mail e senha corretos"() {
        when: "Login com credenciais do candidato"
        def resultado = loginService.realizarLogin("ana@email.com", "senha123")

        then: "Deve retornar o candidato"
        resultado instanceof Candidato
        resultado.email == "ana@email.com"
    }

    def "Deve encontrar empresa com e-mail e senha corretos"() {
        when: "Login com credenciais da empresa"
        def resultado = loginService.realizarLogin("rh@techsul.com", "corp123")

        then: "Deve retornar a empresa"
        resultado instanceof Empresa
        resultado.email == "rh@techsul.com"
    }
}