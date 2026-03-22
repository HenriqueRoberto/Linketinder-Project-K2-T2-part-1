import { AuthService } from "./AuthService.js";
import { Candidato, Empresa } from "../types.js";

export class AuthController {
  private auth = new AuthService();

  constructor() {
    this.login();
    this.candidato();
    this.empresa();
    this.logout();
  }

  private input(id: string): HTMLInputElement {
    return document.getElementById(id) as HTMLInputElement;
  }

  private textarea(id: string): HTMLTextAreaElement {
    return document.getElementById(id) as HTMLTextAreaElement;
  }

  private login(): void {
    const form = document.getElementById("form-login") as HTMLFormElement;
    if (!form) return;

    form.onsubmit = (e) => {
      e.preventDefault();
      this.auth.login(this.input("email").value, this.input("password").value);
    };
  }

  private candidato(): void {
    const form = document.getElementById("form-candidato") as HTMLFormElement;
    if (!form) return;

    form.onsubmit = (e) => {
      e.preventDefault();

      const user: Candidato = {
        id: crypto.randomUUID(),
        tipo: "candidato",
        nome: this.input("nome-candidato").value,
        email: this.input("email-candidato").value,
        senha: this.input("senha-candidato").value,
        cpf: this.input("cpf-candidato").value,
        idade: this.input("idade-candidato").value,
        estado: this.input("estado-candidato").value,
        cep: this.input("cep-candidato").value,
        descricao: this.textarea("descricao-candidato").value,
        competencias: [],
      };

      this.auth.register(user);
    };
  }

  private empresa(): void {
    const form = document.getElementById("form-empresa") as HTMLFormElement;
    if (!form) return;

    form.onsubmit = (e) => {
      e.preventDefault();

      const user: Empresa = {
        id: crypto.randomUUID(),
        tipo: "empresa",
        nome: this.input("nome-empresa").value,
        email: this.input("email-empresa").value,
        senha: this.input("senha-empresa").value,
        cnpj: this.input("cnpj").value,
        pais: this.input("pais-empresa").value,
        estado: this.input("estado-empresa").value,
        cep: this.input("cep-empresa").value,
        descricao: this.textarea("descricao-empresa").value,
      };

      this.auth.register(user);
    };
  }

  private logout(): void {
    document.getElementById("log-out")?.addEventListener("click", () => {
      this.auth.logout();
    });
  }
}
document.addEventListener("DOMContentLoaded", () => {
  new AuthController();
});
