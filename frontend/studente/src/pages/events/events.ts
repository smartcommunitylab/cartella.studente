import { Component } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';
import {Event } from '../../classes/Event.class';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'page-events',
  templateUrl: 'events.html'
})
export class EventsPage {
 events:Event[]=[]
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService){
  }
  ngOnInit():void {
     this.userService.getUserEvents().then(events =>{
      this.events=events
    });
  }
}
