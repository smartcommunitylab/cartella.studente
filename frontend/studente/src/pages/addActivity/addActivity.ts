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
uploader:FileUploader = new FileUploader({url:'https://dev.smartcommunitylab.it/cs-engine/api/student/84f01dc1-694d-40eb-9296-01ca5014ef5d/experience/57eba2de-4ffc-4db3-9dbf-e2676903d123/certificate/file',authToken:' ',disableMultipart:false});


  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, private config: ConfigService){
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
  addActivity(): void {

        //TO DO certification


    this.experienceContaniner.attributes.certified=false;

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
        }
        }
       );
    }
  }
  uploadCertificate(item):Promise<void> {
    return new Promise<void>((resolve, reject) => {
    this.userService.createCertificate(this.experienceContaniner).then(experienceId =>
     {
      var newUrl=this.config.getConfig('apiUrl')+'/student/84f01dc1-694d-40eb-9296-01ca5014ef5d/experience/'+experienceId+'/certificate/file';
      this.uploader.setOptions({ url: newUrl,authToken:' ',disableMultipart:false});
      item.upload();
      resolve();
    })
})

  }
  discard(): void {
    this.navCtrl.pop();
  }
}
