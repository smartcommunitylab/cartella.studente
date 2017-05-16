import { Component } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'
@Component({
  selector: 'page-institute',
  templateUrl: 'institute.html'
})
export class InstitutePage {
  public institute:any;
  constructor(public navCtrl: NavController, public params: NavParams){
    this.institute=params.get('paramRegistration');
  }
}
