import { Component } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';

@Component({
  selector: 'page-stages',
  templateUrl: 'stages.html'
})
export class StagesPage {
  constructor(public navCtrl: NavController, public params: NavParams){
  }
}
