import { Component } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';
import { UserService } from '../../services/user.service';
import { Certification} from '../../classes/Certification.class';

@Component({
  selector: 'page-certifications',
  templateUrl: 'certifications.html'
})
export class CertificationsPage {
certifications:Certification[]=[]
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService){
  }
  ngOnInit():void {
     this.userService.getUserCertifications().then(certifications =>{
      this.certifications=certifications
    });
  }
}
