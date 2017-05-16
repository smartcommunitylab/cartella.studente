import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { ProfilePage } from '../../profile/profile'

@Component({
  selector: 'button-profile',
  templateUrl: 'button-profile.component.html'
})
export class ButtonProfile {
   constructor(public navCtrl: NavController) {}

  goProfile(){
      this.navCtrl.push(ProfilePage);
    }
  }
