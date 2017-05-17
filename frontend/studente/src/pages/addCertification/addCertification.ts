import { Component,OnInit } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';
import {UserService } from '../../services/user.service'
import { Certification } from '../../classes/Certification.class'
import { ExperienceContainer } from '../../classes/ExperienceContainer.class'
import { ExperienceTypes } from '../../assets/conf/expTypes'
@Component({
  selector: 'page-add-certification',
  templateUrl: 'addCertification.html'
})


export class AddCertificationPage implements OnInit {
  experienceContaniner:ExperienceContainer = new ExperienceContainer();
  certification:Certification= new Certification();

  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService){
  }
  ngOnInit():void {
      let certification = this.params.get('certification');
      if (certification!=null){
        this.experienceContaniner=JSON.parse(this.params.get('certification'));
        this.certification = this.experienceContaniner.attributes as Certification;
      }
  }
  addCertification(): void {
        //TO DO certification
//    this.stage.institutional=false;
//    this.stage.educational=false;

    //stage.certifierId="a";
    this.experienceContaniner.attributes.certified=false;

    //stage.categorization={};
    this.certification.type=ExperienceTypes.EXP_TYPE_CERT;
    this.certification.location="location event"
    this.certification.geocode=[0,0]
    this.experienceContaniner.attributes=this.certification;

    if (this.experienceContaniner.id!=null)
      {
       this.userService.updateStage(this.experienceContaniner).then(certification=>
       {
        console.log("done");
        this.navCtrl.pop();
        }
       );
      }
    else {
      this.userService.addCertification(this.experienceContaniner).then(certification=>
       {
        console.log("done");
        this.navCtrl.pop();
        }
       );
    }
  }
  discard(): void {
    this.navCtrl.pop();
  }
}
