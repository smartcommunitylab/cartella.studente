import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { CreditsPage } from '../../credits/credits'

@Component({
  selector: 'button-credits',
  templateUrl: 'button-credits.component.html'
})
export class ButtonCredits {
  constructor(public navCtrl: NavController) { }

  goCreditsPage() {
    this.navCtrl.push(CreditsPage);
  }

}
