import { Component,OnInit } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';
import {UserService } from '../../services/user.service'
import { Stage } from '../../classes/Stage.class'
import { ExperienceContainer } from '../../classes/ExperienceContainer.class'
import { ExperienceTypes } from '../../assets/conf/expTypes'
@Component({
  selector: 'page-add-stage',
  templateUrl: 'addStage.html'
})


export class AddStagePage implements OnInit {
  experienceContaniner:ExperienceContainer = new ExperienceContainer();
  stage:Stage= new Stage();

  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService){
  }
  ngOnInit():void {
      let stage = this.params.get('stage');
      if (stage!=null){
        this.experienceContaniner=JSON.parse(this.params.get('stage'));
        this.stage = this.experienceContaniner.attributes as Stage;
      }
  }
  addStage(): void {
        //TO DO certification
//    this.stage.institutional=false;
//    this.stage.educational=false;

    //stage.certifierId="a";
    this.experienceContaniner.attributes.certified=false;

    //stage.categorization={};
    this.stage.type=ExperienceTypes.EXP_TYPE_STAGE;
    this.stage.duration=10
    this.stage.location="location stage"
    this.stage.geocode=[0,0]
    this.experienceContaniner.attributes=this.stage;

    if (this.experienceContaniner.id!=null)
      {
       this.userService.updateStage(this.experienceContaniner).then(stage=>
       {
        console.log("done");
        this.navCtrl.pop();
        }
       );
      }
    else {
      this.userService.addStage(this.experienceContaniner).then(stage=>
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
