type FormType = "login" | "empresa" | "candidato";

class FormController {
  private login: HTMLFormElement;
  private empresa: HTMLFormElement;
  private candidato: HTMLFormElement;

  constructor() {
    this.login = this.getElement<HTMLFormElement>("form-login");
    this.empresa = this.getElement<HTMLFormElement>("form-empresa");
    this.candidato = this.getElement<HTMLFormElement>("form-candidato");
  }

  private getElement<T extends HTMLElement>(id: string): T {
    const element: HTMLElement | null = document.getElementById(id);

    if (!element) {
      throw new Error(`Elemento #${id} não encontrado`);
    }

    return element as T;
  }

  public show(type: FormType): void {
    this.hideAll();

    if (type === "login") this.login.classList.remove("hidden");
    if (type === "empresa") this.empresa.classList.remove("hidden");
    if (type === "candidato") this.candidato.classList.remove("hidden");
  }

  private hideAll(): void {
    this.login.classList.add("hidden");
    this.empresa.classList.add("hidden");
    this.candidato.classList.add("hidden");
  }
}

document.addEventListener("DOMContentLoaded", (): void => {
  const controller: FormController = new FormController();

  const linkEmpresa: HTMLAnchorElement = document.getElementById(
    "link-empresa",
  ) as HTMLAnchorElement;
  const linkCandidato: HTMLAnchorElement = document.getElementById(
    "link-candidato",
  ) as HTMLAnchorElement;

  const backEmpresa: HTMLButtonElement = document.getElementById(
    "back-login-empresa",
  ) as HTMLButtonElement;
  const backCandidato: HTMLButtonElement = document.getElementById(
    "back-login-candidato",
  ) as HTMLButtonElement;

  if (!linkEmpresa || !linkCandidato || !backEmpresa || !backCandidato) {
    throw new Error("Elementos não encontrados");
  }

  linkEmpresa.addEventListener("click", (e: MouseEvent): void => {
    e.preventDefault();
    controller.show("empresa");
  });

  linkCandidato.addEventListener("click", (e: MouseEvent): void => {
    e.preventDefault();
    controller.show("candidato");
  });

  backEmpresa.addEventListener("click", (): void => {
    controller.show("login");
  });

  backCandidato.addEventListener("click", (): void => {
    controller.show("login");
  });

  document.addEventListener("goToLogin", () => {
    controller.show("login");
  });
});
