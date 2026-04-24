import { StorageService } from "../services/StorageService.js";

document.addEventListener("DOMContentLoaded", () => {
  const usuario = StorageService.obterUsuarioAtual();

  const ocultarTodas = (seletores: string[]) => {
    seletores.forEach((seletor) => {
      document.querySelector(seletor)?.classList.add("hidden");
    });
  };

  const ativarBotao = (btnId: string) => {
    document.querySelectorAll(".menu-nav__link").forEach((el) => {
      el.classList.remove("active");
    });
    document.getElementById(btnId)?.classList.add("active");
  };

  const vincularNavegacao = (btnId: string, alvo: string, secoes: string[]) => {
    const btn = document.getElementById(btnId);
    if (!btn) return;

    btn.addEventListener("click", (e) => {
      e.preventDefault();
      ocultarTodas(secoes);
      document.querySelector(alvo)?.classList.remove("hidden");
      ativarBotao(btnId);
    });
  };

  if (usuario.tipo === "candidato") {
    const secoes = ["#perfil-candidato", "#matches", ".match-swipe"];

    vincularNavegacao("nav-candidato-perfil", "#perfil-candidato", secoes);
    vincularNavegacao("nav-candidato-matches", "#matches", secoes);
    vincularNavegacao("nav-candidato-vagas", ".match-swipe", secoes);

    ativarBotao("nav-candidato-perfil");
  } else {
    const secoes = [
      ".match-swipe",
      "#matches",
      "#vagas-empresa",
      "#perfil-empresa",
    ];

    vincularNavegacao("nav-empresa-vagas-match", ".match-swipe", secoes);
    vincularNavegacao("nav-empresa-matches", "#matches", secoes);
    vincularNavegacao("nav-empresa-vagas", "#vagas-empresa", secoes);
    vincularNavegacao("nav-empresa-perfil", "#perfil-empresa", secoes);

    ativarBotao("nav-empresa-perfil");
  }
});
