import { Component,OnInit } from '@angular/core';
import { NavController } from 'ionic-angular';
import {LoginService} from '../../services/login.service';
import {HomePage } from '../home/home'
@Component({
  selector: 'page-login',
  templateUrl: 'login.html'
})
export class LoginPage implements OnInit {

  types: any = [];
  constructor(public navCtrl: NavController,private loginService: LoginService) {

  }
  getTypes(): void {
    this.types=this.loginService.getLoginTypes();
  }

  // get all the providers when the component is created
 ngOnInit(): void {
    this.getTypes();
  }

  //login as a specific type of user
  login(): void {
    //set the new root=homepage
    this.navCtrl.setRoot(HomePage);
  }
}
