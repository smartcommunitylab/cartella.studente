import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import {NotificationsPage } from '../../notifications/notifications'

@Component({
  selector: 'button-notifications',
  templateUrl: 'button-notifications.component.html'
})
export class ButtonNotifications {
   constructor(public navCtrl: NavController) {}

 goNotifications(){
      this.navCtrl.push(NotificationsPage);
    }
  }
