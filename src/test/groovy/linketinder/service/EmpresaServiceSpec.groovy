package linketinder.service

import spock.lang.Specification
import linketinder.model.Empresa
import linketinder.dao.IEmpresaDAO
import linketinder.dao.IVagaDAO
import linketinder.dao.ICompetenciaDAO

class EmpresaServiceSpec extends Specification {

    IEmpresaDAO mockEmpresaDAO
    IVagaDAO mockVagaDAO
    ICompetenciaDAO mockCompetenciaDAO
    EmpresaService service

    def setup() {
        mockEmpresaDAO    = Mock(IEmpresaDAO)
        mockVagaDAO       = Mock(IVagaDAO)
        mockCompetenciaDAO = Mock(ICompetenciaDAO)
        service = new EmpresaService(mockEmpresaDAO, mockVagaDAO, mockCompetenciaDAO)
    }

    def "Deve lançar exceção ao cadastrar empresa com e-mail duplicado"() {
        given: "O DAO informa que o e-mail já existe"
        mockEmpresaDAO.existeEmail("rh@techsul.com") >> true

        and: "Uma nova empresa com o mesmo e-mail"
        def empresa = new Empresa("OutraEmpresa", "rh@techsul.com", "22222222000199", "Brasil", "SC", "88000-000", "Outra", "pass456")

        when: "Tentamos cadastrar"
        service.cadastrar(empresa)

        then: "Deve lançar IllegalArgumentException"
        thrown(IllegalArgumentException)
    }

    def "Deve cadastrar empresa com e-mail novo"() {
        given: "O DAO informa que o e-mail não existe"
        mockEmpresaDAO.existeEmail("novo@empresa.com") >> false

        and: "Uma empresa nova"
        def empresa = new Empresa("Nova", "novo@empresa.com", "33333333000100", "Brasil", "SP", "01000-000", "Desc", "senha")

        when: "Cadastramos"
        service.cadastrar(empresa)

        then: "O id deve ser preenchido"
        1 * mockEmpresaDAO.inserir(empresa) >> 5
        empresa.id == 5
    }

    def "Deve criar empresa com os dados fornecidos corretamente"() {
        when: "Uma empresa é instanciada com dados válidos"
        def empresa = new Empresa("TechSul", "rh@techsul.com", "11111111000101", "Brasil", "RS", "90000-100", "Software sob demanda", "corp123")

        then: "Os dados devem estar corretos"
        empresa.nome == "TechSul"
        empresa.email == "rh@techsul.com"
        empresa.cnpj == "11111111000101"
        empresa.pais == "Brasil"
    }
}