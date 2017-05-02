import { Component,OnInit } from '@angular/core';
import { NavController } from 'ionic-angular';
import { WebAPIConnectorService } from '../../services/webAPIConnector.service'
import {Registration } from '../../classes/Registration.interface'
import {InstitutePage } from '../institute/institute'
@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage implements OnInit{

  registrations:Registration[]=[];
  constructor(public navCtrl: NavController, private webAPIConnectorService: WebAPIConnectorService ) {

  }
openRegistration(registration: Registration):void {
  this.navCtrl.push(InstitutePage,{paramRegistration:registration})
}
 ngOnInit(): void {
    this.webAPIConnectorService.getRegistrations().then(registrations =>{
      this.registrations=registrations
    });
  }
}
