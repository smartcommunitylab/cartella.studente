import { Component,OnInit } from '@angular/core';
import { NavController,NavParams } from 'ionic-angular';
import {UserService } from '../../services/user.service'
import { Certification } from '../../classes/Certification.class'
import { Certificate } from '../../classes/Certificate.class'
import { StudentExperience } from '../../classes/StudentExperience.class'
import { ExperienceContainer } from '../../classes/ExperienceContainer.class'
import { ExperienceTypes } from '../../assets/conf/expTypes';
import {ConfigService} from '../../services/config.service'
import { FileUploader } from 'ng2-file-upload';
@Component({
  selector: 'page-add-certification',
  templateUrl: 'addCertification.html'
})


export class AddCertificationPage implements OnInit {
  studentExperience:StudentExperience = new StudentExperience();
  experienceContaniner:ExperienceContainer = new ExperienceContainer();
  certification:Certification= new Certification();
  certificate:Certificate=new Certificate();
    dateFrom=new Date().toISOString();
  dateTo=new Date().toISOString();
  uploader:FileUploader = new FileUploader({url:'https://dev.smartcommunitylab.it/cs-engine/api/student/84f01dc1-694d-40eb-9296-01ca5014ef5d/experience/57eba2de-4ffc-4db3-9dbf-e2676903d123/certificate/file',authToken:' ',disableMultipart:false});

  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, private config: ConfigService){
  }
  ngOnInit():void {
      let certification = this.params.get('certification');
      if (certification!=null){
        this.studentExperience=JSON.parse(this.params.get('certification'));
        this.experienceContaniner=this.studentExperience.experience;
        this.certification = this.experienceContaniner.attributes as Certification;
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
  addCertification(): void {

    this.certification.type=ExperienceTypes.EXP_TYPE_CERT;
    this.certification.geocode=[0,0]
    this.certification.dateFrom=new Date(this.dateFrom).getTime();
    this.certification.dateTo=new Date(this.dateTo).getTime();
    this.experienceContaniner.attributes=this.certification;
    this.studentExperience.experience=this.experienceContaniner;
    if (this.experienceContaniner.id!=null)
      {
       this.userService.updateCertification(this.studentExperience).then(certification=>
       {
         this.experienceContaniner=certification;
         if (this.uploader.queue.length>0){
         this.uploadCertificate(this.uploader.queue[0]).then(()=>this.navCtrl.pop())
         } else {
          this.navCtrl.pop();
        }
        }
       );
      }
    else {
      this.userService.addCertification(this.studentExperience).then(certification=>
       {
        this.experienceContaniner=certification;
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
