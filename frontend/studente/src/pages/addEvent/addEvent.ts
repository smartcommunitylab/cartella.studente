import { Component,OnInit } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';
import {UserService } from '../../services/user.service'
import { Event } from '../../classes/Event.class'
import { ExperienceContainer } from '../../classes/ExperienceContainer.class'
import { ExperienceTypes } from '../../assets/conf/expTypes'
@Component({
  selector: 'page-add-event',
  templateUrl: 'addEvent.html'
})


export class AddEventPage implements OnInit {
  experienceContaniner:ExperienceContainer = new ExperienceContainer();
  event:Event= new Event();
  dateFrom=new Date().toISOString();
  dateTo=new Date().toISOString();
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService){
  }
  ngOnInit():void {
      let event = this.params.get('event');
      if (event!=null){
        this.experienceContaniner=JSON.parse(this.params.get('event'));
        this.event = this.experienceContaniner.attributes as Event;
        this.dateFrom=new Date(this.experienceContaniner.attributes.dateFrom).toISOString();
        this.dateTo=new Date(this.experienceContaniner.attributes.dateTo).toISOString();
      }
  }
  addEvent(): void {
        //TO DO certification
//    this.stage.institutional=false;
//    this.stage.educational=false;

    //stage.certifierId="a";
    this.experienceContaniner.attributes.certified=false;

    //stage.categorization={};
    this.event.type=ExperienceTypes.EXP_TYPE_STAGE;
    this.event.geocode=[0,0]
      this.event.dateFrom=new Date(this.dateFrom).getTime();
    this.event.dateTo=new Date(this.dateTo).getTime();
    this.experienceContaniner.attributes=this.event;

    if (this.experienceContaniner.id!=null)
      {
       this.userService.updateStage(this.experienceContaniner).then(event=>
       {
        console.log("done");
        this.navCtrl.pop();
        }
       );
      }
    else {
      this.userService.addEvent(this.experienceContaniner).then(event=>
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
