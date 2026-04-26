package linketinder.service

import spock.lang.Specification
import linketinder.model.Candidato
import linketinder.dao.ICandidatoDAO

class CandidatoServiceSpec extends Specification {

    ICandidatoDAO mockDAO
    CandidatoService service

    def setup() {
        mockDAO = Mock(ICandidatoDAO)
        service = new CandidatoService(mockDAO)
    }

    def "Deve lançar exceção ao cadastrar candidato com e-mail duplicado"() {
        given: "O DAO informa que o e-mail já existe"
        mockDAO.existeEmail("ana@email.com") >> true

        and: "Um candidato com e-mail duplicado"
        def candidato = new Candidato("Carlos", "ana@email.com", "22222222222", 30, "SP", "01000-000", "Dev", [], "senha")

        when: "Tentamos cadastrar"
        service.cadastrar(candidato)

        then: "Deve lançar IllegalArgumentException"
        thrown(IllegalArgumentException)
    }

    def "Deve cadastrar candidato com e-mail novo"() {
        given: "O DAO informa que o e-mail não existe"
        mockDAO.existeEmail("novo@email.com") >> false

        and: "Um candidato novo"
        def candidato = new Candidato("Ana", "novo@email.com", "11111111111", 22, "SC", "88000-000", "Dev", [], "senha")

        when: "Cadastramos"
        service.cadastrar(candidato)

        then: "O id deve ser preenchido"
        1 * mockDAO.inserir(candidato) >> 1
        candidato.id == 1
    }

    def "Deve criar candidato com os dados fornecidos corretamente"() {
        when: "Um candidato é instanciado com dados válidos"
        def candidato = new Candidato("Ana Silva", "ana@email.com", "11111111111", 22, "SC", "88000-000", "Desenvolvedora", [], "senha123")

        then: "Os dados devem estar corretos"
        candidato.nome == "Ana Silva"
        candidato.email == "ana@email.com"
        candidato.cpf == "11111111111"
        candidato.idade == 22
        candidato.estado == "SC"
    }
}