import { Component } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';

@Component({
  selector: 'page-curriculum',
  templateUrl: 'curriculum.html'
})
export class CurriculumPage {
  constructor(public navCtrl: NavController, public params: NavParams){
  }
}
