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
  uploader:FileUploader = new FileUploader({url:'https://dev.smartcommunitylab.it/cs-engine/api/student/84f01dc1-694d-40eb-9296-01ca5014ef5d/experience/57eba2de-4ffc-4db3-9dbf-e2676903d123/certificate/file',authToken:'',disableMultipart:false});

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
  addCertification(): void {
        //TO DO certification
//    this.stage.institutional=false;
//    this.stage.educational=false;

    //stage.certifierId="a";
 //   this.experienceContaniner.attributes.certified=false;

    //stage.categorization={};
    this.certification.type=ExperienceTypes.EXP_TYPE_CERT;
    this.certification.geocode=[0,0]
    this.certification.dateFrom=new Date(this.dateFrom).getTime();
    this.certification.dateTo=new Date(this.dateTo).getTime();
    this.experienceContaniner.attributes=this.certification;
    this.studentExperience.experience=this.experienceContaniner;
    if (this.experienceContaniner.id!=null)
      {
       this.userService.updateStage(this.studentExperience).then(certification=>
       {
        console.log("done");
        this.navCtrl.pop();
        }
       );
      }
    else {
      this.userService.addCertification(this.studentExperience).then(certification=>
       {
        console.log("done");
        this.navCtrl.pop();
        }
       );
    }
  }
  uploadCertificate(item):void {
    this.userService.createCertificate(this.experienceContaniner).then(experienceId =>
     {
      var newUrl=this.config.getConfig('apiUrl')+'/student/84f01dc1-694d-40eb-9296-01ca5014ef5d/experience/'+experienceId+'/certificate/file';
      this.uploader.setOptions({ url: newUrl,authToken:'',disableMultipart:false});
      item.upload();
    })

  }
  discard(): void {
    this.navCtrl.pop();
  }
}
