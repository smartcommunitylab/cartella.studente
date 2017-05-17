import { Component } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import {UserService } from '../../services/user.service';
import { ExperienceContainer } from '../../classes/ExperienceContainer.class';
import { AddEventPage } from '../addEvent/addEvent';
import {TranslateService} from 'ng2-translate';
@Component({
  selector: 'page-events',
  templateUrl: 'events.html'
})
export class EventsPage  {
  events:ExperienceContainer[]=[];
  order=true;
icon="ios-arrow-down";
  shownEvent=null;
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService,public loading: LoadingController,private alertCtrl: AlertController, private translate: TranslateService){
  }

toggleDetails(event) {
    if (this.isDetailsShown(event)) {
        this.shownEvent = null;
    } else {
        this.shownEvent = event;
    }
};
isDetailsShown(event) {
    return this.shownEvent === event;
};

  addNewEvent(): void {
    this.navCtrl.push(AddEventPage);
  }

updateEvent(event): void {
    this.navCtrl.push(AddEventPage, {stage:JSON.stringify(event)});
  }

  deleteEvent(event): void {
   //ask confirmation

      let alert = this.alertCtrl.create({
    title: this.translate.instant('alert_delete_event_title'),
    message:  this.translate.instant('alert_delete_event_message'),
    buttons: [
      {
        text: this.translate.instant('alert_cancel'),
        role: 'cancel'

      },
      {
        text: this.translate.instant('alert_confirm'),
        handler: () => {
              let loader = this.loading.create({
    content: this.translate.instant('loading'),
  });
              this.userService.deleteStage(event).then(event =>{
       //remove stage from stage
      for (var i=0; i<this.events.length;i++)
        {
          if (this.events[i].id==event.id)
            {
                 this.events.splice(i, 1);
            }
          }
        loader.dismiss();
        })
        }
      }
    ]
  });
  alert.present();

  }
//loaded when it is showed
ionViewWillEnter () {
    let loader = this.loading.create({
    content: this.translate.instant('loading'),
  });
  loader.present().then(() => {
        this.userService.getUserEvents().then(events =>{
        this.events=events
          loader.dismiss();
  })
  })
}
}
