import { Component } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';

@Component({
  selector: 'page-events',
  templateUrl: 'events.html'
})
export class EventsPage {
  constructor(public navCtrl: NavController, public params: NavParams){
  }
}
