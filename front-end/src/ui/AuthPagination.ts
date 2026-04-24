type TipoFormulario = "login" | "empresa" | "candidato";

class AlternadorDeFormularios {
  private formLogin: HTMLFormElement;
  private formEmpresa: HTMLFormElement;
  private formCandidato: HTMLFormElement;

  constructor() {
    this.formLogin = this.obterElemento<HTMLFormElement>("form-login");
    this.formEmpresa = this.obterElemento<HTMLFormElement>("form-empresa");
    this.formCandidato = this.obterElemento<HTMLFormElement>("form-candidato");
  }

  exibir(tipo: TipoFormulario): void {
    this.ocultarTodos();
    if (tipo === "login") this.formLogin.classList.remove("hidden");
    if (tipo === "empresa") this.formEmpresa.classList.remove("hidden");
    if (tipo === "candidato") this.formCandidato.classList.remove("hidden");
  }

  private ocultarTodos(): void {
    this.formLogin.classList.add("hidden");
    this.formEmpresa.classList.add("hidden");
    this.formCandidato.classList.add("hidden");
  }

  private obterElemento<T extends HTMLElement>(id: string): T {
    const elemento = document.getElementById(id);
    if (!elemento) throw new Error(`Elemento #${id} não encontrado`);
    return elemento as T;
  }
}

document.addEventListener("DOMContentLoaded", (): void => {
  const alternador = new AlternadorDeFormularios();

  const linkEmpresa = document.getElementById(
    "link-empresa",
  ) as HTMLAnchorElement;
  const linkCandidato = document.getElementById(
    "link-candidato",
  ) as HTMLAnchorElement;
  const voltarEmpresa = document.getElementById(
    "back-login-empresa",
  ) as HTMLButtonElement;
  const voltarCandidato = document.getElementById(
    "back-login-candidato",
  ) as HTMLButtonElement;

  if (!linkEmpresa || !linkCandidato || !voltarEmpresa || !voltarCandidato) {
    throw new Error("Elementos de navegação não encontrados");
  }

  linkEmpresa.addEventListener("click", (e) => {
    e.preventDefault();
    alternador.exibir("empresa");
  });
  linkCandidato.addEventListener("click", (e) => {
    e.preventDefault();
    alternador.exibir("candidato");
  });
  voltarEmpresa.addEventListener("click", () => alternador.exibir("login"));
  voltarCandidato.addEventListener("click", () => alternador.exibir("login"));

  document.addEventListener("goToLogin", () => alternador.exibir("login"));
});
