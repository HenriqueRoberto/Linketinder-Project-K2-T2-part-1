package linketinder

import linketinder.controller.AppController
import linketinder.view.MenuView

class Main {
    static void main(String[] args) {
        new MenuView(AppController.criar()).iniciar()
    }
}