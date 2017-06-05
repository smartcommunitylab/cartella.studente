import { Component,OnInit } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';
import {UserService } from '../../services/user.service'
import { Activity } from '../../classes/Activity.class'
import { Certificate } from '../../classes/Certificate.class'
import { StudentExperience } from '../../classes/StudentExperience.class'
import { ExperienceContainer } from '../../classes/ExperienceContainer.class'
import { ExperienceTypes } from '../../assets/conf/expTypes'
import {ConfigService} from '../../services/config.service'
import { FileUploader } from 'ng2-file-upload';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'
import {WebAPIConnectorService} from '../../services/webAPIConnector.service'

@Component({
  selector: 'page-add-activity',
  templateUrl: 'addActivity.html'
})


export class AddActivityPage implements OnInit {
  studentExperience:StudentExperience = new StudentExperience();
  experienceContaniner:ExperienceContainer = new ExperienceContainer();
  activity:Activity= new Activity();
    certificate:Certificate=new Certificate();
  dateFrom=new Date().toISOString();
  dateTo=new Date().toISOString();
uploader:FileUploader = new FileUploader({});


  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, private config: ConfigService, private utilsService: UtilsService,private translate: TranslateService, private webAPIConnectorService:WebAPIConnectorService){
  }
  ngOnInit():void {
      let activity = this.params.get('activity');
      if (activity!=null){
         this.studentExperience=JSON.parse(this.params.get('activity'));
        this.experienceContaniner=this.studentExperience.experience;
        this.activity = this.experienceContaniner.attributes as Activity;
                this.certificate=this.studentExperience.certificate as Certificate;
        this.dateFrom=new Date(this.experienceContaniner.attributes.dateFrom).toISOString();
        this.dateTo=new Date(this.experienceContaniner.attributes.dateTo).toISOString();
      }
  }

removeCertification(): void {
  this.uploader.clearQueue();
  (<HTMLInputElement>document.getElementById("uploadInputFile")).value="";
}
removeActualCertificate(): void {
  this.userService.deleteCertificate(this.studentExperience).then(()=>
                                           this.certificate=null)
}
  addActivity(): void {

        //TO DO certification



    this.activity.type=ExperienceTypes.EXP_TYPE_ACTIVITY;
    this.activity.duration=10
    this.activity.geocode=[0,0]
    this.activity.dateFrom=new Date(this.dateFrom).getTime();
    this.activity.dateTo=new Date(this.dateTo).getTime();
    this.experienceContaniner.attributes=this.activity;
this.studentExperience.experience=this.experienceContaniner;
    if (this.experienceContaniner.id!=null)
      {
       this.userService.updateActivity(this.studentExperience).then(activity=>
       {
        this.experienceContaniner=activity;
         if (this.uploader.queue.length>0){
         this.uploadCertificate(this.uploader.queue[0]).then(()=>this.navCtrl.pop())
         } else {
          this.navCtrl.pop();
          this.utilsService.toast(this.translate.instant('toast_add_activity'), 3000, 'middle');

        }
        }
       );
      }
    else {
      this.userService.addActivity(this.studentExperience).then(activity=>
       {
        this.experienceContaniner=activity;
        if (this.uploader.queue.length>0){
         this.uploadCertificate(this.uploader.queue[0]).then(()=>this.navCtrl.pop())
        } else {
          this.navCtrl.pop();
                    this.utilsService.toast(this.translate.instant('toast_add_activity'), 3000, 'middle');

        }
        }
       );
    }
  }
  uploadCertificate(item):Promise<void> {
    return new Promise<void>((resolve, reject) => {
    this.userService.createCertificate(this.experienceContaniner).then(experienceId =>
     {
      this.webAPIConnectorService.uploadCertificate(this.uploader,this.userService.getUserId(),experienceId,item);
      resolve();
    })
})

  }
  discard(): void {
    this.navCtrl.pop();
  }
}
