import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import {HomePage } from '../../home/home'

@Component({
  selector: 'button-home',
  templateUrl: 'button-home.component.html'
})
export class ButtonHome {
   constructor(public navCtrl: NavController) {}
    goHome(){
      this.navCtrl.setRoot(HomePage);
    }

  }
